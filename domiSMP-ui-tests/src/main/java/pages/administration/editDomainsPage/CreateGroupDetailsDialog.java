package pages.administration.editDomainsPage;

import ddsl.dcomponents.AlertComponent;
import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import rest.models.GroupModel;
/**
 * Page object create new group dialog of Edit Groups page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class CreateGroupDetailsDialog extends DComponent {
    @FindBy(id = "name_id")
    private WebElement groupNameLbl;
    @FindBy(id = "description_id")
    private WebElement groupDescriptionLbl;
    @FindBy(id = "group_id")
    private WebElement groupVisibilityDdl;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;

    private AlertComponent alertComponent = null;

    public CreateGroupDetailsDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

//    public AlertComponent getAlertArea() {
//        return new AlertComponent(driver);
//    }


    public AlertComponent getAlertArea(){
        if (  alertComponent == null){
            alertComponent = new AlertComponent(driver);
                }
    return alertComponent;
        }


    public void fillGroupDetails(GroupModel group) {
        groupNameLbl.sendKeys(group.getGroupName());
        groupDescriptionLbl.sendKeys(group.getGroupDescription());
        weToDSelect(groupVisibilityDdl).selectValue(group.getVisibility());
    }

    public Boolean tryClickOnSave(){
        wait.forElementToBeClickable(saveBtn);
        if (weToDButton(saveBtn).isEnabled()) {
            weToDButton(saveBtn).click();
            return true;
        } else {
            return false;
        }
    }
}
