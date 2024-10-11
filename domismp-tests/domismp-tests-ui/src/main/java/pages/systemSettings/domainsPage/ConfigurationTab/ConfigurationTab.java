package pages.systemSettings.domainsPage.ConfigurationTab;

import ddsl.dcomponents.DComponent;
import ddsl.dcomponents.Grid.GridWithoutPagination;
import ddsl.dcomponents.commonComponents.domanPropertyEditDialog.DomainPropertyEditDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for the Configuration tab of Domains page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class ConfigurationTab extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(ConfigurationTab.class);

    @FindBy(css = "[class~=smp-column-data]")
    public WebElement rightPanel;
    @FindBy(id = "saveButton")
    public WebElement saveBtn;


    public ConfigurationTab(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    private GridWithoutPagination getConfigurationGrid() {
        return new GridWithoutPagination(driver, rightPanel);
    }

    public DomainPropertyEditDialog openProperty(String propertyName) {
        getConfigurationGrid().searchAndDoubleClickElementInColumn("Domain property", propertyName);
        return new DomainPropertyEditDialog(driver);
    }

    public void saveChanges() {
        try {
            weToDButton(saveBtn).click();
        } catch (Exception e) {
            LOG.error("Could not save changes on Configuration tab!");
        }
    }

    public Boolean isSystemValueUsed(String propertyName) {
        WebElement currentCell = getConfigurationGrid().searchAndGetPrecedentSiblingElementInColumn("Domain property", propertyName);
        //check if previous sibling is checked
        return currentCell.findElement(By.cssSelector("mat-checkbox")).getAttribute("class").contains("checkbox-checked");
    }

    public String getCurrentPropertyValue(String propertyName) {
        WebElement currentCell = getConfigurationGrid().searchAndGetFollowingSiblingElementInColumn("Domain property", propertyName);
        //check if previous sibling is checked
        return currentCell.getText();
    }


}
