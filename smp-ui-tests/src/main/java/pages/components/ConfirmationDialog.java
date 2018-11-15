package pages.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

public class ConfirmationDialog extends PageComponent {


	public ConfirmationDialog(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}

	@FindBy(id = "yesbuttondialog_id")
	private WebElement yesBtn;

	@FindBy(id = "nobuttondialog_id")
	private WebElement noBtn;

	public void confirm(){
		log.info("dialog .. confirm");
		waitForElementToBeClickable(yesBtn);
		yesBtn.click();
		waitForElementToBeGone(yesBtn);

	}

	public void cancel(){
		log.info("dialog .. cancel");
		waitForElementToBeClickable(noBtn);
		noBtn.click();
		waitForElementToBeGone(noBtn);
	}


}
