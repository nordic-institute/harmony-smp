package pages.users;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.ConfirmationDialog;
import pages.components.GenericSelect;
import pages.components.baseComponents.PageComponent;

import static org.openqa.selenium.remote.DriverCommand.CLICK_ELEMENT;

public class UserPopup extends PageComponent {
	public UserPopup(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
		rolesSelect = new GenericSelect(driver, rolesSelectContainer);
	}


	@FindBy(id = "userDetailsToggle_id")
	WebElement userDetailsToggle;

	@FindBy(css = "#active_id > label > div > div")
	WebElement activeToggle;

	@FindBy(id = "username_id")
	WebElement userNameInput;

	@FindBy(id = "emailAddress_id")
	WebElement emailInput;

	@FindBy(id = "np_id")
	WebElement passwordInput;

	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(id = "cnp_id")
	WebElement confirmationInput;

	@FindBy(css = "input#cp_id")
	WebElement adminPassInput;

	@FindBy(css = "mat-form-field.username> div > div.mat-form-field-flex > div > div")
	WebElement usernameValidationError;

	@FindBy(css = "mat-form-field.password > div > div.mat-form-field-flex > div > div")
	WebElement passValidationError;

	@FindBy(css = "mat-form-field.password-confirmation > div > div.mat-form-field-flex > div > div")
	WebElement passConfirmationValidationError;

	@FindBy(css = ".mat-form-field-infix > div.has-error")
	WebElement passMatchValidationError;

	@FindBy(css = "mat-dialog-actions button:nth-child(1)")
	WebElement okBtn;

	@FindBy(css = "mat-dialog-actions button:nth-child(2)")
	WebElement cancelBtn;

	@FindBy(css ="#changePassword_id")
	WebElement changePassword;

	@FindBy(css ="smp-password-change-dialog mat-dialog-actions button:nth-child(1)")
	WebElement changedPassword;

	@FindBy(css = "#nobuttondialog_id")
	WebElement passChangedClose;

	@FindBy(css = "smp-password-change-dialog mat-dialog-actions button:nth-child(2)")
	WebElement passwordDialogClose;

	@FindBy(css = "#role_id")
	WebElement rolesSelectContainer;
	public GenericSelect rolesSelect;


	public boolean isOKButtonActive() {
		return isEnabled(okBtn);
	}
	public boolean isChangePasswordActive() {
		return isEnabled(passChangedClose);
	}
	public boolean isChangePasswordButtonActive() {
		return isEnabled(changedPassword);
	}

	public boolean isCancelButtonActive() {
		return isEnabled(cancelBtn);
	}

	public void fillData(String user, String email, String role, String password, String confirmation) {
		clearAndFillInput(userNameInput, user);
		clearAndFillInput(emailInput, email);
		clearAndFillInput(passwordInput, password);
		clearAndFillInput(confirmationInput, confirmation);

		GenericSelect rolesSelect = new GenericSelect(driver, rolesSelectContainer);
		rolesSelect.selectOptionByText(role);

	}

	public void clickOK() {
		log.info("click OK");
		waitForElementToBeClickable(okBtn);
		okBtn.click();
		waitForElementToBeGone(okBtn);
	}

	public void clickCancel() {
		log.info("click cancel");
		waitForElementToBeClickable(cancelBtn);
		cancelBtn.click();
		waitForElementToBeGone(cancelBtn);
	}


	public void clickUserDetailsToggle() {
		log.info("details toggle");
		waitForElementToBeClickable(userDetailsToggle).click();
		waitForElementToBeEnabled(userNameInput);
	}

	//public void fillDetailsForm(String username, String pass, String confirmation)
	public void fillDetailsForm(String username){
		clearAndFillInput(userNameInput, username);
		//clearAndFillInput(passwordInput, pass);
		//clearAndFillInput(confirmationInput, confirmation);
		emailInput.click();
	}
	public void clickChangePassword(){
		log.info("click change password");
		waitForElementToBeClickable(changePassword);
		changePassword.click();
		//waitForElementToBeGone(changePassword);
	}
	public void clickCloseAfterChangedPass()
	{
		log.info("click close after change password");
		try {
			Thread.sleep(10000);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		waitForElementToBeClickable(passChangedClose);
		log.info("ab");
		passChangedClose.click();
		log.info("bd");
		waitForElementToBeGone(passChangedClose);
		log.info("cd");
	}
	public ConfirmationDialog clickChangedPassword()
	{
		log.info("click changed password");
		waitForElementToBeClickable(changedPassword);
		changedPassword.click();
		waitForElementToBeGone(changedPassword);
		//return new UsersPage(driver);
		return new ConfirmationDialog(driver);
	}
	public void setPassword(String adminPass,String newPass,String confirmPass)
	{

		clearAndFillInput(adminPassInput,adminPass);
		clearAndFillInput(passwordInput,newPass);
		clearAndFillInput(confirmationInput,confirmPass);
	}
	public void clickClosePasswordDialog()
	{
		passwordDialogClose.click();
		waitForElementToBeGone(passwordDialogClose);
	}


	public String getUsernameValidationError() {
		try {
			waitForElementToBeVisible(usernameValidationError);
			return usernameValidationError.getText();
		} catch (Exception e) {
		}
		return null;
	}

	public String getPassValidationError() {
		try {
			waitForElementToBeVisible(passValidationError);
			return passValidationError.getText();
		} catch (Exception e) {
		}
		return null;
	}

	public String getConfirmationPassValidationError() {

		try {
			waitForElementToBeVisible(passConfirmationValidationError);
			return passConfirmationValidationError.getText();
		} catch (Exception e) {
		}
		return null;
	}

	public boolean isDuplicateUserNameErrorMsgDisPlayed() {
		try {
			return driver.findElement(By.cssSelector("mat-form-field.username > div .has-error")).isDisplayed();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getPassDontMatchValidationMsg() {
//		WebElement passwordUnmatchingMsg = driver.findElement(By.cssSelector(".mat-form-field-infix > div.has-error"));
		return passMatchValidationError.getText();
	}
}
