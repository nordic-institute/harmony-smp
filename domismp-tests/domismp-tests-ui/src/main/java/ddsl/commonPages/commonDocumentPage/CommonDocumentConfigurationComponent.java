package ddsl.commonPages.commonDocumentPage;

import ddsl.dcomponents.DComponent;
import ddsl.dobjects.DCheckbox;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common Page object for the Subresource Document configuration section. This contains the locators of the page and the methods for the behaviour of the page
 */
public class CommonDocumentConfigurationComponent extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(CommonDocumentConfigurationComponent.class);
    @FindBy(id = "publishedVersion_id")
    private WebElement publishVersion;
    @FindBy(id = "sharingEnabled_id")
    private WebElement sharingEnableCheckBox;
    @FindBy(css = "mat-toolbar.mat-toolbar:nth-child(5) > mat-toolbar-row:nth-child(1) > button:nth-child(1)")
    public WebElement selectReferenceBtn;
    @FindBy(id = "reference-document-name_id")
    private WebElement refereceDocumentName;

    public CommonDocumentConfigurationComponent(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public String getDocumentPublishedVersion() {
        return weToDInput(publishVersion).getText();
    }

    public void enableSharing() throws Exception {
        wait.forElementToBeClickable(sharingEnableCheckBox);
        weToDChecked(sharingEnableCheckBox).check();
        LOG.debug("Sharing document was enabled");
    }

    public void disableSharing() throws Exception {
        wait.forElementToBeClickable(sharingEnableCheckBox);
        weToDChecked(sharingEnableCheckBox).uncheck();
        LOG.debug("Sharing document was disabled");

    }

    public DCheckbox getEnableSharingCheckBox() {
        return weToDChecked(sharingEnableCheckBox);
    }


    public String getReferenceDocumentName(){
        return weToDInput(refereceDocumentName).getText();
    }



}
