package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

public class DriverManager {


	public static WebDriver getDriver() {

		ChromeOptions options = new ChromeOptions();
		options.setHeadless(Boolean.valueOf(PROPERTIES.HEADLESS));
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--no-sandbox"); // Bypass OS security model

		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--window-size=1920,1080");


		WebDriver driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.manage().window().maximize();

		return driver;
	}


}
