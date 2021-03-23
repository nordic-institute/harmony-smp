#!/bin/bash

WORKING_DIR="$(dirname $0)"
SMP_INIT_DATABASE="../../../smp-webapp/src/main/smp-setup/database-scripts/oracle10g.ddl"
SMP_INIT_DATABASE_DATA="../../../smp-webapp/src/main/smp-setup/database-scripts/oracle10g-data.sql"
# soap ui data
PREFIX="smp-wls-orcl"
SMP_VERSION=


ORA_VERSION="19.3.0"
ORA_EDITION="se2"
ORA_SERVICE="ORCLPDB1"
ORACLE_PDB="ORCLPDB1"

SMP_DB_USERNAME=smp;
SMP_DB_PASSWORD=test;

# clear volume and containers - to run  restart from strach 


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
  docker volume rm "${PREFIX}_shared-status-folder"
}




createDatabaseSchemaForUser $SMP_DB_USERNAME $SMP_DB_PASSWORD ./properties/db-scripts/01_create_user.sql

# create  database init script from 
echo "CONNECT smp/test@//localhost:1521/${ORA_SERVICE};" > ./properties/db-scripts/02_oracle10g.sql
cat  "${SMP_INIT_DATABASE}" >> ./properties/db-scripts/02_oracle10g.sql



# copy init database data for  SMP    
if [ ! -f "${SMP_INIT_DATABASE_DATA}" ]
  then
  echo "SMP sql init data '${SMP_INIT_DATABASE_DATA} not found!!"
  exit 1;
else
  # copy artefact to docker build folder
  echo "CONNECT smp/test@//localhost:1521/${ORA_SERVICE};" > ./properties/db-scripts/03_oracle10g-data.sql
  cat  "${SMP_INIT_DATABASE_DATA}" >> ./properties/db-scripts/03_oracle10g-data.sql
fi


# Because statuses are synchronized through folder: ./status-folder it could contain a state from a previous start.
# Set content of the file database.status to "Database starting"!
echo "Database starting" > ./status-folder/database.status
clearOldContainers
# start 
export SMP_VERSION="${SMP_VERSION}"
docker-compose -p ${PREFIX} up -d --force-recreate


# wait until service is up
for i in `seq 200`; do timeout 10  bash -c ' curl --head --silent --fail http://localhost:7901/smp/'; if [ $? -eq 0  ] ; then break;fi; echo "$i. Wait for weblogic to start!";  sleep 10;  done;

