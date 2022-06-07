#!/bin/bash
#
#Copyright (c) 2014-2018 Oracle and/or its affiliates. All rights reserved.
#
#Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.

#Define DOMAIN_HOME
echo "Oracle Home is: " "$ORACLE_HOME"
echo "Domain Home is: " "${WL_DOMAIN_HOME}"

# init setup properties
STATUS_FILE=${WL_STATUS_FOLDER}/wls-admin.started
# delete status file if already exists..
[ -e "${STATUS_FILE}" ] && rm "${STATUS_FILE}"

function initWebLogicDomain(){
  echo "Init WebLogic domain"
  INIT_SCRIPTS=$1
  echo "Start createWLSDomain.sh from ${INIT_SCRIPTS}"
  "${INIT_SCRIPTS}"/createWLSDomain.sh "${INIT_SCRIPTS}"
  echo "Set execution flag for all sh scripts in ${WL_DOMAIN_HOME}/bin"
  chmod -R a+x ${WL_DOMAIN_HOME}/bin/*.sh
}

function deploy_smp() {
  echo "Deploy SMP"
  INIT_SCRIPTS=$1
  "${INIT_SCRIPTS}"/deploySMPToDomain.sh "${INIT_SCRIPTS}"

  # set enforce-valid-basic-auth-credentials false to allow basic authentication for rest services
  sed -i -e "s/<\/security-configuration>/  <enforce-valid-basic-auth-credentials>false<\/enforce-valid-basic-auth-credentials>\n<\/security-configuration>/g" "${WL_DOMAIN_HOME}/config/config.xml"

}

# If AdminServer.log does not exists, container is starting for 1st time
# So it should start NM and also associate with AdminServer
# Otherwise, only start NM (container restarted)
########### SIGTERM handler ############
function _term() {
  echo "Stopping container."
  echo "SIGTERM received, shutting down the server!"
  ${DOMAIN_HOME}/bin/stopWebLogic.sh
}

########### SIGKILL handler ############
function _kill() {
  echo "SIGKILL received, shutting down the server!"
  kill -9 $childPID
}

# Set SIGTERM handler
trap _term SIGTERM

# Set SIGKILL handler
trap _kill SIGKILL

#Loop determining state of WLS
function check_wls() {
  action=$1
  host=$2
  port=$3
  sleeptime=$4
  echo "action:$action,host:$host,port:$port,sleeptime:$sleeptime,"
  while true; do
    sleep $sleeptime
    if [ "$action" == "started" ]; then
      started_url="http://$host:$port/weblogic/ready"
      echo -e "Waiting for WebLogic server to get $action, checking $started_url"
      status=$(/usr/bin/curl -s -i $started_url | grep "200 OK")
      echo "Status:" $status
      if [ ! -z "$status" ]; then
        break
      fi
    elif [ "$action" == "shutdown" ]; then
      shutdown_url="http://$host:$port"
      echo -e "Waiting for WebLogic server to get $action, checking $shutdown_url"
      status=$(/usr/bin/curl -s -i $shutdown_url | grep "500 Can't connect")
      if [ ! -z "$status" ]; then
        break
      fi
    fi
  done
  echo -e "WebLogic Server has $action"
}

export AS_HOME="${WL_DOMAIN_HOME}/servers/${WL_ADMIN_NAME}"
export AS_SECURITY="${AS_HOME}/security"



if [ -f ${AS_HOME}/logs/${ADMIN_NAME}.log ]; then
  exit
fi
echo "Initialize domain and deploy smp"
# initialize docker image
cd ~ || exit 13
if [ ! -f ".initialized" ]; then
  echo "Initialize domain and deploy smp"
  INIT_SCRIPTS=${ORACLE_HOME}/init/scripts
  initWebLogicDomain "${INIT_SCRIPTS}"
  deploy_smp "${INIT_SCRIPTS}"
  [ -f "${DOCKER_DATA}/${WL_CLUSTER_NAME}.jar" ] && rm -rf "${DOCKER_DATA}/${WL_CLUSTER_NAME}-${SMP_VERSION}.jar"
  pack.sh -domain="${WL_DOMAIN_HOME}" \
          -template="${DOCKER_DATA}/${WL_CLUSTER_NAME}-${SMP_VERSION}.jar" \
          -template_name="${WL_CLUSTER_NAME}" \
          -managed="true" \
          -template_desc="${WL_DOMAIN_NAME}-managed-template-for-SMP-${SMP_VERSION}"

  touch ~/.initialized
fi

echo "Admin Server Home: ${AS_HOME}"
echo "Admin Server Security: ${AS_SECURITY}"

# WL_SECURITY_FILE should be created in createWLSDomain script
SEC_PROPERTIES_FILE=${WL_SECURITY_FILE}
if [ ! -e "${SEC_PROPERTIES_FILE}" ]; then
  echo "A security.properties file with the username and password needs to be supplied."
  exit
fi

#Define Java Options
JAVA_OPTIONS=$(awk '{print $1}' ${SEC_PROPERTIES_FILE} | grep ^JAVA_OPTIONS= | cut -d "=" -f2)
if [ -z "${JAVA_OPTIONS}" ]; then
  JAVA_OPTIONS="-Dweblogic.StdoutDebugEnabled=false"
fi
export JAVA_OPTIONS=${JAVA_OPTIONS}


#echo 'Running Admin Server in background'
${WL_DOMAIN_HOME}/bin/startWebLogic.sh &

#echo 'Waiting for Admin Server to reach RUNNING state'
check_wls "started" localhost ${WL_ADMIN_PORT} 2
echo "Smp admin server started" >>"$STATUS_FILE"

# tail the Admin Server Logs
tail -f ${AS_HOME}/logs/${WL_ADMIN_NAME}.log &

childPID=$!
wait $childPID
