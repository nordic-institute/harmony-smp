FROM ubuntu:18.04
MAINTAINER Joze Rihtarsic

ARG SMP_VERSION
ARG SMP_LOGICAL_ADDRESS="${SMP_LOGICAL_ADDRESS:-http://localhost:8080/smp/}"

# Set the SMP_VERSION env variable  \
ENV SMP_HOME=/opt/smp  \
    MYSQL_DRV_VERSION=5.1.46  \
    MYSQL_DRV_SHA1=9a3e63b387e376364211e96827bc27db8d7a92e9	  \
    TOMCAT_MAJOR=8    \
    TOMCAT_VERSION=8.5.73   \
    #native version is dependant from tomcat version!
    TOMCAT_NAVITVE_VERSION=1.2.31  \
    TOMCAT_SHA512=bc39e4545c779326803bc2574592d72dfea1b56e4cb95d7ac5264c1b91f3da8b1b94f53c53390d1c8348ebe1dad2c10719cc2746b179e1085719d964aa60a779   \
    SMP_DB_SCHEMA=smp  \
    SMP_DB_USER=smp \
    SMP_INIT_PROPERTY_DELIMITER="||"  \
    SMP_INIT_PROPERTIES=""   \
    SMP_DB_USER_PASSWORD=smp  \
    MYSQL_ROOT_PASSWORD=root \
# sml environment variables
    SML_VERSION=4.2.RC1 \
    SML_DISTRIBUTION_URL=https://ec.europa.eu/digital-building-blocks/artifact/repository/public/eu/europa/ec/bdmsl/bdmsl-webapp/ \
    SML_SHA512=2330e6caf557fd6a6e8725eb339c26cb2d06f0ca768fd1766989f5dec7557e41375ef61b65cad5d87fa478f3c468272880ebe8521bb66e8e7dee9bb16d0a3d51  \
    SML_SETUP_SHA512=f9b7a9607f34f2d547acac13e7044df04fdf616b163f4cae8788f7b1eccd837c3db947458b4f55273d263f6af2e794c18d5216484cc8132e3cfd2dc176d9e1bf  \
    SML_DB_SCHEMA=sml  \
    SML_DB_USER=sml \
    SML_DB_USER_PASSWORD=sml  \
    BIND_USER=bind   \
# misc variables
   JACOCO_VERSION=0.8.4 \
   LANG=en_US.utf8  \
   LD_LIBRARY_PATH=/usr/local/apr/lib \
   # set debug
   JPDA_ADDRESS="5005" \
   JPDA_TRANSPORT="dt_socket"


# Exposing ports used in entrypoint.sh ..
# - 3306 Mysql port
# - 6901 JaCoCo port
# - 8080 Tomcat port
# - 53 dns port
# - JDPA debug port
EXPOSE 3306 8080 6901 53 5005



VOLUME ["/data"]

# install utils, java, mysql   \
RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        mysql-server \
        openjdk-8-jdk \
        locales\
	    curl \
        unzip   \
        bind9 \
        bind9utils  \
        dnsutils \
        libapr1 \
	    libapr1-dev \
	    libssl-dev \
	    gcc \
	    make \
    && rm -rf /var/lib/apt/lists/*  \
    && localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8 \
# set user 
    && addgroup mysql mysql   \
# download SMP, SMP setup, tomcat mysql driver
    && mkdir -p $SMP_HOME   \
    && cd $SMP_HOME    \
    && curl -O  https://repo1.maven.org/maven2/mysql/mysql-connector-java/$MYSQL_DRV_VERSION/mysql-connector-java-$MYSQL_DRV_VERSION.jar    \
    && sha1sum mysql-connector-java-$MYSQL_DRV_VERSION.jar  | grep $MYSQL_DRV_SHA1    \
    && curl -o tomcat.zip "https://archive.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.zip"    \
    && sha512sum tomcat.zip | grep $TOMCAT_SHA512    \
    && unzip tomcat.zip   \
    && mv mysql-connector-java-$MYSQL_DRV_VERSION.jar $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/lib/    \
    && rm tomcat.zip   \
    && mkdir -p $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/  \
    && echo "export CLASSPATH=$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes" >   $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/bin/setenv.sh  \
# add sml and smp datasource 
    && sed -i -e "s/<\/Context>/<Resource name=\"jdbc\/eDeliverySmpDs\" auth=\"Container\" type=\"javax.sql.DataSource\" maxTotal=\"100\" maxIdle=\"30\" maxWaitMillis=\"10000\" username=\"$SMP_DB_USER\" password=\"$SMP_DB_USER_PASSWORD\" driverClassName=\"com.mysql.jdbc.Driver\" url=\"jdbc:mysql:\/\/localhost:3306\/$SMP_DB_SCHEMA?useSSL=false\&amp;characterEncoding=UTF-8\&amp;useUnicode=true\"\/>\\n<Resource name=\"jdbc\/eDeliverySmlDs\" auth=\"Container\" type=\"javax.sql.DataSource\" maxTotal=\"100\" maxIdle=\"30\" maxWaitMillis=\"10000\" username=\"$SML_DB_USER\" password=\"$SML_DB_USER_PASSWORD\" driverClassName=\"com.mysql.jdbc.Driver\" url=\"jdbc:mysql:\/\/localhost:3306\/$SML_DB_SCHEMA?useSSL=false\&amp;characterEncoding=UTF-8\&amp;useUnicode=true\"\/><\/Context>/g" "$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/conf/context.xml"  \
    && sed -i -e "s/<Connector /<Connector URIEncoding=\"UTF-8\" /g" "$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/conf/server.xml"  \
# add SMP init configuration
    && echo "datasource.jndi=java:comp/env/jdbc/eDeliverySmpDs" >  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
    && echo "hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect" >>  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/classes/smp.config.properties   \
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
    
RUN export JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:/bin/javac::") \
    && cd $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/bin \
    && tar xfz tomcat-native.tar.gz \
    && cd tomcat-native-${TOMCAT_NAVITVE_VERSION}-src/native \
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


ENTRYPOINT ["/sbin/entrypoint.sh"]

