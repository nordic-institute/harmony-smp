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
#      $ docker build -t weblogic-sml .
# 
# Pull base image
# ---------------
FROM oracle/12213-domain-home-in-image

# Define variable
ARG SMP_VERSION
ENV APP_VERSION=$SMP_VERSION



# Copy files and deploy application in WLST Offline mode
COPY container-scripts/* /u01/oracle/
COPY smp.war /u01/oracle/

RUN /u01/oracle/deploySMPToDomain.sh && \
    wlst -loadProperties /u01/oracle/datasource.properties.oracle /u01/oracle/ds-deploy.py 
    
# Define default command to start bash.
CMD ["startAdminServer.sh"]
