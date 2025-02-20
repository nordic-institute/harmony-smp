package ddsl.commonPages.commonDocumentPage;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.ConfirmationDialog;
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


public class CommonBaseDocumentPage extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(CommonBaseDocumentPage.class);

    @FindBy(id = "document-version_id")
    public WebElement versionDdl;
    @FindBy(id = "status_id")
    public WebElement statusLbl;
    @FindBy(css = "smp-titled-label[title=\"Document mimeType:\"] div.smp-tl-value")
    private WebElement currentDocumentVersionLbl;
    // Bottom page buttons

    @FindBy(id = "back_id")
    private WebElement backBtn;


    @FindBy(css = "button.mat-mdc-tooltip-trigger:nth-child(8)")
    private WebElement approveBtn;
    @FindBy(css = "button.mat-mdc-tooltip-trigger:nth-child(9)")
    private WebElement rejectBtn;
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


    public CommonBaseDocumentPage(WebDriver driver) {
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

    public DButton getBackBtn() {
        return weToDButton(backBtn);
    }

    public DButton getRejectBtn() {
        return weToDButton(rejectBtn);
    }

    public DButton getApproveBtn() {
        return weToDButton(approveBtn);
    }

    public String getStatusValue() {
        wait.forXMillis(data.getWaitTimeoutShortMilliseconds());
        return weToDInput(statusLbl).getText();
    }

    public void clickOnApproveAndConfirm() {
        weToDButton(approveBtn).click();
        new ConfirmationDialog(driver).confirm();
    }

    public void clickOnARejectAndConfirm() {
        weToDButton(rejectBtn).click();
        new ConfirmationDialog(driver).confirm();
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

}
