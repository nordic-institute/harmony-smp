package pages;

import ddsl.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.systemSettings.UsersPage;

import java.util.ArrayList;
import java.util.List;

public class ResetCredentialsPage extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(UsersPage.class);
    @FindBy(id = "reset_username_id")
    private WebElement usernameInput;
    @FindBy(id = "np_id")
    private WebElement newPasswordInput;
    @FindBy(id = "cnp_id")
    private WebElement confirmNewPasswordInput;
    @FindBy(id = "closeDialogButton")
    private WebElement canceBtn;
    @FindBy(id = "changeCurrentUserPasswordButton")
    private WebElement setNewPasswordBtn;
    @FindBy(css = ".smp-field-error")
    private List<WebElement> fieldsError;


    public ResetCredentialsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }


    public void fillChangePasswordFields(String username, String newPassword, String confirmNewPassword) {
        weToDInput(usernameInput).fill(username, true);
        weToDInput(newPasswordInput).fill(newPassword, true);
        weToDInput(confirmNewPasswordInput).fill(confirmNewPassword);
    }

    public void clickSetChangePasswordButton() {
        if (weToDButton(setNewPasswordBtn).isEnabled()) {
            weToDButton(setNewPasswordBtn).click();
        } else {
            LOG.error("Set/Change password button is disabled");
        }
    }

    public List<String> getFieldErrorMessage() {
        ArrayList<String> fieldErrors = new ArrayList<>();
        if (!fieldsError.isEmpty()) {
            fieldsError.forEach(error -> {
                fieldErrors.add(error.getText());
            });
        }
        return fieldErrors;
    }

}
