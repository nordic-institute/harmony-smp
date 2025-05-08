package ddsl.dcomponents;

import ddsl.DomiSMPPage;
import ddsl.dobjects.DButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 * Page object for the Set/change password dialog. This contains the locators of the page and the methods for the behaviour of the page
 */
public class SetChangePasswordDialog extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(SetChangePasswordDialog.class);
    @FindBy(css = ".smp-field-error")
    List<WebElement> fieldsError;
    @FindBy(id = "cp_id")
    private WebElement currentPasswordInput;
    @FindBy(id = "np_id")
    private WebElement newPasswordInput;
    @FindBy(id = "cnp_id")
    private WebElement confirmationPasswordInput;
    @FindBy(id = "changeCurrentUserPasswordButton")
    private WebElement setPasswordBtn;


    public SetChangePasswordDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public void fillChangePassword(String currentPassword, String newPassword) {

        LOG.info("Set new password");
        weToDInput(currentPasswordInput).fill(currentPassword);
        weToDInput(newPasswordInput).fill(newPassword, true);
        weToDInput(confirmationPasswordInput).fill(newPassword, true);
    }

    public void fillChangePassword(String currentPassword, String newPassword, String confirmationNewPassword) {

        LOG.info("Set new password");
        weToDInput(currentPasswordInput).fill(currentPassword);
        weToDInput(newPasswordInput).fill(newPassword, true);
        weToDInput(confirmationPasswordInput).fill(confirmationNewPassword, true);
    }

    public DomiSMPPage tryClickOnChangePassword() {
        //wait.forElementToBeClickable(setPasswordBtn);
        if (weToDButton(setPasswordBtn).isEnabled()) {
            weToDButton(setPasswordBtn).click();
            return new DomiSMPPage(driver);
        } else {
            return null;
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

    public DButton getSetPasswordBtn() {
        return weToDButton(setPasswordBtn);
    }
}

