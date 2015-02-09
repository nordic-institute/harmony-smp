rem This script is designed for windows
rem It can be used to prepare a virtual box from the empty "edelivery virtual box template"

rem Instructions:
rem 1 - Download the file virtual-box-empty.ova at :
rem 2 - Start the virtual box
rem 3 - Run this script
rem 4 - Stop the virtual box and export it as an .OVA

rem Pre-requisites:
rem pscp.exe: http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html
rem plink.exe: http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html
rem Maven binaries must be in the PATH environment variable

SET VERSION=2.2.4-SNAPSHOT
SET DISTRIBUTION_DIR=../../../

rem Build the distribution zip with with the "receiver" profile
cd %DISTRIBUTION_DIR%
mvn clean install -Dtype=receiver -Xmx512m -XX:MaxPermSize=256m

rem sending the artifact to the virtual box
pscp -P 22 -pw adminuser target/cipa-edelivery-distribution-%VERSION%-tomcat-full.zip adminuser@192.168.10.11:cipa-edelivery-distribution.zip

rem extracting zip on the virtual box
echo unzip cipa-edelivery-distribution.zip -d ~/cipa-edelivery-distribution > script.tmp
echo chmod 755 -R ~/cipa-edelivery-distribution >> script.tmp
echo rm ~/cipa-edelivery-distribution.zip >> script.tmp
plink -ssh 192.168.10.11 -P 22 -l adminuser -pw adminuser -m script.tmp
del script.tmp