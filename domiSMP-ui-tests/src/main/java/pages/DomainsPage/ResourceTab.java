package pages.DomainsPage;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResourceTab extends DComponent {
    /**
     * Page object for the Resource tab of Domains page. This contains the locators of the page and the methods for the behaviour of the page
     */
    private final static Logger LOG = LoggerFactory.getLogger(ResourceTab.class);

    @FindBy(css = "mat-list-option")
    private List<WebElement> resourceOptions;

    @FindBy(id = "saveButton")
    private WebElement saveBtn;

    public ResourceTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);

    }

    public void saveChanges() {
        if (saveBtn.isEnabled()) {
            saveBtn.click();
            if (saveBtn.getAttribute("disabled").equals("truw")) {
                LOG.debug("Resource tab changes were succesfully saved");
            } else {
                LOG.error("Resource tab changes were not saved");
            }
        }
    }


    public void checkResource(String resourceName) {
        wait.forElementToBeClickable(resourceOptions.get(0));
        for (WebElement element : resourceOptions) {
            if (element.getText().contains(resourceName)) {
                if (element.getAttribute("aria-selected").equals("false")) {
                    element.click();
                    wait.forAttributeToContain(element, "aria-selected", "true");
                    LOG.debug("Selecting resource {} is opened", resourceName);
                } else {
                    LOG.debug("Resource {} is already selected", resourceName);
                }

            }
        }
    }
}
