package pages.ProfilePage;

import ddsl.PageWithGrid;
import ddsl.dcomponents.UserDataCommonComponent;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilePage extends PageWithGrid {
    /**
     * Page object for the Profile page. This contains the locators of the page and the methods for the behaviour of the page
     */
    private final static Logger LOG = LoggerFactory.getLogger(ProfilePage.class);
    public UserDataCommonComponent userData;


    public ProfilePage(WebDriver driver) {
        super(driver);
        userData = new UserDataCommonComponent(driver);

    }
}