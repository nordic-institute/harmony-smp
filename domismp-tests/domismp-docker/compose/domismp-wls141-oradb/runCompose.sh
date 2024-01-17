#!/bin/bash

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
SMP_INIT_DATABASE_DATA="${SMP_PROJECT_FOLDER}/domismp-tests/domismp-tests-api/groovy/oracle-4.1_integration_test_data.sql"


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
  esac
done


echo "*************************************************************************"
echo "SMP version: $SMP_VERSION"
echo "Init sql data: ${SMP_INIT_DATABASE_DATA}"
echo "Working Directory: ${WORKDIR}"
echo "*************************************************************************"
cd "$WORKDIR" || exit 1

# clear old containers mounted volume ./data
function clearMoundDataVolume() {
  : "${WORKDIR?"Need to set $WORKDIR non-empty!"}"
  : "${SMP_DB_SCRIPTS?"Need to set SMP_DB_SCRIPTS non-empty!"}"
  echo "Clear container data ${WORKDIR}/data/"
  rm -rf "${WORKDIR}/data"
  rm -rf "${SMP_DB_SCRIPTS}"
  mkdir -p ${WORKDIR}/data/upload
  mkdir -p ${WORKDIR}/data/smp/config
  mkdir -p ${WORKDIR}/data/smp/security
  mkdir -p ${WORKDIR}/data/weblogic/keystores
  # create database init scripts
  mkdir -p "${SMP_DB_SCRIPTS}"
}

# method creates init scripts for application oracle database initialization from scratch!
# - 01_create_user.sql - recreate user and schema
# - 02_oracle10g.sql - init database script
# - 03_oracle10g-data.sql - init database data script
function createDatabaseSchemaForUser() {
  : "${1?"Need to set Database user as first parameter non-empty!"}"
  : "${2?"Need to set Database user password as second parameter non-empty!"}"
  : "${3?"Need to set Database script folder as third parameter non-empty!"}"
  : "${SMP_INIT_DATABASE?"Need to set init Database script SMP_INIT_DATABASE as variable non-empty!"}"
  : "${SMP_INIT_DATABASE_DATA?"Need to set init-data Database script SMP_INIT_DATABASE as variable non-empty!"}"

  echo "Create file [$3]/01_create_user.sql to recreate schema/user [$1]!"
  {
    if [ -n "$ORACLE_PDB" ]; then
        echo "ALTER SESSION SET CONTAINER=$ORACLE_PDB;"
    fi
    # magic with double quotes  - first end " then put '"' and then add variable to "$Var" and repeat the stuff :)
    echo "CREATE USER $1 IDENTIFIED BY "'"'"$2"'"'" DEFAULT TABLESPACE users QUOTA UNLIMITED ON users; "
    echo "GRANT CREATE SESSION TO $1;"
    echo "GRANT CREATE TABLE TO $1;"
    echo "GRANT CREATE VIEW TO $1;"
    echo "GRANT CREATE SEQUENCE TO $1;"
    echo "GRANT SELECT ON PENDING_TRANS$ TO $1;"
    echo ""
  } > "$3/01_create_user.sql"


  # create  database init script from
  echo "CONNECT ${1}/${2}@//localhost:1521/${ORA_SERVICE};" > "${3}/02_oracle10g.sql"
  cat  "${SMP_INIT_DATABASE}" >> "${3}/02_oracle10g.sql"

  # copy init database data for  SMP
  if [ ! -f "${SMP_INIT_DATABASE_DATA}" ]
    then
    echo "SMP sql init data '${SMP_INIT_DATABASE_DATA} not found!!"
    exit 1;
  else
    # copy artefact to docker build folder
    echo "CONNECT ${1}/${2}@//localhost:1521/${ORA_SERVICE};" > "${3}/03_oracle10g-data.sql"
    cat  "${SMP_INIT_DATABASE_DATA}" >>  "${3}/03_oracle10g-data.sql"
  fi

}

# start
export SMP_VERSION
export ORA_VERSION
export ORA_EDITION
export SMP_VERSION

echo "Clear old containers"
stopAndClearTestContainers
clearMoundDataVolume
createDatabaseSchemaForUser $SMP_DB_USERNAME $SMP_DB_PASSWORD "${SMP_DB_SCRIPTS}"

# start "
echo "Start containers"
startTestContainers
