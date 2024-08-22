package ddsl.dcomponents;


import ddsl.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class BreadcrumpComponent extends DomiSMPPage {
    /**
     * Component for the Breadcrump of DomiSMP
     */

    @FindBy(css = "smp-breadcrumb smp-breadcrumb-item div span")
    public List<WebElement> BreadcrumpItems;

    public BreadcrumpComponent(WebDriver driver) {
        super(driver);
    }

    public String getCurrentPage() {
        int numOflinks = BreadcrumpItems.size();

        return BreadcrumpItems.get(numOflinks-1).getText();
    }


}
