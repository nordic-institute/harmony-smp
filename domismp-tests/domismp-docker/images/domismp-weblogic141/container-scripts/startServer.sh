#!/usr/bin/env bash

#Define DOMAIN_HOME
echo "Oracle Home is: [$ORACLE_HOME]"
echo "Domain Home is: [${WL_DOMAIN_HOME}]"
HEALTHCHECK_URL="http://localhost:$([[ "V$WL_START_ADMIN" == "Vtrue" ]] && echo "$WL_ADMIN_PORT/weblogic/ready" || echo "$WL_MANAGED_SERVER_PORT/smp/")"
export HEALTHCHECK_URL
echo "Container HEALTHCHECK_URL is: [${HEALTHCHECK_URL}]"

echo "Delay startup in seconds: [${WL_DELAY_STARTUP_IN_S:-0}]"
echo "Start as admin: [${WL_START_ADMIN}]"

if [[ "V$WL_START_ADMIN" == "Vtrue" ]]; then
  echo "Start admin server"
  "${ORACLE_HOME}/startAdminServer.sh"
else
  echo "Start managed server"
  "${ORACLE_HOME}/startManagedServer.sh"
fi
