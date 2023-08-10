package ddsl;

import ddsl.dcomponents.Grid.GridPagination;
import ddsl.dcomponents.Grid.SmallGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class PageWithGrid extends DomiSMPPage {

    @FindBy(css = "mat-form-field input")
    public WebElement filterInput;
    @FindBy(css = "data-panel >div >div> mat-toolbar button:first-of-type")
    public WebElement addBtn;

    public PageWithGrid(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);
    }

    public GridPagination getPagination() {
        return new GridPagination(driver);
    }

    public SmallGrid getGrid() {
        return new SmallGrid(driver);
    }
}
