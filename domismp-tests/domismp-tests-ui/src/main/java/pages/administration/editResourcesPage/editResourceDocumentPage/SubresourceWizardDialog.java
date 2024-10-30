package pages.administration.editResourcesPage.editResourceDocumentPage;

import ddsl.dcomponents.DComponent;
import ddsl.dobjects.DInput;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

/**
 * Page object of Document wizard for Subresource document. This contains the locators of the page and the methods for the behaviour of the page
 */
public class SubresourceWizardDialog extends DComponent {
    @FindBy(id = "processidentifier_id")
    private WebElement processIdentifierInput;
    @FindBy(id = "processSchema_id")
    private WebElement processSchemeInput;
    @FindBy(id = "endpointUrl_id")
    private WebElement accessPointUrlInput;
    @FindBy(id = "uploadCertificateButton")
    private WebElement uploadCertificateBtn;
    @FindBy(id = "serviceDescription_id")
    private WebElement serviceDescriptionInput;
    @FindBy(id = "technicalContactUrl_id")
    private WebElement technicalContactUrlInput;
    @FindBy(id = "certificate-file-upload")
    private WebElement uploadInput;
    @FindBy(id = "generateSubresourceButton")
    private WebElement okBtn;


    public SubresourceWizardDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public void clickOK() {
        weToDButton(okBtn).click();
    }

    public DInput processIdentifierInput() {
        return weToDInput(processIdentifierInput);
    }

    public DInput accessPointUrlInput() {
        return weToDInput(accessPointUrlInput);
    }

    public void uploadCertificateBtn(String filepath) {
        uploadInput.sendKeys(filepath);
    }


}
