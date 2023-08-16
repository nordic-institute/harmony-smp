package ddsl;

import ddsl.dcomponents.Grid.GridPagination;
import ddsl.dcomponents.Grid.SmallGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class PageWithGrid extends DomiSMPPage {

    /**
     * Generic page used for pages which have small grid in the right of the page. This element gives access to action buttons and elements of the page.
     */

    @FindBy(css = "mat-form-field input")
    public WebElement FilterInput;
    @FindBy(css = "data-panel >div >div> mat-toolbar button:first-of-type")
    public WebElement AddBtn;

    @FindBy(css = "data-panel >div >div> mat-toolbar button:last-of-type")
    public WebElement DeleteBtn;
    @FindBy(css = "data-panel > div [class=\"smp-column-data\"]")
    public WebElement SidePanel;

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

    public void filter(String filterValue) {
        try {
            weToDInput(FilterInput).fill(filterValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
