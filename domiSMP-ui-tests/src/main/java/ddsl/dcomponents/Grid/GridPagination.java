package ddsl.dcomponents.Grid;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Integer getTotalPageNumber() {
        String val = currentElementsLbl.getText();
        Pattern pattern = Pattern.compile("of (\\d+)");
        Matcher matcher = pattern.matcher(val);
        if (matcher.find()) {
            Integer totalRecord = Integer.parseInt(matcher.group(1));
            return (totalRecord % 5 == 0) ? (totalRecord / 5) : ((totalRecord / 5) + 1);
        }
        return null;
    }


    public void goToNextPage() {
        try {
            if (weToDButton(nextPageBtn).isPresent()) {
                weToDButton(nextPageBtn).click();
                LOG.error("Click on Grid Next Page button");

            } else {
                LOG.error("Next page button is not available.");
            }
        } catch (Exception e) {
            LOG.error("Next page button is not available.");
            throw new RuntimeException(e);
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
