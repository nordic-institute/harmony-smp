#!/bin/bash


WORKDIR="$(cd -P $(dirname ${BASH_SOURCE[0]} ) && pwd)"
cd "${WORKDIR}" || exit 100
echo "Working Directory: ${WORKDIR}"
# project folder
SMP_PROJECT_FOLDER=$(readlink -e "${WORKDIR}/../../..")
#load common functions
source "${SMP_PROJECT_FOLDER}/smp-docker/functions/common.functions"
source "${SMP_PROJECT_FOLDER}/smp-docker/functions/run-test.functions"
[ -f "${WORKDIR}/.env" ] && source "${WORKDIR}/.env"
initializeCommonVariables

SMP_INIT_DATABASE="${SMP_PROJECT_FOLDER}/smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb.ddl"
#SMP_INIT_DATABASE_DATA="${SMP_PROJECT_FOLDER}/smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb-data.sql"
SMP_INIT_DATABASE_DATA="${SMP_PROJECT_FOLDER}/smp-soapui-tests/groovy/mysql-4.1_integration_test_data.sql"
# soap ui data
SMP_VERSION=


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

# wait until service is up
for i in `seq 100`; do timeout 1  bash -c 'curl --silent --fail http://localhost:8282/smp/'; if [ $? -eq 0  ] ; then break;fi; echo "$i. Wait for tomcat to start!";  sleep 5;  done;

