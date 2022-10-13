package pages.password;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.PageComponent;
import pages.service_groups.search.SearchPage;
import utils.PROPERTIES;

public class PasswordChangepopup extends PageComponent {


	@FindBy(id = "username_id")
	WebElement userNameInput;
	@FindBy(id = "emailAddress_id")
	WebElement emailInput;
	@FindBy(id = "np_id")
	WebElement newPasswordInput;
	@FindBy(id = "cnp_id")
	WebElement confirmationInput;
	/* @FindBy(css = "input#cp_id")
	 WebElement adminPassInput;
*/
	@FindBy(xpath = "//input[@data-placeholder='Admin password for user [system]']")
	WebElement adminPassInput;
	@FindBy(xpath = "//input[@data-placeholder='Current password']")
	WebElement currentPassInput;
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
	@FindBy(css = "#changePassword_id")
	WebElement changePassword;
	@FindBy(css = "smp-password-change-dialog mat-dialog-actions button:nth-child(1)")
	WebElement changedPassword;
	@FindBy(css = "#closebuttondialog_id")
	WebElement passChangedClose;
	@FindBy(css = "smp-password-change-dialog mat-dialog-actions button:nth-child(2)")
	WebElement passwordDialogClose;

	public PasswordChangepopup(WebDriver driver) {
		super(driver);
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
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

	public void clickChangePassword() {
		log.info("click change password");
		waitForElementToBeClickable(changePassword);
		changePassword.click();
	}

	public SearchPage clickCloseAfterChangedPassForLoggedUser() {
		log.info("click close after change password");
		waitForElementToBeClickable(passChangedClose);
		passChangedClose.click();
		waitForElementToBeGone(passChangedClose);
		return new SearchPage(driver);
	}

	public void clickCloseAfterChangedPass() {
		log.info("click close after change password");

		waitForElementToBeClickable(passChangedClose);
		passChangedClose.click();
		waitForElementToBeGone(passChangedClose);

	}

	public void setOrChangePassword(String adminPass, String newPass, String confirmPass) {

		clearAndFillInput(adminPassInput, adminPass);
		clearAndFillInput(newPasswordInput, newPass);
		clearAndFillInput(confirmationInput, confirmPass);
	}

	public void fillDataForLoggedUser(String currentPass, String newPass, String confirmPass) {
		clearAndFillInput(currentPassInput, currentPass);
		clearAndFillInput(newPasswordInput, newPass);
		clearAndFillInput(confirmationInput, confirmPass);
	}

	public ConfirmationDialog clickChangedPassword() {
		log.info("click changed password");
		waitForElementToBeClickable(changedPassword);
		changedPassword.click();
		waitForElementToBeGone(changedPassword);
		return new ConfirmationDialog(driver);
	}

	public SearchPage clickClosePasswordDialog() {
		passwordDialogClose.click();
		waitForElementToBeGone(passwordDialogClose);
		return new SearchPage(driver);
	}

	public boolean isCurrentPasswordInputEnable() {
		boolean bool = currentPassInput.isEnabled();
		return bool;
	}

	public boolean isNewPasswordInputEnable() {
		boolean bool = newPasswordInput.isEnabled();
		return bool;
	}

	public boolean isConfirmPasswordInputEnable() {
		boolean bool = confirmationInput.isEnabled();
		return bool;
	}

}

