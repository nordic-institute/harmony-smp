package ddsl;

import ddsl.dcomponents.Grid.SmallGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
/**
 * Generic page used for pages which have small grid in the right of the page and tabs in the left. This element gives access to action buttons and elements of the page.
 */
public class CommonPageWithTabsAndGrid extends CommonPageWithTabs {
    @FindBy(css = "mat-form-field input")
    public WebElement filterInput;
    @FindBy(css = "data-panel >div >div> mat-toolbar button:first-of-type")
    public WebElement addBtn;
    @FindBy(css = "[class~=smp-column-label]")
    public WebElement rightPanel;

    public CommonPageWithTabsAndGrid(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public SmallGrid getLeftSideGrid() {
        return new SmallGrid(driver, rightPanel);
    }


}
