package ddsl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class StorePage extends PageWithGrid {
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

    public StorePage(WebDriver driver) {
        super(driver);
    }

    public String getPublicKeyTypeLbl() {
        return publicKeyTypeLbl.getText();
    }

    public String getAliasIdLbl() {
        return aliasIdLbl.getText();
    }

    public String getSmpCertificateIdLbl() {
        return smpCertificateIdLbl.getText();
    }

    public String getSubjectNameLbl() {
        return subjectNameLbl.getText();
    }

    public String getValidFromLbl() {
        return validFromLbl.getText();
    }

    public String getValidToLbl() {
        return validToLbl.getText();
    }

    public String getIssuerLbl() {
        return issuerLbl.getText();
    }

    public String getSerialNumberLbl() {
        return serialNumberLbl.getText();
    }

    public String getCertificateRevolcationListURLlbl() {
        return certificateRevolcationListURLlbl.getText();
    }
}
