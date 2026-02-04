package ddsl.commonPages.commonDocumentPage;

import ddsl.dcomponents.DComponent;
import ddsl.dcomponents.Grid.SmallGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class CommonSelectReferenceDialog extends DComponent {
    @FindBy(id = "resource-value_id")
    public WebElement resourceValueInput;
    @FindBy(id = "resource-scheme_id")
    private WebElement resourceSchemeInput;
    @FindBy(id = "searchbutton_id")
    public WebElement searchBtn;
    @FindBy(id = "saveButton")
    public WebElement saveBtn;
    @FindBy(css = ".mat-mdc-dialog-content")
    private WebElement panel;

    public CommonSelectReferenceDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public SmallGrid getGrid() {
        return new SmallGrid(driver, panel);
    }



}
