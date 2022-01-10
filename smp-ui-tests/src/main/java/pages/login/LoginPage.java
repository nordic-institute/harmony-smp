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
		
		log.info("check if Login page is loaded");
		
		if(!isEnabled(username)){
			log.error("Could not find username input!");
			return false;
		}
		if(!isEnabled(password)){
			log.error("Could not find password input!");
			return false;
		}
		if(!isVisible(loginBtn)){
			log.error("Could not find login button!");
			return false;
		}

		if(!isVisible(smpVersion)){
			log.error("Could not find version text!");
			return false;
		}
		log.info("Login page controls loaded!");
		return true;
	}

	public <T extends SMPPage> T login(String user, String pass, Class<T> expect){
		log.info("Login started!!");

		clearAndFillInput(username, user);
		clearAndFillInput(password, pass);

		waitForElementToBeClickable(loginBtn);
		loginBtn.click();

		closeChangePassModal();

		log.info("Login action done!");

		return PageFactory.initElements(driver, expect);
	}
	
	public SearchPage login(String user, String pass){
		log.info("Login started!!");

		clearAndFillInput(username, user);
		clearAndFillInput(password, pass);

		waitForElementToBeClickable(loginBtn);
		loginBtn.click();

		closeChangePassModal();
		log.info("Login action done!");

		return new SearchPage(driver);
	}

	public void invalidLogin(String user, String pass){
		log.info("Invalid login started!!");

		clearAndFillInput(username, user);
		clearAndFillInput(password, pass);

		waitForElementToBeClickable(loginBtn);
		loginBtn.click();
	}

	public SearchPage login(String role){
		log.info("Login started!!");

		HashMap<String, String> user = new TestDataProvider().getUserWithRole(role);

		clearAndFillInput(username, user.get("username"));
		clearAndFillInput(password, user.get("password"));

		waitForElementToBeClickable(loginBtn);
		loginBtn.click();

		closeChangePassModal();

		log.info("Login action done!");

		return new SearchPage(driver);
	}


	public String getListedSMPVersion(){
		log.info("getting listed version");
		return waitForElementToBeVisible(smpVersion).getText().trim();
	}

	public String getTextInUsernameInput(){
		log.info("getting text in username input");
		return waitForElementToBeVisible(username).getText().trim();
	}

	public String getTextInPasswordInput(){
		log.info("getting text in pass input");
		return waitForElementToBeVisible(password).getText().trim();
	}

	private void closeChangePassModal(){
		log.info("Closing Change password modal");
		try{
			waitForElementToBeClickable(dialogOKBtn).click();
			waitForElementToBeGone(dialogOKBtn);
		}catch (Exception e){}
	}

	public void loginWithoutUserAndPassword() {
		username.clear();
		password.clear();

	}

	public boolean isLoginButtonEnable() {
		try {
			return !loginBtn.isEnabled();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void fillLoginInput(String user, String pass) {

		clearAndFillInput(username, user);
		clearAndFillInput(password, pass);
	}

}
