package ddsl.commonPages.commonDocumentPage;

import ddsl.dcomponents.DComponent;
import ddsl.dcomponents.Grid.MatSmallGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

/**
 * Common Page object for the Subresource Document Properties section. This contains the locators of the page and the methods for the behaviour of the page
 */
public class CommonDocumentPropertiesComponent extends DComponent {
    @FindBy(id = "name_id")
    private WebElement documentNameInput;
    @FindBy(id = "mimeType_id")
    private WebElement mimeType;
    @FindBy(id = "publishedVersion_id")
    private WebElement publishVersion;
    @FindBy(id = "sharingEnabled_id-input")
    private WebElement sharingEnableCheckBox;

    @FindBy(css = "document-properties-panel")
    private WebElement rightPanel;

    public CommonDocumentPropertiesComponent(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);

    }

    private MatSmallGrid getPropertiesGrid() {
        return new MatSmallGrid(driver, rightPanel);
    }

    public String getPropertyValue(String propertyName) {
        return getPropertiesGrid().getColumnValueForSpecificRow("Property", propertyName, "Value");
    }
}
