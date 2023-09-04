package ddsl.dcomponents.commonComponents.subcategoryTab;

import ddsl.dcomponents.DComponent;
import ddsl.dcomponents.Grid.SmallGrid;
import org.apache.poi.ss.formula.functions.T;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @FindBy(css = "div smp-column-data")
    private WebElement sidePanel;

    public SubcategoryTabComponent(WebDriver driver) {
        super(driver);
    }

    public SmallGrid getGrid() {
        return new SmallGrid(driver, sidePanel);
    }

    public DComponent create() throws Exception {
        weToDButton(createBtn).click();
        return new DComponent(driver);
    }

    public T edit(String columnName, String value) throws Exception {
        WebElement tobeEdited = getGrid().searchAndGetElementInColumn(columnName, value);
        tobeEdited.click();
        wait.forElementToBeEnabled(editBtn);
        weToDButton(editBtn).click();
        return new T();
    }
}
