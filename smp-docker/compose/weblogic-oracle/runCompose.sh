#!/bin/bash

WORKING_DIR="$(dirname $0)"
SMP_INIT_DATABASE="../../../smp-webapp/src/main/smp-setup/database-scripts/oracle10g.ddl"
#SMP_INIT_DATABASE_DATA="../../../smp-webapp/src/main/smp-setup/database-scripts/oracle10g-data.sql"
SMP_INIT_DATABASE_DATA="../../../smp-soapui-tests/groovy/oracle-4.1_integration_test_data.sql"

# soap ui data
PREFIX="smp-wls-orcl"
# TODO sync with build script
ORA_VERSION="11.2.0.2"
ORA_EDITION="xe"
ORA_SERVICE="xe"


SMP_VERSION=
echo "Working Directory: ${WORKING_DIR}"
cd "$WORKING_DIR"
# clear volume and containers - to run  restart from strach 


# READ argumnets 
while getopts i:v: option
do
  case "${option}"
  in
    i) SMP_INIT_DATABASE_DATA=${OPTARG};;
    v) SMP_VERSION=${OPTARG};;
  esac
done

export SMP_VERSION
export ORA_VERSION
export ORA_EDITION
export ORA_SERVICE
# create  database init script from 
echo "CONNECT smp/test@//localhost:1521/xe;" > ./properties/db-scripts/02_oracle10g.sql
cat  "${SMP_INIT_DATABASE}" >> ./properties/db-scripts/02_oracle10g.sql



# copy init database data for  SMP    
if [ ! -f "${SMP_INIT_DATABASE_DATA}" ]
  then
  echo "SMP sql init data '${SMP_INIT_DATABASE_DATA} not found!!"
  exit 1;
else
  # copy artefact to docker build folder
  echo "CONNECT smp/test@//localhost:1521/xe;" > ./properties/db-scripts/03_oracle10g-data.sql
  cat  "${SMP_INIT_DATABASE_DATA}" >> ./properties/db-scripts/03_oracle10g-data.sql
fi




function clearOldContainers {
  echo "Clear containers and volumes"
  docker-compose -p "${PREFIX}" rm -s -f -v
  docker volume rm "${PREFIX}_shared-status-folder"
}


#
# Always delete shared-status-folder else weblogic will start to quick!
# because statuses are sync over shared-status-folder folders and it could contain status from previous run.

clearOldContainers
# start 
docker-compose -p ${PREFIX} up -d --force-recreate

# wait until service is up
for i in `seq 100`; do timeout 1  bash -c ' curl --head --silent --fail http://localhost:7901/smp/'; if [ $? -eq 0  ] ; then break;fi; echo "$i. Wait for weblogic to start!";  sleep 5;  done;

