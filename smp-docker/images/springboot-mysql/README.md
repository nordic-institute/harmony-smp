BDMSL with sprin-boot and mysql database.
================================

The Images are intended for internal testing of the BDSML nightly snapshots builds. The images should not
be used in production environment.  

Image contains BDMSL deployed on the Tomcat 9 server with the MYSQL.

# How to build

To build an image with BDMSL application first copy and rename arterfacts into folder:

    cp "${SML_ARTEFACTS}/bdmsl-springboot-${BDMSL_VERSION}-exec.jar" ./springboot-mysql/artefacts/bdmsl-springboot.jar
    cp "${SML_ARTEFACTS}/bdmsl-webapp-${BDMSL_VERSION}-setup.zip" ./springboot-mysql/artefacts/bdmsl-webapp-setup.zip

Then build image with command:

    docker build -t bdmsl_springboot_mysql ./springboot-mysql/


# How to run

Tu run image execute command:

    docker run --name bdmsl -p 8084:8080 -p 3304:3306 bdmsl_springboot_mysql:4.2-SNAPSHOT


In your browser, enter `https://localhost:8080/edelivery-sml` .



# how to run image from edelivery nexus.

The edelivery nexus contains prebuild images for the testing. To start the Tomcat Mysql image
login to docker registry 'edelivery-docker.devops.tech.ec.europa.eu' and execute the following command. 

    docker run --name bdmsl-tomcat edelivery-docker.devops.tech.ec.europa.eu/bdmsl_springboot_mysql:4.2-SNAPSHOT  -p 3306:3306 -p 8080:8080