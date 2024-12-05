package pages.systemSettings.domainsPage;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * Page object for the Resource tab of Domains page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class ResourceTypesTab extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(ResourceTypesTab.class);
    @FindBy(css = "mat-list-option")
    private List<WebElement> resourceOptions;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;

    public ResourceTypesTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public void saveChanges() {
        try {
        if (saveBtn.isEnabled()) {
            saveBtn.click();
            if (saveBtn.getDomAttribute("disabled").equals("true")) {
                LOG.debug("Resource tab changes were succesfully saved");
            } else {
                LOG.error("Resource tab changes were not saved");
            }
        }
        } catch (NullPointerException exception) {
            LOG.error("Resource tab changes were not saved");

        }
    }

    public void checkResource(String resourceName, boolean isChecked) throws Exception {
        wait.forElementToBeClickable(resourceOptions.get(0));
        for (WebElement element : resourceOptions) {
            if (element.getText().contains(resourceName)) {
                if (isChecked) {
                    if (!weToDChecked(element).isChecked()) {
                        element.click();
                        LOG.debug("Selecting resource {} is opened", resourceName);
                    }
                } else {
                    if (weToDChecked(element).isChecked()) {
                        element.click();
                    }


                }
            }
        }
    }

    public boolean getResourceTypeStatus(String resourceName) throws Exception {

        wait.forElementToBeClickable(resourceOptions.get(0));
        for (WebElement element : resourceOptions) {
            if (element.getText().contains(resourceName)) {
                return weToDChecked(element).isChecked();
            }
        }
        LOG.error("Could not get the status of the resource type.");
        return false;
    }


}
