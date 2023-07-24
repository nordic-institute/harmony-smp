package pages;

import ddsl.dcomponents.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsersPage extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(UsersPage.class);


    public UsersPage(WebDriver driver) {
        super(driver);
    }


}
