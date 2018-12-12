package pages.login;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.SMPPage;
import pages.service_groups.search.SearchPage;
import utils.PROPERTIES;
import utils.TestDataProvider;

import java.util.HashMap;

public class LoginPage extends SMPPage {

	public LoginPage(WebDriver driver) {
		super(driver);
		
		log.info(".... init");
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}

	@FindBy(id = "username_id")
	private WebElement username;

	@FindBy(id = "password_id")
	private WebElement password;

	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(id = "loginbutton_id")
	private WebElement loginBtn;

	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(id = "okbuttondialog_id")
	private WebElement dialogOKBtn;

	@FindBy(className = "smpVersion")
	private WebElement smpVersion;

	public boolean isLoaded(){
		
		log.info("check if is loaded");
		
		if(!username.isEnabled()){
			log.error("Could not find username input!");
			return false;
		}
		if(!password.isEnabled()){
			log.error("Could not find password input!");
			return false;
		}
		if(!loginBtn.isDisplayed()){
			log.error("Could not find login button!");
			return false;
		}

		if(!smpVersion.isDisplayed()){
			log.error("Could not find version text!");
			return false;
		}
		log.info("Login page controls loaded!");
		return true;
	}

	public <T extends SMPPage> T login(String user, String pass, Class<T> expect){
		log.info("Login started!!");
		username.clear();
		username.sendKeys(user);
		password.clear();
		password.sendKeys(pass);
		loginBtn.click();

		closeChangePassModal();

		log.info("Login action done!");

		return PageFactory.initElements(driver, expect);
	}
	
	public SearchPage login(String user, String pass){
		log.info("Login started!!");

		clearAndFillInput(username, user);
		clearAndFillInput(password, pass);

		loginBtn.click();

		closeChangePassModal();
		log.info("Login action done!");

		return new SearchPage(driver);
	}

	public SearchPage login(String role){
		log.info("Login started!!");

		HashMap<String, String> user = new TestDataProvider().getUserWithRole(role);

		clearAndFillInput(username, user.get("username"));
		clearAndFillInput(password, user.get("password"));

		loginBtn.click();
		closeChangePassModal();
		log.info("Login action done!");

		return new SearchPage(driver);
	}


	public String getListedSMPVersion(){
		return waitForElementToBeVisible(smpVersion).getText().trim();
	}

	public String getTextInUsernameInput(){
		return waitForElementToBeVisible(username).getText().trim();
	}

	public String getTextInPasswordInput(){
		return waitForElementToBeVisible(password).getText().trim();
	}

	private void closeChangePassModal(){
		try{
			waitForElementToBeClickable(dialogOKBtn).click();
			waitForElementToBeGone(dialogOKBtn);
		}catch (Exception e){}
	}



}
