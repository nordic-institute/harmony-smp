#!/bin/bash

WORKING_DIR="$(dirname $0)"
SML_INIT_DATABASE="../../../smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb.ddl"
SML_INIT_DATABASE_DATA="../../../smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb-data.sql"
# soap ui data
PREFIX="smp-tomcat-mysql"



echo "Working Directory: ${WORKING_DIR}"
cd "$WORKING_DIR"
# clear volume and containers - to run  restart from strach 


# READ argumnets 
while getopts i: option
do
  case "${option}"
  in
    i) SML_INIT_DATABASE_DATA=${OPTARG};;
  esac
done

# check if property folder exists if not create it
if  [ ! -d "./properties/db-scripts/" ]
then
    mkdir -p "./properties/db-scripts/"
fi

# create  database init script from l
cp   "${SML_INIT_DATABASE}" ./properties/db-scripts/mysql5innodb.ddl
cp   "${SML_INIT_DATABASE_DATA}" ./properties/db-scripts/mysql5innodb-data.sql



function clearOldContainers {
  echo "Clear containers and volumes"
  docker-compose -p "${PREFIX}" rm -s -f -v
}


#
# Always delete shared-status-folder else weblogic will start to quick!
# because statuses are sync over shared-status-folder folders and it could contain status from previous run.

clearOldContainers
# start 
docker-compose -p ${PREFIX} up -d --force-recreate

# wait until service is up
for i in `seq 100`; do timeout 1  bash -c ' curl --head --silent --fail http://localhost:8980/smp/'; if [ $? -eq 0  ] ; then break;fi; echo "$i. Wait for tomcat to start!";  sleep 5;  done;

