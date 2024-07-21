#!/bin/bash

#set -e

# set java home
if [ "${JDK_VERSION}" == "8" ]; then
  export JAVA_HOME=/opt/java/${JAVA_8_VERSION}
fi
# parameters
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-"root"}
SMP_DB_USER=${SMP_DB_USER:-"smp"}
SMP_DB_USER_PASSWORD=${SMP_DB_USER_PASSWORD:-"secret123"}
SMP_DB_SCHEMA=${SMP_DB_SCHEMA:-"smp"}

DATA_DIR=/data
MYSQL_DATA_DIR=${DATA_DIR}/mysql

if [ ! -d ${DATA_DIR} ]; then
  mkdir -p ${DATA_DIR}
fi

init_mysql() {
  echo "[INFO] init database:"
  if [ ! -d "/run/mysqld" ]; then
    mkdir -p /run/mysqld
    chown -R mysql:mysql /run/mysqld
  fi

  if [ ! -d ${MYSQL_DATA_DIR} ]; then
    # sleep a little to avoid mv issues
    sleep 3s
    mv /var/lib/mysql ${DATA_DIR}
  fi

  rm -rf /var/lib/mysql
  ln -sf ${MYSQL_DATA_DIR} /var/lib/mysql
  chmod -R 0777 ${MYSQL_DATA_DIR}
  chown -R mysql:mysql ${MYSQL_DATA_DIR}
  echo '[INFO] start MySQL'
  sleep 5s
  service mysql start
  echo "[INFO] ----------------------------------------"
  echo "[INFO] create SMP database: ${SMP_DB_SCHEMA}"
  if [ -d ${MYSQL_DATA_DIR}/${SMP_DB_SCHEMA} ]; then
    echo "[INFO] MySQL ${SMP_DB_SCHEMA} already present, skipping creation"
  else
    echo "[INFO] MySQL ${SMP_DB_SCHEMA}  not found, creating initial DBs"

    echo 'Create smp database'
    mysql -h localhost -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '$MYSQL_ROOT_PASSWORD';drop schema if exists $SMP_DB_SCHEMA;DROP USER IF EXISTS $SMP_DB_USER;  create schema $SMP_DB_SCHEMA;alter database $SMP_DB_SCHEMA charset=utf8; create user $SMP_DB_USER identified by '$SMP_DB_USER_PASSWORD';grant all on $SMP_DB_SCHEMA.* to $SMP_DB_USER;"

    if [ -f "/tmp/custom-data/mysql5innodb.sql" ]; then
      echo "Use custom database script! "
      mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD $SMP_DB_SCHEMA <"tmp/custom-data/mysql5innodb.ddl"
    else
      echo "Use default database ddl script!"
      mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD $SMP_DB_SCHEMA <"/tmp/smp-setup/database-scripts/mysql5innodb.ddl"
    fi

    if [ -f "/tmp/custom-data/mysql5innodb-data.sql" ]; then
      echo "Use custom init script! "
      mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD $SMP_DB_SCHEMA <"/tmp/custom-data/mysql5innodb-data.sql"
    else
      echo "Use default init script!"
      mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD $SMP_DB_SCHEMA < "/tmp/smp-setup/database-scripts/mysql5innodb-data.sql"
    fi
  fi
  sleep 5s
}

addOrReplaceProperties() {

  PROP_FILE=$1
  INIT_PROPERTIES=$2
  INIT_PROPERTY_DELIMITER=$3

  # replace domibus properties
  if [ -n "$INIT_PROPERTIES" ]; then
    echo "Parse init properties: $INIT_PROPERTIES"
    # add delimiter also to end :)
    s="$INIT_PROPERTIES$INIT_PROPERTY_DELIMITER"

    array=()
    while [[ $s ]]; do
      array+=("${s%%"$INIT_PROPERTY_DELIMITER"*}")
      s=${s#*"$INIT_PROPERTY_DELIMITER"}
    done

    # replace parameters
    IFS='='
    for property in "${array[@]}"; do
      read -r key value <<<"$property"
      # escape regex chars and remove trailing and leading spaces..
      keyRE="$(printf '%s' "${key// /}" | sed 's/[[\*^$()+?{|]/\\&/g')"
      propertyRE="$(printf '%s' "${property// /}" | sed 's/[[\*^$()+?{|/]/\\&/g')"

      echo "replace or add property: [$keyRE] with value [$propertyRE]"
      # replace key line and commented #key line with new property
      sed -i "s/^$keyRE=.*/$propertyRE/;s/^#$keyRE=.*/$propertyRE/" $PROP_FILE
      # test if replaced if the line not exists add in on the end
      grep -qF -- "$propertyRE" "$PROP_FILE" || echo "$propertyRE" >>"$PROP_FILE"
    done

  fi
}

init_smp() {
  # set smp data/security folder
  mkdir -p "${SMP_HOME}/smp/"
  mkdir -p  "${SMP_HOME}/smp-libs"
  # copy smp keystore with sml authorized sml certificates
  cp /tmp/artefacts/shared-artefacts/smp-logback.xml "${SMP_HOME}/logback.xml"
  cp "/tmp/artefacts/shared-artefacts/smp-keystore-docker-demo.p12" "${SMP_HOME}/smp/smp-keystore-docker-demo.p12"
  cp "/tmp/artefacts/shared-artefacts/smp-truststore-docker-demo.p12" "${SMP_HOME}/smp/smp-truststore-docker-demo.p12"
  chown -R smp:smp "${SMP_HOME}"
}

init_smp_properties() {
  echo "[INFO] init application.properties:"
  {
    echo "# mysql database configuration"
    echo "smp.jdbc.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect"
    echo "smp.jdbc.driver=com.mysql.cj.jdbc.Driver"
    echo "smp.jdbc.url=jdbc:mysql://localhost:3306/${SMP_DB_SCHEMA}?allowPublicKeyRetrieval=true"
    echo "smp.jdbc.user=${SMP_DB_USER}"
    echo "smp.jdbc.password=${SMP_DB_USER_PASSWORD}"
    echo "# SMP init parameters"
    echo "smp.security.folder=${SMP_HOME}/smp/"
    echo "smp.libraries.folder=${SMP_HOME}/smp-libs"
    echo "smp.locale.folder=${SMP_HOME}/locales"
    echo "smp.automation.authentication.external.tls.clientCert.enabled=true"
    echo "bdmsl.integration.enabled=false"
    echo "bdmsl.participant.multidomain.enabled=false"
    echo "smp.keystore.filename=smp-keystore-docker-demo.p12"
    echo "smp.keystore.type=PKCS12"
    echo "smp.truststore.filename=smp-truststore-docker-demo.p12"
    echo "smp.truststore.type=PKCS12"
    echo "smp.keystore.password={DEC}{test123}"
    echo "smp.truststore.password={DEC}{test123}"
  } >>"$SMP_HOME/application.properties"

  addOrReplaceProperties "$SMP_HOME/application.properties" "$SMP_INIT_PROPERTIES" "$SMP_INIT_PROPERTY_DELIMITER"
}

init_mysql
init_smp_properties
init_smp

echo '[INFO] start running SMP with JAVA version:'
"${JAVA_HOME}/bin/java" -version
cd $SMP_HOME/
ls -ltr
su -s /bin/sh smp -c "${JAVA_HOME}/bin/java -jar smp-springboot-exec.jar"
