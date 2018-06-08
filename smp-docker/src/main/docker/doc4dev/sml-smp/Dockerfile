FROM centos

MAINTAINER jrc


ENV SML_VERSION=3.1.2 \
    SML_DISTRIBUTION_URL=https://ec.europa.eu/cefdigital/artifact/content/repositories/eDelivery/eu/europa/ec/bdmsl/bdmsl-webapp \
    SML_SHA1=2000b1db9739e04559b7bf47faffed9abded137e  \
    SMP_VERSION=4.0.0  \
    SMP_DISTRIBUTION_URL=https://ec.europa.eu/cefdigital/artifact/content/repositories/eDelivery/eu/europa/ec/edelivery/smp  \
    SMP_SHA1=52ba09257948233e920638f7a90f9bfa120910ca  \
    SMP_CONF_SHA1=af40537d2874775f2ab34c538682ef7ac50efdb3  \
    TOMCAT_MAJOR=8  \
    TOMCAT_VERSION=8.5.31 \
    TOMCAT_SHA512=51d8877782bc975b8c566263df7e55f383e617aa3c81ea2c219fed18e1f5d9e8233a92100de95b9a8df69ce5c0ad89a195d5b7e5647fcf9df26231870073a9cb \
    MYSQL_DRV_VERSION=5.1.46  \
    MYSQL_DRV_SHA1=9a3e63b387e376364211e96827bc27db8d7a92e9 \
    DATA_DIR=/data  \
    USER_BIND=named  \
    USER_TOMCAT=tomcat  \
    DB_SML_SCHEMA=bdmsl \
    DB_SML_USER=sml \
    DB_SML_PASSWORD=sml \
    DB_SMP_SCHEMA=smp \
    DB_SMP_USER=smp \
    DB_SMP_PASSWORD=smp \
    MYSQL_ROOT_PASSWORD=root 

ENV TOMCAT_HOME=/opt/apache-tomcat-$TOMCAT_VERSION  


# add mysql repo to install mysql instead mariadb
RUN rpm -Uvh http://dev.mysql.com/get/mysql57-community-release-el7-7.noarch.rpm  \
#  	&& yum update -y \
	&& yum install -y wget \
                which \
		unzip \
# MySQL server and client will be used by Domibus to Create MySQL users/schemas
		mysql-community-server \
# add bind server
		bind \
        	bind-utils \
		java-1.8.0-openjdk-devel \ 
# clean yum to decrease image size
	&& yum clean all \
# get tomcat, mysql driver, bdmsl 
   && cd /opt \
   && wget -O /opt/tomcat.zip "https://archive.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.zip" \
   && sha512sum /opt/tomcat.zip | grep $TOMCAT_SHA512 \
   && wget -O mysql-connector-java-$MYSQL_DRV_VERSION.jar https://repo1.maven.org/maven2/mysql/mysql-connector-java/$MYSQL_DRV_VERSION/mysql-connector-java-$MYSQL_DRV_VERSION.jar \
   && sha1sum mysql-connector-java-$MYSQL_DRV_VERSION.jar  | grep $MYSQL_DRV_SHA1 \
   && wget -O "edelivery-sml.war" "$SML_DISTRIBUTION_URL/$SML_VERSION/bdmsl-webapp-$SML_VERSION-tomcat-mysql.war"  \
   && sha1sum edelivery-sml.war  | grep $SML_SHA1 \
   && wget -O smp.war "$SMP_DISTRIBUTION_URL/$SMP_VERSION/smp-$SMP_VERSION.war"   \
   && sha1sum smp.war | grep $SMP_SHA1   \
   && wget -O smp-setup.zip "$SMP_DISTRIBUTION_URL/$SMP_VERSION/smp-$SMP_VERSION-setup.zip"    \
   && sha1sum smp-setup.zip | grep $SMP_CONF_SHA1    \
   && unzip tomcat.zip  \
   && rm -rf $TOMCAT_HOME/webapps/* \
   && mv /opt/mysql-connector-java-$MYSQL_DRV_VERSION.jar $TOMCAT_HOME/lib/ \
   && mv /opt/edelivery-sml.war $TOMCAT_HOME/webapps/edelivery-sml.war \
   && mv /opt/smp.war $TOMCAT_HOME/webapps/smp.war \
   && unzip smp-setup.zip  \
   && rm *.zip  \
# create user
   && groupadd "$USER_TOMCAT" \
   && useradd -s /sbin/nologin -g $USER_TOMCAT -M $USER_TOMCAT  \
   && cd $TOMCAT_HOME \
# install SML, set database param. and init data
   && mkdir -p $TOMCAT_HOME/sml/conf/  \
   && sed -i -e "s/<\/Context>/<Resource name=\"jdbc\/edelivery\" auth=\"Container\" type=\"javax.sql.DataSource\" maxTotal=\"100\" maxIdle=\"30\" maxWaitMillis=\"10000\" username=\"$DB_SML_USER\" password=\"$DB_SML_PASSWORD\" driverClassName=\"com.mysql.jdbc.Driver\" url=\"jdbc:mysql:\/\/localhost:3306\/$DB_SML_SCHEMA\"\/><\/Context>/g" "$TOMCAT_HOME/conf/context.xml"  \
# install SMP, set database bugfix for version 3.1.2
   && mkdir -p $TOMCAT_HOME/smp/conf/  \
   && echo "export CLASSPATH=$TOMCAT_HOME/smp/conf" >   $TOMCAT_HOME/bin/setenv.sh  \
   && cp /opt/smp-$SMP_VERSION/smp.config.properties $TOMCAT_HOME/smp/conf/  \
   && cp /opt/smp-$SMP_VERSION/sample_signatures_keystore.jks  $TOMCAT_HOME/smp/conf/ \
   && sed -i -e "s#jdbc:mysql://localhost:3306/smp#jdbc:mysql://localhost:3306/$DB_SMP_SCHEMA#g" "$TOMCAT_HOME/smp/conf/smp.config.properties" \
   && sed -i -e "s#jdbc.user\s*=\s*smp#jdbc.user=$DB_SMP_USER#g" "$TOMCAT_HOME/smp/conf/smp.config.properties" \
   && sed -i -e "s#jdbc.password\s*=\s*secret123#jdbc.password=$DB_SMP_PASSWORD#g" "$TOMCAT_HOME/smp/conf/smp.config.properties" \
   && sed -i -e "s#/keystores/sample_signatures_keystore.jks#$TOMCAT_HOME/smp/conf/sample_signatures_keystore.jks#g" "$TOMCAT_HOME/smp/conf/smp.config.properties" \
# configure  SMP, SML integration
   && sed -i -e "s#bdmsl.integration.enabled=false#bdmsl.integration.enabled=true#g" "$TOMCAT_HOME/smp/conf/smp.config.properties" \
   && sed -i -e "s#bdmsl.integration.url=http://localhost:8080/manageparticipantidentifier#bdmsl.integration.url=http://localhost:8080/edelivery-sml/manageparticipantidentifier#g" "$TOMCAT_HOME/smp/conf/smp.config.properties" \
   && awk '/\[mysqld\]/ { print; print "lower_case_table_names = 1\nbind-address=0.0.0.0"; next }1' /etc/my.cnf >> /etc/my.cnf  \
# set tomcat user
   && chown -R $USER_TOMCAT:$USER_TOMCAT bin/ webapps/ work/ temp/ logs/ \
   && chmod ug+x bin/*.sh  \
# set bind
   && mkdir -p /var/named/data/log \
   && chown -R $USER_BIND:$USER_BIND /var/named \
   && mkdir -p /var/named/data/log  \
# set init mysqld
   && mysqld --initialize-insecure  --user=mysql \
   && chown -R mysql:mysql /var/lib/mysql   

# set init data for SML
COPY bdmsl/db.* /temp/
RUN mkdir -p $TOMCAT_HOME/webapps/WEB-INF/classes/liquibase/ \
   && cp /temp/db.* $TOMCAT_HOME/webapps/WEB-INF/classes/liquibase/ \
   && jar -uf  $TOMCAT_HOME/webapps/edelivery-sml.war -C $TOMCAT_HOME/webapps/ WEB-INF/classes/liquibase/db.changelog-master.xml \
   && jar -uf  $TOMCAT_HOME/webapps/edelivery-sml.war -C $TOMCAT_HOME/webapps/ WEB-INF/classes/liquibase/db.init-data-inserts.xml \
# bugfix for mysql: fixed in 3.1.3
   && jar -uf  $TOMCAT_HOME/webapps/edelivery-sml.war -C $TOMCAT_HOME/webapps/ WEB-INF/classes/liquibase/db.changelog-update-3.1.RC1.xml \
   && rm -rf $TOMCAT_HOME/webapps/WEB-INF 

COPY bind/named.conf  /etc/named.conf
COPY bind/named.conf.local  /etc/named.conf.local
COPY bind/zones  /var/named/zones

COPY bdmsl/keystore.jks  $TOMCAT_HOME/sml/conf/keystore.jks 
COPY bdmsl/encriptionPrivateKey.private  $TOMCAT_HOME/sml/conf/encriptionPrivateKey.private
COPY entrypoint.sh /sbin/entrypoint.sh 

RUN chmod 755 /sbin/entrypoint.sh

EXPOSE 53/udp 53/tcp 3306 8080 

ENTRYPOINT ["/sbin/entrypoint.sh"]

CMD ["bash"]




