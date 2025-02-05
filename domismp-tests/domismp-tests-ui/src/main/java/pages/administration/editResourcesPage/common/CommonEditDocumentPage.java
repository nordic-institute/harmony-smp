package pages.administration.editResourcesPage.common;

import ddsl.DomiSMPPage;
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
import pages.administration.editResourcesPage.editSubresourceDocumentPage.SubresourceDocumentConfigurationSection;
import pages.administration.editResourcesPage.editSubresourceDocumentPage.SubresourceDocumentPropertiesSection;

import java.util.ArrayList;
import java.util.List;

public class CommonEditDocumentPage extends DomiSMPPage {


    private final static Logger LOG = LoggerFactory.getLogger(CommonEditDocumentPage.class);
    @FindBy(id = "documentWizard_id")
    public WebElement documentWizardBtn;
    @FindBy(id = "document-version_id")
    public WebElement versionDdl;
    @FindBy(id = "status_id")
    public WebElement statusLbl;
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
    @FindBy(id = "back_id")
    // Bottom page buttons
    private WebElement backBtn;
    @FindBy(id = "saveResource_id")
    private WebElement saveBtn;
    @FindBy(id = "cancel_id")
    private WebElement cancelBtn;
    @FindBy(id = "reviewResource_id")
    private WebElement reviewRequestBtn;
    @FindBy(css = "button.mat-mdc-tooltip-trigger:nth-child(8)")
    private WebElement approveBtn;
    @FindBy(css = "button.mat-mdc-tooltip-trigger:nth-child(9)")
    private WebElement rejectBtn;
    @FindBy(id = "publishResource_id")
    private WebElement publishBtn;
    @FindBy(css = ".cm-content")
    private WebElement codeEditorSendValueElement;
    @FindBy(css = "div.cm-line")
    private List<WebElement> codeEditorReadValueElement;
    // Right Menu buttons
    @FindBy(css = ".panel > expandable-panel:nth-child(2) > div:nth-child(1) > div:nth-child(2) button:nth-of-type(2)")
    private WebElement currentDocumentVersionsMenuBtn;
    @FindBy(css = ".panel > expandable-panel:nth-child(2) > div:nth-child(1) > div:nth-child(2) button:nth-of-type(3)")
    private WebElement documentConfigurationMenuBtn;
    @FindBy(css = ".panel > expandable-panel:nth-child(2) > div:nth-child(1) > div:nth-child(2) button:nth-of-type(4)")
    private WebElement documentPropertiesnMenuBtn;
    @FindBy(css = ".panel > expandable-panel:nth-child(2) > div:nth-child(1) > div:nth-child(2) button:nth-of-type(5)")
    private WebElement AllDocumentVersionsMenuBtn;

    public CommonEditDocumentPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

        LOG.debug("Loading Edit resource document page.");
    }

    public String getDocumentValue() {
        ArrayList<String> document = new ArrayList<>();
        for (WebElement el : codeEditorReadValueElement) {
            document.add(el.getText());
        }

        String formatedDoc = document.toString();

        formatedDoc
                = formatedDoc.replace("[", "")
                .replace("]", "")
                .replace(",", "");


        return formatedDoc;
    }

    public void setDocumentValue(String documentValue) throws Exception {
        weToDInput(codeEditorSendValueElement).click();
        weToDInput(codeEditorSendValueElement).clear();
        weToDInput(codeEditorSendValueElement).fill(documentValue);

    }

    public DButton getNewVersionBtn() {
        return weToDButton(newVersionBtn);
    }

    public DButton getGenerateBtn() {
        return weToDButton(generateBtn);
    }

    public DButton getBackBtn() {
        return weToDButton(backBtn);

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

    public DButton getRejectBtn() {
        return weToDButton(rejectBtn);
    }
    public DButton getApproveBtn() {
        return weToDButton(approveBtn);
    }

    public DButton getPublishBtn() {
        return weToDButton(publishBtn);
    }

    public String getStatusValue() {
        wait.forXMillis(data.getWaitTimeoutShortMilliseconds());
        return weToDInput(statusLbl).getText();
    }

    public DButton getValidateBtn() {
        return weToDButton(validateBtn);
    }

    public void clickOnApproveAndConfirm() {
        weToDButton(approveBtn).click();
        new ConfirmationDialog(driver).confirm();
    }

    public void clickOnARejectAndConfirm() {
        weToDButton(rejectBtn).click();
        new ConfirmationDialog(driver).confirm();
    }

    public void clickOnPublishAndConfirm() {
        weToDButton(publishBtn).click();
        new ConfirmationDialog(driver).confirm();
    }

    public void selectVersion(int version) {
        weToMatSelect(versionDdl).selectByVisibleText(String.valueOf(version));
    }

    public SubresourceDocumentConfigurationSection getDocumentConfigurationSection() {
        weToDButton(documentConfigurationMenuBtn).click();
        //Click again to remove the tooltip of the button
        weToDButton(documentConfigurationMenuBtn).click();

        return new
                SubresourceDocumentConfigurationSection(driver);
    }

    public SubresourceDocumentPropertiesSection getDocumentPropertiesSection() {
        wait.forXMillis(500);
        weToDButton(documentPropertiesnMenuBtn).click();
        //Click again to remove the tooltip of the button
        weToDButton(documentPropertiesnMenuBtn).click();
        LOG.debug("Opening Document properties section.");
        return new SubresourceDocumentPropertiesSection(driver);
    }

    public MatSelect getViewDocumentSelect() {
        return weToMatSelect(viewDocumentDdl);
    }
}
