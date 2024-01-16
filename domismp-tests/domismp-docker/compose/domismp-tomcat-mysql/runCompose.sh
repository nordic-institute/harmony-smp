#!/bin/bash

# init plan variables
WORKDIR="$(cd -P $(dirname ${BASH_SOURCE[0]} ) && pwd)"
source "${WORKDIR}/../../functions/run-test.functions"
initializeVariables

SMP_INIT_DATABASE="${SMP_PROJECT_FOLDER}/smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb.ddl"
#SMP_INIT_DATABASE_DATA="${SMP_PROJECT_FOLDER}/smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb-data.sql"
SMP_INIT_DATABASE_DATA="${SMP_PROJECT_FOLDER}/smp-soapui-tests/groovy/mysql-4.1_integration_test_data.sql"
# READ arguments
while getopts i:v: option
do
  case "${option}"
  in
    i) SMP_INIT_DATABASE_DATA=${OPTARG};;
    v) SMP_VERSION=${OPTARG};;
  esac
done

echo "*****************************************************************"
echo "* Start SMP image for version: [$SMP_VERSION]"
echo "* Plan prefix: [${PLAN_PREFIX}]"
echo "* WORKDIR: [${WORKDIR}]"
echo "*****************************************************************"
echo ""
# export plan variables
export SMP_VERSION
# init database scripts
DB_SCRIPT_FOLDER="./properties/db-scripts"
[[ ! -d "${DB_SCRIPT_FOLDER}" ]] &&  mkdir -p "${DB_SCRIPT_FOLDER}"
# create  database init script from l
cp  "${SMP_INIT_DATABASE}" ./properties/db-scripts/mysql5innodb.ddl
cp  "${SMP_INIT_DATABASE_DATA}" ./properties/db-scripts/mysql5innodb-data.sql

echo "Clear old containers"
stopAndClearTestContainers
# start " 
echo "Start containers"
startTestContainers
