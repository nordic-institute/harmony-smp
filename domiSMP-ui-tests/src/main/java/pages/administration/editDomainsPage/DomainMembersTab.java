package pages.administration.editDomainsPage;

import ddsl.dcomponents.commonComponents.members.MembersComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
/**
 * Page object Members tab of Edit Groups page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class DomainMembersTab extends MembersComponent {
    public DomainMembersTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }
}

