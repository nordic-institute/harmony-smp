DomiSMP with sprinboot and mysql database.
================================

The Image is intended for internal testing of the DomiSMP snapshots builds. The images should not
be used in production environment.  
The image is shipped with jdk 8 and 11. By default the jdk 11 is used, but it can be changed by setting the environment
variable JDK_VERSION=8 to start the container with JAVA_HOME pointing to jdk 8.

Image contains SMP deployed on the spring Embedded Tomcat 9 server with the MySQL 8.

# How to build

To build an image with DomiSMP application first copy and rename arterfacts into folder:

    cp "${SMP_ARTEFACTS}/smp-springboot-${SMP_VERSION}-exec.jar" ./artefacts/smp-springboot.jar
    cp "${SMP_ARTEFACTS}/smp-${SMP_VERSION}-setup.zip" ./artefacts/smp-setup.zip

Then build image with command:

    r build -t smp_springboot_mysql --build-arg SMP_VERSION=5.1-SNAPSHOT .


# How to run

Tu run image execute command:

    docker run --name smp -p 8084:8080 -p 3304:3306 smp_springboot_mysql


In your browser, enter `https://localhost:8080/smp` .



# how to run image from edelivery nexus.

The edelivery nexus contains prebuild images for the testing. To start the Tomcat Mysql image
login to docker registry 'edelivery-docker.devops.tech.ec.europa.eu' and execute the following command. 

    docker run --name smp edelivery-docker.devops.tech.ec.europa.eu/smp_springboot_mysql:5.1-SNAPSHOT  -p 3306:3306 -p 8080:8080
