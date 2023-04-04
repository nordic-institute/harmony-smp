#!/usr/bin/env bash

#Define WL_DOMAIN_HOME
INIT_SCRIPTS=$1
echo "Domain Home is: $WL_DOMAIN_HOME"
echo "Scripts folder is: $INIT_SCRIPTS"

# set datasource property
DATA_SOURCE_PROPERTY_FILE="${INIT_SCRIPTS}/../properties/datasource.properties"
if [  -f "${WL_INIT_PROPERTIES}/datasource.properties" ]; then
  DATA_SOURCE_PROPERTY_FILE="${WL_INIT_PROPERTIES}/datasource.properties"
fi

#deploy smp datasource
wlst.sh -loadProperties "${DATA_SOURCE_PROPERTY_FILE}" "${INIT_SCRIPTS}/ds-deploy.py"

# copy smp startup configuration  - check first init folder else use default
if [  -f "${WL_INIT_PROPERTIES}/smp.config.properties" ]; then
  cat "${WL_INIT_PROPERTIES}/smp.config.properties" > "${SMP_CONFIG_DIR}/config/smp.config.properties"
elif [ -f "${INIT_SCRIPTS}/../properties/smp.config.properties" ]; then
  cat "${INIT_SCRIPTS}/../properties/smp.config.properties" > "${SMP_CONFIG_DIR}/config/smp.config.properties"
else
  cat <<EOT >"${SMP_CONFIG_DIR}/config/smp.config.properties"
smp.jdbc.hibernate.dialect=org.hibernate.dialect.Oracle10gDialect
smp.datasource.jndi=jdbc/eDeliverySmpDs
smp.automation.authentication.external.tls.clientCert.enabled=true
log.folder=./logs/
smp.security.folder=${SMP_SECURITY_DIR}/
EOT
fi

cp /u01/oracle/init/smp.war "${WL_DOMAIN_HOME}/"
ls -ltr "${WL_DOMAIN_HOME}/"

# Deploy Application
wlst.sh -skipWLSModuleScanning "${ORACLE_HOME}"/smp-app-deploy.py
