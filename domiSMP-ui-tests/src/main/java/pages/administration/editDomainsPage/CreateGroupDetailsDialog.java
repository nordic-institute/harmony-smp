package pages.administration.editDomainsPage;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import rest.models.GroupModel;

public class CreateGroupDetailsDialog extends DComponent {
    @FindBy(id = "name_id")
    private WebElement groupNameLbl;
    @FindBy(id = "description_id")
    private WebElement groupDescriptionLbl;
    @FindBy(id = "group_id")
    private WebElement groupVisibilityDdl;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;
    public CreateGroupDetailsDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);

    }

    public void fillGroupDetails(GroupModel group) {
        groupNameLbl.sendKeys(group.getGroupName());
        groupDescriptionLbl.sendKeys(group.getGroupDescription());
        weToDSelect(groupVisibilityDdl).selectValue(group.getVisibility());
    }

    public Boolean tryClickOnSave() throws Exception {
        wait.forElementToBeClickable(saveBtn);
        if (weToDButton(saveBtn).isEnabled()) {
            weToDButton(saveBtn).click();
            return true;
        } else {
            return false;
        }
    }
}
