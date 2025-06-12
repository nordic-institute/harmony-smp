FROM ubuntu:24.04

ARG SMP_VERSION
ARG SMP_LOGICAL_ADDRESS="${SMP_LOGICAL_ADDRESS:-http://localhost:8080/smp/}"

# Set the SMP_VERSION env variable  \
ENV SMP_HOME=/opt/smp \
    MYSQL_DRV_VERSION=8.0.32 \
    MYSQL_DRV_SHA1=41ec3f8cdaccf6c46a47d7cd628eeb59a926d9d4 \
    TOMCAT_MAJOR=10 \
    TOMCAT_VERSION=10.1.34 \
    TOMCAT_NATIVE_VERSION=2.0.8  \
    TOMCAT_SHA512=aa314a6a52c5e3ccd9bdab7645bc6c0a2986b79b9df46acbeff1021d0bed820f1acf8c199498ad3f74285e5b1376e26527f24a921be3c54660719fe8558cb380 \
    TOMCAT_TLS_KEY_ALIAS=eulogin.protected.smp.local  \
    DATABASE_TYPE=INTERNAL_MYSQL \
    MYSQL_DRV_VERSION=8.4.0  \
    MYSQL_DRV_SHA1=b1bc0f47bcad26ad5f9bceefb63fcb920d868fca \
    ORACLE_JDBC_NAME=ojdbc11 \
    ORACLE_JDBC_VERSION=23.7.0.25.01 \
    ORACLE_JDBC_SHA512=42517c67c966d6d907def1df2a53e41c1a896de95a40b772cb93e2d66bc7652eb72ebc345e78104aacf0a3f137e327416efb3328b84db6dad8299104f8df35be \
    JAVA_RELEASE="temurin21-binaries" \
    JAVA_VERSION="jdk-21.0.7+6" \
    JAVA_URL_FILENAME="OpenJDK21U-jdk_x64_linux_hotspot_21.0.7_6.tar.gz" \
    JAVA_SHA256=974d3acef0b7193f541acb61b76e81670890551366625d4f6ca01b91ac152ce0 \
    SMP_DB_SCHEMA=smp  \
    SMP_DB_USER=smp \
    DB_DRIVER=com.mysql.cj.jdbc.Driver \
    DB_DIALECT=org.hibernate.dialect.MySQLDialect \
    SMP_INIT_PROPERTY_DELIMITER="||"  \
    SMP_INIT_PROPERTIES=""   \
    SMP_DB_USER_PASSWORD=smp  \
    MYSQL_ROOT_PASSWORD=root \
# sml environment variables
    SML_VERSION=5.0 \
    SML_DISTRIBUTION_URL=https://ec.europa.eu/digital-building-blocks/artifact/repository/public/eu/europa/ec/bdmsl/bdmsl-webapp/ \
    SML_SHA512=929e749f56c02a1a94e4a45bc6ee765d89188c55c2801f8039099d4946d21dae70eb952e82873f2758e0e3cbaea379ca898c64aa10a4e65612a2526aca9975f9  \
    SML_SETUP_SHA512=ed41d44900e43f769d7f4ceeaf5b01532361baf811629771c28988435c06910550af3091bc67fde8f1ca68e287a68e8fdcc88a2ebff62655c31187ae4d6fcc37 \
    SML_DB_SCHEMA=sml  \
    SML_DB_USER=sml \
    SML_DB_USER_PASSWORD=sml  \
    BIND_USER=bind   \
# misc variables
    JACOCO_VERSION=0.8.13 \
    JACOCO_SHA512=f0a09d84bfa5f5c07bdee0ba506911d80922e9838e824a53bf9c64e643e7300f902db4b503339cfb4bd7b4151248ba83d268bdfa40fcd0a55a9a40f8c64c3683 \
    LANG=en_US.utf8  \
    LD_LIBRARY_PATH=/usr/local/apr/lib \
   # set debug
    JPDA_ADDRESS="*:5005" \
    JPDA_TRANSPORT="dt_socket"

# set tomcat and java home environment variables
ENV TOMCAT_HOME=/opt/apache-tomcat-$TOMCAT_VERSION  \
    JAVA_HOME=/opt/java/${JAVA_VERSION}/

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
RUN mkdir -p /opt/java \
    && wget -O /opt/java/${JAVA_VERSION}.tar.gz https://github.com/adoptium/${JAVA_RELEASE}/releases/download/${JAVA_VERSION}/${JAVA_URL_FILENAME} \
    && sha256sum /opt/java/${JAVA_VERSION}.tar.gz | grep ${JAVA_SHA256} \
    && tar -xvf /opt/java/${JAVA_VERSION}.tar.gz -C /opt/java/

RUN   localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8 \
# set user # download SMP, SMP setup, tomcat mysql driver
    && mkdir -p $SMP_HOME   \
    && cd $SMP_HOME \
    && wget -O mysql-connector-j-$MYSQL_DRV_VERSION.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/${MYSQL_DRV_VERSION}/mysql-connector-j-${MYSQL_DRV_VERSION}.jar \
    && sha1sum mysql-connector-j-$MYSQL_DRV_VERSION.jar  | grep $MYSQL_DRV_SHA1 \
    && wget -O ${ORACLE_JDBC_NAME}-${ORACLE_JDBC_VERSION}.jar https://repo1.maven.org/maven2/com/oracle/database/jdbc/${ORACLE_JDBC_NAME}/${ORACLE_JDBC_VERSION}/${ORACLE_JDBC_NAME}-${ORACLE_JDBC_VERSION}.jar \
    && sha512sum ojdbc11-${ORACLE_JDBC_VERSION}.jar  | grep $ORACLE_JDBC_SHA512 \
    && curl -o tomcat.zip "https://archive.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.zip" \
    && sha512sum tomcat.zip | grep $TOMCAT_SHA512    \
    && unzip tomcat.zip \
    && mv mysql-connector-j-$MYSQL_DRV_VERSION.jar $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/lib/ \
    && rm tomcat.zip   \
    && mkdir -p $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/  \
    && echo "export CLASSPATH=$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes" >   $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/bin/setenv.sh  \
# add sml and smp datasource 
    && sed -i -e "s/<\/Context>/<Resource name=\"jdbc\/eDeliverySmpDs\" auth=\"Container\" type=\"javax.sql.DataSource\" maxTotal=\"100\" maxIdle=\"30\" maxWaitMillis=\"10000\" username=\"$SMP_DB_USER\" password=\"$SMP_DB_USER_PASSWORD\" driverClassName=\"${DB_DRIVER}\" url=\"jdbc:mysql:\/\/localhost:3306\/$SMP_DB_SCHEMA?characterEncoding=UTF-8\&amp;useUnicode=true\"\/>\\n<Resource name=\"jdbc\/eDeliverySmlDs\" auth=\"Container\" type=\"javax.sql.DataSource\" maxTotal=\"100\" maxIdle=\"30\" maxWaitMillis=\"10000\" username=\"$SML_DB_USER\" password=\"$SML_DB_USER_PASSWORD\" driverClassName=\"${DB_DRIVER}\" url=\"jdbc:mysql:\/\/localhost:3306\/$SML_DB_SCHEMA?characterEncoding=UTF-8\&amp;useUnicode=true\"\/><\/Context>/g" "$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/conf/context.xml"  \
    && sed -i -e "s/<Connector /<Connector URIEncoding=\"UTF-8\" /g" "$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/conf/server.xml"
# add SMP init configuration
# echo "smp.datasource.jndi=java:comp/env/jdbc/eDeliverySmpDs" >  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
RUN  echo "smp.jdbc.driver=${DB_DRIVER}" >  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
  && echo "smp.jdbc.url=jdbc:mysql://localhost:3306/$SMP_DB_SCHEMA?useSSL=false&characterEncoding=UTF-8&useUnicode=true&allowPublicKeyRetrieval=true" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
     && echo "smp.database.hibernate.dialect=${DB_DIALECT}" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
     && echo "smp.jdbc.user=$SMP_DB_USER" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
     && echo "smp.jdbc.password=$SMP_DB_USER_PASSWORD" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
   # add SML init configuration
   && echo "sml.datasource.jndi=java:comp/env/jdbc/eDeliverySmlDs" >  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/sml.config.properties  \
   && echo "sml.hibernate.dialect=${DB_DIALECT}" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/sml.config.properties  \
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
    
ADD ./artefacts /tmp/artefacts
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


