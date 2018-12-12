package pages.domain;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.PaginationControls;
import pages.components.baseComponents.SMPPage;
import pages.components.grid.BasicGrid;
import pages.users.UserPopup;
import utils.PROPERTIES;

public class DomainPage extends SMPPage {
	public DomainPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}
	
	@FindBy(id = "searchTable")
	private WebElement domainTableContainer;
	
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

	public PaginationControls pagination = new PaginationControls(driver);
	
	
	
	public boolean isLoaded(){

		waitForElementToBeVisible(newBtn);

		if(!cancelBtn.isDisplayed()){return false;}
		if(!saveBtn.isDisplayed()){return false;}
		if(!newBtn.isDisplayed()){return false;}
		if(!newBtn.isEnabled()){return false;}
		if(!editBtn.isDisplayed()){return false;}
		return deleteBtn.isDisplayed();
	}
	
	public boolean isCancelButtonEnabled(){
		return cancelBtn.isEnabled();
	}
	public boolean isSaveButtonEnabled(){
		return saveBtn.isEnabled();
	}
	public boolean isDeleteButtonEnabled(){
		return deleteBtn.isEnabled();
	}
	public boolean isEditButtonEnabled(){
		return editBtn.isEnabled();
	}
	public boolean isNewButtonEnabled(){
		return newBtn.isEnabled();
	}
	
	public ConfirmationDialog clickCancel(){
		waitForElementToBeClickable(cancelBtn).click();
		return new ConfirmationDialog(driver);
	}
	
	public ConfirmationDialog clickSave(){
		waitForElementToBeClickable(saveBtn).click();
		return new ConfirmationDialog(driver);
	}
	
	public void clickDelete(){
		waitForElementToBeClickable(deleteBtn).click();
	}
	public DomainPopup clickNew(){
		waitForElementToBeClickable(newBtn).click();
		return new DomainPopup(driver);
	}
	public DomainPopup clickEdit(){
		waitForElementToBeClickable(editBtn).click();
		return new DomainPopup(driver);
	}
	
	
	public DomainGrid grid(){
		return new DomainGrid(driver, domainTableContainer);
	}
	
	
	
	
	
	
	
}
