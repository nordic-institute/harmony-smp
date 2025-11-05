package pages.systemSettings.propertiesPage;

import ddsl.dcomponents.DComponent;
import ddsl.dobjects.DButton;
import ddsl.dobjects.DCheckbox;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Property popup component used when modifying a property.
 */
public class PropertyPopup extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(PropertiesPage.class);
    @FindBy(id = "updatePropertyButton")
    WebElement popupOkBtn;
    @FindBy(css = "mat-dialog-actions button:nth-of-type(2)")
    WebElement popupCancelBtn;
    @FindBy(css = "property-details-dialog mat-checkbox")
    WebElement propertyCheckbox;
    @FindBy(css = "property-details-dialog input")
    WebElement propertryEditInput;
    @FindBy(css = "property-details-dialog .alert-message-error")
    WebElement errorMessageLbl;
    @FindBy(css = "property-details-dialog mat-expansion-panel")
    WebElement propertyNameExpand;
    @FindBy(css = "property-details-dialog mat-expansion-panel label")
    WebElement propertyDescriptionLbl;



    public PropertyPopup(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);

    }

    public void clickOK(){
        LOG.info("click OK");
        wait.forElementToBeClickable(popupOkBtn);
        weToDButton(popupOkBtn).click();
        try {
            if (!errorMessageLbl.isDisplayed()) {
                wait.forElementToBeGone(popupOkBtn);
            }
        } catch (RuntimeException e) {
            LOG.debug("No error are present");
        }
    }
    public PropertiesPage clickCancel() {
        LOG.info("click cancel");
        wait.forElementToBeClickable(popupCancelBtn);
        popupCancelBtn.click();
        wait.forElementToBeGone(popupCancelBtn);
        return new PropertiesPage(driver);
    }

    public void editInputField(String string) {
        propertryEditInput.clear();
        propertryEditInput.sendKeys(string);
    }


    public DButton getPropertyNameExpandBtn() {
        return weToDButton(propertyNameExpand);
    }

    public DCheckbox getPropertyCheckbox() {
        return new DCheckbox(driver, propertyCheckbox);
    }

    public String getPropertyName() {
        return propertyNameExpand.getText();
    }

    public String getPropertyDescription() {
        return propertyDescriptionLbl.getText();
    }


    public String getErrorMessage() {
        if (!errorMessageLbl.isDisplayed()) {
            return null;
        }
        return errorMessageLbl.getText();
    }
}
