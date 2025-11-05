package pages.userSettings.certificatesPage.accessTokensPage;

import ddsl.dcomponents.DComponent;
import ddsl.dobjects.DButton;
import ddsl.dobjects.DInput;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class ImportNewCertificatesDialog extends DComponent {
    @FindBy(css = "mat-dialog-content mat-form-field input[formcontrolname=\"description\"]")
    private WebElement descriptionInput;
    @FindBy(css = "mat-dialog-content mat-form-field input[placeholder=\"Start date\"]")
    private WebElement startDateInput;
    @FindBy(css = "mat-dialog-content mat-form-field input[placeholder=\"End date\"]")
    private WebElement enddateInput;
    @FindBy(css = "mat-dialog-content mat-checkbox")
    private WebElement activeCheckbox;
    @FindBy(id = "custom-file-upload")
    private WebElement importBtn;
    @FindBy(css = "mat-dialog-content mat-form-field input[formcontrolname=\"certificateId\"]")
    private WebElement smpCertificateId;
    @FindBy(css = "mat-dialog-content mat-form-field input[formcontrolname=\"subject\"]")
    private WebElement subjectName;
    @FindBy(css = "mat-dialog-content mat-form-field input[formcontrolname=\"validFrom\"]")
    private WebElement validFrom;
    @FindBy(css = "mat-dialog-content mat-form-field input[formcontrolname=\"validTo\"]")
    private WebElement validTo;
    @FindBy(css = "mat-dialog-content mat-form-field input[formcontrolname=\"issuer\"]")
    private WebElement issuerName;
    @FindBy(css = "mat-dialog-content mat-form-field input[formcontrolname=\"serialNumber\"]")
    private WebElement serialNumber;
    @FindBy(id = "storeCertificateCredentialsButton")
    private WebElement saveCertificateBtn;
    @FindBy(id = "closeDialogButton")
    private WebElement closeBtn;
    @FindBy(css = "mat-dialog-content div[id=\"alertmessage_id\"]")
    private WebElement alertMessage;


    public ImportNewCertificatesDialog(WebDriver driver) {

        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    public DInput getDescriptionInput() {
        return new DInput(driver, descriptionInput);
    }

    public void importCertificate(String filePath) {
        importBtn.sendKeys(filePath);
    }

    public DButton getSaveCertificateBtn() {
        return new DButton(driver, saveCertificateBtn);
    }

    public String getAlertMessage() {
        return alertMessage.getText();
    }
}
