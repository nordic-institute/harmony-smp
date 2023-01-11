package pages.domain;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.PaginationControls;
import pages.components.baseComponents.SMPPage;
import pages.keystore.KeyStoreEditDialog;
import utils.PROPERTIES;

public class DomainPage extends SMPPage {
	public DomainPage(WebDriver driver) {
		super(driver);
		this.pageHeader.waitForTitleToBe("Domain");
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

	@FindBy(xpath = "//span[text()=' Edit keystore']")
	private WebElement editKeyStore;

	public PaginationControls pagination = new PaginationControls(driver);
	
	
	
	public boolean isLoaded(){
		log.info("checking if Domain page is loaded");
		return isVisible(cancelBtn)
				&& isVisible(saveBtn)
				&& isVisible(newBtn)
				&& isEnabled(newBtn)
				&& isVisible(editBtn)
				&& isVisible(deleteBtn);
	}

	public boolean isCancelButtonEnabled(){
		log.info("cancel button");
		return isEnabled(cancelBtn);
	}
	public boolean isSaveButtonEnabled(){
		log.info("save button");
		return isEnabled(saveBtn);
	}
	public boolean isDeleteButtonEnabled(){
		log.info("delete button");
		return isEnabled(deleteBtn);
	}
	public boolean isEditButtonEnabled(){
		log.info("edit button");
		return isEnabled(editBtn);
	}
	public boolean isNewButtonEnabled(){
		log.info("new button");
		return isEnabled(newBtn);
	}

	public ConfirmationDialog clickCancel(){
		log.info("cancelling ..");
		waitForElementToBeClickable(cancelBtn).click();
		return new ConfirmationDialog(driver);
	}
	
	public ConfirmationDialog clickSave(){
		log.info("saving ...");
		waitForElementToBeClickable(saveBtn).click();
		return new ConfirmationDialog(driver);
	}
	
	public void clickDelete(){
		log.info("deleting ...");
		waitForElementToBeClickable(deleteBtn).click();
		waitForElementToBeEnabled(saveBtn);
	}
	public DomainPopup clickNew(){
		log.info("clicking new ...");
		waitForElementToBeClickable(newBtn).click();
		return new DomainPopup(driver);
	}
	public DomainPopup clickEdit(){
		log.info("editing ...");
		waitForElementToBeClickable(editBtn).click();
		return new DomainPopup(driver);
	}

	public KeyStoreEditDialog clickEditKeyStore(){
		log.info("clicking edit keystore");
		waitForElementToBeClickable(editKeyStore).click();
		return new KeyStoreEditDialog(driver);
	}
	
	
	public DomainGrid grid(){
		return new DomainGrid(driver, domainTableContainer);
	}
	
	
	
	
	
	
	
}
