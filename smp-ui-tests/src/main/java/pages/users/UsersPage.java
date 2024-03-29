package pages.users;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.PaginationControls;
import pages.components.baseComponents.SMPPage;
import utils.PROPERTIES;

public class UsersPage extends SMPPage {
    public PaginationControls pagination = new PaginationControls(driver);
    @FindBy(id = "searchTable")
    private WebElement userTableContainer;
    @FindBy(id = "cancelButton")
    private WebElement cancelBtn;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;
    @FindBy(id = "newButton")
    private WebElement newBtn;
    @FindBy(id = "editButton")
    private WebElement editBtn;
    @FindBy(id = "deleteButton")
    private WebElement deleteBtn;
    @FindBy(xpath = "//span[text()=' Edit truststore']")
    private WebElement editTruststore;

    public UsersPage(WebDriver driver) {
        super(driver);
        this.pageHeader.waitForTitleToBe("Users");
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
    }

    public boolean isLoaded() {
        log.info("checking Users page is loaded");

        return isVisible(cancelBtn)
                && isVisible(saveBtn)
                && isVisible(newBtn)
                && isEnabled(newBtn)
                && isVisible(editBtn)
                && isVisible(deleteBtn);
    }

    public boolean isCancelButtonEnabled() {
        log.info("cancel button");
        return isEnabled(cancelBtn);
    }

    public boolean isSaveButtonEnabled() {
        log.info("save button");
        return isEnabled(saveBtn);
    }

    public boolean isEditButtonEnabled() {
        log.info("save button");
        return isEnabled(editBtn);
    }

    public boolean isDeleteButtonEnabled() {
        waitForXMillis(200);
        log.info("delete button");
        return isEnabled(deleteBtn);
    }

    public ConfirmationDialog clickCancel() {
        log.info("click cancel button");
        waitForElementToBeClickable(cancelBtn).click();
        return new ConfirmationDialog(driver);
    }

    public ConfirmationDialog clickSave() {
        log.info("click save button");
        waitForElementToBeClickable(saveBtn).click();
        return new ConfirmationDialog(driver);
    }

    public void clickDelete() {
        log.info("click delete button");
        waitForElementToBeClickable(deleteBtn).click();
        waitForRowsToLoad();
    }

    public UserPopup clickNew() {
        log.info("click new button");
        waitForElementToBeClickable(newBtn).click();
        return new UserPopup(driver);
    }

    public UserPopup clickEdit() {
        log.info("click edit button");
        waitForElementToBeClickable(editBtn).click();
        return new UserPopup(driver);
    }


    public UsersGrid grid() {
        return new UsersGrid(driver, userTableContainer);
    }


    public void createUser() {
        log.info("create user");

        waitForElementToBeClickable(newBtn).click();

        UserPopup popup = new UserPopup(driver);
        popup.clickOK();

    }

    public boolean isNewButtonEnabled() {
        try {
            return isEnabled(newBtn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEditTruststoreButtonEnabled() {
        try {
            return isEnabled(editTruststore);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean isDeleteButtonVisible() {
        try {
            return isVisible(deleteBtn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEditButtonVisible() {
        try {
            return isVisible(editBtn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean isCancelButtonVisible() {

        return isVisible(cancelBtn);
    }

    public boolean isSaveButtonVisible() {

        return isVisible(saveBtn);
    }


}
