FROM ubuntu:22.04

MAINTAINER jrc

ENV BDMSL_VERSION=5.0-SNAPSHOT \
    DATA_DIR=/data  \
    JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/ \
    DB_SCHEMA=smp \
    DB_USER=smp \
    DB_USER_PASSWORD=smp \
    MYSQL_ROOT_PASSWORD=root 

ENV BDMSL_HOME=/opt/bdmsl

RUN apt-get update \
# get utils, bind9, java, mysql 
   && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        mysql-server \
        openjdk-8-jdk \
	    unzip \
        wget \
        curl \
        vim \
        haveged \
	    apache2-utils \
   && rm -rf /var/lib/apt/lists/* \
# create bdmsl user
   && mkdir "${BDMSL_HOME}" \
   && groupadd smp \
   && useradd -s /bin/false -g bdmsl -d ${BDMSL_HOME} smp \
   && cd /opt
#install 
RUN cd /opt \
   && mkdir -p /opt/smpconf \
   && echo "[mysqld]" >> /etc/mysql/my.cnf  \
   && echo "lower_case_table_names=1" >> /etc/mysql/my.cnf  \
   && echo "collation-server=utf8_unicode_ci" >> /etc/mysql/my.cnf  \
   && echo "init-connect='SET NAMES utf8'" >> /etc/mysql/my.cnf  \
   && echo "character-set-server=utf8" >> /etc/mysql/my.cnf  \
   && echo "default_authentication_plugin=mysql_native_password" >> /etc/mysql/my.cnf  \
   && sed -i -e "s/127.0.0.1/0.0.0.0/g" /etc/mysql/mysql.conf.d/mysqld.cnf


# deploy application
COPY entrypoint.sh /sbin/entrypoint.sh
COPY ./artefacts/smp-springboot-exec.jar ${BDMSL_HOME}/smp-springboot-exec.jar
COPY ./artefacts/smp-setup.zip /tmp/smp-setup.zip

# set configuration
RUN jar -xf /tmp/smp-setup.zip \
   && cp bdmsl-webapp-*/encriptionPrivateKey.private  /opt/smlconf/encriptionPrivateKey.private \
   && cp bdmsl-webapp-*/keystore.jks /opt/smlconf/keystore.jks \
   && cp bdmsl-webapp-*/truststore.p12 /opt/smlconf/truststore.p12 \
   && mv bdmsl-webapp-*/database-scripts /opt/smlconf/database-scripts \
   && mv bdmsl-webapp-*/sml.config.properties /tmp/sml.config.properties \
   && rm -rf bdmsl-webapp-*/ \
   && chown -R bdmsl:bdmsl /opt/smlconf/ \
   && chmod 755 /sbin/entrypoint.sh

EXPOSE  3306 8084

VOLUME /data

ENTRYPOINT ["/sbin/entrypoint.sh"]
