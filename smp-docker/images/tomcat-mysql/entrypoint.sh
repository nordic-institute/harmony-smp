#!/bin/sh

#set -e

# parameters
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-"root"}
DB_USER=${DB_USER:-"smp"}
DB_USER_PASSWORD=${DB_USER_PASSWORD:-"secret123"}
DB_SCHEMA=${DB_SCHEMA:-"smp"}

DATA_DIR=/smp/data
MYSQL_DATA_DIR=${DATA_DIR}/mysql
TOMCAT_DIR=${DATA_DIR}/tomcat
TOMCAT_HOME=${SMP_HOME}/apache-tomcat-$TOMCAT_VERSION/
SQUID_CONFIG=/etc/squid/squid.smp.conf
SQUID_USERS=/etc/squid/passwd
SQUID_USER="proxyuser"
SQUID_PASSWORD="test123"

if [ ! -d ${DATA_DIR} ]; then
   mkdir -p ${DATA_DIR}
fi

init_tomcat() {
  # add java code coverage angent to image
  JAVA_OPTS="-javaagent:/opt/jacoco/jacoco-agent.jar=output=tcpserver,address=*,port=6901 $JAVA_OPTS"
  # add allow encoded slashes and disable scheme for proxy
  JAVA_OPTS="$JAVA_OPTS -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true -Djdk.http.auth.tunneling.disabledSchemes="
  export  JAVA_OPTS


  echo "[INFO] init tomcat folders: $tfile"
  if [ ! -d ${TOMCAT_DIR} ]; then
    mkdir -p ${TOMCAT_DIR}
  fi

  # move tomcat log folder to data folder
  if [ ! -d ${TOMCAT_DIR}/logs ]; then
    if [ ! -d  ${TOMCAT_HOME}/logs  ]; then
      mkdir -p ${TOMCAT_DIR}/logs
    else 
      mv ${TOMCAT_HOME}/logs ${TOMCAT_DIR}/
      rm -rf ${TOMCAT_HOME}/logs 
    fi
  fi
  rm -rf ${TOMCAT_HOME}/logs 
  ln -sf ${TOMCAT_DIR}/logs ${TOMCAT_HOME}/logs

  # move tomcat conf folder to data folder
  if [ ! -d ${TOMCAT_DIR}/conf ]; then
    mv ${TOMCAT_HOME}/conf ${TOMCAT_DIR}/ 
  fi
  rm -rf ${TOMCAT_HOME}/conf 
  ln -sf ${TOMCAT_DIR}/conf ${TOMCAT_HOME}/conf

  # move smp conf folder to data folder
  if [ ! -d ${TOMCAT_DIR}/smp ]; then
    mv ${TOMCAT_HOME}/smp ${TOMCAT_DIR}/
  fi
  rm -rf ${TOMCAT_HOME}/smp
  ln -sf ${TOMCAT_DIR}/smp ${TOMCAT_HOME}/

   # sleep a little to avoid mv issues
   sleep 5s
}


init_mysql() {
  echo "[INFO] init database: $tfile"
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


  if [ -d ${MYSQL_DATA_DIR}/${DB_SCHEMA} ]; then
    echo '[INFO] MySQL ${DB_SCHEMA} already present, skipping creation'
  else 
    echo "[INFO] MySQL ${DB_SCHEMA}  not found, creating initial DBs"

    echo 'Create smp database'
    mysql -h localhost -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '$MYSQL_ROOT_PASSWORD';drop schema if exists $DB_SCHEMA;DROP USER IF EXISTS $DB_USER;  create schema $DB_SCHEMA;alter database $DB_SCHEMA charset=utf8; create user $DB_USER identified by '$DB_USER_PASSWORD';grant all on $DB_SCHEMA.* to $DB_USER;"

    if [ -f "/tmp/custom-database-scripts/mysql5innodb-data.sql" ]
    then
        echo "Use custom database script! "
        mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD $DB_SCHEMA < "tmp/custom-database-scripts/mysql5innodb.ddl"
    else
          echo "Use default database ddl script!"
           mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD $DB_SCHEMA < "/tmp/artefacts/database-scripts/mysql5innodb.ddl"
    fi

    if [ -f "/tmp/custom-database-scripts/mysql5innodb-data.sql" ]
    then
         echo "Use custom init script! "
         mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD $DB_SCHEMA < "/tmp/custom-database-scripts/mysql5innodb-data.sql"
     else
        echo "Use default init script!"
         mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD $DB_SCHEMA < "/tmp/artefacts/database-scripts/mysql5innodb-data.sql"
    fi
    
  fi
sleep 5s
  # start mysql
}

init_squid() {
echo '[INFO] start squid'
#  create squid property file
  echo "auth_param basic program /usr/lib/squid3/basic_ncsa_auth $SQUID_USERS" > $SQUID_CONFIG
  echo "auth_param basic children 1" >> $SQUID_CONFIG
  echo "auth_param basic credentialsttl 1 minute" >> $SQUID_CONFIG
  echo "auth_param basic casesensitive off" >> $SQUID_CONFIG
  echo "" >> $SQUID_CONFIG
  echo "acl auth proxy_auth REQUIRED" >> $SQUID_CONFIG
  echo "http_access allow auth" >> $SQUID_CONFIG
  echo "http_access deny all" >> $SQUID_CONFIG
  echo "" >> $SQUID_CONFIG
# just to make sure this configuration is loaded :)
  echo "http_port 3127" >> $SQUID_CONFIG

  # create a user
  htpasswd -b -c $SQUID_USERS $SQUID_USER $SQUID_PASSWORD

  $(which squid) -N -f /etc/squid/squid.conf -z
  { nohup $(which squid) -f /etc/squid/squid.smp.conf -NYCd 1 &> /var/log/squid/squid.out & }

}

init_mysql
init_tomcat
init_squid

echo '[INFO] start running SMP'
chmod u+x $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/bin/*.sh
cd $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/
# run from this folder in order to be smp log in logs folder
exec ./bin/catalina.sh run




