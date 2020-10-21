package ui;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import pages.components.baseComponents.SMPPage;
import utils.DriverManager;
import utils.Generator;
import utils.PROPERTIES;
import utils.TestDataProvider;
import utils.customReporter.ExcelTestReporter;
import utils.customReporter.TestProgressReporter;
import utils.rest.SMPRestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Listeners({ExcelTestReporter.class, TestProgressReporter.class})
public class BaseTest {

	static WebDriver driver;
	protected Logger logger = Logger.getLogger(this.getClass());
	static TestDataProvider testDataProvider = new TestDataProvider();

	static ArrayList<String> createdDomains = new ArrayList<>();
	static ArrayList<String> createdUsers = new ArrayList<>();
	static ArrayList<String> createdServiceGroups = new ArrayList<>();



	@BeforeSuite(alwaysRun = true)
	/*Starts the browser and navigates to the homepage. This happens once before the test
	suite and the browser window is reused for all tests in suite*/
	public void beforeSuite(){
		logger.info("Creating necessary data !!!!");
		createDomains();
		createUsers();
		createSGs();

//		logger.info("Starting this puppy!!!!");
//		driver = DriverManager.getDriver();
//		driver.get(PROPERTIES.UI_BASE_URL);
	}


	@AfterSuite(alwaysRun = true)
	/*After the test suite is done we close the browser*/
	public void afterSuite(){
		logger.info("Deleting created data!!!");

		deleteTestData();

		logger.info("Quitting!!!! Buh bye!!!");
		try {
			driver.quit();
		} catch (Exception e) {
			logger.warn("Closing the driver failed !!!!");
			e.printStackTrace();
		}
	}

	@AfterClass(alwaysRun = true)
	public void afterClass(){
		driver.quit();
//		driver.get(PROPERTIES.UI_BASE_URL);
//		SMPPage page = new SMPPage(driver);
//		page.refreshPage();
//
//		if(page.pageHeader.sandwichMenu.isLoggedIn()){
//			logger.info("Logout!!");
//			page.pageHeader.sandwichMenu.logout();
//		}
	}

	@BeforeClass(alwaysRun = true)
	public void beforeClass(){
		driver = DriverManager.getDriver();
		driver.get(PROPERTIES.UI_BASE_URL);
	}



	private void createDomains(){
		for (int i = 0; i < 5; i++) {
			String generated = Generator.randomAlphaNumeric(10);
			boolean created = SMPRestClient.createDomain(generated);
			if(created){
			createdDomains.add(generated);}
			else{
				logger.warn("Test data creation: Domain creation failed for " + generated);
			}
		}
	}

	private void createUsers(){
		String[] roles = {"SMP_ADMIN", "SERVICE_GROUP_ADMIN", "SYSTEM_ADMIN"};
		for (int i = 0; i < 6; i++) {
			String generated = Generator.randomAlphaNumeric(10);
			String role = roles[i%roles.length];
			boolean created = SMPRestClient.createUser(generated, role);
			if(created){
			createdUsers.add(generated);}
			else{
				logger.warn("Test data creation: User creation failed for " + generated);
			}
		}
	}

	private void createSGs(){
		for (int i = 0; i < 5; i++) {
			String generated = Generator.randomAlphaNumeric(10);
			List<String> users = Arrays.asList(createdUsers.get(0));
			List<String> domains = Arrays.asList(createdDomains.get(0));
			boolean created = SMPRestClient.createServiceGroup(generated, generated, users, domains);
			if(created){
				createdServiceGroups.add(generated);
			}
			else{
				logger.warn("Test data creation: SG creation failed for " + generated);
			}
		}
	}

	private void deleteTestData(){
		for (String createdServiceGroup : createdServiceGroups) {
			SMPRestClient.deleteSG(createdServiceGroup);
		}
		for (String createdUser : createdUsers) {
			SMPRestClient.deleteUser(createdUser);
		}
		for (String createdDomain : createdDomains) {
			SMPRestClient.deleteDomain(createdDomain);
		}
	}


}

