package ddsl.dcomponents;

import ddsl.enums.Pages;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.ProfilePage;

public class SideNavigation extends DomiSMPPage {
    @FindBy(id = "window-sidenav-panel")
    public WebElement sideBar;

    //	--------------------Search-------------------------
    @FindBy(id = "")
    private WebElement resourcesLnk;

    @FindBy(css = "mat-nested-tree-node cdk-nested-tree-node cdk-tree-node ng-star-inserted")
    private WebElement resourcesExpandLnk;
    //	----------------------------------------------------

    //	--------------Administration---------------------------
    @FindBy(id = "")
    private WebElement editDomainsLnk;

    @FindBy(id = "")
    private WebElement editGroupsLnk;

    @FindBy(id = "")
    private WebElement editResourcesLnk;

    @FindBy(id = "mat-expansion-panel-header-1")
    private WebElement administrationExpand;
    //	----------------------------------------------------

    //	--------------System Settings ---------------------------
    @FindBy(id = "")
    private WebElement usersLnk;

    @FindBy(id = "")
    private WebElement domainsLnk;

    @FindBy(id = "")
    private WebElement keystoreLnk;
    @FindBy(id = "")
    private WebElement truststoreLnk;

    @FindBy(id = "")
    private WebElement extensionsLnk;

    @FindBy(id = "")
    private WebElement propertiesLnk;

    @FindBy(id = "")
    private WebElement alersLnk;

    @FindBy(id = "mat-expansion-panel-header-1")
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

    public SideNavigation(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 1), this);
    }

    private MenuNavigation getNavigationLinks(Pages pages) {
        switch (pages) {

            case USER_SETTINGS_PROFILE:
                return new MenuNavigation(userSettingsExpand, profileLnk);

            default:
                return null;
        }
    }

    public DomiSMPPage navigateTo(Pages page) {

        wait.forElementToHaveText(sideBar);

        log.debug("Get link to " + page.name());
        switch (page) {
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
            case USER_SETTINGS_PROFILE:
                openSubmenu(userSettingsExpand, profileLnk);
                return new ProfilePage(driver);
//            case USER_SETTINGS_ACCESS_TOKEN:
//                //expandSection(userSettingsExpand);
//                //accessTokensLnk.click();
//                return new ProfilePage(driver);
//            case USER_SETTINGS_CERTIFICATES:
//                expandSection(userSettingsExpand);
//                return new DLink(driver, certificatesLnk);
            default:
                return null;
        }
    }

    public Boolean isMenuAvailable(Pages page) {
        MenuNavigation navigationLinks = getNavigationLinks(page);
        try {
            if (navigationLinks.menuLink.isEnabled()) {
                navigationLinks.menuLink.click();
                if (navigationLinks.submenuLink.isEnabled()) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private void openSubmenu(WebElement menu, @org.jetbrains.annotations.NotNull WebElement submenu) {
        try {
            submenu.click();
            if (submenu.getText().contains(getBreadcrump().getCurrentPage())) {
                log.info("Current page is " + getBreadcrump().getCurrentPage());

            } else {
                log.error("Current page is not as expected. EXPECTED: " + submenu.getText() + "but ACTUAL PAGE: " + getBreadcrump().getCurrentPage());
                throw new RuntimeException();
            }

        } catch (ElementNotInteractableException exception) {
            menu.click();
            submenu.click();
            if (submenu.getText().contains(getBreadcrump().getCurrentPage())) {
                log.info("Current page is " + getBreadcrump().getCurrentPage());

            } else {
                log.error("Current page is not as expected. EXPECTED: " + submenu.getText() + "but ACTUAL PAGE: " + getBreadcrump().getCurrentPage());
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



