package ddsl.dobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DLink extends DObject {


    public DLink(WebDriver driver, WebElement element) {
        super(driver, element);
    }

    public String getLinkText() throws Exception {
        if (isPresent()) {
            return super.getText();
        }
        throw new Exception();
    }

}
