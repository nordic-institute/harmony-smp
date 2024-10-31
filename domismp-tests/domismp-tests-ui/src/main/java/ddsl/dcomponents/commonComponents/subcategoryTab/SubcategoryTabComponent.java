package ddsl.dcomponents.commonComponents.subcategoryTab;

import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dcomponents.DComponent;
import ddsl.dcomponents.Grid.MatSmallGrid;
import ddsl.dcomponents.commonComponents.members.InviteMembersWithGridPopup;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Generic component for Subcategory tabs. It contains the WebElements and the methods specific to the dialog.
 */
public class SubcategoryTabComponent extends DComponent {
    @FindBy(id = "createButton")
    private WebElement createBtn;
    @FindBy(css = "mat-toolbar-row button[id=\"editButton\"]")
    private WebElement editBtn;
    @FindBy(id = "groupMembersButton")
    private WebElement membersBtn;
    @FindBy(id = "deleteButton")
    private WebElement deleteBtn;
    @FindBy(css = "[class=smp-column-data]")
    private WebElement rightSidePanel;

    public SubcategoryTabComponent(WebDriver driver) {
        super(driver);
    }

    public MatSmallGrid getGrid() {
        return new MatSmallGrid(driver, rightSidePanel);
    }

    protected void create() throws ElementNotInteractableException {
        weToDButton(createBtn).click();
    }

    protected InviteMembersWithGridPopup clickOnMembersButton() {
        weToDButton(membersBtn).click();
        return new InviteMembersWithGridPopup(driver);
    }

    protected void edit(String columnName, String value) throws Exception {
        WebElement tobeEdited = getGrid().searchAndGetElementInColumn(columnName, value);
        try {
            tobeEdited.click();
            wait.forElementToBeEnabled(editBtn);
            weToDButton(editBtn).click();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Element was not found in the grid: " + value);
        }

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
