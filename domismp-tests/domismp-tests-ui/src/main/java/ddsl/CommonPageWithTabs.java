package ddsl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * Generic page used for pages which have tabs in the left part. This element gives access to action buttons and elements of the page.
 */
public class CommonPageWithTabs extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(CommonPageWithTabs.class);
    @FindBy(css = "[role = \"tab\"]")
    private List<WebElement> tabList;

    public CommonPageWithTabs(WebDriver driver) {
        super(driver);
    }

    public void goToTab(String tabName) {
        for (WebElement element : tabList) {
            if (element.getText().contains(tabName)) {
                element.click();
                wait.forAttributeToContain(element, "aria-selected", "true");
                LOG.debug("Domain tab {} is opened", tabName);
                break;
            }
        }
    }

    public String getAlertMessageAndClose() {
        return getAlertArea().getAlertMessage();
    }
}
