package pages.administration.editDomainsPage;

import ddsl.dcomponents.commonComponents.members.MembersComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class DomainMembersTab extends MembersComponent {

    public DomainMembersTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }
}

