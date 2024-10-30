package pages.administration.editGroupsPage;

import ddsl.dcomponents.AlertComponent;
import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import rest.models.ResourceModel;

public class CreateResourceDetailsDialog extends DComponent {

    @FindBy(id = "resourceTypeIdentifier")
    private WebElement resourceTypeDdl;
    @FindBy(id = "identifierValue_id")
    private WebElement resourceIdentifierInput;
    @FindBy(id = "identifierScheme_id")
    private WebElement resourceSchemeInput;
    @FindBy(id = "visibility_id")
    private WebElement resourceVisibilityDdl;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;
    private AlertComponent alertComponent = null;

    public CreateResourceDetailsDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public AlertComponent getAlertArea() {
        if (alertComponent == null) {
            alertComponent = new AlertComponent(driver);
        }
        return alertComponent;
    }


    public void fillResourceDetails(ResourceModel resourceModel) {
        weToDSelect(resourceTypeDdl).selectValue(resourceModel.getResourceTypeIdentifier());
        weToDInput(resourceIdentifierInput).fill(resourceModel.getIdentifierValue());
        weToDInput(resourceSchemeInput).fill(resourceModel.getIdentifierScheme());

        weToDSelect(resourceVisibilityDdl).selectValue(resourceModel.getVisibility());
    }

    public Boolean tryClickOnSave() {
        try {
            wait.forElementToBeClickable(saveBtn);
            if (weToDButton(saveBtn).isEnabled()) {
                weToDButton(saveBtn).click();
                wait.forElementToBeGone(saveBtn);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
