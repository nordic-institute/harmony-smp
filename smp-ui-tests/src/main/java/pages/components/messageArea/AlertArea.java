package pages.components.messageArea;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import utils.PROPERTIES;
import pages.components.baseComponents.PageComponent;

public class AlertArea  extends PageComponent {

	public AlertArea(WebDriver driver) {
		super(driver);
		PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
	}

	@SuppressWarnings("SpellCheckingInspection")
	@FindBy(id = "alertmessage_id")
	private WebElement alertMessage;

	@FindBy(css = "#alertmessage_id span")
	private WebElement closeAlertSpan;

	public AlertMessage getAlertMessage(){
		if(!alertMessage.isDisplayed()){
			log.warn("No messages displayed.");
			return null;
		}

		String rawMessTxt = alertMessage.getText().replaceAll("\\n", "").trim();
		String closeChr = closeAlertSpan.getText();
		String messageTxt = rawMessTxt.replaceAll(closeChr, "").trim();

		log.info("Getting alert message ...");
		return new AlertMessage(messageTxt, alertMessage.getAttribute("class").contains("error"));
	}


}
