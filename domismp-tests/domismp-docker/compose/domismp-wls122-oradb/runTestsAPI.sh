#!/bin/bash

# init plan variables
WORKDIR="$(cd -P $(dirname "${BASH_SOURCE[0]}" ) && pwd)"
source "${WORKDIR}/../../functions/run-test.functions"
initializeVariables

runTestsFromContainer "testapi" "test-api" "$@"

