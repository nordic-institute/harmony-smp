package pages.profilePage;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SuccesfullPasswordChangedPopup extends DComponent {

    /**
     * This class is used to map the succesfull password changed dialog.
     */
    @FindBy(css = "#mat-mdc-dialog-2 > div > div > app-information-dialog > div > div.panel")
    private WebElement message;
    @FindBy(id = "closebuttondialog_id")
    private WebElement closeBtn;

    public SuccesfullPasswordChangedPopup(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        wait.forElementToBeClickable(closeBtn);

    }

    public void closePopup() {
        try {
            weToDButton(closeBtn).click();
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
}
