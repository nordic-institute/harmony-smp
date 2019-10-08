#!/bin/bash

#Define DOMAIN_HOME
echo "Domain Home is: " $DOMAIN_HOME


if [ ! -d "$DOMAIN_HOME/classes" ]; then
  mkdir -p "$DOMAIN_HOME/classes";
fi

# create smp property file
echo "hibernate.dialect=org.hibernate.dialect.Oracle10gDialect" > "$DOMAIN_HOME/classes/smp.config.properties"
echo "\ndatasource.jndi=jdbc/cipaeDeliveryDs" >> "$DOMAIN_HOME/classes/smp.config.properties"
echo "\nauthentication.blueCoat.enabled=true" >> "$DOMAIN_HOME/classes/smp.config.properties"
echo "\nsmp.truststore.password={DEC}{test123}" >> "$DOMAIN_HOME/classes/smp.config.properties"
echo "\nsmp.keystore.password={DEC}{test123}" >> "$DOMAIN_HOME/classes/smp.config.properties"
echo "\nlog.folder=./logs/" >> "$DOMAIN_HOME/classes/smp.config.properties"

# create weblogic classpath to classes folder
echo "export CLASSPATH=\${CLASSPATH}:\${DOMAIN_HOME}/classes" >> "$DOMAIN_HOME/bin/setDomainEnv.sh"


cp /u01/oracle/smp.war "$DOMAIN_HOME/"


# Deploy Application
wlst.sh -skipWLSModuleScanning /u01/oracle/smp-app-deploy.py
