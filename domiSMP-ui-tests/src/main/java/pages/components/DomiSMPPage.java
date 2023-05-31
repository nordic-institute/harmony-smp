package pages.components;

import ddsl.SideNavigation;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.baseComponents.PageComponent;

public class DomiSMPPage extends PageComponent {
    public DomiSMPPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);
    }

    public SideNavigation getSidebar() {
        return new SideNavigation(driver);
    }

}
