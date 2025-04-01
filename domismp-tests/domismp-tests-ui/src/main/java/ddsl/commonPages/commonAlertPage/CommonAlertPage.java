package ddsl.commonPages.commonAlertPage;

import ddsl.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CommonAlertPage extends DomiSMPPage {
    @FindBy(id = "searchTable")
    private WebElement alertTableContainer;

    public CommonAlertPage(WebDriver driver) {
        super(driver);
    }

    public CommonAlertsGrid getAlertGrid() {
        return new CommonAlertsGrid(driver, alertTableContainer);
    }

}
