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
ARG WL_ADMIN_HOST="${WL_ADMIN_HOST:-localhost}"
ARG CUSTOM_DOMAIN_NAME="${CUSTOM_DOMAIN_NAME:-domain1}"
ARG CUSTOM_ADMIN_PORT="${CUSTOM_ADMIN_PORT:-7001}"
ARG CUSTOM_ADMIN_HTTPS_PORT="${CUSTOM_ADMIN_HTTPS_PORT:-7002}"
ARG CUSTOM_MANAGED_SERVER_PORT="${CUSTOM_MANAGED_SERVER_PORT:-8001}"
ARG CUSTOM_DEBUG_PORT="${CUSTOM_DEBUG_PORT:-8453}"
ARG CUSTOM_ADMIN_NAME="${CUSTOM_ADMIN_NAME:-admin-server}"
ARG CUSTOM_ADMIN_HOST="${CUSTOM_ADMIN_HOST:-wlsadmin}"
ARG CUSTOM_CLUSTER_NAME="${CUSTOM_CLUSTER_NAME:-DockerCluster}"
ARG WL_SERVER_TLS_KEYSTORE_PASS="${WL_SERVER_TLS_KEYSTORE_PASS:-test123}"

ENV APP_VERSION=$SMP_VERSION \
    LC_ALL=en_US.UTF-8 \
    LANG="en_US.UTF-8" \
    LANGUAGE="en_US"  \
    ORACLE_HOME=/u01/oracle \
    PROPERTIES_FILE_DIR="${ORACLE_HOME}/properties" \
    DOMAIN_NAME="${CUSTOM_DOMAIN_NAME}" \
    DOMAIN_HOME="${ORACLE_HOME}/user_projects/domains/${CUSTOM_DOMAIN_NAME}" \
    ADMIN_PORT="${CUSTOM_ADMIN_PORT}" \
    ADMIN_HTTPS_PORT="${CUSTOM_ADMIN_HTTPS_PORT}" \
    ADMIN_NAME="${CUSTOM_ADMIN_NAME}" \
    ADMIN_HOST="${CUSTOM_ADMIN_HOST}" \
    CLUSTER_NAME="${CUSTOM_CLUSTER_NAME}" \
    MANAGED_SERVER_PORT="${CUSTOM_MANAGED_SERVER_PORT}" \
    MANAGED_SERV_NAME="${MANAGED_SERV_NAME}" \
     # set following build arguments also the environment arguments
    WL_DEBUG_PORT="${WL_DEBUG_PORT}"  \
    WL_ADMIN_NAME="${WL_ADMIN_NAME}"  \
    WL_ADMIN_PORT="${WL_ADMIN_PORT}"  \
    WL_ADMIN_HOST="${WL_ADMIN_HOST}"  \
    WL_CLUSTER_NAME="${WL_CLUSTER_NAME}" \
    ENABLE_JACOCO="${ENABLE_JACOCO}" \
    # set path variable for script execution
    PATH=$PATH:/u01/oracle/oracle_common/common/bin:/u01/oracle/wlserver/common/bin:${DOMAIN_HOME}:${DOMAIN_HOME}/bin:/u01/oracle


# Add files required to build this image
COPY container-scripts/* ${ORACLE_HOME}/
COPY container-scripts/init-scripts ${ORACLE_HOME}/init-scripts
COPY artefacts/smp.war ${ORACLE_HOME}/
COPY properties/docker-build/domain*.properties ${PROPERTIES_FILE_DIR}/
#Create directory where domain will be written to
USER root
RUN chmod +xw ${ORACLE_HOME}/*.sh && \
    chmod +xw ${ORACLE_HOME}/init-scripts/*.sh && \
    chmod +xw ${ORACLE_HOME}/init-scripts/*.py && \
    mkdir -p ${PROPERTIES_FILE_DIR} && \
    chown -R oracle:oracle ${PROPERTIES_FILE_DIR} && \
    mkdir -p $DOMAIN_HOME && \
    chown -R oracle:oracle $DOMAIN_HOME && \
    chmod -R a+xwr $DOMAIN_HOME


# Configuration of WLS Domain
#RUN /u01/oracle/createWLSDomain.sh && \
#     echo ". $DOMAIN_HOME/bin/setDomainEnv.sh" >> /u01/oracle/.bashrc && \
#     chmod -R a+x $DOMAIN_HOME/bin/*.sh  && \
#     rm ${PROPERTIES_FILE_DIR}/*.properties

# Expose ports for admin, managed server, and debug
EXPOSE $ADMIN_PORT $CUSTOM_ADMIN_HTTPS_PORT $MANAGED_SERVER_PORT $DEBUG_PORT

WORKDIR $DOMAIN_HOME



#RUN /u01/oracle/deploySMPToDomain.sh && \
#     wlst -loadProperties /u01/oracle/datasource.properties.oracle /u01/oracle/ds-deploy.py \
     # set enforce-valid-basic-auth-credentials false to allow basic authentication for rest services
#     && sed -i -e "s/<\/security-configuration>/  <enforce-valid-basic-auth-credentials>false<\/enforce-valid-basic-auth-credentials>\n<\/security-configuration>/g" "/u01/oracle/user_projects/domains/domain1/config/config.xml"
    


# Define default command to start bash.
CMD ["startManagedServer.sh"]
