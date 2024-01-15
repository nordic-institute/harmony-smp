#!/usr/bin/env bash

# This is build script for building image.
# first it copies external resources to resources folder
# then it builds the image using docker-compose.build.yml
# and finally it cleans the external resources
: "${SMP_PROJECT_FOLDER:?Need to set SMP project folder non-empty!}"
: "${SMP_VERSION:?Need to set SMP version non-empty!}"
: "${SMP_ARTEFACTS:?Need to set SMP_ARTEFACTS non-empty!}"
: "${SMP_SPRINGBOOT_ARTEFACTS:?Need to set SMP_SPRINGBOOT_ARTEFACTS non-empty!}"

WORKING_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${WORKING_DIR}"

copyExternalImageResources() {
		echo "Copy test project resources ..."
     # copy artefact to docker build folder
     cleanExternalImageResources
     mkdir -p ./artefacts
     # copy artefact to docker build folder
     cp -r ../shared-artefacts ./artefacts/

    if [[ ! -f "${SMP_SPRINGBOOT_ARTEFACTS}/smp-springboot-$SMP_VERSION-exec.jar" ]]; then
      echo "SMP artefact '${SMP_SPRINGBOOT_ARTEFACTS}/smp-springboot-$SMP_VERSION-exec.jar' not found!"
      exit 1
    else
      # for mysql tomcat
      cp "${SMP_SPRINGBOOT_ARTEFACTS}/smp-springboot-$SMP_VERSION-exec.jar" ./artefacts/smp-springboot-exec.jar
    fi

    if [[ ! -f "${SMP_ARTEFACTS}/smp-${SMP_VERSION}-setup.zip" ]]; then
      echo "SMP bundle artefact  '${SMP_ARTEFACTS}/smp-${SMP_VERSION}-setup.zip' not found!"
      exit 1
    else
      # for mysql data
      cp "${SMP_ARTEFACTS}/smp-${SMP_VERSION}-setup.zip" ./artefacts/smp-setup.zip
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
