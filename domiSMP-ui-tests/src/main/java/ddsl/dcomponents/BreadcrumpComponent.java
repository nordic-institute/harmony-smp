package ddsl.dcomponents;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class BreadcrumpComponent extends DomiSMPPage {

    @FindBy(xpath = "//smp-breadcrumb/div/smp-breadcrumb-item/a/div[3]/span")
    public List<WebElement> BreadcrumpItems;

    public BreadcrumpComponent(WebDriver driver) {
        super(driver);
    }

    public String getCurrentPage() {
        int numOflinks = BreadcrumpItems.size();

        return BreadcrumpItems.get(numOflinks - 1).getText();
    }


}
