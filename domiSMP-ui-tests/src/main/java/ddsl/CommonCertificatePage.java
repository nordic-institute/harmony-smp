package ddsl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CommonCertificatePage extends CommonPageWithTabsAndGrid {
    /**
     * Common page used for Keystore and Truststore
     */
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
        return publicKeyTypeLbl.getAttribute("value");
    }

    public String getAliasIdLbl() {
        return aliasIdLbl.getAttribute("value");
    }

    public String getSmpCertificateIdLbl() {
        return smpCertificateIdLbl.getAttribute("value");
    }

    public String getSubjectNameLbl() {
        return subjectNameLbl.getAttribute("value");
    }

    public String getValidFromLbl() {
        return validFromLbl.getAttribute("value");
    }

    public String getValidToLbl() {
        return validToLbl.getAttribute("value");
    }

    public String getIssuerLbl() {
        return issuerLbl.getAttribute("value");
    }

    public String getSerialNumberLbl() {
        return serialNumberLbl.getAttribute("value");
    }

    public String getCertificateRevolcationListURLlbl() {
        return certificateRevolcationListURLlbl.getAttribute("value");
    }
}
