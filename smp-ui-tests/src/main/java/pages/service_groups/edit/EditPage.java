package pages.service_groups.edit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.PaginationControls;
import pages.components.baseComponents.SMPPage;
import pages.service_groups.FilterArea;
import pages.service_groups.ServiceGroupGrid;
import utils.PROPERTIES;

import java.util.List;

public class EditPage extends SMPPage {
	public EditPage(WebDriver driver) {
		super(driver);
		this.pageHeader.waitForTitleToBe("Edit");
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
		filterArea = new FilterArea(driver);
	}


	public FilterArea filterArea;

	@FindBy(id = "searchTable")
	private WebElement searchTable;

	@FindBy(id = "okButton")
	private WebElement okButton;

	@FindBy(id = "cancelButton")
	private WebElement cancelButton;

	@FindBy(id = "saveButton")
	private WebElement saveButton;

	@FindBy(id = "newButton")
	private WebElement newButton;

	@FindBy(id = "editButton")
	private WebElement editButton;

	@FindBy(id = "deleteButton")
	private WebElement deleteButton;

	public PaginationControls pagination = new PaginationControls(driver);

	public boolean isCancelButtonEnabled() {
		log.info("cancel button");
		return isEnabled(cancelButton);
	}

	public boolean isSaveButtonEnabled() {
		log.info("save button");
		return isEnabled(saveButton);
	}

	public boolean isDeleteButtonEnabled() {
		log.info("delete button");
		return isEnabled(deleteButton);
	}

	public boolean isEditButtonEnabled() {
		log.info("edit button");
		return isEnabled(editButton);
	}

	public boolean isNewButtonEnabled() {
		log.info("new button");
		return isEnabled(newButton);
	}


	public ServiceGroupPopup clickEdit() {
		log.info("editing ...");
		if (!isEditButtonEnabled()) {
			return null;
		}

		editButton.click();

		return new ServiceGroupPopup(driver);

	}

	public ServiceGroupPopup clickNew() {
		log.info("new ...");
		waitForElementToBeClickable(newButton).click();
		return new ServiceGroupPopup(driver);
	}

	public ConfirmationDialog clickSave() {
		log.info("saving ...");
		waitForElementToBeClickable(saveButton).click();
		return new ConfirmationDialog(driver);
	}

	public ConfirmationDialog clickCancel() {
		log.info("canceling ...");
		waitForElementToBeClickable(cancelButton).click();
		return new ConfirmationDialog(driver);
	}
	public ConfirmationDialog clickOk() {
		log.info("canceling ...");
		waitForElementToBeClickable(okButton).click();
		return new ConfirmationDialog(driver);
	}

	public void clickDelete() {
		log.info("deleting ...");
		waitForElementToBeClickable(deleteButton).click();
	}

	public boolean isNewButtonPresent() {
		log.info("check if NEW button is visible");
		return isVisible(newButton);
	}

	public boolean isDeleteButtonPresent() {
		log.info("check if DELETE button is visible");
		return isVisible(deleteButton);
	}


	public ServiceGroupGrid getGrid() {
		log.info("getting grid");
		waitForElementToBeVisible(searchTable);
		ServiceGroupGrid grid = new ServiceGroupGrid(driver, searchTable);
		return grid;
	}


	public void addNewServiceGroup(String identifier, String scheme, List<String> owners, List<String> domains, String extension) {
		log.info("adding new service group");
		waitForElementToBeClickable(newButton).click();

		ServiceGroupPopup popup = new ServiceGroupPopup(driver);
		popup.fillForm(identifier, scheme, owners, domains, extension);
		popup.clickOK();

		waitForXMillis(300);
	}

	public void saveChangesAndConfirm() {
		log.info("saving..");
		waitForElementToBeClickable(saveButton).click();
		new ConfirmationDialog(driver).confirm();
	}

}





























