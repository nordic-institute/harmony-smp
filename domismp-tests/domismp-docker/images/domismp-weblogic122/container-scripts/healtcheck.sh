#!/usr/bin/env bash

HEALTHCHECK_URL="http://localhost:$([[ "V$WL_START_ADMIN" == "Vtrue" ]] && echo "$WL_ADMIN_PORT/weblogic/ready" || echo "$WL_MANAGED_SERVER_PORT/smp/")"

export HEALTHCHECK_URL
echo "Container HEALTHCHECK_URL is: [${HEALTHCHECK_URL}]"

curl -f ${HEALTHCHECK_URL}
