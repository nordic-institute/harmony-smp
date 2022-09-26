package pages.service_groups.edit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import utils.Generator;
import utils.PROPERTIES;

import java.io.File;

public class ServiceMetadataWizardPopup extends PageComponent {
    public ServiceMetadataWizardPopup(WebDriver driver) {
        super(driver);
        PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
    }
    @FindBy(css = "#documentIdentifierScheme_id")
    private WebElement docIdSchemeField;

    @FindBy(css = "#processSchema_id")
    private WebElement processSchemeField;

    @FindBy(css = "service-metadata-wizard-dialog input#documentIdentifier_id")
    private WebElement docIdField;

    @FindBy(css = "#processidentifier_id")
    private WebElement processIdField;

    @FindBy(css = "#endpointUrl_id")
    private WebElement endpointUrl;

    @FindBy(css = "#transportProfiler_id")
    private WebElement transportProfileField;

    @FindBy(css = "service-metadata-wizard-dialog > mat-dialog-actions > button[type='button']:nth-child(1)")
    private WebElement okButton;

    @FindBy(css = "service-metadata-wizard-dialog > mat-dialog-actions > button[type='button']:nth-child(2)")
    private WebElement cancelButton;

    @FindBy(css="input#certificate-file-upload")
    private WebElement uploadCertificateBtn;


    @FindBy(css="#metadatacertificate_id")
    private WebElement certificateDescriptionBox;


    public String docIDFieldContain(){
       return docIdField.getAttribute("value");
    }

    public String docIDSchemeFieldContain(){
        return docIdSchemeField.getAttribute("value");
    }

    public String transportProfileFieldContent(){
        return transportProfileField.getAttribute("value");
    }


    public void clickUploadCertificate(){
        waitForElementToBeVisible(uploadCertificateBtn);
        uploadCertificateBtn.click();
    }

    public void fillCerificateBox(String generator){
        certificateDescriptionBox.sendKeys(generator);
    }

    public void uploadCertificate(String relativePath){
        waitForElementToBeVisible(uploadCertificateBtn);
        uploadCertificateBtn.click();
        String path = new File(relativePath).getAbsolutePath();
        uploadCertificateBtn.sendKeys(path);
    }

    public boolean isEnableOkBtn(){
        waitForElementToBeVisible(okButton);
        return okButton.isEnabled();
    }

    public ServiceMetadataPopup clickOK(){
        waitForElementToBeVisible(okButton);
        okButton.click();
        return new ServiceMetadataPopup(driver);
    }

    public void fillForm(String docID,String docScheme,String processID,String processScheme,String transportProfile,String url){
        waitForElementToBeVisible(docIdField);

        clearAndFillInput(docIdField, docID);
        clearAndFillInput(docIdSchemeField, docScheme);
        clearAndFillInput(processIdField, processID);
        clearAndFillInput(processSchemeField, processScheme);
        clearAndFillInput(transportProfileField, transportProfile);
        clearAndFillInput(endpointUrl,url);


    }
}
