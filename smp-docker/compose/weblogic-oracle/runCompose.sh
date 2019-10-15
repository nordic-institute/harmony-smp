#!/bin/bash

WORKING_DIR="$(dirname $0)"
SMP_INIT_DATABASE="../../../smp-webapp/src/main/smp-setup/database-scripts/oracle10g.ddl"
SMP_INIT_DATABASE_DATA="../../../smp-webapp/src/main/smp-setup/database-scripts/oracle10g-data.sql"
# soap ui data
PREFIX="smp-wls-orcl"
SMP_VERSION=




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
export SMP_VERSION="${SMP_VERSION}"
docker-compose -p ${PREFIX} up -d --force-recreate

# wait until service is up
for i in `seq 100`; do timeout 1  bash -c ' curl --head --silent --fail http://localhost:7901/smp/'; if [ $? -eq 0  ] ; then break;fi; echo "$i. Wait for weblogic to start!";  sleep 5;  done;

