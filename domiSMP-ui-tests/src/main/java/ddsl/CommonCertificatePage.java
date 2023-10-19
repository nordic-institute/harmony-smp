package ddsl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
/**
 * Common page used for Keystore and Truststore
 */
public class CommonCertificatePage extends CommonPageWithTabsAndGrid {
    @FindBy(id = "publicKeyType_id")
    private WebElement publicKeyTypeLbl;
    @FindBy(id = "alias_id")
    private WebElement aliasIdLbl;
    @FindBy(id = "certificateId_id")
    private WebElement smpCertificateIdLbl;
    @FindBy(id = "subject_id")
    private WebElement subjectNameLbl;
    @FindBy(css = "certificate-panel [placeholder=\"Valid from date\"]")
    private WebElement validFromLbl;
    @FindBy(css = "certificate-panel [placeholder=\"Valid to date\"]")
    private WebElement validToLbl;
    @FindBy(id = "issuer_id")
    private WebElement issuerLbl;
    @FindBy(id = "servialNumber_id")
    private WebElement serialNumberLbl;
    @FindBy(id = "clrUrl_id")
    private WebElement certificateRevolcationListURLlbl;
    @FindBy(css = ".smp-warning-panel span")
    private WebElement smpWarningLbl;

    public CommonCertificatePage(WebDriver driver) {
        super(driver);
    }

    public String getPublicKeyTypeLbl() {
        return weToDInput(publicKeyTypeLbl).getText();
    }

    public String getAliasIdLbl() {
        return weToDInput(aliasIdLbl).getText();
    }

    public String getSmpCertificateIdLbl() {
        return weToDInput(smpCertificateIdLbl).getText();
    }

    public String getSubjectNameLbl() {
        return weToDInput(subjectNameLbl).getText();
    }

    public String getValidFromLbl() {
        return weToDInput(validFromLbl).getText();
    }

    public String getValidToLbl() {
        return weToDInput(validToLbl).getText();
    }

    public String getIssuerLbl() {
        return weToDInput(issuerLbl).getText();
    }

    public String getSerialNumberLbl() {
        return weToDInput(serialNumberLbl).getText();
    }

    public String getCertificateRevolcationListURLlbl() {
        return weToDInput(certificateRevolcationListURLlbl).getText();
    }
}
