package ddsl.dcomponents;

import ddsl.dcomponents.Grid.BasicGrid;
import ddsl.dcomponents.Grid.GridPagination;
import ddsl.dcomponents.Grid.SmallGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GenericPageWithGrid extends DomiSMPPage {

    public BasicGrid grid;
    @FindBy(css = "mat-form-field input")
    public WebElement FilterInput;
    @FindBy(css = "data-panel >div >div> mat-toolbar button:first-of-type")
    public WebElement AddBtn;

    @FindBy(css = "data-panel >div >div> mat-toolbar button:last-of-type")
    public WebElement DeleteBtn;
    @FindBy(css = "data-panel > div [class=\"smp-column-data\"]")
    public WebElement SidePanel;


    public GenericPageWithGrid(WebDriver driver) {
        super(driver);
    }

    public GridPagination GridPagination() {
        return new GridPagination(driver);
    }

    public SmallGrid SmallGrid() {
        return new SmallGrid(driver);
    }


}
