package pages.PropertiesPage;

import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dcomponents.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesPage extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(PropertiesPage.class);

    @FindBy(id = "searchTable")
    private WebElement propertyTableContainer;
    @FindBy(id = "cancelButton")
    private WebElement cancelBtn;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;
    @FindBy(id = "editButton")
    private WebElement editBtn;
    @FindBy(id = "searchProperty")
    private WebElement searchPropertyField;
    @FindBy(id = "searchbutton_id")
    private WebElement searchBtn;


    public PropertiesPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);
    }

    public PropGrid grid() {
        return new PropGrid(driver, propertyTableContainer);
    }

    public void propertySearch(String propertyname) {
        LOG.info("Search for property");
        wait.forElementToBeVisible(searchPropertyField).sendKeys(propertyname);
        wait.forElementToBeClickable(searchBtn).click();
    }

    public void setPropertyValue(String propertyName, String propertyValue) {
        PropertyPopup popup = grid().selectValue(propertyName);
        popup.editInputField(propertyValue);
        try {
            popup.clickOK();
        } catch (Exception e) {
            LOG.error("Cannot set value for property {1}", propertyName);
        }
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
