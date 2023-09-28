package ddsl.dcomponents.commonComponents.members;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InviteMembersPopup extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(InviteMembersPopup.class);
    @FindBy(id = "saveButton")
    public WebElement saveBtn;
    @FindBy(id = "member-user")
    private WebElement selectMemberBtn;
    @FindBy(css = "[formcontrolname=\"member-roleType\"]")
    private WebElement selectMemberROleDdl;

    public InviteMembersPopup(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public void selectMember(String username, String role) {
        LOG.debug("Inviting member {} with role {}", username, role);
        selectMemberBtn.sendKeys(username);
        selectMemberBtn.sendKeys(Keys.ARROW_DOWN);
        selectMemberBtn.sendKeys(Keys.ENTER);
        weToDSelect(selectMemberROleDdl).selectByVisibleText(role);
        saveBtn.click();
    }

    public void changeRole(String newRole) {

        if (!weToDSelect(selectMemberROleDdl).getCurrentValue().equals(newRole)) {
            weToDSelect(selectMemberROleDdl).selectByVisibleText(newRole);
            LOG.debug("Changing role to {}", newRole);
        }
        saveBtn.click();
    }
}
