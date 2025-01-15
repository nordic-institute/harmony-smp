package pages.administration.editResourcesPage.common;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

/**
 * Common Page object for the Subresource Document configuration section. This contains the locators of the page and the methods for the behaviour of the page
 */
public class CommonDocumentConfigurationComponent extends DComponent {
    @FindBy(id = "name_id")
    private WebElement documentNameInput;
    @FindBy(id = "mimeType_id")
    private WebElement mimeType;
    @FindBy(id = "publishedVersion_id")
    private WebElement publishVersion;
    @FindBy(id = "sharingEnabled_id-input")
    private WebElement sharingEnableCheckBox;

    public CommonDocumentConfigurationComponent(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public String getDocumentPublishedVersion() {
        return weToDInput(publishVersion).getText();
    }
}
