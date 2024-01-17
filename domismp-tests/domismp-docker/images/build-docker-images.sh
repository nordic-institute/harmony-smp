#!/usr/bin/env bash

WORKDIR="$(cd -P $( dirname "${BASH_SOURCE[0]}" ) && pwd)"
cd ${WORKDIR} || exit 100
echo "Working Directory: ${WORKDIR}"
#load common functions
source "${WORKDIR}/../functions/common.functions"
initializeCommonVariables
exportBuildArtefactNames

# Script builds docker images for SMP oracle/weblogic environment. Docker images for database and weblogic are from
# https://github.com/oracle/docker-images

# Prerequisites:
# 1. From oracle download:
#  - OracleDB: oracle-xe-11.2.0-1.0.x86_64.rpm.zip
#  - Server JDK 1.8:  server-jre-8u211-linux-x64.tar.gz  (https://github.com/oracle/docker-images/tree/master/OracleJava)
#  - weblogic 12.2.1.3: fmw_12.2.1.3.0_wls_quick_Disk1_1of1.zip
#  - weblogic 14.1.1.0: fmw_14.1.1.0.0_wls_lite_Disk1_1of1.zip
# and put them to folder ${ORACLE_ARTEFACTS}
#
# 2. build SMP mvn clean install
# 3. run the scripts with arguments
# build-docker-images.sh  -f build-docker-images.sh  -f ${oracle_artefact_folder}

#ORA_VERSION="19.3.0"
#ORA_EDITION="se2"
#ORA_SERVICE="ORCLPDB1"

ORA_VERSION="11.2.0.2"
ORA_EDITION="xe"
ORA_SERVICE="xe"

SMP_VERSION=
ORACLE_ARTEFACTS="/CEF/repo"

SMP_PROJECT_FOLDER=$(readlink -e "${WORKDIR}/../../..")
SMP_ARTEFACTS="${SMP_PROJECT_FOLDER}/smp-webapp/target"
SMP_SPRINGBOOT_ARTEFACTS="${SMP_PROJECT_FOLDER}/smp-springboot/target"
SMP_PLUGIN_EXAMPLE="${SMP_PROJECT_FOLDER}/smp-examples/smp-spi-payload-validation-example/target"
SMP_ARTEFACTS_CLEAR="false"

SMP_IMAGE_PUBLISH="false"
DOCKER_USER=${bamboo_DOCKER_USER:-edeliverytest}$
DOCKER_PASSWORD=$bamboo_DOCKER_PASSWORD
DOCKER_REGISTRY_HOST=${bamboo_DOCKER_REGISTRY_HOST}
DOCKER_FOLDER=${bamboo_DOCKER_FOLDER:-${bamboo_DOCKER_USER}}

# READ arguments
while getopts v:o:a:s:c:p: option; do
  case "${option}" in

  v) SMP_VERSION=${OPTARG} ;;
  o) ORACLE_ARTEFACTS=${OPTARG} ;;
  a) SMP_ARTEFACTS=${OPTARG} ;;
  s) SMP_SPRINGBOOT_ARTEFACTS=${OPTARG} ;;
  c) SMP_ARTEFACTS_CLEAR=${OPTARG} ;;
  p) SMP_IMAGE_PUBLISH=${OPTARG} ;;
  *) echo "Unknown option: $option. Use [-v version] [-o oracle_artefact_folder] [-a smp_artefact_folder] [-s smp_springboot_artefact_folder] [-c clear_smp_artefact_folder] [-p publish_image]"
    exit 1 ;;
  esac
done

# discover SMP  version
discoverApplicationVersion

echo "*****************************************************************"
echo "* SMP artefact folders: [$SMP_ARTEFACTS], (Clear folder after build: [$SMP_ARTEFACTS_CLEAR] )"
echo "* SMP artefact springboot folders: [$SMP_SPRINGBOOT_ARTEFACTS]"
echo "* SMP Plugin example: [$SMP_PLUGIN_EXAMPLE] "
echo "* Build SMP image for version [$SMP_VERSION]"
echo "* Oracle artefact folders: [$ORACLE_ARTEFACTS]"
echo "*****************************************************************"
echo ""

export SMP_VERSION
export SMP_PROJECT_FOLDER
export SMP_ARTEFACTS
export SMP_PLUGIN_EXAMPLE
export SMP_SPRINGBOOT_ARTEFACTS
export ORACLE_ARTEFACTS

# -----------------------------------------------------------------------------
# validate all necessary artefacts and prepare files to build images
# -----------------------------------------------------------------------------
validateAndPrepareArtefacts() {
  case "${ORA_VERSION}" in
  "19.3.0")
    ORACLE_DB_FILE="${ORACLE_DB19_FILE}"
    ORACLE_DOCKERFILE="Dockerfile"
    ;;
  "11.2.0.2")
    ORACLE_DB_FILE="${ORACLE_DB11_FILE}"
    ORACLE_DOCKERFILE="Dockerfile.xe"
    ;;
  esac

  export ORA_VERSION
  export ORA_EDITION
  export ORA_SERVICE

  # check oracle database
  if [[ ! -f "${ORACLE_ARTEFACTS}/Oracle/OracleDatabase/${ORA_VERSION}/${ORACLE_DB_FILE}" ]]; then
    echo "Oracle database artefacts '${ORACLE_ARTEFACTS}/Oracle/OracleDatabase/${ORA_VERSION}/${ORACLE_DB_FILE}' not found."
    exit 1
  else
    # copy artefact to docker build folder
    cp "${ORACLE_ARTEFACTS}/Oracle/OracleDatabase/${ORA_VERSION}/${ORACLE_DB_FILE}" ./oracle/oracle-db-${ORA_VERSION}/
  fi

}


# -----------------------------------------------------------------------------
# build docker images
# -----------------------------------------------------------------------------
buildImages() {
  buildOracleDatabaseImage
  buildUtils
  buildImage "${IMAGE_SMP_WEBLOGIC122}"
  buildImage "${IMAGE_SMP_WEBLOGIC141}"
  buildImage "${IMAGE_SMP_TOMCAT_MYSQL}"
  buildImage "${IMAGE_SMP_SPRINGBOOT_MYSQL}"
  buildImage "${IMAGE_SMP_TESTS}"
}

buildImage(){
  echo "Build image [${IMAGE_TAG:-edeliverytest}/$1:${SMP_VERSION}]."
  ./"$1"/build.sh
  if [ $? -ne 0 ]; then
    echo "Error occurred while building image [${IMAGE_TAG:-edeliverytest}/$1:${SMP_VERSION}]!"
    exit 10
  fi
}

buildOracleDatabaseImage(){
  # -----------------------------------------------------------------------------
  # build docker image for oracle database
  # -----------------------------------------------------------------------------
  # oracle 1.2.0.2-xe (https://github.com/oracle/docker-images/tree/master/OracleDatabase/SingleInstance/dockerfiles/11.2.0.2)
  docker build -f ./oracle/oracle-db-${ORA_VERSION}/${ORACLE_DOCKERFILE} -t "${IMAGE_TAG:-edeliverytest}/${IMAGE_SMP_DB_ORACLE}-${ORA_VERSION}-${ORA_EDITION}:${SMP_VERSION}" --build-arg DB_EDITION=${ORA_EDITION} ./oracle/oracle-db-${ORA_VERSION}/
  if [ $? -ne 0 ]; then
    echo "Error occurred while building image [${IMAGE_TAG:-edeliverytest}/${IMAGE_SMP_DB_ORACLE}-${ORA_VERSION}-${ORA_EDITION}:${SMP_VERSION}]!"
    exit 10
  fi
}



buildUtils(){
 # build the httpd image for LB. The Http is configured to allow encoded characters which
  # are not decoded!
  docker build -t "${IMAGE_TAG:-edeliverytest}/smp-httpd:${SMP_VERSION}" ./smp-httpd/
   if [ $? -ne 0 ]; then
     echo "Error occurred while building image [smp-httpd:${SMP_VERSION}]!"
     exit 10
   fi
}

function pushImageToDockerhub() {

  if [[ "V$SMP_IMAGE_PUBLISH" == "Vtrue" ]]; then
    # login to docker
    docker login --username="${DOCKER_USER}" --password="${DOCKER_PASSWORD}" "${DOCKER_REGISTRY_HOST}"
    # push images
    pushImageIfExisting "${IMAGE_SMP_TOMCAT_MYSQL}:${SMP_VERSION}"
    pushImageIfExisting "${IMAGE_SMP_WEBLOGIC122}:${SMP_VERSION}"
    pushImageIfExisting "${IMAGE_SMP_WEBLOGIC141}:${SMP_VERSION}"
    pushImageIfExisting "${IMAGE_SMP_DB_ORACLE}-${ORA_VERSION}-${ORA_EDITION}:${SMP_VERSION}"
    pushImageIfExisting "${IMAGE_SMP_TESTS}:${SMP_VERSION}"
  fi
}

function pushImageIfExisting() {
  if [[ "x$(docker images -q "${1}")" != "x" ]]; then
    local TAGGED_IMAGE="${DOCKER_REGISTRY_HOST:+$DOCKER_REGISTRY_HOST/}${DOCKER_FOLDER:+$DOCKER_FOLDER/}${1}"
    docker tag "${IMAGE_TAG:-edeliverytest}/${1}" "${TAGGED_IMAGE}"
    echo "Pushing image ${1} as ${TAGGED_IMAGE}"
    docker push "${TAGGED_IMAGE}"
  else
    echo "Could not find image ${1} to push!"
  fi
  return 0
}

# -----------------------------------------------------------------------------
# clean
# -----------------------------------------------------------------------------
cleanArtefacts() {
  rm "./oracle/oracle-db-${ORA_VERSION}/${ORACLE_DB_FILE}"   # clean
  rm "./oracle/OracleJava/java-8/${SERVER_JDK_FILE}"         # clean
  rm "./oracle/weblogic-12.2.1.4/${WEBLOGIC_122_QUICK_FILE}" # clean
  rm "./oracle/weblogic-14.1.1.0/${WEBLOGIC_14_FILE}" # clean

  rm -rf "./${IMAGE_SMP_WEBLOGIC122}/artefacts/*.*"

  if [[ "V$SMP_ARTEFACTS_CLEAR" == "Vtrue" ]]; then
    rm -rf "${SMP_ARTEFACTS}/smp-setup.zip"
    rm -rf "${SMP_ARTEFACTS}/smp.war"
  fi
}

validateAndPrepareArtefacts
buildImages
pushImageToDockerhub
cleanArtefacts
