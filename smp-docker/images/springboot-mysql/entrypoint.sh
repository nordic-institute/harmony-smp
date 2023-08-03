#!/bin/bash
set -e

ROOT_PASSWORD=${ROOT_PASSWORD:-password}
export JAVA_HOME=`type -p javac|xargs readlink -f|xargs dirname|xargs dirname`

BIND_DATA_DIR=${DATA_DIR}/bind
MYSQL_DATA_DIR=${DATA_DIR}/mysql
BDMSL_DIR=${DATA_DIR}/smp

if [ ! -d ${DATA_DIR} ]; then
   mkdir -p ${DATA_DIR}
fi


init_mysql() {
  # start MYSQL
  echo  "[INFO]  Initialize mysql service: $(service mysql status)."
  #service mysql start
  # reinitialize mysql to start it with enabled lowercase tables, 'root' password and change the data folder
  service mysql stop
  rm -rf /var/lib/mysql
  if [ ! -d ${MYSQL_DATA_DIR} ]; then
    mkdir -p ${MYSQL_DATA_DIR}
  fi
  ln -sf ${MYSQL_DATA_DIR} /var/lib/mysql

  chmod -R 0775 ${MYSQL_DATA_DIR}
  usermod -d ${MYSQL_DATA_DIR} mysql

  chown mysql:mysql ${MYSQL_DATA_DIR}
  chmod 0775 ${MYSQL_DATA_DIR}
  echo "ALTER USER 'root'@'localhost' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';" > /tmp/mysql-init

  mysqld --defaults-file=/etc/mysql/my.cnf --initialize --lower_case_table_names=1 --init-file=/tmp/mysql-init --user=mysql --console
  service mysql start

  PID_MYSQL=$(cat /var/run/mysqld/mysqld.sock.lock);
  if  [ ! -d ${MYSQL_DATA_DIR}/${DB_SCHEMA} ]; then
    # create database
    mysql -h localhost -u root --password=${MYSQL_ROOT_PASSWORD} -e "drop schema if exists $DB_SCHEMA;DROP USER IF EXISTS $DB_USER;  create schema $DB_SCHEMA;alter database     $DB_SCHEMA charset=utf8; create user $DB_USER identified by '$DB_USER_PASSWORD';grant all on $DB_SCHEMA.* to $DB_USER;"
    # initialize database
    mysql -h localhost -u root --password=${MYSQL_ROOT_PASSWORD} $DB_SCHEMA < /opt/smlconf/database-scripts/mysql5innodb.ddl
    # init data
    mysql -h localhost -u root --password=${MYSQL_ROOT_PASSWORD} $DB_SCHEMA < /opt/smlconf/database-scripts/mysql5innodb-data.sql
  fi
}


init_bdmsl() {

  echo "[INFO] init smp folders: ${BDMSL_HOME}/application.properties"
  {
      echo "# BDMSL application configuration"
      echo "server.port=8080"
      echo "# Database configuration"
      echo "sml.hibernate.dialect=org.hibernate.dialect.MySQLDialect"
      echo "sml.jdbc.driver=com.mysql.cj.jdbc.Driver"
      echo "sml.jdbc.url=jdbc:mysql://localhost:3306/$DB_SCHEMA?allowPublicKeyRetrieval=true"
      echo "sml.jdbc.user=$DB_USER"
      echo "sml.jdbc.password=$DB_USER_PASSWORD"
  } >>  ${BDMSL_HOME}/application.properties




  echo "[INFO] copy smp to shared folders: ${BDMSL_DIR}"
  if [ ! -d ${BDMSL_DIR} ]; then
    mv ${BDMSL_HOME} ${BDMSL_DIR}
  fi

  rm -rf ${BDMSL_HOME}
  ln -sf ${BDMSL_DIR} ${BDMSL_HOME}


  # override init artefacts as keystore, truststore, keys, ...
  if [  -d /opt/smlconf/init-configuration ]; then
     cp -r /opt/smlconf/init-configuration/*.*  /opt/smlconf/
  fi

  # add trusted hostname certificate for CRL download over HTTPS test
  if [  -f /opt/smlconf/init-configuration/sml_crl_hostname.cer ]; then
    "${JAVA_HOME}"/bin/keytool -importcert -alias test-host -keystore "/etc/ssl/certs/java/cacerts" -storepass changeit -file /opt/smlconf/init-configuration/sml_crl_hostname.cer -noprompt
  fi

}

#
# Function initialize  and star squid proxy. Prepositions for function are
# installed packages squid and apache2-utils!
#
function init_squid() {

    PROXY_FOLDER=${PROXY_FOLDER:-/data/proxy}
    PROXY_CONFIG_LOGS="${PROXY_FOLDER}/logs"
    PROXY_CONFIG_FILE="${PROXY_FOLDER}/squid.conf"
    PROXY_USERS_FILE=/etc/squid/passwd
    PROXY_AUTHENTICATION=${PROXY_AUTHENTICATION:-true}
    PROXY_USERS=${PROXY_USERS:-proxyuser1:test123,proxyuser2:test123}
    PROXY_PORT=${PROXY_PORT:-3127}
    # system dependant
    PROXY_LIBS=${PROXY_LIBS:-/usr/lib/squid}

    echo "[INFO] starting squid configuration"
    echo "---------------------------< suquid conf >---------------------------"
    echo "PROXY_CONFIG_FILE=${PROXY_CONFIG_FILE}"
    echo "PROXY_FOLDER=${PROXY_FOLDER}"
    echo "PROXY_USERS_FILE=${PROXY_USERS_FILE}"
    echo "PROXY_AUTHENTICATION=${PROXY_AUTHENTICATION}"
    echo "PROXY_USERS=${PROXY_USERS}"
    echo "------------------------------[ suquid conf ]-------------------------------"; echo


    # set configuration
    [[ ! -d "${PROXY_FOLDER}" ]] &&  mkdir -p "${PROXY_FOLDER}"
    [[ ! -d "${PROXY_CONFIG_LOGS}" ]] &&  mkdir -p "${PROXY_CONFIG_LOGS}"
    [[ ! -d "/var/run/squid/" ]] &&  mkdir -p "/var/run/squid/"


    echo "# BDMSL squid configuration" > "${PROXY_CONFIG_FILE}"

    {
        echo "cache_access_log ${PROXY_CONFIG_LOGS}/access.log"
        echo "cache_log ${PROXY_CONFIG_LOGS}/cache.log"
        echo "cache_store_log ${PROXY_CONFIG_LOGS}/store.log"
        echo ""
        echo "pid_filename /var/run/squid/squidm.pid"
        echo "cache_effective_user smp"
        echo ""
        echo "http_port ${PROXY_PORT}"
        echo ""
    } >> "${PROXY_CONFIG_FILE}"

    if [ "${PROXY_AUTHENTICATION}" == "true" ]; then
        local users=(${PROXY_USERS//,/ })
        local userNames=()

        # clear file
        echo "# BDMSL proxy users" > "${PROXY_USERS_FILE}"
        for user in "${users[@]}"; do
            local userCredentials=(${user//:/ })
            userNames+=(${userCredentials[0]})
            # create a user
            htpasswd -b  ${PROXY_USERS_FILE} ${userCredentials[0]} ${userCredentials[1]} || exit $?
        done
        echo "Created proxy users: ${userNames[*]}"

        # create squid property file
        {
            echo "auth_param basic program ${PROXY_LIBS}/basic_ncsa_auth  ${PROXY_USERS_FILE}"
            echo "auth_param basic children 5"
            echo "auth_param basic realm Squid proxy-caching web server"
            echo "auth_param basic credentialsttl 1 minute"
            echo "auth_param basic casesensitive off"
            echo ""
            echo "acl ncsa_users proxy_auth REQUIRED"
            echo "http_access allow ncsa_users"
            echo ""
        } >> ${PROXY_CONFIG_FILE}
        # example to test
        # wget -e use_proxy=yes --proxy-user=proxyuser2 --proxy-password=test123 -e http_proxy=http://127.0.0.1:3127 -e https_proxy=http://127.0.0.1:3127 https://www.google.com/ --no-check-certificate
    else
      {
         echo "http_access allow all"
         echo ""
      } >> ${PROXY_CONFIG_FILE}
    fi
    chown -R smp:smp ${PROXY_FOLDER}
    echo "Start squid proxy server"
   $(nohup $(which squid) -f ${PROXY_CONFIG_FILE} -NYCd 1 &> ${PROXY_CONFIG_LOGS}/squid.out &)
}

init_squid
init_bind
init_mysql
init_bdmsl


#----------------------------------------------------
# stard bind 9
# allow arguments to be passed to named
echo  "[INFO]  Start bind"
if [[ ${1:0:1} = '-' ]]; then
  EXTRA_ARGS="$@"
  set --
elif [[ ${1} == named || ${1} == $(which named) ]]; then
  EXTRA_ARGS="${@:2}"
  set --
fi

echo "Starting named..."
service named stop
$(which named) -4 -u ${BIND_USER} ${EXTRA_ARGS} -d 0 -L ${BIND_DATA_DIR}/logs/stdout.txt  &
  
#----------------------------------------------------
# start tomcat
echo  "[INFO]  Start smp"
cd ${BDMSL_DIR}
ls -ltr
su -s /bin/sh smp -c "${JAVA_HOME}/bin/java -jar smp-springboot-exec.jar"

