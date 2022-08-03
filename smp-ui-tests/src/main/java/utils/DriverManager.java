package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

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

    public static WebDriver getDriver() {
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);

		driver.manage().window().maximize();

        return driver;
    }


}
