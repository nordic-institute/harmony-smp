package utils;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import java.util.concurrent.TimeUnit;

public class DriverManager {
	protected static Logger logger = Logger.getLogger(DriverManager.class);
//	public static WebDriver getDriver(){
//
//		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless");
//		options.addArguments("--window-size=1920x1080");
//		options.addArguments("--no-sandbox");
//
//		WebDriver driver = new ChromeDriver(options);
//		driver.manage().window().maximize();
//
//		return driver;
//    }

    public static WebDriver getDriver(){
		logger.info("getDriver entry");
		System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
		//System.setProperty("webdriver.gecko.driver", "D:\\Users\\monhaso\\data\\WorkSpace\\restBaseIssue\\smp\\smp-ui-tests\\geckodriver.exe");
//		System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"C:\\temp\\logs.txt");
		FirefoxOptions options = new FirefoxOptions();
		FirefoxProfile profile= new FirefoxProfile();
		profile.setPreference( "layout.css.devPixelsPerPx", "0.8" );
		profile.setAcceptUntrustedCertificates(true);
		profile.setAssumeUntrustedCertificateIssuer(true);
		options.setProfile(profile);
		options.setLogLevel(FirefoxDriverLogLevel.TRACE);
		//options.setCapability("marionette", true);
		logger.info("getDriver entry1");
//		WebDriverManager.firefoxdriver().setup();
		WebDriver driver = new FirefoxDriver(options);
		logger.info("getDriver entry2");
     //	WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(2, TimeUnit.SECONDS);

		driver.manage().window().maximize();
		logger.info("getDriver exit");
		return driver;
    }




}
