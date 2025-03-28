package ddsl.commonPages.commonDocumentPage;

import ddsl.dcomponents.DComponent;
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

    @FindBy(id = "name_id")
    private WebElement documentNameInput;
    @FindBy(id = "mimeType_id")
    private WebElement mimeType;
    @FindBy(id = "publishedVersion_id")
    private WebElement publishVersion;
    @FindBy(id = "sharingEnabled_id")
    private WebElement sharingEnableCheckBox;
    @FindBy(css = "document-configuration-panel .mdc-button--unelevated")
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
        weToDChecked(sharingEnableCheckBox).check();
        LOG.debug("Sharing document was enabled");
    }

    public void disableSharing() throws Exception {
        weToDChecked(sharingEnableCheckBox).uncheck();
        LOG.debug("Sharing document was disabled");

    }


    public String getReferenceDocumentName(){
        return weToDInput(refereceDocumentName).getText();
    }



}
