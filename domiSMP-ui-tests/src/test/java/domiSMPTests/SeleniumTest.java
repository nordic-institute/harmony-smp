package domiSMPTests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.annotations.*;
import utils.DriverManager;

import java.lang.reflect.Method;

public class SeleniumTest extends BaseTest {
    public static final Logger log = LoggerFactory.getLogger(SeleniumTest.class);
    static int methodCount = 1;
    public String logFilename;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {

        log.info("Log file name is " + logFilename);
        log.info("-------- Starting -------");
    }


    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        log.info("--------Initialize test class-------");
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

        log.info("--------------------------- Running test number: " + methodCount);
        log.info("--------------------------- Running test method: " + method.getDeclaringClass().getSimpleName() + "." + method.getName());
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

        log.info("-------- Quitting driver after test class-------");
        try {
            driver.quit();
        } catch (Exception e) {
            log.warn("Closing the driver failed");
            log.error("EXCEPTION: ", e);
        }
    }

}
