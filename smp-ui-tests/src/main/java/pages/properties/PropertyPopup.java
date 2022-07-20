package pages.properties;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.GenericSelect;
import pages.components.baseComponents.PageComponent;

public class PropertyPopup extends PageComponent {

        public PropertyPopup(WebDriver driver) {
            super(driver);
            PageFactory.initElements(driver, this);
        }

        @FindBy(css = "mat-dialog-actions button:nth-child(1)")
        WebElement popupOkBtn;

        @FindBy(css = "mat-dialog-actions button:nth-child(2)")
        WebElement popupCancelBtn;

        @FindBy(css = "span.mat-checkbox-inner-container input")
        WebElement propertyCheckbox;

        @FindBy(css = ".mat-input-element.mat-form-field-autofill-control[type='text']")
        WebElement propertryEditInput;

        public boolean isOKButtonActive() {
            return isEnabled(popupOkBtn);
        }

        public boolean isCancelButtonActive() {
            return isEnabled(popupCancelBtn);
        }
        public PropertiesPage clickOK() {
            log.info("click OK");
            waitForElementToBeClickable(popupOkBtn);
            popupOkBtn.click();
            waitForElementToBeGone(popupOkBtn);
            return new PropertiesPage(driver);
        }

        public PropertiesPage clickCancel() {
            log.info("click cancel");
            waitForElementToBeClickable(popupCancelBtn);
            popupCancelBtn.click();
            waitForElementToBeGone(popupCancelBtn);
            return new PropertiesPage(driver);
        }
       public PropertiesPage enableCheckboxOfProperty() {
           Boolean bool = propertyCheckbox.isSelected();

           if (bool == false) {
               propertyCheckbox.click();
               popupOkBtn.click();
           }
           else
           {
               popupCancelBtn.click();
           }
           return new PropertiesPage(driver);
       }
    public PropertiesPage disableCheckboxOfProperty(){
        Boolean bool = propertyCheckbox.isSelected();
        if(bool == true){
            JavascriptExecutor executor = (JavascriptExecutor)driver;
            executor.executeScript("arguments[0].click();", propertyCheckbox);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            popupOkBtn.click();
        }
        else
        {
            popupCancelBtn.click();
        }
        return new PropertiesPage(driver);

    }
    public void editInputField(String string)
    {
        propertryEditInput.sendKeys(string);
    }


    }
