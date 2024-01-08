#!/usr/bin/env bash

echo '[INFO] start execution of the tests'
cd /home/${SEL_USER}/domiSMP-ui-tests
mvn clean verify -Dtest.properties.path=./src/main/resources/docker-firefox.properties
EXIT_CODE=$?
echo '[INFO] finished execution of the tests'
cp -R /home/${SEL_USER}/domiSMP-ui-tests/target/surefire-reports /results
cp -R /home/${SEL_USER}/domiSMP-ui-tests/target/domismp-test.log /results
exit ${EXIT_CODE}
