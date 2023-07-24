package ddsl.dcomponents.Grid;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridPagination extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(GridPagination.class);

    @FindBy(css = "#extension-paginator > div > div > div > div")
    public WebElement currentElementsLbl;

    @FindBy(css = "#extension-paginator > div > div > div  [aria-label = \"First page\"]")
    public WebElement firstPageBtn;
    @FindBy(css = "#extension-paginator > div > div > div  [aria-label = \"Next page\"]")
    public WebElement previousPageBtn;

    @FindBy(css = "#extension-paginator > div > div > div  [aria-label = \"Next page\"]")
    public WebElement nextPageBtn;

    @FindBy(css = "#extension-paginator > div > div > div  [aria-label = \"Last page\"]")
    public WebElement lastPageBtn;


    public GridPagination(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }


    public void goToNextPage() throws Exception {
        if (weToDButton(nextPageBtn).isEnabled()) {
            weToDButton(nextPageBtn).click();

        } else {
            LOG.error("Next page button is not available.");
        }
    }


    public void goToPreviousPage() throws Exception {
        if (weToDButton(previousPageBtn).isEnabled()) {
            weToDButton(previousPageBtn).click();

        } else {
            LOG.error("Previous page button is not available.");
        }
    }
}
