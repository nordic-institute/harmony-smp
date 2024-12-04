package pages.userSettings.accessTokensPage;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dcomponents.Grid.GridPagination;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class AccessTokensPage extends DomiSMPPage {
    protected static final By nameLocator = By.cssSelector("input:first-of-type");
    protected static final By deleteBtnLocator = By.id("deleteButton");
    protected static final By saveBtnLocator = By.id("saveButton");
    protected static final By descriptionLocator = By.cssSelector("input[formcontrolname=\"description\"]");
    protected static final By isActiveLocator = By.cssSelector("mat-checkbox");
    protected static final By startDateLocator = By.cssSelector("input[formcontrolname=\"activeFrom\"");
    protected static final By endDateLocator = By.cssSelector("input[formcontrolname=\"expireOn\"]");
    protected static final By sequenceFailedAttempsLocator = By.id("sequentialTokenLoginFailureCount_id");
    protected static final By lastFailedAttemptLocator = By.id("LastFailedAttempt_id");
    protected static final By suspendedUtilLocator = By.id("SuspendedUtil_id");
    protected static final By pagination = By.id("tokens-paginator");
    private final static Logger LOG = LoggerFactory.getLogger(AccessTokensPage.class);
    GridPagination gridPagination = new GridPagination(driver, driver.findElement(pagination));
    @FindBy(id = "createAccessTokenButton")
    private WebElement createBtn;
    @FindBy(css = "td access-token-panel")
    private List<WebElement> accessTokens;

    public AccessTokensPage(WebDriver driver) {
        super(driver);
        LOG.debug("Access Tokens page has loaded");

    }

    public CreateNewAccessTokenDialog clickCreateAccessTokenBtn() {
        weToDButton(createBtn).click();
        return new CreateNewAccessTokenDialog(driver);
    }

    private WebElement getAccessToken(String accessTokenName) {
        for (int i = 0; i <= gridPagination.getTotalPageNumber(); i++) {
            for (WebElement accessToken : accessTokens) {
                String currentElementName = weToDInput(accessToken.findElement(nameLocator)).getText();
                if (currentElementName.equals(accessTokenName)) {
                    LOG.debug("Access Token [{}] has been found on page  [{}].", accessTokenName, i);

                    return accessToken;
                }

            }
            gridPagination.goToNextPage();
        }
        throw new NoSuchElementException();
    }

    public Boolean isAccessTokenPresent(String accessTokenName) {
        try {
            getAccessToken(accessTokenName);
            return true;

        } catch (NoSuchElementException e) {
            return false;
        }


    }

    public HashMap<String, String> getAccessTokenInfo(String accessTokenName) throws Exception {
        HashMap<String, String> accessTokenInfo = new HashMap<>();
        WebElement accessToken = getAccessToken(accessTokenName);

        accessToken.findElement(By.cssSelector("mat-expansion-panel")).click();

        accessTokenInfo.put("Description", weToDInput(accessToken.findElement(descriptionLocator)).getText());
        accessTokenInfo.put("Active", String.valueOf(weToDChecked(accessToken.findElement(isActiveLocator)).isChecked()));
        accessTokenInfo.put("StartDate", weToDInput(accessToken.findElement(startDateLocator)).getText());
        accessTokenInfo.put("EndDate", weToDInput(accessToken.findElement(endDateLocator)).getText());
        accessTokenInfo.put("SequenceFailedAttempts", weToDInput(accessToken.findElement(sequenceFailedAttempsLocator)).getText());
        accessTokenInfo.put("LastFailedAttempts", weToDInput(accessToken.findElement(lastFailedAttemptLocator)).getText());
        accessTokenInfo.put("SuspendedUntil", weToDInput(accessToken.findElement(suspendedUtilLocator)).getText());
        return accessTokenInfo;

    }

    public String deleteAccessToken(String accessTokenName) {

        WebElement accessToken = getAccessToken(accessTokenName);

        accessToken.findElement(By.cssSelector("mat-expansion-panel")).click();
        weToDButton(accessToken.findElement(deleteBtnLocator)).click();
        new ConfirmationDialog(driver).confirm();
        return getAlertArea().getAlertMessage();
    }

    public String modifyIsActiveForAccessToken(String accessTokenName, boolean isActive) throws Exception {

        WebElement accessToken = getAccessToken(accessTokenName);

        accessToken.findElement(By.cssSelector("mat-expansion-panel")).click();
        if (isActive) {
            weToDChecked(accessToken.findElement(isActiveLocator)).check();

        } else {
            weToDChecked(accessToken.findElement(isActiveLocator)).uncheck();

        }
        if (weToDButton(accessToken.findElement(saveBtnLocator)).isEnabled())
        {
            LOG.debug("Changing active value of access token to: [{}]", isActive);

            weToDButton(accessToken.findElement(saveBtnLocator)).click();
            new ConfirmationDialog(driver).confirm();
            return getAlertArea().getAlertMessage();
        }
        return null;
    }

}
