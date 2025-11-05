FROM ubuntu:22.04
MAINTAINER Joze Rihtarsic

ARG SMP_VERSION

# Set the SMP_VERSION env variable  \
ENV SMP_HOME=/opt/smp  \
    SMP_VERSION=${SMP_VERSION}  \
    SMP_DB_SCHEMA=smp  \
    SMP_DB_USER=smp \
    SMP_INIT_PROPERTY_DELIMITER="||"  \
    SMP_INIT_PROPERTIES=""   \
    SMP_DB_USER_PASSWORD=smp  \
    MYSQL_ROOT_PASSWORD=root \
# misc variables
    LANG=en_US.utf8  \
    LD_LIBRARY_PATH=/usr/local/apr/lib \
   # set debug
    JPDA_ADDRESS="5005" \
    JPDA_TRANSPORT="dt_socket" \
    SMP_PORT=8084 \
    JAVA_RELEASE="temurin21-binaries" \
    JAVA_VERSION="jdk-21.0.7+6" \
    JAVA_URL_FILENAME="OpenJDK21U-jdk_x64_linux_hotspot_21.0.7_6.tar.gz" \
    JAVA_SHA256=974d3acef0b7193f541acb61b76e81670890551366625d4f6ca01b91ac152ce0

# set default java home environment variable
ENV JAVA_HOME=/opt/java/${JAVA_VERSION}/
# Exposing ports used in entrypoint.sh ..
# - 3306 Mysql port
# - 8080 springboot port
# - 5005 JDPA debug port
EXPOSE 3306 8084 5005


VOLUME ["/data"]

# install utils, java, mysql   \
RUN apt-get update -qqy \
    && apt-get upgrade -qqy \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        mysql-server \
        ca-certificates \
        locales\
        wget \
	    curl \
        unzip   \
        haveged \
    && rm -rf /var/lib/apt/lists/*
RUN  mkdir -p /opt/java/
RUN  wget -O /opt/java/${JAVA_VERSION}.tar.gz https://github.com/adoptium/${JAVA_RELEASE}/releases/download/${JAVA_VERSION}/${JAVA_URL_FILENAME}
RUN  sha256sum /opt/java/${JAVA_VERSION}.tar.gz | grep ${JAVA_SHA256}
RUN  tar -xvf /opt/java/${JAVA_VERSION}.tar.gz -C /opt/java/
RUN   localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8 \
# set user
    && addgroup mysql mysql   \
    && groupadd smp \
    && useradd -s /bin/false -g smp -d ${SMP_HOME} smp \
    && mkdir -p $SMP_HOME/logs   \
    && cd $SMP_HOME  \
    && echo "server.port=${SMP_PORT}" >  $SMP_HOME/application.properties

ADD ./artefacts /tmp/artefacts
COPY ./entrypoint.sh /sbin/entrypoint.sh

RUN unzip /tmp/artefacts/smp-setup.zip -d /tmp/    \
    && mv /tmp/smp-$SMP_VERSION /tmp/smp-setup \
    && mv /tmp/artefacts/smp-springboot-exec.jar $SMP_HOME/     \
    && chmod u+x /sbin/entrypoint.sh

HEALTHCHECK --interval=20s --timeout=10s --start-period=60s --retries=5 \
  CMD curl -f http://localhost:${SMP_PORT}/smp/ || exit 1

ENTRYPOINT ["/sbin/entrypoint.sh"]
