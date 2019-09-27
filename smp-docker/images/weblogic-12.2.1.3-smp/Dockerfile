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
# Pull base image
# ---------------
FROM oracle/12213-domain-home-in-image

# Define variable
ARG SMP_VERSION
ENV APP_VERSION=$SMP_VERSION
ENV LC_ALL=en_US.UTF-8
ENV LANG="en_US.UTF-8"
ENV LANGUAGE="en_US"




# Copy files and deploy application in WLST Offline mode
COPY container-scripts/* /u01/oracle/
COPY smp.war /u01/oracle/

RUN /u01/oracle/deploySMPToDomain.sh && \
     wlst -loadProperties /u01/oracle/datasource.properties.oracle /u01/oracle/ds-deploy.py \
     # set enforce-valid-basic-auth-credentials false to allow basic authentication for rest services
     && sed -i -e "s/<\/security-configuration>/  <enforce-valid-basic-auth-credentials>false<\/enforce-valid-basic-auth-credentials>\n<\/security-configuration>/g" "/u01/oracle/user_projects/domains/domain1/config/config.xml"  
    


# Define default command to start bash.
CMD ["startAdminServer.sh"]
