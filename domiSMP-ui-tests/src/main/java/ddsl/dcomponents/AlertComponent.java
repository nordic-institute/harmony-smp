package ddsl.dcomponents;

import ddsl.dobjects.DObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AlertComponent extends DComponent {
    @FindBy(id = "alertmessage_id")
    public WebElement alertToaster;
    @FindBy(css = "#alertmessage_id > span.closebtn")
    public WebElement closeButton;

    public AlertComponent(WebDriver driver) {
        super(driver);
    }

    public void closeAlert() throws Exception {
        weToDButton(closeButton).click();
    }

    public String getAlertMessage() {
        try {
            wait.forElementToBeVisible(alertToaster, true);

            log.error(closeButton.getText());
        } catch (Exception e) {
        }
        DObject alertObject = new DObject(driver, alertToaster);

        if (!alertObject.isPresent()) {
            log.debug("No messages displayed.");
            return null;
        }

        String messageTxt = alertToaster.getText().replace(closeButton.getText(), "").replaceAll("\n", "").trim();

        log.debug("messageTxt = " + messageTxt);

        log.debug("Getting alert message ...");
        return messageTxt.trim();
    }

}
