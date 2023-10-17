package ddsl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CommonCertificatePage extends CommonPageWithGrid {
    /**
     * Common page used for Keystore and Truststore
     */
    @FindBy(id = "publicKeyType_id")
    private WebElement publicKeyTypeInput;
    @FindBy(id = "alias_id")
    private WebElement aliasIdInput;
    @FindBy(id = "certificateId_id")
    private WebElement smpCertificateIdInput;
    @FindBy(id = "subject_id")
    private WebElement subjectNameInput;
    @FindBy(css = "certificate-panel [placeholder=\"Valid from date\"]")
    private WebElement validFromInput;
    @FindBy(css = "certificate-panel [placeholder=\"Valid to date\"]")
    private WebElement validToInput;
    @FindBy(id = "issuer_id")
    private WebElement issuerInput;
    @FindBy(id = "servialNumber_id")
    private WebElement serialNumberInput;
    @FindBy(id = "clrUrl_id")
    private WebElement certificateRevolcationListURLInput;
    @FindBy(css = ".smp-warning-panel span")
    private WebElement smpWarningLbl;

    public CommonCertificatePage(WebDriver driver) {
        super(driver);
    }

    public String getPublicKeyTypeValue() {
        return weToDInput(publicKeyTypeInput).getText();
    }

    public String getAliasIdValue() {
        return weToDInput(aliasIdInput).getText();
    }

    public String getSmpCertificateIdValue() {
        return weToDInput(smpCertificateIdInput).getText();
    }

    public String getSubjectNameValue() {
        return weToDInput(subjectNameInput).getText();
    }

    public String getValidFromValue() {
        return weToDInput(validFromInput).getText();
    }

    public String getValidToValue() {
        return weToDInput(validToInput).getText();
    }

    public String getIssuerValue() {
        return weToDInput(issuerInput).getText();
    }

    public String getSerialNumberValue() {
        return weToDInput(serialNumberInput).getText();
    }

    public String getCertificateRevolcationListURLValue() {
        return weToDInput(certificateRevolcationListURLInput).getText();
    }
}
