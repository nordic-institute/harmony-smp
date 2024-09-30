package pages.administration.editResourcesPage;

import ddsl.dcomponents.AlertComponent;
import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import rest.models.ResourceModel;

public class CreateSubresourceDetailsDialog extends DComponent {


    @FindBy(id = "identifierValue_id")
    private WebElement subresourceIdentifierInput;
    @FindBy(id = "identifierScheme_id")
    private WebElement subresourceSchemeInput;
    @FindBy(id = "createButton")
    private WebElement createBtn;
    private AlertComponent alertComponent = null;

    public CreateSubresourceDetailsDialog(WebDriver driver) {
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
        weToDInput(subresourceIdentifierInput).fill(resourceModel.getIdentifierValue());
        weToDInput(subresourceSchemeInput).fill(resourceModel.getIdentifierScheme());
    }

    public Boolean tryClickOnSave() {
        wait.forElementToBeClickable(createBtn);
        if (weToDButton(createBtn).isEnabled()) {
            weToDButton(createBtn).click();
            return true;
        } else {
            return false;
        }
    }
}
