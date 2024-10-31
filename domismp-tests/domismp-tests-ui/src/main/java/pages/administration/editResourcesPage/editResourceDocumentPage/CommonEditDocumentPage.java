package pages.administration.editResourcesPage.editResourceDocumentPage;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dobjects.DButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @FindBy(css = "smp-titled-label[title=\"Resource identifier:\"] div.smp-tl-value")
    private WebElement resourceIdentifierLbl;
    @FindBy(css = "smp-titled-label[title=\"Resource scheme:\"] div.smp-tl-value")
    private WebElement resourceSchemeLbl;
    @FindBy(css = "smp-titled-label[title=\"Document name:\"] div.smp-tl-value")
    private WebElement documentNameLbl;
    @FindBy(css = "smp-titled-label[title=\"Document mimeType:\"] div.smp-tl-value")
    private WebElement documentMimeTypeLbl;
    @FindBy(css = "smp-titled-label[title=\"Current document version:\"] div.smp-tl-value")
    private WebElement currentDocumentVersionLbl;
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

    @FindBy(css = "ngx-codemirror[formcontrolname= \"payload\"] div textarea")
    private WebElement codeEditorSendValueElement;

    @FindBy(css = "div.cm-line")
    private List<WebElement> codeEditorReadValueElement;


    public CommonEditDocumentPage(WebDriver driver) {
        super(driver);
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

    public void clickOnNewVersion() {
        weToDButton(newVersionBtn).click();
    }

    public void clickOnGenerate() {
        weToDButton(generateBtn).click();
    }

    public void clickOnSave() {
        weToDButton(saveBtn).click();
    }

    public DButton getRequestReviewBtn() {
        return weToDButton(reviewRequestBtn);
    }

    public DButton getApproveBtn() {
        return weToDButton(approveBtn);
    }

    public DButton getPublishBtn() {
        return weToDButton(publishBtn);
    }

    public String getStatusValue() {
        return weToDInput(statusLbl).getText();
    }

    public void clickOnValidate() {
        weToDButton(validateBtn).click();
    }

    public void clickOnApproveAndConfirm() {
        weToDButton(approveBtn).click();
        new ConfirmationDialog(driver).confirm();
    }

    public void clickOnPublishAndConfirm() {
        weToDButton(publishBtn).click();
        new ConfirmationDialog(driver).confirm();
    }

    public void selectVersion(int version) {
        weToMatSelect(versionDdl).selectByVisibleText(String.valueOf(version));
    }


}
