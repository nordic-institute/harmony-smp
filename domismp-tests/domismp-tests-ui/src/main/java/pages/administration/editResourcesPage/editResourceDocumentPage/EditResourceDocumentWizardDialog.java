package pages.administration.editResourcesPage.editResourceDocumentPage;

import ddsl.dcomponents.DComponent;
import ddsl.dobjects.DInput;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

/**
 * Page object of Document Wizard for Resources with Oasis 1 from EditResourceDocument page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class EditResourceDocumentWizardDialog extends DComponent {
    @FindBy(id = "ExtensionID_id")
    private WebElement extensionIdInput;
    @FindBy(id = "ExtensionName_id")
    private WebElement extensionNamenput;
    @FindBy(id = "ExtensionAgencyID_id")
    private WebElement extensionAgencyIdnput;
    @FindBy(id = "ExtensionAgencyName_id")
    private WebElement extensionAgencyNameInput;
    @FindBy(id = "ExtensionAgencyURI_id")
    private WebElement extensionAgencyURIInput;
    @FindBy(id = "ExtensionVersionID_id")
    private WebElement extensionVersionIDInput;
    @FindBy(id = "ExtensionURI_id")
    private WebElement extensionURIInput;
    @FindBy(id = "ExtensionReasonCode_id")
    private WebElement extensionReasonCodeInput;
    @FindBy(id = "ExtensionReason_id")
    private WebElement extensionReasonInput;
    @FindBy(id = "generateDocumentButton")
    private WebElement okBtn;


    public EditResourceDocumentWizardDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public void clickOK() {
        weToDButton(okBtn).click();
    }


    public DInput getExtensionIdInput() {
        return weToDInput(extensionIdInput);
    }

    public DInput getExtensionNamenput() {
        return weToDInput(extensionNamenput);
    }

    public DInput getExtensionAgencyIdnput() {
        return weToDInput(extensionAgencyIdnput);
    }

    public DInput getExtensionAgencyNameInput() {
        return weToDInput(extensionAgencyNameInput);
    }

    public DInput getExtensionAgencyURIInput() {
        return weToDInput(extensionAgencyURIInput);
    }

    public DInput getExtensionVersionIDInput() {
        return weToDInput(extensionVersionIDInput);
    }

    public DInput getExtensionURIInput() {
        return weToDInput(extensionURIInput);
    }

    public DInput getExtensionReasonCodeInput() {
        return weToDInput(extensionReasonCodeInput);
    }

    public DInput getExtensionReasonInput() {
        return weToDInput(extensionReasonInput);
    }
}
