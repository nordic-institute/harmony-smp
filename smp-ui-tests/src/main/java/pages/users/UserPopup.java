package pages.users;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.GenericSelect;
import pages.components.baseComponents.PageComponent;

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

	@FindBy(id = "password_id")
	WebElement passwordInput;

	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(id = "usernameconfirmation_id")
	WebElement confirmationInput;

	@FindBy(css = "mat-form-field.username> div > div.mat-form-field-flex > div > div")
	WebElement usernameValidationError;

	@FindBy(css = "mat-form-field.password > div > div.mat-form-field-flex > div > div")
	WebElement passValidationError;

	@FindBy(css = "mat-form-field.password-confirmation > div > div.mat-form-field-flex > div > div")
	WebElement passConfirmationValidationError;

	@FindBy(css = "mat-dialog-content > table > tbody > tr > td > button:nth-child(1)")
	WebElement okBtn;

	@FindBy(css = "mat-dialog-content > table > tbody > tr > td > button:nth-child(2)")
	WebElement cancelBtn;

	@FindBy(xpath = "//div[text()=\'Passwords do not match \']")
	WebElement passwordUnmatchingMsg;

	@FindBy(css = "#role_id")
	WebElement rolesSelectContainer;
	public GenericSelect rolesSelect;


	public boolean isOKButtonActive() {
		return isEnabled(okBtn);
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

	public void fillDetailsForm(String username, String pass, String confirmation) {
		clearAndFillInput(userNameInput, username);
		clearAndFillInput(passwordInput, pass);
		clearAndFillInput(confirmationInput, confirmation);
		emailInput.click();
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

	public String getPasswordUnmatchingMsg()
	{
		return passwordUnmatchingMsg.getText();
	}
}
