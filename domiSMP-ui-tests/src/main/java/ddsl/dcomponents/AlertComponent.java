package ddsl.dcomponents;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlertComponent extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(AlertComponent.class);

    @FindBy(id = "alertmessage_id")
    public WebElement alertToaster;

    public AlertComponent(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);
    }

    public String getAlertMessage() {
        try {
            wait.forElementToBeVisible(alertToaster, true);
            String alertMesageText = alertToaster.getText().replace("×", "").replaceAll("\n", "");
            LOG.debug("Displayed message : {}.", alertToaster.getText());

            return alertMesageText;
        } catch (Exception e) {
            LOG.debug("No messages displayed.");
            return null;
        }
    }

}
