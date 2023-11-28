package pages.administration.editResourcesPage;

import ddsl.DomiSMPPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Page object for the Edit resource document page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class EditResourceDocumentPage extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(EditResourceDocumentPage.class);

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
    @FindBy(id = "document version_id")
    private WebElement versionDdl;

    @FindBy(css = "ngx-codemirror[formcontrolname= \"payload\"] div textarea")
    private WebElement codeEditorSendValueElement;

    @FindBy(css = ".CodeMirror-line")
    private List<WebElement> codeEditorReadValueElement;


    protected EditResourceDocumentPage(WebDriver driver) {
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
    public void clickOnGenerate() {
        weToDButton(generateBtn).click();
    }
    public void clickOnSave() {
        weToDButton(saveBtn).click();
    }
    public void clickOnValidate() {
        weToDButton(validateBtn).click();
    }

    public void selectDocumentVersion(int version) {
        weToDSelect(versionDdl).selectValue(String.valueOf(version));
    }
}
