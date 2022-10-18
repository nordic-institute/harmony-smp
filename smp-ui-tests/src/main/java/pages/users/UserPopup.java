package pages.users;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.ConfirmationDialog;
import pages.components.GenericSelect;
import pages.components.baseComponents.PageComponent;

public class UserPopup extends PageComponent {
	public GenericSelect rolesSelect;
	@FindBy(id = "userDetailsToggle_id")
	WebElement userDetailsToggle;

	@FindBy(css = "#active_id > label > div > div")
	WebElement activeToggle;

	@FindBy(id = "username_id")
	WebElement userNameInput;

	@FindBy(id = "emailAddress_id")
	WebElement emailInput;

	@FindBy(id = "np_id")
	WebElement newPasswordInput;

	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(id = "cnp_id")
	WebElement confirmationInput;

	@FindBy(css = "input#cp_id")
	WebElement adminPassInput;

	@FindBy(css = "mat-form-field.username> div > div.mat-form-field-flex > div > div")
	WebElement usernameValidationError;

	@FindBy(css = "smp-password-change-dialog .password-panel mat-form-field:nth-child(2) .mat-form-field-subscript-wrapper mat-error")
	WebElement passValidationError;

	@FindBy(css = "mat-form-field.password-confirmation > div > div.mat-form-field-flex > div > div")
	WebElement passConfirmationValidationError;

	@FindBy(css = ".ng-trigger.ng-trigger-transitionMessages.ng-star-inserted > mat-error")
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

	@FindBy(css = "#role_id")
	WebElement rolesSelectContainer;

	@FindBy(xpath = "//span[text()='Regenerate access token']")
	WebElement regenarateAccessTokenBtn;

	@FindBy(css = "label > button.mat-focus-indicator.mat-flat-button.mat-button-base.mat-primary")
	WebElement importBtn;

	@FindBy(xpath = "//span[text()='Show details']")
	WebElement showDetailsBtn;

	@FindBy(xpath = "//span[text()='Clear']")
	WebElement clearBtn;

	@FindBy(css = ".has-error.ng-star-inserted")
	WebElement emailValidationError;


	public UserPopup(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
		rolesSelect = new GenericSelect(driver, rolesSelectContainer);
	}

	public boolean isAdminPasswordInputEnable() {
		boolean bool = adminPassInput.isEnabled();
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


	public boolean isOKButtonActive() {
		return isEnabled(okBtn);
	}

	public boolean isChangedPasswordActive() {
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
		clearAndFillInput(newPasswordInput, password);
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

	public void fillDetailsForm(String username, String email) {
		clearAndFillInput(userNameInput, username);
		clearAndFillInput(emailInput, email);
	}

	public void clickSetOrChangePassword() {
		log.info("click change password");
		waitForElementToBeClickable(changePassword);
		waitForXMillis(500);
		changePassword.click();
		waitForXMillis(500);
	}

	public void clickCloseAfterChangedPass() {
		log.info("click close after change password");
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		waitForElementToBeClickable(passChangedClose);
		log.info("ab");
		passChangedClose.click();
		log.info("bd");
		waitForElementToBeGone(passChangedClose);
		log.info("cd");
	}

	public ConfirmationDialog clickChangedPassword() {
		log.info("click changed password");
		waitForElementToBeClickable(changedPassword);
		waitForXMillis(500);
		changedPassword.click();
		waitForElementToBeGone(changedPassword);
		return new ConfirmationDialog(driver);
	}

	public boolean isPopupChangedPasswordEnabled() {
		return changedPassword.isEnabled();
	}

	public void setOrChangePassword(String adminPass, String newPass, String confirmPass) {

		clearAndFillInput(adminPassInput, adminPass);
		clearAndFillInput(newPasswordInput, newPass);
		clearAndFillInput(confirmationInput, confirmPass);
	}

	public void clickClosePasswordDialog() {
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

	public String userEmailValidationGetErrMsg() {
		try {
			waitForElementToBeVisible(emailValidationError);
			return emailValidationError.getText();
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
		return passMatchValidationError.getText();
	}

	public boolean isUsernameFieldEnabled() {
		return isEnabled(userNameInput);
	}

	public boolean isEmailFieldEnabled() {
		return isEnabled(emailInput);
	}

	public boolean isRoleSelectFieldEnabled() {
		return isEnabled(rolesSelectContainer);
	}

	public boolean isSetOrChangePassOptionBtnEnabled() {
		return isEnabled(changePassword);
	}

	public boolean isRegenerateAccesstokenBtnEnabled() {
		return isEnabled(regenarateAccessTokenBtn);
	}

	public boolean isImportButtonActive() {
		return isEnabled(importBtn);
	}

	public boolean isShowDetailsButtonActive() {
		return isEnabled(showDetailsBtn);
	}

	public boolean isClearButtonActive() {
		return isEnabled(clearBtn);
	}

}
