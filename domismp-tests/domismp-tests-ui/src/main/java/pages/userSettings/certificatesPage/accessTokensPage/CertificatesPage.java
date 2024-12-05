package pages.userSettings.certificatesPage.accessTokensPage;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dcomponents.Grid.GridPagination;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

public class CertificatesPage extends DomiSMPPage {
    protected static final By nameLocator = By.cssSelector("input:first-of-type");
    protected static final By deleteBtnLocator = By.id("deleteButton");
    protected static final By showDetailsBtnLocator = By.id("showButton");
    protected static final By descriptionLocator = By.cssSelector("input[formcontrolname=\"description\"]");
    protected static final By isActiveLocator = By.cssSelector("mat-checkbox");
    protected static final By startDateLocator = By.cssSelector("input[formcontrolname=\"activeFrom\"");
    protected static final By endDateLocator = By.cssSelector("input[formcontrolname=\"expireOn\"]");
    protected static final By pagination = By.id("tokens-paginator");
    private final static Logger LOG = LoggerFactory.getLogger(CertificatesPage.class);
    GridPagination gridPagination = new GridPagination(driver, driver.findElement(pagination));
    @FindBy(id = "importNewCertificateButton")
    private WebElement importBtn;
    @FindBy(css = "td user-certificate-panel")
    private List<WebElement> certificateList;

    public CertificatesPage(WebDriver driver) {
        super(driver);
        LOG.debug("Certificate page has loaded");
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public ImportNewCertificatesDialog clickOnImportNewCertificate() {
        weToDButton(importBtn).click();
        return new ImportNewCertificatesDialog(driver);
    }

    private WebElement getCertificate(String certificateName) {
        for (int i = 0; i <= gridPagination.getTotalPageNumber(); i++) {
            for (WebElement accessToken : certificateList) {
                String currentElementName = weToDInput(accessToken.findElement(nameLocator)).getText();
                if (currentElementName.equals(certificateName)) {
                    LOG.debug("Certificate [{}] has been found on page  [{}].", certificateName, i);

                    return accessToken;
                }

            }
            gridPagination.goToNextPage();
        }
        return null;
    }

    public Boolean isCertificatePresent(String certificateId) {
        WebElement certificate = getCertificate(certificateId);


        return certificate != null;
    }

    public HashMap<String, String> getCertificateInfo(String certificateId) throws Exception {
        HashMap<String, String> certificateIdInfo = new HashMap<>();
        WebElement accessToken = getCertificate(certificateId);

        accessToken.findElement(By.cssSelector("mat-expansion-panel")).click();

        certificateIdInfo.put("Description", weToDInput(accessToken.findElement(descriptionLocator)).getText());
        certificateIdInfo.put("Active", String.valueOf(weToDChecked(accessToken.findElement(isActiveLocator)).isChecked()));
        certificateIdInfo.put("StartDate", weToDInput(accessToken.findElement(startDateLocator)).getText());
        certificateIdInfo.put("EndDate", weToDInput(accessToken.findElement(endDateLocator)).getText());
        return certificateIdInfo;

    }

    public HashMap<String, String> getCertificateDetailsInfo(String certificateId) {
        WebElement accessToken = getCertificate(certificateId);
        accessToken.findElement(By.cssSelector("mat-expansion-panel")).click();
        accessToken.findElement(showDetailsBtnLocator).click();
        return new CertificateDetailsDialog(driver).getCertificateDetails();
    }


    public String deleteCertificate(String certificateId) {

        WebElement certificate = getCertificate(certificateId);

        certificate.findElement(By.cssSelector("mat-expansion-panel")).click();
        weToDButton(certificate.findElement(deleteBtnLocator)).click();
        new ConfirmationDialog(driver).confirm();
        String alertMessage = getAlertArea().getAlertMessage();
        return alertMessage;
    }

}
