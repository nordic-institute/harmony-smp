package pages.service_groups.edit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.SMPPage;
import pages.service_groups.FilterArea;
import pages.service_groups.ServiceGroupGrid;
import utils.PROPERTIES;

import java.util.List;

public class EditPage extends SMPPage {
	public EditPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
		filterArea = new FilterArea(driver);
	}


	public FilterArea filterArea;

	@FindBy(id = "searchTable")
	private WebElement searchTable;

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

	public boolean isCancelButtonEnabled(){
		return cancelButton.isEnabled();
	}
	public boolean isSaveButtonEnabled(){
		return saveButton.isEnabled();
	}
	public boolean isDeleteButtonEnabled(){
		return deleteButton.isEnabled();
	}
	public boolean isEditButtonEnabled(){
		return editButton.isEnabled();
	}
	public boolean isNewButtonEnabled(){	return newButton.isEnabled();	}


	public ServiceGroupPopup clickEdit(){
		if(!isEditButtonEnabled()){return null;}

		editButton.click();

		return new ServiceGroupPopup(driver);

	}

	public ServiceGroupPopup clickNew(){
		waitForElementToBeClickable(newButton).click();
		return new ServiceGroupPopup(driver);
	}
	public ConfirmationDialog clickSave(){
		waitForElementToBeClickable(saveButton).click();
		return new ConfirmationDialog(driver);
	}

	public ConfirmationDialog clickCancel(){
		waitForElementToBeClickable(cancelButton).click();
		return new ConfirmationDialog(driver);
	}

	public void clickDelete(){
		waitForElementToBeClickable(deleteButton).click();
	}

	public boolean isNewButtonPresent(){
		try {
			return waitForElementToBeVisible(newButton).isDisplayed();
		} catch (Exception e) {	}
		return false;
	}

	public boolean isDeleteButtonPresent(){
		try {
			return waitForElementToBeVisible(deleteButton).isDisplayed();
		} catch (Exception e) {	}
		return false;
	}


	public ServiceGroupGrid getGrid(){
		ServiceGroupGrid grid = new ServiceGroupGrid(driver, searchTable);
		return grid;
	}


	public void addNewSerivceGroup(String identifier, String scheme, List<String> owners, List<String> domains, String extension) {
		waitForElementToBeClickable(newButton).click();

		ServiceGroupPopup popup = new ServiceGroupPopup(driver);
		popup.fillForm(identifier, scheme, owners, domains, extension);
		popup.clickOK();

	}

	public void saveChanges(){
		waitForElementToBeClickable(saveButton).click();
		new ConfirmationDialog(driver).confirm();

	}

}





























