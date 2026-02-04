package ddsl.commonPages.commonDocumentPage;

import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dcomponents.mat.MatSelect;
import ddsl.dobjects.DButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.administration.editResourcesPage.editResourceDocumentPage.ResourceDocumentEditor;

import javax.xml.parsers.ParserConfigurationException;

public class CommonExtendedEditDocumentPage extends CommonBaseDocumentPage {


    private final static Logger LOG = LoggerFactory.getLogger(CommonExtendedEditDocumentPage.class);
    @FindBy(id = "documentWizard_id")
    public WebElement documentWizardBtn;

    @FindBy(id = "newVersion_id")
    private WebElement newVersionBtn;
    @FindBy(id = "GenerateResource_id")
    private WebElement generateBtn;
    @FindBy(id = "validateResource_id")
    private WebElement validateBtn;
    @FindBy(css = "mat-select[formcontrolname=\"selectDocumentSource\"]")
    private WebElement viewDocumentDdl;
    @FindBy(css = "smp-titled-label[title=\"Document mimeType:\"] div.smp-tl-value")
    private WebElement currentDocumentVersionLbl;
    // Bottom page buttons

    @FindBy(id = "saveResource_id")
    private WebElement saveBtn;
    @FindBy(id = "cancel_id")
    private WebElement cancelBtn;
    @FindBy(id = "reviewResource_id")
    private WebElement reviewRequestBtn;
    @FindBy(id = "publishResource_id")


    private WebElement publishBtn;
    @FindBy(css = ".cm-content")

    private WebElement AllDocumentVersionsMenuBtn;

    public CommonExtendedEditDocumentPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
        LOG.debug("Loading Edit resource document page.");
    }

    public DButton getNewVersionBtn() {
        return weToDButton(newVersionBtn);
    }

    public DButton getGenerateBtn() {
        return weToDButton(generateBtn);
    }

    public void clickOnCancelAndConfirm() {
        weToDButton(cancelBtn).click();
        new ConfirmationDialog(driver).confirm();
    }

    public DButton getSaveBtn() {
        return weToDButton(saveBtn);

    }

    public DButton getRequestReviewBtn() {
        return weToDButton(reviewRequestBtn);
    }

    public DButton getPublishBtn() {
        return weToDButton(publishBtn);
    }


    public DButton getValidateBtn() {
        return weToDButton(validateBtn);
    }

    public void clickOnPublishAndConfirm() {
        weToDButton(publishBtn).click();
        new ConfirmationDialog(driver).confirm();
    }

    public void selectVersion(int version) {
        weToMatSelect(versionDdl).selectByVisibleText(String.valueOf(version));
    }

    public MatSelect getViewDocumentSelect() {
        return weToMatSelect(viewDocumentDdl);
    }

    public ResourceDocumentEditor getEditor() throws ParserConfigurationException {
        return new ResourceDocumentEditor(this.getDocumentValue());
    }

}
