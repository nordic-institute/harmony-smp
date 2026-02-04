package pages.userSettings.certificatesPage.accessTokensPage;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.HashMap;

public class CertificateDetailsDialog extends DComponent {
    @FindBy(id = "certificateId_id")
    private WebElement smpCertificateId;
    @FindBy(id = "subject_id")
    private WebElement subjectName;
    @FindBy(css = "keystore-certificate-dialog input[placeholder=\"Valid from date\"]")
    private WebElement validFrom;
    @FindBy(css = "mat-dialog-content mat-form-field input[placeholder=\"Valid to date\"]")
    private WebElement validTo;
    @FindBy(id = "issuer_id")
    private WebElement issuerName;
    @FindBy(id = "servialNumber_id")
    private WebElement serialNumber;
    @FindBy(id = "closeDialogButton")
    private WebElement closeBtn;


    public CertificateDetailsDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public HashMap<String, String> getCertificateDetails() {
        HashMap<String, String> certificateDetailsInfo = new HashMap<>();
        certificateDetailsInfo.put("SmpCertificateId", weToDInput(smpCertificateId).getText());
        certificateDetailsInfo.put("SubjectName", weToDInput(subjectName).getText());
        certificateDetailsInfo.put("ValidFromDate", weToDInput(validFrom).getText());
        certificateDetailsInfo.put("ValidToDate", weToDInput(validTo).getText());
        certificateDetailsInfo.put("Issuer", weToDInput(issuerName).getText());
        certificateDetailsInfo.put("SerialNumber", weToDInput(serialNumber).getText());
        return certificateDetailsInfo;
    }

}
