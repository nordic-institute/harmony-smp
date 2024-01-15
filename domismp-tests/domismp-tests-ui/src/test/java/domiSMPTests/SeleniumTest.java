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
import java.util.logging.Level;

public class SeleniumTest {

    /**
     * This class is extending all the test classes to have access to the Base tests methods.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(SeleniumTest.class);
    public static TestRunData data = TestRunData.getInstance();
    public static DomiSMPRestClient rest = new DomiSMPRestClient();
    static int methodCount = 1;
    public String logFilename;
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
        java.util.logging.Logger.getLogger("io.netty.util.NetUtil").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.asynchttpclient.netty.handler").setLevel(Level.OFF);


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
            LOG.warn("Driver is not initialized, try to reninitialize it");
            driver = DriverManager.getDriver();
            driver.get(data.getUiBaseUrl());
        }
    }

    @AfterMethod
    protected void afterMethod(Method method) {
        try {
            driver.quit();
        } catch (Exception e) {
            LOG.warn("Closing the driver failed");
            LOG.error("EXCEPTION: ", e);
        }
    }

    @AfterClass(alwaysRun = true)
    protected void afterClass() {
        if (driver == null) {
            return;
        }
        LOG.info("-------- Quitting driver after test class-------");
        try {
            driver.quit();
        } catch (Exception e) {
            LOG.warn("Closing the driver failed");
            LOG.error("EXCEPTION: ", e);
        }
    }

}
