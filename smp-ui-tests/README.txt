This folder contains the Webdriver scripts that test the SMP 4.1.0 UI

Tests could not be written for any functionality that involves uploading files, 
encountered in Domain and Users page because the select file dialog is a system dialog and not accessible from the browser.

Also there are no tests for Audit functionality because this happens now only in database.

* Setup:

The tests are written in Java so Oracle Java 8 or above and Maven are required.
To drive the browser ChromeDriver or GekoDriver is required. This can be downloaded from the following locations:
ChromeDriver - http://chromedriver.storage.googleapis.com/index.html
GekoDriver - https://github.com/mozilla/geckodriver/releases

After unzipping the path to the executable need to be updated in the pom.xml file
under "/project/profiles/profile/build/plugins/plugin/configuration/systemPropertyVariables".

In the same set of variables please update the URL for the SMP home page.
Maven parameters
 - url - Sets the SMP url without ui context. Ex.: http://localhost:7001/smp
 - gecko.driver.path - set the path to gecko driver. Ex.:  /opt/drivers/geckodriver
 - chrome.driver.path - set the path to gecko driver. Ex.:  drivers/chromedriver.exe


* Run:

** Windows:
mvn clean test -P<profileName>

** Linux:
To run on Linux command line you need to install "Xvfb" and of course Firefox or Chromium
(There is a crash when starting Chromium so it is better to run using Firefox until the crash is resolved)

sudo xvfb-run --server-args="-screen 0 1920x1080x24" mvn test -P<profileName>
example
mvn clean install  -Pubuntu -Durl=http://localhost:7001/smp -Dgecko.driver.path=drivers/geckodriver


* Reports

Scripts provide multiple reports like JUnit and Surefire reports 
but also a custom Excel report in the ./target folder.

For any questions and complains please refer to the creator of these scripts - CATALIN COMANICI