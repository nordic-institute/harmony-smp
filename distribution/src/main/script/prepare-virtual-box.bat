rem This script is designed for windows
rem It can be used to prepare a virtual box from the empty "edelivery virtual box template"

rem Instructions:
rem 1 - Download the file virtual-box-empty.ova at : https://joinup.ec.europa.eu/nexus/service/local/repositories/releases/content/eu/europa/ec/cipa/virtualbox-template/1.0.0/virtualbox-template-1.0.0.ova
rem 2 - Start the virtual box
rem 3 - Run this script
rem 4 - Stop the virtual box and export it as an .OVA

rem Pre-requisites:
rem pscp.exe: http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html
rem plink.exe: http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html
rem Mysql and Maven binaries must be in the PATH environment variable

SET DISTRIBUTION_DIR=../../../
SET MAVEN_OPTS=-Xmx512m -XX:MaxPermSize=256m

rem clean up
call rd /s /q C:\feriaad\workspace\CIPA\distribution\target

rem Build the distribution zip with with the "receiver" profile
cd %DISTRIBUTION_DIR%
call mvn clean install -Dtype=receiver

rem Execute MySQL installation script
echo drop SCHEMA if exists edelivery; > mysql.tmp
echo create SCHEMA edelivery; >> mysql.tmp
mysql --host=192.168.56.11 --port=3306 --user=root --password=adminuser < mysql.tmp
del mysql.tmp
mysql --host=192.168.56.11 --port=3306 --user=edelivery --password=edelivery edelivery < target/sql-scripts/create-mysql.sql

rem sending the artifact to the virtual box
pscp -P 22 -pw adminuser target/cipa-edelivery-distribution-*.zip adminuser@192.168.56.11:

rem extracting zip on the virtual box
echo mv ~/*.zip ~/cipa-edelivery-distribution.zip > script.tmp
echo unzip cipa-edelivery-distribution.zip -d ~/cipa-edelivery-distribution >> script.tmp
echo chmod 755 -R ~/cipa-edelivery-distribution >> script.tmp
echo rm ~/cipa-edelivery-distribution.zip >> script.tmp
plink -ssh 192.168.56.11 -P 22 -l adminuser -pw adminuser -m script.tmp
del script.tmp

cd /d %~dp0