#!/usr/bin/env bash

# This is build script for building image.
# first it copies external resources to resources folder
# then it builds the image using docker-compose.build.yml
# and finally it cleans the external resources
: "${SMP_PROJECT_FOLDER:?Need to set SMP project folder non-empty!}"
: "${SMP_VERSION:?Need to set SMP version non-empty!}"
: "${SMP_ARTEFACTS:?Need to set SMP_ARTEFACTS non-empty!}"
: "${ORACLE_ARTEFACTS:?Need to set folder ORACLE_ARTEFACTS  non-empty!}"

# init plan variables
WORKDIR="$(cd -P $(dirname "${BASH_SOURCE[0]}" ) && pwd)"
source "${WORKDIR}/../../functions/run-test.functions"
initializeVariables
# set folder with oralce artefacts which were manually downloaded from oracle site
ORACLE_DOCKER_FOLDER="${WORKDIR}/../oracle"

copyExternalImageResources() {
		echo "Copy test project resources ..."
    cd "${WORKDIR}" || exit 1
    # copy artefact to docker build folder
    cleanExternalImageResources
    mkdir -p ./artefacts
    # copy artefact to docker build folder
    cp -r ../shared-artefacts ./artefacts/
    buildPrepareSMPArtefacts ./artefacts

    if [[ ! -f "${ORACLE_ARTEFACTS}/Oracle/Java/${SERVER_JDK_FILE}" ]]; then
     echo "Server JDK 8 artefacts '${ORACLE_ARTEFACTS}/Oracle/Java/${SERVER_JDK_FILE}' not found."
     exit 1
    else
     # copy artefact to build java for weblogic 14c
     cp "${ORACLE_ARTEFACTS}/Oracle/Java/${SERVER_JDK_FILE}" "${ORACLE_DOCKER_FOLDER}"/OracleJava/java-8/
    fi

    # WeblLogic 14c
    if [[ ! -f "${ORACLE_ARTEFACTS}/${WEBLOGIC_122_QUICK_FILE}" ]]; then
      echo "Weblogic 122 artefacts '${ORACLE_ARTEFACTS}/${WEBLOGIC_122_QUICK_FILE}' not found."
      exit 1
    else
      # copy artefact to docker build folder
      cp "${ORACLE_ARTEFACTS}/${WEBLOGIC_122_QUICK_FILE}" "${ORACLE_DOCKER_FOLDER}"/weblogic-12.2.1.4/
    fi

}

cleanExternalImageResources() {
  echo "Clean external resources ..."
  [[ -d  ./artefacts ]] && rm -rf ./artefacts/
}

composeBuildImage() {
	echo "Build ${IMAGE_NAME_DOMIBUS_SOAPUI} image..."
	docker compose -f docker-compose.build.yml build
}


# clean external resources before copy
copyExternalImageResources
composeBuildImage
cleanExternalImageResources
