package ui;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import pages.components.baseComponents.SMPPage;
import utils.DriverManager;
import utils.PROPERTIES;
import utils.TestDataProvider;
import utils.customReporter.ExcelTestReporter;

import java.util.HashMap;

@Listeners(ExcelTestReporter.class)
public class BaseTest {

	static WebDriver driver;
	protected Logger logger = Logger.getLogger(this.getClass());
	static TestDataProvider testDataProvider = new TestDataProvider();

	@BeforeSuite(alwaysRun = true)
	/*Starts the browser and navigates to the homepage. This happens once before the test
	suite and the browser window is reused for all tests in suite*/
	public void beforeSuite(){
		logger.info("Starting this puppy!!!!");
		driver = DriverManager.getDriver();
		driver.get(PROPERTIES.UI_BASE_URL);
	}


	@AfterSuite(alwaysRun = true)
	/*After the test suite is done we close the browser*/
	public void afterSuite(){
		logger.info("Quitting!!!! Buh bye!!!");
		try {
			driver.quit();
		} catch (Exception e) {
			logger.warn("Closing the driver failed !!!!");
			e.printStackTrace();
		}
	}

	@AfterClass(alwaysRun = true)
	public void logoutAndReset(){
		driver.get(PROPERTIES.UI_BASE_URL);
		SMPPage page = new SMPPage(driver);
		page.refreshPage();

		if(page.pageHeader.sandwichMenu.isLoggedIn()){
			logger.info("Logout!!");
			page.pageHeader.sandwichMenu.logout();
		}
	}
	


}
