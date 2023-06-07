package domiSMPTests;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.annotations.*;
import rest.DomiSMPRestClient;
import utils.DriverManager;
import utils.TestRunData;

import java.lang.reflect.Method;

public class SeleniumTest {

    /**
     * This class is extending all the test classes to have access to the Base tests methods.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(SeleniumTest.class);
    static int methodCount = 1;
    public String logFilename;

    public static TestRunData data = new TestRunData();
    public static DomiSMPRestClient rest = new DomiSMPRestClient();
    public WebDriver driver;


    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {

        LOG.info("Log file name is " + logFilename);
        LOG.info("-------- Starting -------");
    }


    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        LOG.info("--------Initialize test class-------");
        driver = DriverManager.getDriver();

    }

    @BeforeMethod(alwaysRun = true)
    protected void beforeMethod(Method method) {

        MDC.put("logFileName", method.getDeclaringClass().getSimpleName());

        LOG.info("--------------------------- Running test number: " + methodCount);
        LOG.info("--------------------------- Running test method: " + method.getDeclaringClass().getSimpleName() + "." + method.getName());
        methodCount++;

        try {
            driver.get(data.getUiBaseUrl());
        } catch (Exception e) {
            driver = DriverManager.getDriver();
            driver.get(data.getUiBaseUrl());
        }
    }


    @AfterClass(alwaysRun = true)
    protected void afterClass() {

        LOG.info("-------- Quitting driver after test class-------");
        try {
            driver.quit();
        } catch (Exception e) {
            LOG.warn("Closing the driver failed");
            LOG.error("EXCEPTION: ", e);
        }
    }

}
