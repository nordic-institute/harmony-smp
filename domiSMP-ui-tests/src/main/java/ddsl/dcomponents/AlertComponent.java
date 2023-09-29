package ddsl.dcomponents;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlertComponent extends DComponent {

    /**
     * This component is used to retrive the alerts which appear in application
     */
    private final static Logger LOG = LoggerFactory.getLogger(AlertComponent.class);

    @FindBy(id = "alertmessage_id")
    public WebElement alertToaster;

    @FindBy(css = "[class = \"closebtn\"]")
    public WebElement closeBtn;


    public AlertComponent(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public String getAlertMessage() {
        try {
            wait.forElementToBeVisible(alertToaster, true);
            String alertMesageText = alertToaster.getText();
            alertMesageText = alertMesageText.replace("×", "").replaceAll("\n", "");
            LOG.debug("Displayed message : {}.", alertToaster.getText());
            closeBtn.click();
            return alertMesageText;
        } catch (Exception e) {
            LOG.error("No messages displayed.");
            return "No alert message found";
        }
    }

}
