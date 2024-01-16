package pages.userSettings;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.commonComponents.UserDataCommonComponent;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Page object for the Profile page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class ProfilePage extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(ProfilePage.class);
    public UserDataCommonComponent profileData;
    public ProfilePage(WebDriver driver) {
        super(driver);
        profileData = new UserDataCommonComponent(driver);
        LOG.debug("Profile page has loaded");
    }
}