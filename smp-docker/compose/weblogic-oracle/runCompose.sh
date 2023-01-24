#!/bin/bash

#WORKING_DIR="$(dirname $0)"
WORKING_DIR="$(cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd)"

SMP_INIT_DATABASE="../../../smp-webapp/src/main/smp-setup/database-scripts/oracle10g.ddl"
#SMP_INIT_DATABASE_DATA="../../../smp-webapp/src/main/smp-setup/database-scripts/oracle10g-data.sql"
SMP_INIT_DATABASE_DATA="../../../smp-soapui-tests/groovy/oracle-4.1_integration_test_data.sql"
# soap ui data
PREFIX="smp-wls-orcl"
SMP_VERSION=


#ORA_VERSION="19.3.0"
#ORA_EDITION="se2"
#ORA_SERVICE="ORCLPDB1"
#ORACLE_PDB="ORCLPDB1"
ORA_VERSION="11.2.0.2"
ORA_EDITION="xe"
ORA_SERVICE="xe"

SMP_DB_USERNAME=smp;
SMP_DB_PASSWORD=test;
SMP_DB_SCRIPTS=./properties/db-scripts

# READ arguments
while getopts i:v: option
do
  case "${option}"
  in
    i) SMP_INIT_DATABASE_DATA=${OPTARG};;
    v) SMP_VERSION=${OPTARG};;
  esac
done


if [  -z "${SMP_VERSION}" ]
then
  # get version from POM file
  SMP_VERSION="$(mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout)"
fi

echo "*************************************************************************"
echo "SMP version: $SMP_VERSION"
echo "Init sql data: ${SMP_INIT_DATABASE_DATA}"
echo "Working Directory: ${WORKING_DIR}"
echo "*************************************************************************"
cd "$WORKING_DIR"

echo "Create folder (if not exist) for database scripts ${SMP_DB_SCRIPTS}"
[ -d  ${SMP_DB_SCRIPTS}  ] || mkdir -p "${SMP_DB_SCRIPTS}"


function createDatabaseSchemaForUser() {

  echo "Clear file [$3] to recreate schema for user creation!"
  echo ""  > "$3"
  echo "Create database schema/user: $1"
  if [ -n "$ORACLE_PDB" ]; then
    echo "ALTER SESSION SET CONTAINER=$ORACLE_PDB;" >>"$3"
  fi
  {
    # magic with double quotes  - first end " then put '"' and then add variable to "$Var" and repeat the stuff :)
    echo "CREATE USER $1 IDENTIFIED BY "'"'"$2"'"'" DEFAULT TABLESPACE users QUOTA UNLIMITED ON users; "
    echo "GRANT CREATE SESSION TO $1;"
    echo "GRANT CREATE TABLE TO $1;"
    echo "GRANT CREATE VIEW TO $1;"
    echo "GRANT CREATE SEQUENCE TO $1;"
    echo "GRANT SELECT ON PENDING_TRANS$ TO $1;"
    echo ""
  } >>"$3"
}


function clearOldContainers {
  echo "Clear containers and volumes"
  docker-compose -p "${PREFIX}" rm -s -f -v
  echo "Clear container data ${WORKING_DIR}/data/"
  rm -rf ${WORKING_DIR}/data/smp/config/*.*
  rm -rf ${WORKING_DIR}/data/smp/security/*.*
  rm -rf ${WORKING_DIR}/data/weblogic/keystores/*.*
  rm -rf ${WORKING_DIR}/data/weblogic/security.properties
  rm -rf ${WORKING_DIR}/data/*.jar
}

createDatabaseSchemaForUser $SMP_DB_USERNAME $SMP_DB_PASSWORD "${SMP_DB_SCRIPTS}/01_create_user.sql"

# create  database init script from
echo "CONNECT ${SMP_DB_USERNAME}/${SMP_DB_PASSWORD}@//localhost:1521/${ORA_SERVICE};" > "${SMP_DB_SCRIPTS}/02_oracle10g.sql"
cat  "${SMP_INIT_DATABASE}" >> "${SMP_DB_SCRIPTS}/02_oracle10g.sql"



# copy init database data for  SMP    
if [ ! -f "${SMP_INIT_DATABASE_DATA}" ]
  then
  echo "SMP sql init data '${SMP_INIT_DATABASE_DATA} not found!!"
  exit 1;
else
  # copy artefact to docker build folder
  echo "CONNECT ${SMP_DB_USERNAME}/${SMP_DB_PASSWORD}@//localhost:1521/${ORA_SERVICE};" > "${SMP_DB_SCRIPTS}/03_oracle10g-data.sql"
  cat  "${SMP_INIT_DATABASE_DATA}" >>  "${SMP_DB_SCRIPTS}/03_oracle10g-data.sql"
fi


# Because statuses are synchronized through folder: ./status-folder it could contain a state from a previous start.
# Set content of the file database.status to "Database starting"!
echo "Database starting" > ./status-folder/database.status
clearOldContainers
# start
export SMP_VERSION
export ORA_VERSION
export ORA_EDITION

docker-compose -p ${PREFIX} up -d --force-recreate


# wait until service is up
for i in `seq 200`; do timeout 10  bash -c ' curl --silent --fail http://localhost:7980/smp/'; if [ $? -eq 0  ] ; then break;fi; echo "$i. Wait for weblogic to start!";  sleep 10;  done;

