package ddsl.dcomponents;

import ddsl.enums.Pages;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.ProfilePage;

import java.util.Objects;

public class SideNavigationComponent extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(SideNavigationComponent.class);

    @FindBy(id = "window-sidenav-panel")
    public WebElement sideBar;

    //	--------------------Search-------------------------
    @FindBy(id = "search-resourcesButton")
    private WebElement resourcesLnk;

    @FindBy(id = "search-toolsButton")
    private WebElement resourcesExpandLnk;
    //	----------------------------------------------------

    //	--------------Administration---------------------------
    @FindBy(id = "edit-domainButton")
    private WebElement editDomainsLnk;

    @FindBy(id = "edit-groupButton")
    private WebElement editGroupsLnk;

    @FindBy(id = "edit-resourceButton")
    private WebElement editResourcesLnk;

    @FindBy(id = "editButton")
    private WebElement administrationExpand;
    //	----------------------------------------------------

    //	--------------System Settings ---------------------------
    @FindBy(id = "system-admin-userButton")
    private WebElement usersLnk;

    @FindBy(id = "system-admin-domainButton")
    private WebElement domainsLnk;

    @FindBy(id = "system-admin-keystoreButton")
    private WebElement keystoreLnk;
    @FindBy(id = "system-admin-truststoreButton")
    private WebElement truststoreLnk;

    @FindBy(id = "system-admin-extensionButton")
    private WebElement extensionsLnk;

    @FindBy(id = "system-admin-propertiesButton")
    private WebElement propertiesLnk;

    @FindBy(id = "system-admin-alertButton")
    private WebElement alersLnk;

    @FindBy(id = "system-settingsButton")
    private WebElement systemSettingsExpand;
    //	----------------------------------------------------

    //	--------------User Settings---------------------------
    @FindBy(id = "user-data-profileButton")
    private WebElement profileLnk;

    @FindBy(id = "user-data-access-tokenButton")
    private WebElement accessTokensLnk;

    @FindBy(id = "user-data-certificatesButton")
    private WebElement certificatesLnk;

    @FindBy(id = "user-dataButton")
    private WebElement userSettingsExpand;
    //	----------------------------------------------------

    public SideNavigationComponent(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 1), this);
    }

    private MenuNavigation getNavigationLinks(Pages pages) {
        if (Objects.requireNonNull(pages) == Pages.USER_SETTINGS_PROFILE) {
            return new MenuNavigation(userSettingsExpand, profileLnk);
        }
        return null;
    }

    public DomiSMPPage navigateTo(Pages page) {

        wait.forElementToHaveText(sideBar);

        LOG.debug("Get link to " + page.name());
        //            case SEARCH_RESOURCES:
        //                expandSection(resourcesExpandLnk);
        //                return new DLink(driver, resourcesLnk);
        //            case ADMINISTRATION_EDIT_DOMAINS:
        //                expandSection(administrationExpand);
        //                return new DLink(driver, editDomainsLnk);
        //            case ADMINISTRATION_EDIT_GROUPS:
        //                expandSection(administrationExpand);
        //                return new DLink(driver, editGroupsLnk);
        //            case ADMINISTRATION_EDIT_RESOURCES:
        //                expandSection(administrationExpand);
        //                return new DLink(driver, editResourcesLnk);
        //            case SYSTEM_SETTINGS_USERS:
        //                expandSection(systemSettingsExpand);
        //                return new DLink(driver, usersLnk);
        //            case SYSTEM_SETTINGS_DOMAINS:
        //                expandSection(systemSettingsExpand);
        //                return new DLink(driver, domainsLnk);
        //            case SYSTEM_SETTINGS_KEYSTORE:
        //                expandSection(systemSettingsExpand);
        //                return new DLink(driver, keystoreLnk);
        //            case SYSTEM_SETTINGS_TRUSTSTORE:
        //                expandSection(systemSettingsExpand);
        //                return new DLink(driver, truststoreLnk);
        //            case SYSTEM_SETTINGS_EXTENSIONS:
        //                expandSection(systemSettingsExpand);
        //                return new DLink(driver, extensionsLnk);
        //            case SYSTEM_SETTINGS_PROPERTIES:
        //                expandSection(systemSettingsExpand);
        //                return new DLink(driver, propertiesLnk);
        //            case SYSTEM_SETTINGS_ALERS:
        //                expandSection(systemSettingsExpand);
        //                return new DLink(driver, alersLnk);
        if (page == Pages.USER_SETTINGS_PROFILE) {
            openSubmenu(userSettingsExpand, profileLnk);
            return new ProfilePage(driver);
//            case USER_SETTINGS_ACCESS_TOKEN:
//                //expandSection(userSettingsExpand);
//                //accessTokensLnk.click();
//                return new ProfilePage(driver);
//            case USER_SETTINGS_CERTIFICATES:
//                expandSection(userSettingsExpand);
//                return new DLink(driver, certificatesLnk);
        }
        return null;
    }

    public Boolean isMenuAvailable(Pages page) {
        MenuNavigation navigationLinks = getNavigationLinks(page);
        try {
            if (navigationLinks.menuLink.isEnabled()) {
                navigationLinks.menuLink.click();
                return navigationLinks.submenuLink.isEnabled();
            }
            return false;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private void openSubmenu(WebElement menu, WebElement submenu) {
        try {
            submenu.click();
            if (submenu.getText().contains(getBreadcrump().getCurrentPage())) {
                LOG.info("Current page is " + getBreadcrump().getCurrentPage());

            } else {
                LOG.error("Current page is not as expected. EXPECTED: [{}] but ACTUAL PAGE [{}]", submenu.getText().toString(), getBreadcrump().getCurrentPage().toString());
                throw new RuntimeException();
            }

        } catch (ElementNotInteractableException exception) {
            menu.click();
            submenu.click();
            if (submenu.getText().contains(getBreadcrump().getCurrentPage())) {
                LOG.info("Current page is " + getBreadcrump().getCurrentPage());

            } else {
                LOG.error("Current page is not as expected. EXPECTED: " + submenu.getText() + "but ACTUAL PAGE: " + getBreadcrump().getCurrentPage());
                throw new RuntimeException();
            }
        }
    }

    public class MenuNavigation {
        WebElement menuLink;
        WebElement submenuLink;

        public MenuNavigation(WebElement menuLink, WebElement submenuLink) {
            this.menuLink = menuLink;
            this.submenuLink = submenuLink;
        }
    }
}



