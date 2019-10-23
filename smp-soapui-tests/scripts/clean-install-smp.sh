#!/bin/bash


SMP_VERSION=4.1.1
SMP_SHA256=9fdfeceef69978ab2436f79d075ef2b847563f6266ea09e7c7bb476e9e73c0b3
SMP_SETUP_SHA256=faede91a13ca8e464c8703b3074c7307058633eb5c969dd1a495bf17fdaa02e8


MYSQL_DRV_VERSION=5.1.46  
MYSQL_DRV_SHA1=9a3e63b387e376364211e96827bc27db8d7a92e9	  
TOMCAT_MAJOR=8    
TOMCAT_VERSION=8.5.31   
TOMCAT_SHA512=51d8877782bc975b8c566263df7e55f383e617aa3c81ea2c219fed18e1f5d9e8233a92100de95b9a8df69ce5c0ad89a195d5b7e5647fcf9df26231870073a9cb   
SMP_DB_SCHEMA=supportsmp  
SMP_DB_USER=smp 
SMP_DB_USER_PASSWORD=smp  
MYSQL_ROOT_PASSWORD=root 

ARTEFACTS_FOLDER=./artefacts

CWD="$(pwd)"


dowloadArtefacts() {
    if [ ! -d  $ARTEFACTS_FOLDER  ]; then
      mkdir -p $ARTEFACTS_FOLDER 
    fi
    
    # download the mysql connector    
    curl -o mysql-connector-java.jar   https://repo1.maven.org/maven2/mysql/mysql-connector-java/$MYSQL_DRV_VERSION/mysql-connector-java-$MYSQL_DRV_VERSION.jar
    
    if [[ $(sha1sum mysql-connector-java.jar | awk '{print $1}') !=  "$MYSQL_DRV_SHA1" ]]; then
        echo "error" "Checksum values do not match"
        echo "$(sha1sum  mysql-connector-java.jar)  -->  $MYSQL_DRV_SHA1"
        exit -1;
    fi
    mv  mysql-connector-java.jar $ARTEFACTS_FOLDER/mysql-connector-java.jar
    
    # download tomcat    
    curl -o tomcat.zip "https://archive.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.zip"
    
    if [[ $(sha512sum tomcat.zip | awk '{print $1}') !=  "$TOMCAT_SHA512" ]]; then
        echo "error" "Checksum values do not match"
        echo "$(sha512sum  tomcat.zip)  -->  $TOMCAT_SHA512"
        exit -1;
    fi
    mv  tomcat.zip $ARTEFACTS_FOLDER/tomcat.zip

    # download SMP    
    curl -o smp.war "https://ec.europa.eu/cefdigital/artifact/repository/public/eu/europa/ec/edelivery/smp/$SMP_VERSION/smp-$SMP_VERSION.war"

   if [[ $(sha256sum smp.war | awk '{print $1}') !=  "$SMP_SHA256" ]]; then
        echo "error" "Checksum values do not match"
        echo "$(sha256sum  smp.war)  -->  $SMP_SHA256"
        exit -1;
    fi
    mv  smp.war $ARTEFACTS_FOLDER/smp.war
    # download SMP setup
    curl -o smp-setup.zip "https://ec.europa.eu/cefdigital/artifact/repository/public/eu/europa/ec/edelivery/smp/$SMP_VERSION/smp-$SMP_VERSION-setup.zip"

   if [[ $(sha256sum smp-setup.zip | awk '{print $1}') !=  "$SMP_SETUP_SHA256" ]]; then
        echo "error" "Checksum values do not match"
        echo "$(sha256sum smp-setup.zip)  -->  $SMP_SETUP_SHA256"
        exit -1;
    fi
    mv  smp-setup.zip $ARTEFACTS_FOLDER/smp-setup.zip

}


deploySMP() {
    # clean deployment 
   
    if [  -d  "apache-tomcat-$TOMCAT_VERSION"  ]; then
      rm -rf  "apache-tomcat-$TOMCAT_VERSION"
    fi

    if [  -d  tomcat  ]; then
      rm -rf  tomcat
    fi
    
    if [  -d  "smp-$SMP_VERSION"  ]; then
      rm -rf  "smp-$SMP_VERSION"
    fi


    # unzip folders 
    unzip $ARTEFACTS_FOLDER/tomcat.zip 
    mv apache-tomcat-$TOMCAT_VERSION "./tomcat"

   # deploy SMP
   cp $ARTEFACTS_FOLDER/smp.war tomcat/webapps/smp.war

   # deploy mysql driver 
   cp $ARTEFACTS_FOLDER/mysql-connector-java.jar tomcat/lib/mysql-connector-java.jar
  
   # unzip configuration  
   unzip $ARTEFACTS_FOLDER/smp-setup.zip 
   # create classpath folder
   mkdir tomcat/classes
   # set enironment variable
   echo "export CLASSPATH=$CWD/tomcat/classes" >   $CWD/tomcat/bin/setenv.sh

   # set database connection
   sed -i -e "s/<\/Context>/<Resource name=\"jdbc\/eDeliverySmpDs\" auth=\"Container\" type=\"javax.sql.DataSource\" maxTotal=\"100\" maxIdle=\"30\" maxWaitMillis=\"10000\" username=\"$SMP_DB_USER\" password=\"$SMP_DB_USER_PASSWORD\" driverClassName=\"com.mysql.jdbc.Driver\" url=\"jdbc:mysql:\/\/localhost:3306\/$SMP_DB_SCHEMA?useSSL=false\&amp;characterEncoding=UTF-8\&amp;useUnicode=true\"\/><\/Context>/g" "$CWD/tomcat/conf/context.xml"  
    
   
   # set initial application properties
   echo "datasource.jndi=java:comp/env/jdbc/eDeliverySmpDs" >   $CWD/tomcat/classes/smp.config.properties 
   echo "hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect" >>  $CWD/tomcat/classes/smp.config.properties    
   # optional parameters 
  echo "" >>  $CWD/tomcat/classes/smp.config.properties  
   echo "log.folder=$CWD/tomcat/logs/" >>  $CWD/tomcat/classes/smp.config.properties  
   echo "smp.property.refresh.cronJobExpression=0 */1 * * * *" >>  $CWD/tomcat/classes/smp.config.properties  
   echo "authentication.blueCoat.enabled=true" >>  $CWD/tomcat/classes/smp.config.properties 
   echo "configuration.dir=$CWD/tomcat/smp/" >>  $CWD/tomcat/classes/smp.config.properties 
   
   

   # Example for using old keystores, truststore and encKey 
   #echo "configuration.dir=/cef/presentation/smp-init-test" >>  $CWD/tomcat/classes/smp.config.properties  
   #echo "smp.truststore.filename=smp-old-truststore.jks" >>  $CWD/tomcat/classes/smp.config.properties  
   #echo "smp.truststore.password={DEC}{test123}" >>  $CWD/tomcat/classes/smp.config.properties  
   #echo "encryption.key.filename=myEncKey.private" >>  $CWD/tomcat/classes/smp.config.properties  
      


   chmod u+x  $CWD/tomcat/bin/*.sh
    

}


initDatabaseSMP() {
    #recreate database
    mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD -e "drop schema if exists $SMP_DB_SCHEMA;DROP USER IF EXISTS $SMP_DB_USER;  create schema $SMP_DB_SCHEMA;alter database $SMP_DB_SCHEMA charset=utf8; create user $SMP_DB_USER identified by '$SMP_DB_USER_PASSWORD';grant all on $SMP_DB_SCHEMA.* to $SMP_DB_USER;"
    
    mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD $SMP_DB_SCHEMA < $CWD/smp-$SMP_VERSION/database-scripts/mysql5innodb.ddl

    mysql -h localhost -u root --password=$MYSQL_ROOT_PASSWORD $SMP_DB_SCHEMA < $CWD/smp-$SMP_VERSION/database-scripts/mysql5innodb-data.sql

}




#dowloadArtefacts
deploySMP
initDatabaseSMP
# start server
./tomcat/bin/catalina.sh run

