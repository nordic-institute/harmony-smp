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


discoverApplicationVersion

echo "*************************************************************************"
echo "SMP version: [${SMP_VERSION}]"
echo "Init sql data: [${SMP_INIT_DATABASE_DATA}]"
echo "Working Directory: [${WORKDIR}]"
echo "*************************************************************************"

export SMP_VERSION


# check if property folder exists if not create it
if  [ ! -d "./properties/db-scripts/" ]
then
    mkdir -p "./properties/db-scripts/"
fi

# create  database init script from l
cp   "${SMP_INIT_DATABASE}" ./properties/db-scripts/mysql5innodb.ddl
cp   "${SMP_INIT_DATABASE_DATA}" ./properties/db-scripts/mysql5innodb-data.sql

echo "Clear old containers"
stopAndClearTestContainers
# start "
echo "Start containers"
startTestContainers


