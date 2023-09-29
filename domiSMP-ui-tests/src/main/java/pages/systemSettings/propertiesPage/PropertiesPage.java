package pages.systemSettings.propertiesPage;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.ConfirmationDialog;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesPage extends DomiSMPPage {

    /**
     * Page object for the Properties page. This contains the locators of the page and the methods for the behaviour of the page
     */
    private final static Logger LOG = LoggerFactory.getLogger(PropertiesPage.class);

    @FindBy(id = "searchTable")
    private WebElement propertyTableContainer;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;
    @FindBy(id = "searchProperty")
    private WebElement searchPropertyField;
    @FindBy(id = "searchbutton_id")
    private WebElement searchBtn;
    @FindBy(css = "smp-search-table [id=\"editButton\"]")
    private WebElement editBtn;


    public PropertiesPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public PropGrid grid() {
        return new PropGrid(driver, propertyTableContainer);
    }

    public void propertySearch(String propertyname) {
        LOG.info("Search for property");
        wait.forElementToBeVisible(searchPropertyField).sendKeys(propertyname);
        wait.forElementToBeClickable(searchBtn).click();
    }

    public PropertyPopup openEditPropertyPopupup(String propertyName) {
        return grid().doubleClickValue(propertyName);
    }

    public PropertyPopup clickEdit() {
        try {
            if (!weToDButton(editBtn).isEnabled()) {
                LOG.error("Edit property button is not enabled");
                return null;
            }
            weToDButton(editBtn).click();
        } catch (Exception e) {
            LOG.error("Edit property button is not enabled");
            throw new RuntimeException(e);
        }
        return new PropertyPopup(driver);


    }


    public String getPropertyValue(String propertyName) {
        return grid().getPropertyValue(propertyName);

    }

    public void save() throws Exception {
        weToDButton(saveBtn).click();
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(driver);
        confirmationDialog.confirm();
    }
}
