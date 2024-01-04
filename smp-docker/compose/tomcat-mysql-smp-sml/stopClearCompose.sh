#!/bin/bash

WORKDIR="$(dirname $0)"
source "${WORKDIR}/../../functions/common.functions"
[ -f "${WORKDIR}/.env" ] && source "${WORKDIR}/.env"
initializeCommonVariables

# clear volume and containers - to run  restart from scratch
function clearOldContainers {
  echo "Save docker log to docker-file"
  docker logs "${PLAN_PREFIX}-domismp-service_1" > smp-container.log 2>&1
  echo "Clear containers and volumes"
  docker-compose -p "${PLAN_PREFIX}" rm -s -f -v
}

# stop and clear  
clearOldContainers

