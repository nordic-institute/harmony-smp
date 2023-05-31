#!/bin/bash

WORKING_DIR="$(dirname $0)"
SMP_INIT_DATABASE="../../../smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb.ddl"
#SMP_INIT_DATABASE_DATA="../../../smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb-data.sql"
SMP_INIT_DATABASE_DATA="../../../smp-soapui-tests/groovy/mysql-4.1_integration_test_data.sql"
# soap ui data
PREFIX="smp-sml-tomcat-mysql"
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


if [  -z "${SMP_VERSION}" ]
then
  # get version from POM file 
  SMP_VERSION="$(mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout)"
  
fi

echo "SMP version: $SMP_VERSION"
echo "Working Directory: ${WORKING_DIR}"
cd "$WORKING_DIR"



# check if property folder exists if not create it
if  [ ! -d "./properties/db-scripts/" ]
then
    mkdir -p "./properties/db-scripts/"
fi

# create  database init script from l
cp   "${SMP_INIT_DATABASE}" ./properties/db-scripts/mysql5innodb.ddl
cp   "${SMP_INIT_DATABASE_DATA}" ./properties/db-scripts/mysql5innodb-data.sql
cp


function clearOldContainers {
  echo "Clear containers and volumes"
  docker-compose -p "${PREFIX}" rm -s -f -v 
  echo "Clear containers and volumes"
}


export SMP_VERSION="${SMP_VERSION}"
echo "Clear old containers"
clearOldContainers
# start " 
echo "Start compose"
docker-compose -p ${PREFIX} up -d --force-recreate 

# wait until service is up
for i in `seq 100`; do timeout 1  bash -c 'curl --silent --fail http://localhost:8982/smp/'; if [ $? -eq 0  ] ; then break;fi; echo "$i. Wait for tomcat to start!";  sleep 5;  done;

