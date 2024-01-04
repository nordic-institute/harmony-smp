FROM ubuntu:22.04
MAINTAINER Joze Rihtarsic

ARG SMP_VERSION

# Set the SMP_VERSION env variable  \
ENV SMP_HOME=/opt/smp  \
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
    SMP_PORT=8084

# Exposing ports used in entrypoint.sh ..
# - 3306 Mysql port
# - 8080 springboot port
# - 5005 JDPA debug port
EXPOSE 3306 8084 5005


VOLUME ["/data"]

# install utils, java, mysql   \
RUN apt-get -y update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        mysql-server \
        openjdk-8-jdk \
        locales\
	    curl \
        unzip   \
        haveged \
    && rm -rf /var/lib/apt/lists/*
RUN   localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8 \
# set user
    && addgroup mysql mysql   \
    && groupadd smp \
    && useradd -s /bin/false -g smp -d ${SMP_HOME} smp \
    && mkdir -p $SMP_HOME/logs   \
    && cd $SMP_HOME  \
    && export JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:/bin/javac::") \
    && echo "server.port=${SMP_PORT}" >  $SMP_HOME/application.properties

ADD ./artefacts /tmp/artefacts
COPY ./entrypoint.sh /sbin/entrypoint.sh

RUN unzip /tmp/artefacts/smp-setup.zip -d /tmp/    \
    && mv /tmp/smp-$SMP_VERSION /tmp/smp-setup \
    && mv /tmp/artefacts/smp-springboot-exec.jar $SMP_HOME/     \
    && chmod u+x /sbin/entrypoint.sh

HEALTHCHECK --interval=20s --timeout=5s \
  CMD curl -f http://localhost:${SMP_PORT}/smp/ || exit 1

ENTRYPOINT ["/sbin/entrypoint.sh"]
