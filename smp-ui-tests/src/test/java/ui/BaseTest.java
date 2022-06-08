package ui;

import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Listeners({ExcelTestReporter.class, TestProgressReporter.class})
public class BaseTest {

	static int methodCount = 1;

	static WebDriver driver;
	protected Logger logger = Logger.getLogger(this.getClass());
	static TestDataProvider testDataProvider = new TestDataProvider();

	static ArrayList<String> createdDomains = new ArrayList<>();
	static ArrayList<String> createdUsers = new ArrayList<>();
	static ArrayList<String> createdServiceGroups = new ArrayList<>();

	@BeforeSuite(alwaysRun = true)
	/*Starts the browser and navigates to the homepage. This happens once before the test
	suite and the browser window is reused for all tests in suite*/
	public void beforeSuite() {
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
	public void afterSuite() {
		logger.info("Deleting created data!!!");

		deleteTestData();

		logger.info("Quitting!!!! Buh bye!!!");
		try {
			if(null!=driver){
				driver.quit();
			}
		} catch (Exception e) {
			logger.warn("Closing the driver failed !!!!");
			e.printStackTrace();
		}
	}

	@AfterClass(alwaysRun = true)
	public void afterClass() {
		if(null!=driver){
			driver.quit();
		}
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
	public void beforeClass() {
        logger.info("beforeClass entry");
		driver = DriverManager.getDriver();
		driver.get(PROPERTIES.UI_BASE_URL);
        logger.info("beforeClass exit");
	}

	@BeforeMethod(alwaysRun = true)
	protected void logSeparator(Method method) throws Exception {

		logger.info("--------------------------- Running test number: " + methodCount);
		logger.info("--------------------------- Running test method: " + method.getDeclaringClass().getSimpleName() + "." + method.getName());
		methodCount++;
	}


	private void createDomains() {
		for (int i = 0; i < 5; i++) {
			String generated = Generator.randomAlphaNumeric(10);
			logger.info("creating domain whose value is :"+generated);
			boolean created = SMPRestClient.createDomain(generated);
			if (created) {
				createdDomains.add(generated);
			} else {
				logger.warn("Test data creation: Domain creation failed for " + generated);
				System.exit(-1);
			}
		}
	}

	private void createUsers() {
		String[] roles = {"SMP_ADMIN", "SERVICE_GROUP_ADMIN", "SYSTEM_ADMIN"};
		for (int i = 0; i < 6; i++) {
			String generated = Generator.randomAlphaNumeric(10);
			String role = roles[i % roles.length];
			boolean created = SMPRestClient.createUser(generated, role);
			if (created) {
				createdUsers.add(generated);
			} else {
				logger.warn("Test data creation: User creation failed for " + generated);
				System.exit(-1);
			}
		}
	}

	private void createSGs() {
		for (int i = 0; i < 5; i++) {
			String generated = Generator.randomAlphaNumeric(10);
			String generatedHyphen = generated.substring(0,3)+"-"+generated.substring(3,6)+"-"+generated.substring(6,9);
			List<String> users = Arrays.asList(createdUsers.get(0));
			List<String> domains = Arrays.asList(createdDomains.get(0));
			boolean created = SMPRestClient.createServiceGroup(generated, generatedHyphen, users, domains);
			if (created) {
				createdServiceGroups.add(generated);
			} else {
				logger.warn("Test data creation: SG creation failed for " + generated);
				System.exit(-1);
			}
		}
	}

	private void deleteTestData() {
		for (String createdServiceGroup : createdServiceGroups) {
			try {
				SMPRestClient.deleteSG(createdServiceGroup);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (String createdUser : createdUsers) {
			try {
				SMPRestClient.deleteUser(createdUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (String createdDomain : createdDomains) {
			try {
				SMPRestClient.deleteDomain(createdDomain);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	protected void genericLogoutProcedure() {
		logger.info("executing the generic logout procedure");

		SMPPage page = new SMPPage(driver);
		page.refreshPage();

		try {
			if (page.pageHeader.sandwichMenu.isLoggedIn()) {
				logger.info("Logout!!");
				page.pageHeader.sandwichMenu.logout();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		driver.manage().deleteAllCookies();
		((JavascriptExecutor) driver).executeScript("localStorage.clear();");

		page.refreshPage();
		page.waitForXMillis(100);
	}

	protected SMPPage genericLoginProcedure(String role) {
		SMPPage page = new SMPPage(driver);

		genericLogoutProcedure();

		if (!page.pageHeader.sandwichMenu.isLoggedIn()) {
			logger.info("Login!!");
			page.pageHeader.goToLogin().login(role);
		}

		page.waitForRowsToLoad();
		return page;
	}

}

