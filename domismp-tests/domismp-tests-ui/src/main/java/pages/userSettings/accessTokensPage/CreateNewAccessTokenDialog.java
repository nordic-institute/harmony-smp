package pages.userSettings.accessTokensPage;

import ddsl.dcomponents.DComponent;
import ddsl.dobjects.DButton;
import ddsl.dobjects.DInput;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateNewAccessTokenDialog extends DComponent {
    @FindBy(css = "mat-dialog-content mat-form-field input[formcontrolname=\"description\"]")
    private WebElement descriptionInput;
    @FindBy(css = "mat-dialog-content mat-form-field input[placeholder=\"Start date\"]")
    private WebElement startDateInput;
    @FindBy(css = "mat-dialog-content mat-form-field input[placeholder=\"End date\"]")
    private WebElement enddateInput;
    @FindBy(css = "mat-dialog-content mat-checkbox")
    private WebElement activeCheckbox;
    @FindBy(id = "generatedAccessTokenButton")
    private WebElement generateTokenBtn;
    @FindBy(id = "alertmessage_id")
    private WebElement accesstokenGeneratedMessage;
    @FindBy(id = "closeDialogButton")
    private WebElement closeBtn;


    public CreateNewAccessTokenDialog(WebDriver driver) {

        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public DInput getDescriptionInput() {
        return new DInput(driver, descriptionInput);
    }

    public DInput getStartDateInput() {
        return new DInput(driver, startDateInput);
    }

    public DInput getEndDateInput() {
        return new DInput(driver, enddateInput);
    }

    public DButton getCreateNewTokenBtn() {
        return new DButton(driver, generateTokenBtn);
    }

    public String getTokenIdAndCloseDialog() {
        String message = accesstokenGeneratedMessage.getText();
        String pattern = "ID: \"([^\"]+)\"";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(message);
        if (matcher.find()) {
            String tokenId = matcher.group(1);
            if (!tokenId.isEmpty()) {
                weToDButton(closeBtn).click();
                return tokenId;
            }
        }

        return null;
    }


}
