#!/usr/bin/env bash

executeUITests(){
  echo "[INFO] start execution of the tests from  ${TEST_UI}"
  cd  "${TEST_UI}"
  mvn clean verify
  EXIT_CODE=$?
  echo '[INFO] finished execution of the tests'
  cp -R ${TEST_UI}/target/surefire-reports "${RESULT_FOLDER}"/
  cp -R ${TEST_UI}/target/domismp-test.log "${RESULT_FOLDER}"/
  exit ${EXIT_CODE}
}


setConfigPropertiesAPITests() {
  echo "[INFO] Update soapUI project file and testsuite names ..."
     if [ -n "${TEST_SUITE}" ]; then
        sed -i "s#<testSuite>.*</testSuite>#<testSuite>${TEST_SUITE}</testSuite>#g" "${TEST_API}"/pom.xml
     fi
	 # TODO Add the legacy test suite to the new project file then remove the following configuration 
	 if [ -n "${TEST_FILE_NAME}" ]; then
        sed -i "s#<projectFile>\${basedir}/soapui/.*</projectFile>#<projectFile>\${basedir}/soapui/${TEST_FILE_NAME}</projectFile>#g" "${TEST_API}"/pom.xml
     fi
}

executeAPITests() {
  echo "[INFO] start execution of the tests from ${TEST_API}"
     cd  "${TEST_API}"
     mvn clean verify -Durl=${TEST_URL} -Prun-soapui
     EXIT_CODE=$?
     echo '[INFO] finished execution of the tests'
     cp -R ${TEST_API}/target/soapui-reports "${RESULT_FOLDER}"/
     exit ${EXIT_CODE}
}

createUIProperties(){
  echo '[INFO] start execution of the soapui tests'
}

if [ "$TEST_PLAN" == "ui" ]; then
  executeUITests
elif [ "$TEST_PLAN" == "api" ]; then
  setConfigPropertiesAPITests
  executeAPITests
elif [ "$TEST_PLAN" == "manual" ]; then
  echo "[INFO] Container will start in idle mode to allow user to manually login and execute tests!"
  tail -f /dev/null
else
  echo "[ERROR] Unknown test plan [$TEST_PLAN]! Allowed values are [ui, api, manual]!"
  echo "[ERROR] Start container with -e TEST_PLAN=ui|api|manual to execute tests!"
  exit 1
fi
