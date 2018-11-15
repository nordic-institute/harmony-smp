package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.logging.Level;

public class DriverManager {

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
		System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
		System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"C:\\temp\\logs.txt");



		WebDriver driver = new FirefoxDriver();
		driver.manage().window().maximize();

		return driver;
    }




}
