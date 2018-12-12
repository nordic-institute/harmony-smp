package pages.users;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.PaginationControls;
import pages.components.baseComponents.SMPPage;
import pages.components.grid.BasicGrid;
import utils.PROPERTIES;

import java.util.List;

public class UsersPage extends SMPPage {
	public UsersPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}


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
	
	
	public boolean isLoaded(){
		
		if(!cancelBtn.isDisplayed()){return false;}
		if(!saveBtn.isDisplayed()){return false;}
		if(!newBtn.isDisplayed()){return false;}
		if(!newBtn.isEnabled()){return false;}
		if(!editBtn.isDisplayed()){return false;}
		return deleteBtn.isDisplayed();
	}
	
	public boolean isCancelButtonEnabled(){
		waitForElementToBeEnabled(cancelBtn);
		return cancelBtn.isEnabled();
	}
	public boolean isSaveButtonEnabled(){
		waitForElementToBeEnabled(saveBtn);
		return saveBtn.isEnabled();
	}
	public boolean isDeleteButtonEnabled(){
		waitForElementToBeEnabled(deleteBtn);
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
	public UserPopup clickNew(){
		waitForElementToBeClickable(newBtn).click();
		return new UserPopup(driver);
	}
	public UserPopup clickEdit(){
		waitForElementToBeClickable(editBtn).click();
		return new UserPopup(driver);
	}
	
	
	public UsersGrid grid(){
		return new UsersGrid(driver, userTableContainer);
	}
	


	public void createUser(){
		waitForElementToBeClickable(newBtn).click();

		UserPopup popup = new UserPopup(driver);
//		popup.fillData(user,"",role,password,password);
		popup.clickOK();

	}
	
	
	
	
	
	
}
