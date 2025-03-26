package ddsl.dcomponents;

import ddsl.DomiSMPPage;
import ddsl.enums.Pages;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.administration.editDomainsPage.EditDomainsPage;
import pages.administration.editGroupsPage.EditGroupsPage;
import pages.administration.editResourcesPage.EditResourcePage;
import pages.administration.reviewTasksPage.ReviewTasksPage;
import pages.search.ResourcesPage;
import pages.systemSettings.TruststorePage;
import pages.systemSettings.UsersPage;
import pages.systemSettings.domainsPage.DomainsPage;
import pages.systemSettings.keyStorePage.KeystorePage;
import pages.systemSettings.propertiesPage.PropertiesPage;
import pages.userSettings.ProfilePage;
import pages.userSettings.accessTokensPage.AccessTokensPage;
import pages.userSettings.certificatesPage.accessTokensPage.CertificatesPage;

import java.util.Objects;
/**
 * Navigation object to navigate through application.
 */
public class SideNavigationComponent extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(SideNavigationComponent.class);
    @FindBy(id = "window-sidenav-panel")
    public WebElement sideBar;

    //	--------------------Search-------------------------
    @FindBy(id = "search-resourcesButton")
    private WebElement resourcesLnk;
    @FindBy(id = "dns-toolsButton")
    private WebElement dnsToolsLnk;


    @FindBy(id = "search-toolsButton")
    private WebElement searchExpandLnk;
    //	----------------------------------------------------

    //	--------------Administration---------------------------
    @FindBy(id = "edit-domainButton")
    private WebElement editDomainsLnk;

    @FindBy(id = "edit-groupButton")
    private WebElement editGroupsLnk;

    @FindBy(id = "edit-resourceButton")
    private WebElement editResourcesLnk;

    @FindBy(id = "review-tasksButton")
    private WebElement reviewTasksLnk;

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

    @FindBy(id = "user-data-alertButton")
    private WebElement myAlertsLnk;

    @FindBy(id = "user-dataButton")
    private WebElement userSettingsExpand;
    //	----------------------------------------------------

    public SideNavigationComponent(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 1), this);
        waitForPageToLoaded();
    }

    private MenuNavigation getNavigationLinks(Pages pages) {
        //Search
        if (Objects.requireNonNull(pages) == Pages.SEARCH_RESOURCES) {
            return new MenuNavigation(searchExpandLnk, resourcesLnk);
        }
        if (Objects.requireNonNull(pages) == Pages.SEARCH_DNS_TOOLS) {
            return new MenuNavigation(searchExpandLnk, dnsToolsLnk);
        }
        //Administration
        if (Objects.requireNonNull(pages) == Pages.ADMINISTRATION_EDIT_DOMAINS) {
            return new MenuNavigation(administrationExpand, editDomainsLnk);
        }
        if (Objects.requireNonNull(pages) == Pages.ADMINISTRATION_EDIT_GROUPS) {
            return new MenuNavigation(administrationExpand, editGroupsLnk);
        }
        if (Objects.requireNonNull(pages) == Pages.ADMINISTRATION_EDIT_RESOURCES) {
            return new MenuNavigation(administrationExpand, editResourcesLnk);
        }
        if (Objects.requireNonNull(pages) == Pages.ADMINISTRATION_REVIEW_TASKS) {
            return new MenuNavigation(administrationExpand, reviewTasksLnk);
        }

        //System settings
        if (Objects.requireNonNull(pages) == Pages.SYSTEM_SETTINGS_USERS) {
            return new MenuNavigation(systemSettingsExpand, usersLnk);
        }

        if (Objects.requireNonNull(pages) == Pages.SYSTEM_SETTINGS_DOMAINS) {
            return new MenuNavigation(systemSettingsExpand, domainsLnk);
        }
        if (Objects.requireNonNull(pages) == Pages.SYSTEM_SETTINGS_KEYSTORE) {
            return new MenuNavigation(systemSettingsExpand, keystoreLnk);
        }

        if (Objects.requireNonNull(pages) == Pages.SYSTEM_SETTINGS_TRUSTSTORE) {
            return new MenuNavigation(systemSettingsExpand, truststoreLnk);
        }
        if (Objects.requireNonNull(pages) == Pages.SYSTEM_SETTINGS_EXTENSIONS) {
            return new MenuNavigation(systemSettingsExpand, extensionsLnk);
        }

        if (Objects.requireNonNull(pages) == Pages.SYSTEM_SETTINGS_PROPERTIES) {
            return new MenuNavigation(systemSettingsExpand, propertiesLnk);
        }
        if (Objects.requireNonNull(pages) == Pages.SYSTEM_SETTINGS_ALERS) {
            return new MenuNavigation(systemSettingsExpand, alersLnk);
        }

        //User Settings
        if (Objects.requireNonNull(pages) == Pages.USER_SETTINGS_PROFILE) {
            return new MenuNavigation(userSettingsExpand, profileLnk);
        }
        if (Objects.requireNonNull(pages) == Pages.USER_SETTINGS_ACCESS_TOKEN) {
            return new MenuNavigation(userSettingsExpand, accessTokensLnk);
        }

        if (Objects.requireNonNull(pages) == Pages.USER_SETTINGS_CERTIFICATES) {
            return new MenuNavigation(userSettingsExpand, certificatesLnk);
        }

        if (Objects.requireNonNull(pages) == Pages.USER_SETTINGS_MY_ALERTS) {
            return new MenuNavigation(userSettingsExpand, myAlertsLnk);
        }

        throw new NoSuchElementException("Navigation for page " + pages + " does not exist!");
    }


    @SuppressWarnings("unchecked")
    public <T> T navigateTo(Pages page) {
        LOG.debug("Get link to " + page.name());
        if (page == Pages.SEARCH_RESOURCES) {
            openSubmenu(getNavigationLinks(page));
            return (T) new ResourcesPage(driver);
        }
        if (page == Pages.SEARCH_DNS_TOOLS) {
            openSubmenu(getNavigationLinks(page));
            return (T) new ResourcesPage(driver);
        }
        if (page == Pages.ADMINISTRATION_EDIT_DOMAINS) {
            openSubmenu(getNavigationLinks(page));
            return (T) new EditDomainsPage(driver);
        }
        if (page == Pages.ADMINISTRATION_EDIT_GROUPS) {
            openSubmenu(getNavigationLinks(page));
            return (T) new EditGroupsPage(driver);
        }
        if (page == Pages.ADMINISTRATION_EDIT_RESOURCES) {
            openSubmenu(getNavigationLinks(page));
            return (T) new EditResourcePage(driver);
        }
        if (page == Pages.ADMINISTRATION_REVIEW_TASKS) {
            openSubmenu(getNavigationLinks(page));
            return (T) new ReviewTasksPage(driver);
        }
        if (page == Pages.SYSTEM_SETTINGS_USERS) {
            openSubmenu(getNavigationLinks(page));
            return (T) new UsersPage(driver);
        }
        if (page == Pages.SYSTEM_SETTINGS_DOMAINS) {
            openSubmenu(getNavigationLinks(page));
            return (T) new DomainsPage(driver);
        }

        if (page == Pages.SYSTEM_SETTINGS_KEYSTORE) {
            openSubmenu(getNavigationLinks(page));
            return (T) new KeystorePage(driver);
        }

        if (page == Pages.SYSTEM_SETTINGS_TRUSTSTORE) {
            openSubmenu(getNavigationLinks(page));
            return (T) new TruststorePage(driver);
        }

        if (page == Pages.SYSTEM_SETTINGS_EXTENSIONS) {
            openSubmenu(getNavigationLinks(page));
            return (T) new TruststorePage(driver);
        }

        if (page == Pages.SYSTEM_SETTINGS_PROPERTIES) {
            openSubmenu(getNavigationLinks(page));
            return (T) new PropertiesPage(driver);
        }
        if (page == Pages.SYSTEM_SETTINGS_ALERS) {
            openSubmenu(getNavigationLinks(page));
            return (T) new PropertiesPage(driver);
        }

        if (page == Pages.USER_SETTINGS_PROFILE) {
            openSubmenu(getNavigationLinks(page));
            return (T) new ProfilePage(driver);

        }
        if (page == Pages.USER_SETTINGS_ACCESS_TOKEN) {
            openSubmenu(getNavigationLinks(page));
            return (T) new AccessTokensPage(driver);
        }
        if (page == Pages.USER_SETTINGS_CERTIFICATES) {
            openSubmenu(getNavigationLinks(page));
            return (T) new CertificatesPage(driver);
        }
        if (page == Pages.USER_SETTINGS_MY_ALERTS) {
            openSubmenu(getNavigationLinks(page));
            return (T) new CertificatesPage(driver);
        }

        throw new NoSuchElementException("Menu for page " + page + " does not exist!");
    }

    public Boolean isMenuAvailable(Pages page) {
        MenuNavigation navigationLinks = getNavigationLinks(page);
        try {
            if (navigationLinks.menuBtn.isEnabled()) {
                navigationLinks.menuBtn.click();
                return navigationLinks.submenuBtn.isEnabled();
            }
            return false;
        } catch (NoSuchElementException e) {
            LOG.error("No menu buttons found for page {}!", page);
            return false;
        }
    }

    private void openSubmenu(MenuNavigation menuNavigation) {

        if (!menuNavigation.menuBtn.getAttribute("class").contains("cdk-focused")) {
            // Driver Issue:  is not clickable at point (105, 356). Other element would receive the click:
            Actions actions = new Actions(driver);
            actions.moveToElement(menuNavigation.menuBtn);
            actions.perform();

            menuNavigation.menuBtn.click();
        }
        if (menuNavigation.submenuBtn == null) {
            return;
        }
        menuNavigation.submenuBtn.click();
        if (menuNavigation.submenuBtn.getText().contains(getBreadcrump().getCurrentPage())) {
            LOG.info("Current page is " + getBreadcrump().getCurrentPage());

        } else {
            LOG.error("Current page is not as expected. EXPECTED: " + menuNavigation.submenuBtn.getText() + "but ACTUAL PAGE: " + getBreadcrump().getCurrentPage());
            throw new RuntimeException();
        }
    }

    public static class MenuNavigation {
        WebElement menuBtn;
        WebElement submenuBtn;

        public MenuNavigation(WebElement menuBtn, WebElement submenuBtn) {
            this.menuBtn = menuBtn;
            this.submenuBtn = submenuBtn;
        }
    }
}



