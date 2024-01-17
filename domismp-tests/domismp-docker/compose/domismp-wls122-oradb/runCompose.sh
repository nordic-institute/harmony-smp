#!/usr/bin/env bash

# This is build script clean starting the docker compose containers for weblogic and oracle db integration tests.
# The script is used for local development and CI integration testing only.
#
# IMPORTANT NOTE: The script clears all old containers, volumes and bind volumes and then starts the docker compose containers.


# init plan variables
WORKDIR="$(cd -P $(dirname "${BASH_SOURCE[0]}" ) && pwd)"
source "${WORKDIR}/../../functions/run-test.functions"
initializeVariables

SMP_INIT_DATABASE="${SMP_PROJECT_FOLDER}/smp-webapp/src/main/smp-setup/database-scripts/oracle10g.ddl"
#SMP_INIT_DATABASE_DATA="${SMP_PROJECT_FOLDER}/smp-webapp/src/main/smp-setup/database-scripts/oracle10g-data.sql"
SMP_INIT_DATABASE_DATA="${SMP_PROJECT_FOLDER}/smp-soapui-tests/groovy/oracle-4.1_integration_test_data.sql"
# soap ui data
SMP_VERSION=


#ORA_VERSION="19.3.0"
#ORA_EDITION="se2"
#ORA_SERVICE="ORCLPDB1"
#ORACLE_PDB="ORCLPDB1"
ORA_VERSION="11.2.0.2"
ORA_EDITION="xe"
ORA_SERVICE="xe"

SMP_DB_USERNAME="smp"
SMP_DB_PASSWORD="test"
SMP_DB_SCRIPTS=./properties/db-scripts

# READ arguments
while getopts i:v: option
do
  case "${option}"
  in
    i) SMP_INIT_DATABASE_DATA=${OPTARG};;
    v) SMP_VERSION=${OPTARG};;
    *) echo "Unknown option [${option}].\nUsage: $0 [-i] [-v]"; exit 1;;
  esac
done

discoverApplicationVersion

echo "*************************************************************************"
echo "SMP version: $SMP_VERSION"
echo "Init sql data: ${SMP_INIT_DATABASE_DATA}"
echo "Working Directory: ${WORKDIR}"
echo "*************************************************************************"
cd "$WORKDIR" || exit 1

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

# clear old containers mounted volume ./data
function clearMoundDataVolume() {
  : "${$WORKDIR?"Need to set $WORKDIR non-empty!"}"
  echo "Clear container data ${WORKDIR}/data/"
  rm -rf ${WORKDIR}/data
  mkdir -p ${WORKDIR}/data/upload
  mkdir -p ${WORKDIR}/data/smp/config
  mkdir -p ${WORKDIR}/data/smp/security
  mkdir -p ${WORKDIR}/data/weblogic/keystores
}

clearMoundDataVolume
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
export SMP_VERSION

echo "Clear old containers"
stopAndClearTestContainers
# start "
echo "Start containers"
startTestContainers


# wait until service is up
for i in `seq 200`; do timeout 10  bash -c ' curl --silent --fail http://localhost:7980/smp/'; if [ $? -eq 0  ] ; then break;fi; echo "$i. Wait for weblogic to start!";  sleep 10;  done;

