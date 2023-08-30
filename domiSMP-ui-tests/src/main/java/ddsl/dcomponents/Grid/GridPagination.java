package ddsl.dcomponents.Grid;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridPagination extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(GridPagination.class);
    protected static final By currentElementsLblLocator = By.cssSelector("div.mat-mdc-paginator-range-label");
    protected static final By nextPageBtnLocator = By.cssSelector("[aria-label= \"Next page\"]");
    private final WebElement parentElement;

    public GridPagination(WebDriver driver, WebElement parentElement) {
        super(driver);
        PageFactory.initElements(driver, this);
        this.parentElement = parentElement;

    }

    public Integer getTotalPageNumber() {
        String val = parentElement.findElement(currentElementsLblLocator).getText();
        Integer numofElementsPerPage = 5;
        Pattern pattern = Pattern.compile("of (\\d+)");
        Matcher matcher = pattern.matcher(val);
        if (matcher.find()) {
            Integer totalRecord = Integer.parseInt(matcher.group(1));
            return (totalRecord % numofElementsPerPage == 0) ? (totalRecord / numofElementsPerPage) : ((totalRecord / numofElementsPerPage) + 1);
        }
        return null;
    }


    public void goToNextPage() {
        try {
            if (weToDButton(parentElement.findElement(nextPageBtnLocator)).isPresent()) {
                weToDButton(parentElement.findElement(nextPageBtnLocator)).click();
                LOG.debug("Click on Grid Next Page button");

            } else {
                LOG.error("Next page button is not available.");
            }
        } catch (Exception e) {
            LOG.error("Next page button is not available.");
            throw new RuntimeException(e);
        }
    }

}
