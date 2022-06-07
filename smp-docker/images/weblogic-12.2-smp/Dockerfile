# LICENSE UPL 1.0
#
# Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
#
# ORACLE DOCKERFILES PROJECT
# --------------------------
# This Dockerfile extends the Oracle WebLogic image built under 12213-doma-home-in-image.
#
# It will deploy any package defined in APP_PKG_FILE.
# into the DOMAIN_HOME with name defined in APP_NAME
#
# HOW TO BUILD THIS IMAGE
# -----------------------
# Run:
#      $ docker build -t weblogic-smp .
# 
# create from base image FROM oracle/weblogic:12.2.1.4-developer
# Please build oracle/weblogic:12.2.1.4-developer (smp-docker/images/oracle/weblogic-12.2.1.4) first
# ---------------
FROM oracle/weblogic:12.2.1.4-developer

# Define variable
ARG SMP_VERSION
ARG LC_ALL=en_US.UTF-8
ARG LANG="en_US.UTF-8"
ARG LANGUAGE="en_US"
ARG ORACLE_HOME=/u01/oracle
ARG WL_DOMAIN_NAME="${WL_DOMAIN_NAME:-smp-domain}"
ARG WL_DOMAIN_HOME="${ORACLE_HOME}/user_projects/domains/${WL_DOMAIN_NAME}"
ARG WL_MANAGED_SERV_NAME=${WL_MANAGED_SERV_NAME:-smp-node-1}
ARG WL_MANAGED_SERV_BASE_NAME=${WL_MANAGED_SERV_BASE_NAME:-smp-node-}
ARG WL_MANAGED_SERVER_COUNT=${WL_MANAGED_SERVER_COUNT:-10}
ARG WL_ADMIN_NAME=${WL_ADMIN_NAME:-admin-server}
ARG WL_CLUSTER_NAME=${WL_CLUSTER_NAME:-smp-cluster}
ARG WL_DEBUG_PORT="${WL_DEBUG_PORT:-8453}"
ARG WL_ADMIN_PORT="${WL_ADMIN_PORT:-7001}"
ARG WL_ADMIN_PORT_HTTPS="${WL_ADMIN_PORT_HTTPS:-7002}"
ARG WL_MANAGED_SERVER_PORT="${WL_MANAGED_SERVER_PORT:-8001}"
ARG SMP_CONFIG_DIR=/data/smp
ARG WL_DATA_WEBLOGIC=/data/weblogic

# set following build arguments also the environment arguments
ENV SMP_VERSION=$SMP_VERSION \
    LC_ALL=$LC_ALL \
    LANG=$LANG \
    LANGUAGE=$LANGUAGE  \
    ORACLE_HOME=$ORACLE_HOME \
    DOCKER_DATA=/data \
    WL_INIT_PROPERTIES="/u01/init/" \
    WL_DOMAIN_NAME="${WL_DOMAIN_NAME}" \
    WL_DOMAIN_HOME="${ORACLE_HOME}/user_projects/domains/${WL_DOMAIN_NAME}" \
    WL_MANAGED_SERV_BASE_NAME="${WL_MANAGED_SERV_BASE_NAME}" \
    WL_MANAGED_SERVER_COUNT=${WL_MANAGED_SERVER_COUNT} \
    WL_DEBUG_PORT="${WL_DEBUG_PORT}"  \
    WL_ADMIN_PORT="${WL_ADMIN_PORT}"  \
    WL_ADMIN_PORT_HTTPS="${WL_ADMIN_PORT_HTTPS}"  \
    WL_MANAGED_SERVER_PORT="${WL_MANAGED_SERVER_PORT}" \
    WL_ADMIN_NAME="${WL_ADMIN_NAME}" \
    WL_CLUSTER_NAME="${WL_CLUSTER_NAME}" \
    SMP_CONFIG_DIR=${SMP_CONFIG_DIR:-/data/smp} \
    SMP_SECURITY_DIR="${SMP_CONFIG_DIR}/security" \
    CLASSPATH="${SMP_CONFIG_DIR}/config" \
    WL_DATA_WEBLOGIC="${WL_DATA_WEBLOGIC:-/data/weblogic}" \
    WL_SECURITY_FILE="${WL_DATA_WEBLOGIC}/security.properties" \
    PATH="$PATH:/u01/oracle/oracle_common/common/bin:/u01/oracle/wlserver/common/bin:${WL_DOMAIN_HOME}:${WL_DOMAIN_HOME}/bin:${ORACLE_HOME}" \
     # configurable argumentst at containe start
    WL_ADMIN_HOST="${WL_ADMIN_HOST:-localhost}"  \
    WL_SERVER_TLS_KEYSTORE_PASS="${WL_SERVER_TLS_KEYSTORE_PASS:-test123}" \
    WL_DEBUG_FLAG="${WL_DEBUG_FLAG:-true}" \
    WL_PRODUCTION_MODE_ENABLED="${WL_PRODUCTION_MODE_ENABLED:-false}" \
    WL_MANAGED_SERV_NAME="${WL_MANAGED_SERV_NAME}" \
    WL_STATUS_FOLDER=${WL_STATUS_FOLDER:-/u01/status/} \
    ENABLE_JACOCO="${ENABLE_JACOCO:-false}"


# Add files required to build this image
COPY container-scripts/* ${ORACLE_HOME}/
COPY container-scripts/init-scripts ${ORACLE_HOME}/init/scripts/
COPY properties/init  ${ORACLE_HOME}/init/properties/
COPY artefacts/smp.war ${ORACLE_HOME}/init/


#Create directory where domain will be written to
USER root
WORKDIR  $ORACLE_HOME
RUN mkdir -p $WL_DOMAIN_HOME && \
    mkdir -p $DOCKER_DATA && \
    chown -R oracle:oracle $ORACLE_HOME && \
    chown -R oracle:oracle $WL_DOMAIN_HOME && \
    chown -R oracle:oracle $DOCKER_DATA && \
    chmod +xw ${ORACLE_HOME}/*.sh && \
    chmod +xw ${ORACLE_HOME}/init/scripts/*.sh && \
    chmod +xw ${ORACLE_HOME}/init/scripts/*.py && \
    chmod -R +xwr $WL_DOMAIN_HOME


USER oracle
WORKDIR $WL_DOMAIN_HOME
# Expose ports for admin, managed server, and debug
EXPOSE $WL_ADMIN_PORT $WL_ADMIN_HTTPS_PORT $WL_MANAGED_SERVER_PORT $WL_DEBUG_PORT

# Define default command to start bash.
CMD ["startManagedServer.sh"]
