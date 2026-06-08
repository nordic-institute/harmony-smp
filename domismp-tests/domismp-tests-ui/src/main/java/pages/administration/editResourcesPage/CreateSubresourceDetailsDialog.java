package pages.administration.editResourcesPage;

import ddsl.dcomponents.AlertComponent;
import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import rest.models.SubresourceModel;

public class CreateSubresourceDetailsDialog extends DComponent {

    @FindBy(id = "subresourceTypeIdentifier")
    private WebElement subresourceTypeDdl;
    @FindBy(id = "identifierValue_id")
    private WebElement subresourceIdentifierInput;
    @FindBy(id = "identifierScheme_id")
    private WebElement subresourceSchemeInput;
    @FindBy(css = "mat-dialog-actions button[id=\"createButton\"]")
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


    public void fillResourceDetails(SubresourceModel subresourceModel) {
        weToDInput(subresourceIdentifierInput).fill(subresourceModel.getIdentifierValue());
        weToDInput(subresourceSchemeInput).fill(subresourceModel.getIdentifierScheme());
    }

    public Boolean tryClickOnSave() {
        wait.forElementToBeClickable(createBtn);
        if (weToDButton(createBtn).isEnabled()) {
            weToDButton(createBtn).click();
            wait.forElementToBeGone(createBtn);
            return true;
        } else {
            return false;
        }
    }
}
