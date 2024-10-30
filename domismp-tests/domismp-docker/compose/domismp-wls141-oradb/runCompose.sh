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
START_LOCAL="false"

#ORA_VERSION="19.3.0"
#ORA_EDITION="se2"
#ORA_SERVICE="ORCLPDB1"
#ORACLE_PDB="ORCLPDB1"
ORA_VERSION="11.2.0.2"
ORA_EDITION="xe"
ORA_SERVICE="xe"

SMP_DB_USERNAME="smp"
SMP_DB_PASSWORD="test"
# this is JDBC URL for SMP application, the hostname must match the one from docker-compose.yml for database service
SMP_JDBC_URL="jdbc:oracle:thin:@//smp-oracle-db:1521/${ORA_SERVICE}"
SMP_DB_SCRIPTS=./properties/db-scripts
SMP_WLS_INIT_SCRIPTS=./properties/weblogic-init

# READ arguments
while getopts i:v:l: option
do
  case "${option}"
  in
    i) SMP_INIT_DATABASE_DATA=${OPTARG};;
    v) SMP_VERSION=${OPTARG};;
    l) START_LOCAL=${OPTARG};;
    *) echo "Unknown option [${option}]. Usage: $0 [-i] [-v]"; exit 1;;
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

# start
export SMP_VERSION
export ORA_VERSION
export ORA_EDITION
export SMP_VERSION

echo "Clear old containers"
stopAndClearTestContainers
clearMoundDataVolume
initOracleDatabaseConfiguration $SMP_DB_USERNAME $SMP_DB_PASSWORD "${SMP_DB_SCRIPTS}" "${SMP_WLS_INIT_SCRIPTS}"

# start "
echo "Start containers"
startTestContainers
