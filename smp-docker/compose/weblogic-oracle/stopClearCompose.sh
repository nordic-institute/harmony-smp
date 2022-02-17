#!/bin/bash

WORKING_DIR="$(dirname $0)"
echo "Working Directory: ${WORKING_DIR}"
cd "$WORKING_DIR"

PREFIX="smp-wls-orcl"

# clear volume and containers - to run  restart from strach 



function clearOldContainers {
  echo "Database stopped"  > ./status-folder/database.status

  echo "Save docker log to docker-file"
  docker logs ${PREFIX} 2>&1 > smp-container.log

  echo "Clear containers and volumes"
  docker-compose -p "${PREFIX}" rm -s -f -v
}


# stop and clear  
clearOldContainers

