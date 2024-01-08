#!/bin/bash

WORKDIR="$(dirname $0)"
SMP_PROJECT_FOLDER=$(readlink -e "${WORKDIR}/../../..")
source "${SMP_PROJECT_FOLDER}/functions/common.functions"
source "${SMP_PROJECT_FOLDER}/smp-docker/functions/run-test.functions"
[ -f "${WORKDIR}/.env" ] && source "${WORKDIR}/.env"
cd "${WORKDIR}" || exit 100
initializeCommonVariables
discoverApplicationVersion

echo "Clear old containers"
stopAndClearTestContainers

