#!/usr/bin/env bash
#
#Copyright (c) 2014, 2020, Oracle and/or its affiliates.
#
#Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

#Define WL_DOMAIN_HOME
INIT_SCRIPTS=$1
echo "Domain Home is: $WL_DOMAIN_HOME"
echo "Scripts folder is: $INIT_SCRIPTS"

source "${INIT_SCRIPTS}/functions/keystore.functions"

# If AdminServer.log does not exists, container is starting for 1st time
# So it should start NM and also associate with AdminServer
# Otherwise, only start NM (container restarted)
########### SIGTERM handler ############
function _term() {
  echo "Stopping container."
  echo "SIGTERM received, shutting down the server!"
  ${WL_DOMAIN_HOME}/bin/stopWebLogic.sh
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


function init_server_https_keystore(){
   # configure https
		CERTIFICATES=${CERTIFICATES:-/tmp/}
	  HOST_DOMAIN=${WL_ADMIN_HOST:-localhost}
	  # put keystore to wildfly configuration folder
	  [[ ! -d "${WL_DATA_WEBLOGIC}/keystores" ]] &&  mkdir -p "${WL_DATA_WEBLOGIC}/keystores"
	  KEYSTORE_PATH="${WL_DATA_WEBLOGIC}/keystores/admin-tls-keystore.p12"

    CLIENT_KEYSTORE_PATH="${WL_DATA_WEBLOGIC}/keystores/client-tls-keystore.p12"
    TRUSTSTORE_PATH="${WL_DATA_WEBLOGIC}/keystores/admin-tls-truststore.p12"

    generateKeyStore "${HOST_DOMAIN}" "${WL_SERVER_TLS_KEYSTORE_PASS}" "${WL_SERVER_TLS_KEYSTORE_PASS}" "${KEYSTORE_PATH}"
    generateKeyStore "Client-TLS-Certificate" "${WL_SERVER_TLS_KEYSTORE_PASS}" "${WL_SERVER_TLS_KEYSTORE_PASS}" "${TRUSTSTORE_PATH}"

    wlst.sh -skipWLSModuleScanning  "$INIT_SCRIPTS/enable-server-https.py" "${KEYSTORE_PATH}" "${TRUSTSTORE_PATH}"
}

if [ -f ${WL_DOMAIN_HOME}/servers/${WL_ADMIN_NAME}/logs/${WL_ADMIN_NAME}.log ]; then
  echo "Admin log file: [${WL_DOMAIN_HOME}/servers/${WL_ADMIN_NAME}/logs/${WL_ADMIN_NAME}.log] already exists - Skip domain creation!"
  exit
fi

DOMAIN_PROPERTY_DIR=/tmp/create-domain/properties
mkdir -p "${DOMAIN_PROPERTY_DIR}"

DOMAIN_PROPERTIES_FILE=${DOMAIN_PROPERTY_DIR}/domain.properties
SEC_PROPERTIES_FILE=${DOMAIN_PROPERTY_DIR}/domain_security.properties

# copy domain properties - check first init folder else use default
if [ -e "${WL_INIT_PROPERTIES}/domain.properties" ]; then
  cp -f "${WL_INIT_PROPERTIES}/domain.properties" "${DOMAIN_PROPERTIES_FILE}"
else
  cp -f "${INIT_SCRIPTS}"/../properties/domain.properties "${DOMAIN_PROPERTIES_FILE}"
fi
# copy security properties - check first init folder else use default
if [ -e "${WL_INIT_PROPERTIES}/domain_security.properties" ]; then
  cp -f "${WL_INIT_PROPERTIES}/domain_security.properties" "${SEC_PROPERTIES_FILE}"
elif [ -e "${INIT_SCRIPTS}/../properties/domain_security.properties" ]; then
  cp -f "${INIT_SCRIPTS}/../properties/domain_security.properties" "${SEC_PROPERTIES_FILE}"
else
  echo "To increase security please provide custom admin username and password in ${SEC_PROPERTIES_FILE}."
  defUsername=weblogic
  randPass=$(LC_ALL=C tr -dc A-Za-z0-9 </dev/urandom | head -c 64)
  echo "username=${defUsername}" >"${SEC_PROPERTIES_FILE}"
  echo "password=${randPass}" >>"${SEC_PROPERTIES_FILE}"
  echo "Generated WebLogic admin user with credentials: ${defUsername}/${randPass}"
fi

# Get Username
USER=$(awk '{print $1}' ${SEC_PROPERTIES_FILE} | grep username | cut -d "=" -f2)
if [ -z "${USER}" ]; then
  echo "The domain username is blank.  The Admin username must be set in the properties file."
  exit
fi
# Get Password
PASS=$(awk '{print $1}' ${SEC_PROPERTIES_FILE} | grep password | cut -d "=" -f2)
if [ -z "${PASS}" ]; then
  echo "The domain password is blank.  The Admin password must be set in the properties file."
  exit
fi


cat <<EOT >>  "${DOMAIN_PROPERTIES_FILE}"

DOMAIN_NAME=${WL_DOMAIN_NAME}
ADMIN_PORT=${WL_ADMIN_PORT}
ADMIN_HTTPS_PORT=${WL_ADMIN_PORT_HTTPS}
ADMIN_NAME=${WL_ADMIN_NAME}
ADMIN_HOST=${WL_ADMIN_HOST}
ADMIN_UPLOAD_FOLDER=${WL_ADMIN_UPLOAD_FOLDER}
MANAGED_SERVER_PORT=${WL_MANAGED_SERVER_PORT}
MANAGED_SERVER_NAME_BASE=${WL_MANAGED_SERV_BASE_NAME}
CONFIGURED_MANAGED_SERVER_COUNT=${WL_MANAGED_SERVER_COUNT}
CLUSTER_NAME=${WL_CLUSTER_NAME}
DEBUG_FLAG=${WL_DEBUG_FLAG}
PRODUCTION_MODE_ENABLED=${WL_PRODUCTION_MODE_ENABLED}
EOT

echo "Init domain with following properties"
cat ${DOMAIN_PROPERTIES_FILE}
echo "Show domain home $WL_DOMAIN_HOME"
# Create domain
wlst.sh -skipWLSModuleScanning -loadProperties "${DOMAIN_PROPERTIES_FILE}" -loadProperties "${SEC_PROPERTIES_FILE}" "$INIT_SCRIPTS/create-wls-domain.py"

ENC_PASS=$(java -cp $ORACLE_HOME/wlserver/server/lib/weblogic.jar  -Dweblogic.RootDirectory=${WL_DOMAIN_HOME} weblogic.security.Encrypt ${PASS});
echo "set cluster shared secret file $WL_SECURITY_FILE"
cat <<EOT > "$WL_SECURITY_FILE"
username=${USER}
password=${PASS}
EOT


if [ ! -z "$AS_SECURITY" ];then
  mkdir -p ${AS_SECURITY}
  cat <<EOT > "${AS_SECURITY}/boot.properties"
username=${USER}
password=${ENC_PASS}
EOT
fi

init_server_https_keystore