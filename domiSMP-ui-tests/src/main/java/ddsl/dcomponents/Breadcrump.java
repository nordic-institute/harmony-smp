package ddsl.dcomponents;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Breadcrump extends DomiSMPPage {
    @FindBy(xpath = "//smp-breadcrumb/div/smp-breadcrumb-item[3]/a/div[3]/span")
    public WebElement CurrentPage;

    public Breadcrump(WebDriver driver) {
        super(driver);
    }

    public String getCurrentPage() {
        return CurrentPage.getText();
    }


}
