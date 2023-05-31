package domiSMPTests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.annotations.*;
import utils.DriverManager;

import java.lang.reflect.Method;

public class SeleniumTest extends BaseTest {
    protected static final Logger LOG = LoggerFactory.getLogger(SeleniumTest.class);
    static int methodCount = 1;
    public String logFilename;

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

//        if (!rest.isLoggedIn()) {
//            try {
//                rest.refreshCookies();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
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
        // new DomiSMPPage(driver).waitForPageToLoad();

        // login(data.getAdminUser());
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
