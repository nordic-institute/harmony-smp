#!/usr/bin/env bash

# This is build script for building image.
# first it copies external resources to resources folder
# then it builds the image using docker-compose.build.yml
# and finally it cleans the external resources
: "${SMP_PROJECT_FOLDER:?Need to set SMP project folder non-empty!}"
: "${SMP_VERSION:?Need to set SMP version non-empty!}"

WORKING_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${WORKING_DIR}"

copyExternalImageResources() {
		echo "Copy test project resources ..."
     # copy artefact to docker build folder
     cleanExternalImageResources
     mkdir -p ./artefacts
     # copy artefact to docker build folder
     cp -r "${SMP_PROJECT_FOLDER}/domismp-tests/domismp-tests-ui" ./artefacts/test-ui
     cp -r "${SMP_PROJECT_FOLDER}/domismp-tests/domismp-tests-api" ./artefacts/test-api
}

cleanExternalImageResources() {
    echo "Clean external resources ..."
    [[ -d  ./artefacts/test-ui ]] && rm -rf ./artefacts/test-ui
    [[ -d  ./artefacts/test-api ]] && rm -rf ./artefacts/test-api
}

composeBuildImage() {
	echo "Build ${IMAGE_NAME_DOMIBUS_SOAPUI} image..."
	docker compose -f docker-compose.build.yml build
}


# clean external resources before copy
copyExternalImageResources
composeBuildImage
cleanExternalImageResources
