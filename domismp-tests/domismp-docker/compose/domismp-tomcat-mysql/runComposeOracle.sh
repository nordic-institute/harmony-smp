#!/bin/bash

# Variables
# -i: path to the database data initialization script, default: SMP_PROJECT_FOLDER/smp-webapp/src/main/smp-setup/database-scripts/mysql5innodb-data.sql
# -v: version of the SMP to start. If not provided, the version will defined by maven project version
# -l: start with local compose file docker-compose.localhost.yml, default: false. The compose file is used to start
#      the SMP with local configuration (e.g. exporting ports, etc.)

# init plan variables
WORKDIR="$(cd -P $(dirname ${BASH_SOURCE[0]} ) && pwd)"
source "${WORKDIR}/../../functions/run-test.functions"
initializeVariables
START_LOCAL="false"

SMP_INIT_DATABASE="${SMP_PROJECT_FOLDER}/smp-webapp/src/main/smp-setup/database-scripts/oracle10g.ddl"
SMP_INIT_DATABASE_DATA="${SMP_PROJECT_FOLDER}/domismp-tests/domismp-tests-api/groovy/oracle-4.1_integration_test_data.sql"
#SMP_INIT_DATABASE_DATA="${SMP_PROJECT_FOLDER}/smp-webapp/src/main/smp-setup/database-scripts/oracle10g-data.sql"


# example to test migration from 5.1 to 5.1
#SMP_INIT_DATABASE="/cef/code/tmp/smp/smp-webapp/src/main/smp-setup/database-scripts/oracle10g.ddl"
#SMP_INIT_DATABASE_DATA="/cef/code/tmp/smp/domismp-tests/domismp-tests-api/groovy/oracle-4.1_integration_test_data.sql"
#SMP_MIGRATE_DATABASE="${SMP_PROJECT_FOLDER}/smp-webapp/src/main/smp-setup/database-scripts/migration from 5.1 to 5.2/oracle10g-5.1_to_5.2.sql"

# READ arguments
while getopts i:v:l: option
do
  case "${option}"
  in
    i) SMP_INIT_DATABASE_DATA=${OPTARG};;
    v) SMP_VERSION=${OPTARG};;
    l) START_LOCAL=${OPTARG};;
    *) echo "Unknown option [${option}]. Usage: $0 [-i] [-v] [-l]"; exit 1;;
  esac
done

echo "*****************************************************************"
echo "* Start SMP image for version: [$SMP_VERSION]"
echo "* Plan prefix: [${PLAN_PREFIX}]"
echo "* WORKDIR: [${WORKDIR}]"
echo "*****************************************************************"
echo ""
# export plan variables
#ORA_VERSION="19.3.0"
#ORA_EDITION="se2"
#ORA_SERVICE="ORCLPDB1"
#ORACLE_PDB="ORCLPDB1"
ORA_VERSION="21.3.0"
ORA_EDITION="xe"
ORA_SERVICE="xe"
export SMP_VERSION
export SMP_DB_USER="smp"
export SMP_DB_USER_PASSWORD="test"
export SMP_DB_URL="jdbc:oracle:thin:@//smp-oracle-db:1521/${ORA_SERVICE}"
# init database scripts
SMP_DB_SCRIPTS="./properties/db-scripts/oracle"
# clear old database scripts
[[  -d "${SMP_DB_SCRIPTS}" ]] && rm -rf "${SMP_DB_SCRIPTS}"
mkdir -p "${SMP_DB_SCRIPTS}"

# create  database init script from l
echo "Initialize database scripts in ${SMP_DB_SCRIPTS}, user ${SMP_DB_USER} password ***** and JDBC URL ${SMP_DB_URL}"
initOracleDatabaseConfiguration $SMP_DB_USER $SMP_DB_USER_PASSWORD "${SMP_DB_SCRIPTS}"


echo "Clear old containers"
stopAndClearTestContainers "-oracle"
# start " 
echo "Start containers"
startTestContainers "-oracle"
