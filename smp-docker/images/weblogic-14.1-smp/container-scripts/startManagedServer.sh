#!/bin/bash
#
# Copyright (c) 2014-2018 Oracle and/or its affiliates. All rights reserved.
#
# If log.nm does not exists, container is starting for 1st time
# So it should start NM and also associate with AdminServer, as well Managed Server
# Otherwise, only start NM (container is being restarted)

echo "Delay startup in seconds: ${WL_DELAY_STARTUP_IN_S:-0}"
sleep ${WL_DELAY_STARTUP_IN_S:-0}

function initWebLogicDomain(){
  echo "Init WebLogic domain"
  INIT_SCRIPTS=$1
  echo "Start createWLSDomain.sh from ${INIT_SCRIPTS}"
  "${INIT_SCRIPTS}"/createWLSDomain.sh "${INIT_SCRIPTS}"
  echo "Set execution flag for all sh scripts in ${WL_DOMAIN_HOME}/bin"
  chmod -R a+x ${WL_DOMAIN_HOME}/bin/*.sh
}


export MS_HOME="${WL_DOMAIN_HOME}/servers/${WL_MANAGED_SERV_NAME}"
export MS_SECURITY="${MS_HOME}/security"

if [ -f ${MS_HOME}/logs/${WL_MANAGED_SERV_NAME}.log ]; then
   echo "Log file already exists ${MS_HOME}/logs/${WL_MANAGED_SERV_NAME}.log"
   exit
fi

# Wait for AdminServer to become available for any subsequent operation
/u01/oracle/waitForAdminServer.sh

echo "Managed Server Name: ${WL_MANAGED_SERV_NAME}"
echo "Managed Server Home: ${MS_HOME}"
echo "Managed Server Security: ${MS_SECURITY}"

SEC_PROPERTIES_FILE=${WL_SECURITY_FILE}
if [ ! -e "${SEC_PROPERTIES_FILE}" ]; then
   echo "A properties file with the username and password needs to be supplied. Use default properties"
   exit
fi

# Get Username
USER=`awk '{print $1}' ${SEC_PROPERTIES_FILE} | grep username | cut -d "=" -f2`
if [ -z "${USER}" ]; then
   echo "The domain username is blank.  The Admin username must be set in the properties file."
   exit
fi
# Get Password
PASS=`awk '{print $1}' ${SEC_PROPERTIES_FILE} | grep password | cut -d "=" -f2`
if [ -z "${PASS}" ]; then
   echo "The domain password is blank.  The Admin password must be set in the properties file."
   exit
fi

# initialize docker image
cd ~ || exit 13
if [ ! -f ".initialized" ]; then
  INIT_SCRIPTS=${ORACLE_HOME}/init/scripts
  echo "create domain folder ${WL_DOMAIN_HOME}"
  unpack.sh -template="${DOCKER_DATA}/${WL_CLUSTER_NAME}-${SMP_VERSION}.jar" -domain="${WL_DOMAIN_HOME}" -app_dir="${WL_DOMAIN_HOME}"
  touch ~/.initialized
fi

cd ${WL_DOMAIN_HOME}



#Set Java Options
JAVA_OPTIONS=`awk '{print $1}' ${SEC_PROPERTIES_FILE} | grep ^JAVA_OPTIONS= | cut -d "=" -f2`
if [ -z "${JAVA_OPTIONS}" ]; then
   JAVA_OPTIONS="-Dweblogic.StdoutDebugEnabled=false"
fi
export JAVA_OPTIONS=${JAVA_OPTIONS}
echo "Java Options: ${JAVA_OPTIONS}"

# Create Managed Server
mkdir -p "${MS_SECURITY}"
echo "username=${USER}" >> "${MS_SECURITY}"/boot.properties
echo "password=${PASS}" >> "${MS_SECURITY}"/boot.properties


"${WL_DOMAIN_HOME}"/bin/setDomainEnv.sh

# Start 'ManagedServer'
echo "Start Managed Server"
"${WL_DOMAIN_HOME}"/bin/startManagedWebLogic.sh ${WL_MANAGED_SERV_NAME} http://${WL_ADMIN_HOST}:${WL_ADMIN_PORT}

# tail Managed Server log
tail -f ${MS_HOME}/logs/"${WL_MANAGED_SERV_NAME}".log &

childPID=$!
wait $childPID
