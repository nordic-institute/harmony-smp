#!/bin/bash

WORKDIR="$(cd -P $(dirname ${BASH_SOURCE[0]} ) && pwd)"
cd "${WORKDIR}" || exit 100
echo "Working Directory: ${WORKDIR}"
# project folder
SMP_PROJECT_FOLDER=$(readlink -e "${WORKDIR}/../../..")
RESULT_FOLDER="${WORKDIR}/results"
# clear old results
rm -rf "${RESULT_FOLDER}"
mkdir -p "${RESULT_FOLDER}"
#load common functions
source "${SMP_PROJECT_FOLDER}/smp-docker/functions/common.functions"
source "${SMP_PROJECT_FOLDER}/smp-docker/functions/run-test.functions"
[ -f "${WORKDIR}/.env" ] && source "${WORKDIR}/.env"
initializeCommonVariables
discoverApplicationVersion

# define network to connect the tests
DOCKER_NETWORK_NAME="${PLAN_PREFIX}_default"
export DOCKER_NETWORK_NAME

# Starting Docker Compose TEST (in specific project to avoid orphan container warning)
docker-compose -f docker-compose.test-ui.yml -p "run-${PLAN_PREFIX}" up

docker cp "run-${PLAN_PREFIX}-testui-1:/results/surefire-reports" ./results
docker cp ${PLAN_PREFIX}-domismp-service-1:/opt/smp/apache-tomcat-9.0.73/logs/*.* ./results/tomcat-logs/
