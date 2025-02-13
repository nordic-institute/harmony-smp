package ddsl.dcomponents.commonComponents.domanPropertyEditDialog;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DomainPropertyEditDialog extends DComponent {

    @FindBy(css = "#mat-expansion-panel-header-1")
    private WebElement propertyNameExpand;
    @FindBy(css = "mat-card-content mat-checkbox:nth-of-type(1)")
    private WebElement useDefaultValueCheckBox;
    @FindBy(css = "mat-card-content mat-form-field div div input")
    private WebElement domainValueInput;
    @FindBy(css = "mat-card-content mat-checkbox:nth-of-type(2)")
    private WebElement domainValueCheckbox;
    @FindBy(id = "updatePropertyButton")
    private WebElement okBtn;
    @FindBy(css = ".mat-mdc-dialog-actions > button:nth-child(2)")
    private WebElement cancelBtn;

    public DomainPropertyEditDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        wait.forElementToBeVisible(cancelBtn);
    }

    public void setDomainValue(String domainValue) throws Exception {
        if (weToDChecked(useDefaultValueCheckBox).isChecked()) {
            weToDChecked(useDefaultValueCheckBox).uncheck();
        }
        if (weToDInput(domainValueInput).isEnabled()) {
            weToDInput(domainValueInput).fill(domainValue);
        }
    }

    public void setDomainValue(boolean isEnabled) throws Exception {
        if (weToDChecked(useDefaultValueCheckBox).isChecked()) {
            weToDChecked(useDefaultValueCheckBox).uncheck();
        }

        if (isEnabled) {
            if (!weToDChecked(domainValueCheckbox).isChecked()) {
                weToDChecked(domainValueCheckbox).check();
            }
        } else {
            if (weToDChecked(domainValueCheckbox).isChecked()) {
                weToDChecked(domainValueCheckbox).uncheck();
            }
        }
    }

    public void enableSystemValue() throws Exception {
        if (!weToDChecked(useDefaultValueCheckBox).isChecked()) {
            weToDChecked(useDefaultValueCheckBox).check();
        }
    }

    public void disableSystemValue() throws Exception {
        if (weToDChecked(useDefaultValueCheckBox).isChecked()) {
            weToDChecked(useDefaultValueCheckBox).uncheck();
        }
    }

    public void pressOk() {
        if (weToDButton(okBtn).isEnabled()) {
            weToDButton(okBtn).click();
        }
    }

}
