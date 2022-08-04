package pages.properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.PaginationControls;
import pages.components.baseComponents.SMPPage;
import pages.users.UserPopup;
import pages.users.UsersGrid;
import utils.PROPERTIES;

public class PropertiesPage extends SMPPage {
    public PropertiesPage(WebDriver driver) {
        super(driver);
        this.pageHeader.waitForTitleToBe("Properties");
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
    }
    public PaginationControls pagination = new PaginationControls(driver);

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

    public String getErrorMessage()
    {
        return driver.findElement(By.cssSelector(".alert-message-error")).getText();
    }



    public boolean isLoaded(){
        log.info("checking if Property page is loaded");
        return isVisible(cancelBtn)
                && isVisible(saveBtn)
                && isVisible(editBtn);
    }

    public boolean isCancelButtonEnabled() {
        log.info("checking cancel button is enabled");
        return isEnabled(cancelBtn);
    }

    public boolean isSaveButtonEnabled() {
        log.info("checking save button is enabled");
        return isEnabled(saveBtn);
    }
    public ConfirmationDialog clickCancel() {
        log.info("click cancel button");
        waitForElementToBeClickable(cancelBtn).click();
        return new ConfirmationDialog(driver);
    }
    public boolean isEditButtonEnabled() {
        log.info("checking edit button is enabled");
        return isEnabled(editBtn);
    }



    public ConfirmationDialog clickSave() {
        log.info("click save button");
        waitForElementToBeClickable(saveBtn).click();
        return new ConfirmationDialog(driver);
    }
    public PropertyPopup clickEdit() {
        log.info("click edit button");
        waitForElementToBeClickable(editBtn).click();
        return new PropertyPopup(driver);
    }
    public void propertySearch(String propertyname)
    {
        log.info("Search for property");
        waitForElementToBeVisible(searchPropertyField).sendKeys(propertyname);
        waitForElementToBeClickable(searchBtn).click();
    }
    public PropertiesGrid grid() {
        return new PropertiesGrid(driver, propertyTableContainer);
    }
}
