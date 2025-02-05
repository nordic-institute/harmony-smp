package pages.administration.editResourcesPage.common;

import ddsl.dcomponents.DComponent;
import ddsl.dcomponents.Grid.SmallGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class CommonSelectReferenceDialog extends DComponent {
    @FindBy(id = "resource-value_id")
    private WebElement resourceValueInput;
    @FindBy(id = "resource-scheme_id")
    private WebElement resourceSchemeInput;
    @FindBy(id = "searchbutton_id")
    private WebElement searchBtn;
    @FindBy(id = "saveButton")
    private WebElement saveBtn;
    @FindBy(css = ".mat-mdc-dialog-content")
    private WebElement panel;

    public CommonSelectReferenceDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public SmallGrid getGrid() {
        return new SmallGrid(driver, panel);
    }

    public void selectResourceReferenceByResourceIdentifier(String resourceIdentifier) {
        weToDInput(resourceValueInput).fill(resourceIdentifier);
        weToDButton(searchBtn).click();
        getGrid().searchAndClickElementInColumn("Res. value", resourceIdentifier);
        weToDButton(saveBtn).click();
    }

    public boolean isResourceReferenceByResourceIdentifierPresent(String resourceIdentifier) {
        weToDInput(resourceValueInput).fill(resourceIdentifier);
        weToDButton(searchBtn).click();
        return getGrid().isValuePresentInColumn("Res. value", resourceIdentifier);

    }


}
