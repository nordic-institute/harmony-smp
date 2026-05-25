FROM ubuntu:22.04
MAINTAINER Joze Rihtarsic

ARG SMP_VERSION
ARG SMP_LOGICAL_ADDRESS="${SMP_LOGICAL_ADDRESS:-http://localhost:8080/smp/}"

# Set the SMP_VERSION env variable  \
ENV SMP_HOME=/opt/smp  \
    MYSQL_DRV_VERSION=9.3.0  \
    MYSQL_DRV_SHA1=6dbc33cd9534ec2715a8269fced1cd872630999d \
    TOMCAT_MAJOR=9  \
    TOMCAT_VERSION=9.0.113\
    TOMCAT_NATIVE_VERSION=1.3.1 \
    TOMCAT_TLS_KEY_ALIAS=eulogin.protected.smp.local  \
    TOMCAT_SHA512=1b8d9ba5c5e2ed2b4134a3fe6f206b3bb1184391e5c112ca7ea6a49ecadca63a7fc565c83caa610f0a8341988777870302a8162a84f0880af751531cdd4a2ee5 \
    JAVA_8_VERSION="jdk8u452-b09" \
    JAVA_8_URL_FILENAME="OpenJDK8U-jdk_x64_linux_hotspot_8u452b09.tar.gz" \
    JAVA_8_SHA256=9448308a21841960a591b47927cf2d44fdc4c0533a5f8111a4b243a6bafb5d27 \
    JAVA_11_VERSION="jdk-11.0.27+6" \
    JAVA_11_URL_FILENAME="OpenJDK11U-jdk_x64_linux_hotspot_11.0.27_6.tar.gz" \
    JAVA_11_SHA256=dc6136eaa8c1898cbf8973bb1e203e1f653f4c9166be0f5bebe0b02c5f3b5ae3 \
    SMP_DB_SCHEMA=smp  \
    SMP_DB_USER=smp \
    SMP_INIT_PROPERTY_DELIMITER="||"  \
    SMP_INIT_PROPERTIES=""   \
    SMP_DB_USER_PASSWORD=smp  \
    MYSQL_ROOT_PASSWORD=root \
# sml environment variables
    SML_VERSION=4.2 \
    SML_DISTRIBUTION_URL=https://ec.europa.eu/digital-building-blocks/artifact/repository/public/eu/europa/ec/bdmsl/bdmsl-webapp/ \
    SML_SHA512=b505958c87ce046c0b25f78fbb8555a22a3a636b3906fec77fd24341f649e9e01037083b2617827269ff3554ddbe5168cb4278f1e2d81172e444f6150e92f73e  \
    SML_SETUP_SHA512=e99ee7b8e193566964321ca126f2b20dfcc2ed904e5c2d0dc8253e49b727067e48420813bec3ed617d64724a66525c2aa17e502a130a2156d6cb81406c5d2bf9  \
    SML_DB_SCHEMA=sml  \
    SML_DB_USER=sml \
    SML_DB_USER_PASSWORD=sml  \
    BIND_USER=bind   \
# misc variables
    JACOCO_VERSION=0.8.8 \
    JACOCO_SHA512=bc9bbd10434900e7b48e68678cb280173db86489cdd320552089eb91bb470e7297d2f6f4f595907c35747c36cfa212f3fac71eb4005fce6c65451505e17d19a2 \
    LANG=en_US.utf8  \
    LD_LIBRARY_PATH=/usr/local/apr/lib \
   # set debug
    JPDA_ADDRESS="*:5005" \
    JPDA_TRANSPORT="dt_socket"

# set tomcat and java home environment variables
ENV TOMCAT_HOME=/opt/apache-tomcat-$TOMCAT_VERSION  \
    JAVA_HOME=/opt/java/${JAVA_11_VERSION}/

# Exposing ports used in entrypoint.sh ..
# - 3306 Mysql port
# - 6901 JaCoCo port
# - 8080 Tomcat port
# - 53 dns port
# - JDPA debug port
EXPOSE 3306 8080 8443 6901 53 5005

VOLUME ["/data"]

# install utils, java, mysql   \
RUN apt-get update -qqy \
    && apt-get upgrade -qqy \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        mysql-server \
        locales\
	    curl \
        ca-certificates \
        wget \
        unzip   \
        bind9 \
        bind9utils  \
        dnsutils \
        haveged \
        libapr1 \
	    libapr1-dev \
	    libssl-dev \
	    gcc \
	    make \
  && rm -rf /var/lib/apt/lists/*

ADD ./artefacts /tmp/artefacts

RUN mkdir -p /opt/java \
    && chmod u+x /tmp/artefacts/shared-artefacts/fetch_file.sh \
    && /tmp/artefacts/shared-artefacts/fetch_file.sh "${JAVA_11_URL_FILENAME}" "https://github.com/adoptium/temurin11-binaries/releases/download/${JAVA_11_VERSION}/${JAVA_11_URL_FILENAME}" "/opt/java/${JAVA_11_VERSION}.tar.gz" \
    && /tmp/artefacts/shared-artefacts/fetch_file.sh "${JAVA_11_URL_FILENAME}" "https://github.com/adoptium/temurin8-binaries/releases/download/${JAVA_8_VERSION}/${JAVA_8_URL_FILENAME}" "/opt/java/${JAVA_8_VERSION}.tar.gz" \
    && sha256sum /opt/java/${JAVA_11_VERSION}.tar.gz | grep ${JAVA_11_SHA256} \
    && sha256sum /opt/java/${JAVA_8_VERSION}.tar.gz | grep ${JAVA_8_SHA256} \
    && tar -xvf /opt/java/${JAVA_11_VERSION}.tar.gz -C /opt/java/ \
    && tar -xvf /opt/java/${JAVA_8_VERSION}.tar.gz -C /opt/java/

RUN   localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8 \
# set user 
    && addgroup mysql mysql   \
# download SMP, SMP setup, tomcat mysql driver
    && mkdir -p $SMP_HOME   \
    && cd $SMP_HOME \
    && curl -o mysql-connector-j.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/$MYSQL_DRV_VERSION/mysql-connector-j-$MYSQL_DRV_VERSION.jar \
    && sha1sum mysql-connector-j.jar  | grep $MYSQL_DRV_SHA1 \
    && /tmp/artefacts/shared-artefacts/fetch_file.sh "apache-tomcat-$TOMCAT_VERSION.tar.gz" "https://archive.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz"  "$SMP_HOME/tomcat.tar.gz" \
    && sha512sum tomcat.tar.gz | grep $TOMCAT_SHA512    \
    && tar -xvf tomcat.tar.gz  -C  $SMP_HOME \
    && mv mysql-connector-j.jar $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/lib/ \
    && rm tomcat.tar.gz   \
    && mkdir -p $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/  \
    && echo "export CLASSPATH=$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes" >   $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/bin/setenv.sh  \
# add sml and smp datasource 
    && sed -i -e "s/<\/Context>/<Resource name=\"jdbc\/eDeliverySmpDs\" auth=\"Container\" type=\"javax.sql.DataSource\" maxTotal=\"100\" maxIdle=\"30\" maxWaitMillis=\"10000\" username=\"$SMP_DB_USER\" password=\"$SMP_DB_USER_PASSWORD\" driverClassName=\"com.mysql.cj.jdbc.Driver\" url=\"jdbc:mysql:\/\/localhost:3306\/$SMP_DB_SCHEMA?characterEncoding=UTF-8\&amp;useUnicode=true\"\/>\\n<Resource name=\"jdbc\/eDeliverySmlDs\" auth=\"Container\" type=\"javax.sql.DataSource\" maxTotal=\"100\" maxIdle=\"30\" maxWaitMillis=\"10000\" username=\"$SML_DB_USER\" password=\"$SML_DB_USER_PASSWORD\" driverClassName=\"com.mysql.jdbc.Driver\" url=\"jdbc:mysql:\/\/localhost:3306\/$SML_DB_SCHEMA?characterEncoding=UTF-8\&amp;useUnicode=true\"\/><\/Context>/g" "$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/conf/context.xml"  \
    && sed -i -e "s/<Connector /<Connector URIEncoding=\"UTF-8\" /g" "$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/conf/server.xml"
# add SMP init configuration
# echo "smp.datasource.jndi=java:comp/env/jdbc/eDeliverySmpDs" >  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
RUN  echo "smp.jdbc.driver=com.mysql.cj.jdbc.Driver" >  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
  && echo "smp.jdbc.url=jdbc:mysql://localhost:3306/$SMP_DB_SCHEMA?useSSL=false&characterEncoding=UTF-8&useUnicode=true&allowPublicKeyRetrieval=true" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
     && echo "smp.database.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
     && echo "smp.jdbc.user=$SMP_DB_USER" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
     && echo "smp.jdbc.password=$SMP_DB_USER_PASSWORD" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
   # add SML init configuration
   && echo "sml.datasource.jndi=java:comp/env/jdbc/eDeliverySmlDs" >  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/sml.config.properties  \
   && echo "sml.hibernate.dialect=org.hibernate.dialect.MySQLDialect" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/sml.config.properties  \
   && echo "sml.jsp.servlet.class=org.apache.jasper.servlet.JspServlet" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/sml.config.properties  \
# mysql configuration
   && sed -i -e "s/127.0.0.1/0.0.0.0/g" /etc/mysql/mysql.conf.d/mysqld.cnf   \
# image is also used for code coverage report
    && mkdir /opt/jacoco  \
    && curl -o /opt/jacoco/jacoco-agent.jar https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/$JACOCO_VERSION/org.jacoco.agent-$JACOCO_VERSION-runtime.jar \
    && mkdir /opt/smlconf \
    && mkdir /opt/smlconf/bind 
# enable native TLS on tomcat  
    
RUN cd $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/bin \
    && tar xfz tomcat-native.tar.gz \
    && cd tomcat-native-${TOMCAT_NATIVE_VERSION}-src/native \
    && ./configure && make && make install 

# download and deploy sml
RUN  curl -o $SMP_HOME/edelivery-sml.war "$SML_DISTRIBUTION_URL/$SML_VERSION/bdmsl-webapp-$SML_VERSION.war"  \
    && sha512sum $SMP_HOME/edelivery-sml.war  | grep $SML_SHA512  \
    && curl -o /tmp/sml-setup.zip "$SML_DISTRIBUTION_URL/$SML_VERSION/bdmsl-webapp-$SML_VERSION-setup.zip"   \
    && sha512sum /tmp/sml-setup.zip  | grep $SML_SETUP_SHA512  \
    && mv $SMP_HOME/edelivery-sml.war $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/webapps/

ENV BIND_USER=bind

COPY ./entrypoint.sh /sbin/entrypoint.sh


COPY bind/db.test.edelivery.local /opt/smlconf/bind/db.test.edelivery.local
COPY bind/db.192 /opt/smlconf/bind/db.192
COPY bind/named.conf /opt/smlconf/bind/named.conf
COPY bind/named.conf.local /opt/smlconf/bind/named.conf.local


RUN unzip /tmp/artefacts/smp-setup.zip -d /tmp/    \
    && mv /tmp/smp-$SMP_VERSION /tmp/smp-setup \
    && mv /tmp/artefacts/smp.war $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/webapps/     \
    && unzip /tmp/sml-setup.zip -d /tmp/    \
    && mv /tmp/bdmsl-webapp-$SML_VERSION /tmp/sml-setup \
    && cp /tmp/sml-setup/encriptionPrivateKey.private  /opt/smlconf/encriptionPrivateKey.private \
    && cp /tmp/sml-setup/keystore.jks /opt/smlconf/keystore.jks \
    && mkdir $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/smp-libs \
    && chmod u+x /sbin/entrypoint.sh


HEALTHCHECK --interval=20s --timeout=10s --start-period=60s --retries=5 \
  CMD curl -f http://localhost:8080/smp/ || exit 1

ENTRYPOINT ["/sbin/entrypoint.sh"]


