FROM ubuntu:18.04
MAINTAINER Joze Rihtarsic


# Set the SMP_VERSION env variable  \
ENV SMP_HOME=/opt/smp  \
    MYSQL_DRV_VERSION=5.1.46  \
    MYSQL_DRV_SHA1=9a3e63b387e376364211e96827bc27db8d7a92e9	  \
    TOMCAT_MAJOR=8    \
    TOMCAT_VERSION=8.5.31   \
    TOMCAT_SHA512=51d8877782bc975b8c566263df7e55f383e617aa3c81ea2c219fed18e1f5d9e8233a92100de95b9a8df69ce5c0ad89a195d5b7e5647fcf9df26231870073a9cb   \
    DB_SCHEMA=smp  \
    DB_USER=smp  \ 
    DB_USER_PASSWORD=smp  \
    MYSQL_ROOT_PASSWORD=root  

# install utils, java, mysql   \
RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        mysql-server \
        openjdk-8-jdk \
	curl \
        unzip   \
    && rm -rf /var/lib/apt/lists/*  \
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
    && mkdir -p $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/smp/conf/  \
    && echo "export CLASSPATH=$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/smp/conf" >   $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/bin/setenv.sh  \
    && sed -i -e "s/<\/Context>/<Resource name=\"jdbc\/eDeliverySmpDs\" auth=\"Container\" type=\"javax.sql.DataSource\" maxTotal=\"100\" maxIdle=\"30\" maxWaitMillis=\"10000\" username=\"$DB_USER\" password=\"$DB_USER\" driverClassName=\"com.mysql.jdbc.Driver\" url=\"jdbc:mysql:\/\/localhost:3306\/$DB_SCHEMA\"\/><\/Context>/g" "$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/conf/context.xml"  \
    && sed -i -e "s/<Connector /<Connector URIEncoding=\"UTF-8\" /g" "$SMP_HOME/apache-tomcat-$TOMCAT_VERSION/conf/server.xml"  \
    && echo "datasource.jndi=java:comp/env/jdbc/eDeliverySmpDs" >  $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/smp/conf/smp.config.properties  


# copy application to image 
ADD ./artefacts /tmp/artefacts


# install SMP
RUN unzip /tmp/artefacts/smp-setup.zip -d /tmp/    \
    && mv /tmp/artefacts/smp.war $SMP_HOME/apache-tomcat-$TOMCAT_VERSION/webapps/     
   
VOLUME ["/data"]

COPY ./entrypoint.sh /sbin/entrypoint.sh 
RUN chmod u+x /sbin/entrypoint.sh \
      && sed -i -e "s/127.0.0.1/0.0.0.0/g" /etc/mysql/mysql.conf.d/mysqld.cnf

EXPOSE 3306 8080

ENTRYPOINT ["/sbin/entrypoint.sh"]

