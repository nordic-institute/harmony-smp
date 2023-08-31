package ddsl;

import ddsl.dcomponents.Grid.GridPagination;
import ddsl.dcomponents.Grid.SmallGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PageWithGrid extends DomiSMPPage {
    private final static Logger LOG = LoggerFactory.getLogger(PageWithGrid.class);

    /**
     * Generic page used for pages which have small grid in the right of the page. This element gives access to action buttons and elements of the page.
     */

    @FindBy(css = "mat-form-field input")
    public WebElement filterInput;
    @FindBy(css = "data-panel >div >div> mat-toolbar button:first-of-type")
    public WebElement addBtn;
    @FindBy(css = "data-panel")
    public WebElement dataPanel;
    @FindBy(css = "[role = \"tab\"]")
    private List<WebElement> tabList;

    public PageWithGrid(WebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getTIMEOUT()), this);
    }

    public SmallGrid getGrid() {
        return new SmallGrid(driver, dataPanel);
    }

    public GridPagination getPagination() {
        return new GridPagination(driver, dataPanel);
    }

    public SmallGrid getDataPanelGrid() {
        return new SmallGrid(driver, dataPanel);
    }

    public void goToTab(String tabName) {
        for (WebElement element : tabList) {
            if (element.getText().contains(tabName)) {
                element.click();
                wait.forAttributeToContain(element, "aria-selected", "true");
                LOG.debug("Domain tab {} is opened", tabName);
            }
        }
    }

    public String getAlertMessageAndClose() {

        return getAlertArea().getAlertMessage();
    }
}
