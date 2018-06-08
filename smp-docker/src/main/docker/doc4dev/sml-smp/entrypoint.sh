#!/bin/bash
set -e

MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-root}
export JAVA_HOME=`type -p javac|xargs readlink -f|xargs dirname|xargs dirname`

BIND_DATA_DIR=${DATA_DIR}/bind
MYSQL_DATA_DIR=${DATA_DIR}/mysql
TOMCAT_DIR=${DATA_DIR}/tomcat

if [ ! -d ${DATA_DIR} ]; then
   mkdir -p ${DATA_DIR}
fi

if [ ! -d ${BIND_DATA_DIR}/etc ]; then
   mkdir -p ${BIND_DATA_DIR}/etc
fi

if [ ! -d ${BIND_DATA_DIR}/var ]; then
   mkdir -p ${BIND_DATA_DIR}/var
fi

init_bind() {

  # move configuration if it does not exist
  if [ ! -d ${BIND_DATA_DIR}/etc/named ]; then
    mv /etc/named.conf.local ${BIND_DATA_DIR}/etc/named.conf.local
    mv /etc/named.conf ${BIND_DATA_DIR}/etc/named.conf
  fi
  rm -rf /etc/named.conf.local 
  rm -rf /etc/named.conf
  ln -sf ${BIND_DATA_DIR}/etc/named.conf.local  /etc/named.conf.local 
  ln -sf ${BIND_DATA_DIR}/etc/named.conf /etc/named.conf
  # move data dir if it does not exist
  if [ ! -d ${BIND_DATA_DIR}/var/named ]; then
    mv /var/named ${BIND_DATA_DIR}/var/named
  fi
  rm -rf /var/named
  ln -sf ${BIND_DATA_DIR}/var/named /var/named

  chmod -R 0775 ${BIND_DATA_DIR}
  chown -R named:named  ${BIND_DATA_DIR}
 

}

init_mysql() {
  if [ ! -d ${MYSQL_DATA_DIR} ]; then
    mv /var/lib/mysql ${MYSQL_DATA_DIR}
  fi
  
  rm -rf /var/lib/mysql
  ln -sf ${MYSQL_DATA_DIR} /var/lib/mysql

  chmod -R 0775 ${MYSQL_DATA_DIR}
  
  usermod -d ${MYSQL_DATA_DIR} mysql

  # Start the MySQL daemon in the background.
  mysqld --user=mysql &
  # wait until db is up.
  until mysqladmin ping >/dev/null 2>&1; do
    echo -n "."; sleep 0.2
  done
  echo "SET ROOT PASSWORD"
  # set root password
  mysql -h localhost -u root  -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '$MYSQL_ROOT_PASSWORD';"
  #mysqladmin -u root password $MYSQL_ROOT_PASSWORD


  #--------------------------------------------------
  # INIT SML
  #--------------------------------------------------
  # check if DB_SML_SCHEMA exists
  if  [ ! -d ${MYSQL_DATA_DIR}/${DB_SML_SCHEMA} ]; then
    # create database
     echo "create SML schema"
    mysql -h localhost --user=root  --password=$MYSQL_ROOT_PASSWORD -e "create schema $DB_SML_SCHEMA;alter database $DB_SML_SCHEMA charset=utf8; create user $DB_SML_USER identified by  '$DB_SML_PASSWORD';grant all on $DB_SML_SCHEMA.* to $DB_SML_USER;"
  # change db init file
  fi

  # change db init file alway else at new run (not start container) liquibase will return error
  if  [ -f ${DATA_DIR}/init/db.init.xml ]; then
    mkdir -p $TOMCAT_HOME/webapps/WEB-INF/classes/liquibase/
    cp ${DATA_DIR}/init/db.init.xml $TOMCAT_HOME/webapps/WEB-INF/classes/liquibase/db.init-data-inserts.xml
    jar -uf  $TOMCAT_HOME/webapps/edelivery-sml.war -C $TOMCAT_HOME/webapps/ WEB-INF/classes/liquibase/db.init-data-inserts.xml 
    rm -rf $TOMCAT_HOME/webapps/WEB-INF 
  fi
  #--------------------------------------------------
  # INIT SMP
  #--------------------------------------------------
  if  [ ! -d ${MYSQL_DATA_DIR}/${DB_SMP_SCHEMA} ]; then
    # create database
    echo "create SMP schema"
    mysql -h localhost -u root  --password=$MYSQL_ROOT_PASSWORD -e "create schema $DB_SMP_SCHEMA;alter database $DB_SMP_SCHEMA charset=utf8; create user $DB_SMP_USER identified by '$DB_SMP_PASSWORD';grant all on $DB_SMP_SCHEMA.* to $DB_SMP_USER;"
   
    # update domain client cert for default domain  - SMP-SML connection. SML does not require authetication but SMP does. At this moment value bdmslClientCertHeader is not important as log it is not null
    echo "" >> /opt/smp-$SMP_VERSION/database-scripts/create-Mysql.sql
    echo  "update smp_domain set bdmslClientCertHeader='sno=123456&subject=CN=SMP_CEF_SUPPORT, OU=B4, O=DIGIT, L=Brussels, ST=BE, C=BE&validfrom=Jun 1 10:00:00 2015 CEST&validto=Jun 1 10:00:00 2035 CEST&issuer=EMAILADDRESS=root@test.be,CN=TEST Root CN, OU=B4,O=DIGIT,L=Brussels,ST=BE,C=BE'" >> /opt/smp-$SMP_VERSION/database-scripts/create-Mysql.sql
    mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD $DB_SMP_SCHEMA < "/opt/smp-$SMP_VERSION/database-scripts/create-Mysql.sql"
  
  fi
}


init_tomcat() {

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

  # move domibus conf folder to data folder
  if [ ! -d ${TOMCAT_DIR}/conf ]; then
    mv ${TOMCAT_HOME}/conf ${TOMCAT_DIR}/
  fi
  rm -rf ${TOMCAT_HOME}/conf 
    ln -sf ${TOMCAT_DIR}/conf ${TOMCAT_HOME}/conf
  chown -R tomcat:tomcat ${TOMCAT_DIR}
  chmod u+x $TOMCAT_HOME/bin/*.sh
  # start tomcat
  cd ${TOMCAT_HOME}/bin/
  su -c ./startup.sh -s /bin/sh tomcat

}



init_bind
init_mysql
init_tomcat

echo "Starting named..."
exec $(which named) -u ${USER_BIND} -g --




