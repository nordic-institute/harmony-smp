package ddsl.dcomponents.commonComponents.subcategoryTab;

import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dcomponents.DComponent;
import ddsl.dcomponents.Grid.SmallGrid;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Generic component for Subcategory tabs. It contains the WebElements and the methods specific to the dialog.
 *
 */
public class SubcategoryTabComponent extends DComponent {

    private final static Logger LOG = LoggerFactory.getLogger(SubcategoryTabComponent.class);

    @FindBy(id = "createButton")
    private WebElement createBtn;
    @FindBy(id = "editButton")
    private WebElement editBtn;
    @FindBy(id = "groupMembersButton")
    private WebElement resourceMembersBtn;
    @FindBy(id = "deleteButton")
    private WebElement deleteBtn;
    @FindBy(css = "[class=smp-column-data]")
    private WebElement rightSidePanel;

    public SubcategoryTabComponent(WebDriver driver) {
        super(driver);
    }

    public SmallGrid getGrid() {
        return new SmallGrid(driver, rightSidePanel);
    }

    public void create() throws ElementNotInteractableException {
        weToDButton(createBtn).click();
    }

    protected void edit(String columnName, String value) throws Exception {
        WebElement tobeEdited = getGrid().searchAndGetElementInColumn(columnName, value);
        tobeEdited.click();
        wait.forElementToBeEnabled(editBtn);
        weToDButton(editBtn).click();
    }

    protected void delete(String columnName, String value){
        WebElement tobeDeleted = getGrid().searchAndGetElementInColumn(columnName, value);
        tobeDeleted.click();
        wait.forElementToBeEnabled(deleteBtn);
        weToDButton(deleteBtn).click();
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(driver);
        confirmationDialog.confirm();
    }
}
