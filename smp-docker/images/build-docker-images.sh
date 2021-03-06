#!/bin/bash

# Script builds docker images for SMP oracle/weblogic environment. Docker images for database and weblogic are from  
# https://github.com/oracle/docker-images 

# Prerequisites:
# 1. From oracle download:
#  - OracleDB: oracle-xe-11.2.0-1.0.x86_64.rpm.zip
#  - Server JDK 1.8:  server-jre-8u211-linux-x64.tar.gz  (https://github.com/oracle/docker-images/tree/master/OracleJava)
#  - weblogic 12.2.1.3: fmw_12.2.1.3.0_wls_quick_Disk1_1of1.zip
# and put them to folder ${ORACLE_ARTEFACTS}
#
# 2. build SMP mvn clean install
# 3. run the scripts with arguments 
# build-docker-images.sh  -f build-docker-images.sh  -f ${oracle_artefact_folder}
 


ORACLE_DB_FILE="oracle-xe-11.2.0-1.0.x86_64.rpm.zip"
SERVER_JDK_FILE="server-jre-8u211-linux-x64.tar.gz"
WEBLOGIC_122_QUICK_FILE="fmw_12.2.1.3.0_wls_quick_Disk1_1of1.zip"
SMP_VERSION=
ORACLE_ARTEFACTS="/CEF/oracle-install"

SMP_ARTEFACTS="../../smp-webapp/target/"
SMP_ARTEFACTS_CLEAR="false"

SMP_IMAGE_PUBLISH="false"
DOCKER_USER=$bamboo_DOCKER_USER
DOCKER_PASSWORD=$bamboo_DOCKER_PASSWORD


# READ argumnets 
while getopts v:o:s:c:p: option
do
  case "${option}"
  in
    v) SMP_VERSION=${OPTARG};;
    o) ORACLE_ARTEFACTS=${OPTARG};;
    s) SMP_ARTEFACTS=${OPTARG};;
    c) SMP_ARTEFACTS_CLEAR=${OPTARG};;
    p) SMP_IMAGE_PUBLISH=${OPTARG};;
  esac
done

if [[  -z "${SML_VERSION}" ]]
then
  # get version from setup file
  #cd "${SMP_ARTEFACTS}"
  #SMP_VERSION="$(ls smp-*-setup.zip | sed -e 's/.*smp-//g' | sed -e 's/-setup\.zip$//g')"
  SMP_VERSION="$(mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout)"
  # go back to dirname
fi

DIRNAME=`dirname "$0"`
cd "$DIRNAME"
DIRNAME="$(pwd -P)"
echo "*****************************************************************"
echo "* SMP artefact folders: $SMP_ARTEFACTS, (Clear folder after build: $SMP_ARTEFACTS_CLEAR )"
echo "* Build SMP image for version $SMP_VERSION"
echo "* Oracle artefact folders: $ORACLE_ARTEFACTS"
echo "*****************************************************************"
echo ""



# -----------------------------------------------------------------------------
# validate all necessary artefacts and prepare files to build images 
# -----------------------------------------------------------------------------    
 validateAndPrepareArtefacts() {
   
  # check oracle database
  if [[ ! -f "${ORACLE_ARTEFACTS}/${ORACLE_DB_FILE}" ]]
  then
    echo "Oracle database artefacts '${ORACLE_ARTEFACTS}/${ORACLE_DB_FILE}' not found."
    exit 1;
  else
    # copy artefact to docker build folder
    cp "${ORACLE_ARTEFACTS}/${ORACLE_DB_FILE}"  ./oracle/oracle-db-11.2.0.2/
  fi

  # check server JDK
  if [[ ! -f "${ORACLE_ARTEFACTS}/${SERVER_JDK_FILE}" ]]
  then
    echo "Server JDK artefacts '${ORACLE_ARTEFACTS}/${SERVER_JDK_FILE}' not found."
    exit 1;
  else
    # copy artefact to docker build folder
    cp "${ORACLE_ARTEFACTS}/${SERVER_JDK_FILE}" ./oracle/OracleJava/java-8/
  fi

 # check weblogic 
  if [[ ! -f "${ORACLE_ARTEFACTS}/${WEBLOGIC_122_QUICK_FILE}" ]]
  then
    echo "Weblogic artefacts '${ORACLE_ARTEFACTS}/${WEBLOGIC_122_QUICK_FILE}' not found."
    exit 1;
  else
    # copy artefact to docker build folder
    cp "${ORACLE_ARTEFACTS}/${WEBLOGIC_122_QUICK_FILE}"  ./oracle/weblogic-12.2.1.3/
  fi
 

  if  [[ ! -d "./tomcat-mysql/artefacts/" ]]
  then
    mkdir -p "./tomcat-mysql/artefacts/"
  fi
    

  # SMP artefats 
  if [[ ! -f "${SMP_ARTEFACTS}/smp.war" ]]
  then
    echo "SMP artefact   '${SMP_ARTEFACTS}/smp.war' not found. Was project built!"
    exit 1;
  else
    # copy artefact to docker build folder
    # for weblogic
    cp "${SMP_ARTEFACTS}/smp.war" ./weblogic-12.2.1.3-smp/smp.war
    # for mysql tomcat
    cp "${SMP_ARTEFACTS}/smp.war" ./tomcat-mysql/artefacts/smp.war
  fi

 # SMP setup zip   
  if [[ ! -f "${SMP_ARTEFACTS}/smp-${SMP_VERSION}-setup.zip" ]]
  then
    echo "SMP setup boundle  '${SMP_ARTEFACTS}/smp-${SMP_VERSION}-setup.zip' not found. Was project built!"
    exit 1;
  else
    # copy artefact to docker build folder
    cp "${SMP_ARTEFACTS}/smp-${SMP_VERSION}-setup.zip" ./weblogic-12.2.1.3-smp/smp-setup.zip
    cp "${SMP_ARTEFACTS}/smp-${SMP_VERSION}-setup.zip" ./tomcat-mysql/artefacts/smp-setup.zip
  fi



}


# -----------------------------------------------------------------------------
# build docker images 
# -----------------------------------------------------------------------------    
 buildImages() {

    # -----------------------------------------------------------------------------
    # build docker image for oracle database 
    # -----------------------------------------------------------------------------
    # oracle 1.2.0.2-xe (https://github.com/oracle/docker-images/tree/master/OracleDatabase/SingleInstance/dockerfiles/11.2.0.2)
    docker build -f ./oracle/oracle-db-11.2.0.2/Dockerfile.xe -t "smp-oradb-11.2.0.2-xe:${SMP_VERSION}" ./oracle/oracle-db-11.2.0.2/

    # -----------------------------------------------------------------------------
    # build docker image for oracle database 
    # -----------------------------------------------------------------------------

    # create docker OS image with java (https://github.com/oracle/docker-images/tree/master/OracleJava/java-8)  
    docker build  -t oracle/serverjre:8 ./oracle/OracleJava/java-8/

    # create weblogic basic (https://github.com/oracle/docker-images/tree/master/OracleWebLogic/dockerfiles/12.2.1.3)
    docker build  -f ./oracle/weblogic-12.2.1.3/Dockerfile.developer  -t oracle/weblogic:12.2.1.3-developer ./oracle/weblogic-12.2.1.3/

    # create weblogic domain-home-in-image (https://github.com/oracle/docker-images/tree/master/OracleWebLogic/samples/12213-domain-home-in-image./)
    ./oracle/weblogic-12213-domain-home-in-image/container-scripts/setEnv.sh ./oracle/weblogic-12213-domain-home-in-image/properties/docker-build/domain.properties
    docker build $BUILD_ARG  --force-rm=true -t oracle/12213-domain-home-in-image ./oracle/weblogic-12213-domain-home-in-image/


    # build SMP deployment.
    docker build -t "smp-weblogic-122:${SMP_VERSION}" ./weblogic-12.2.1.3-smp/ --build-arg SMP_VERSION="$SMP_VERSION"

    # build tomcat mysql image  deployment.
    docker build -t "smp-tomcat-mysql:${SMP_VERSION}" ./tomcat-mysql/  --build-arg SMP_VERSION=${SMP_VERSION}

}

function pushImageToDockerhub {

   if [[ "V$SMP_IMAGE_PUBLISH" == "Vtrue" ]]
   then
       # login to docker
       docker login --username="${DOCKER_USER}" --password="${DOCKER_PASSWORD}"
       # push images
       pushImageIfExisting "smp-tomcat-mysql:${SMP_VERSION}"
       pushImageIfExisting "smp-weblogic-122:${SMP_VERSION}"
       pushImageIfExisting "smp-oradb-11.2.0.2-xe:${SMP_VERSION}"
   fi
}


function pushImageIfExisting {
  if [[ "x$(docker images -q "${1}")" != "x" ]]; then
    echo "Pushing image ${1}"
    docker tag "${1}" "${DOCKER_USER}"/"${1}"
    docker push "${DOCKER_USER}"/"${1}"
  else
    echo "Could not find image ${1} to push!"
  fi
  return 0
}

# -----------------------------------------------------------------------------
# clean 
# -----------------------------------------------------------------------------    
 cleanArtefacts() {
  rm "./oracle/oracle-db-11.2.0.2/${ORACLE_DB_FILE}"   # clean
  rm "./oracle/OracleJava/java-8/${SERVER_JDK_FILE}" # clean
  rm "./oracle/weblogic-12.2.1.3/${WEBLOGIC_122_QUICK_FILE}" # clean
  rm "./weblogic-12.2.1.3-smp/smp.war"
  rm "./weblogic-12.2.1.3-smp/smp-setup.zip"

  # clear also the tomcat/mysql image  
  rm -rf "./tomcat-mysql/artefacts/*.*"

  if [[ "V$SMP_ARTEFACTS_CLEAR" == "Vtrue" ]]
  then
    rm -rf  "${SMP_ARTEFACTS}/smp-setup.zip"
    rm -rf  "${SMP_ARTEFACTS}/smp.war"
  fi

}


validateAndPrepareArtefacts
buildImages
pushImageToDockerhub
cleanArtefacts

