package ddsl.dcomponents;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SetChangePasswordDialog extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(SetChangePasswordDialog.class);
    @FindBy(css = ".smp-field-error")
    List<WebElement> fieldsError;
    @FindBy(id = "cp_id")
    private WebElement currentPasswordInput;
    @SuppressWarnings("SpellCheckingInspection")
    @FindBy(id = "np_id")
    private WebElement newPasswordInput;
    @FindBy(id = "cnp_id")
    private WebElement confirmationPasswordInput;
    @FindBy(id = "cnp_id")
    private WebElement setPasswordBtn;
    @FindBy(id = "cnp_id")
    private WebElement closeBtn;


    public SetChangePasswordDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);
    }

    public boolean trySetPassword(String currentPassword, String newPassword) throws Exception {

        LOG.info("Set new password");
        weToDInput(currentPasswordInput).fill(currentPassword);
        weToDInput(newPasswordInput).fill(newPassword);
        weToDInput(confirmationPasswordInput).fill(newPassword);
        Integer hasError;
        if (weToDButton(setPasswordBtn).isEnabled() && fieldsError.size() < 1) {
            weToDButton(setPasswordBtn).click();
            return true;
        }
        {
            getFieldErrorMessage().forEach(LOG::error);
            return false;
        }

    }

    public List<String> getFieldErrorMessage() {
        ArrayList<String> fieldErrors = new ArrayList<>();
        if (fieldsError.size() > 0) {
            fieldsError.forEach(error -> {
                fieldErrors.add(error.getText());
            });
        }
        return fieldErrors;
    }
}

