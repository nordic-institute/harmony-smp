package ddsl.dcomponents.Grid;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SmallGrid extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(SmallGrid.class);

    @FindBy(css = "data-panel table thead th")
    protected List<WebElement> gridHeaders;
    @FindBy(css = "data-panel table tbody tr")
    protected List<WebElement> gridRows;

    public SmallGrid(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);

    }

    public GridPagination getGridPagination() {
        return new GridPagination(driver);
    }

    public List<WebElement> gerGridHeaders() {
        return wait.defaultWait.until(ExpectedConditions.visibilityOfAllElements(gridHeaders));
    }

    public List<WebElement> getRows() {
        wait.forXMillis(50);
        return wait.defaultWait.until(ExpectedConditions.visibilityOfAllElements(gridRows));
    }

    public List<WebElement> getCells(WebElement row) {
        return row.findElements(By.cssSelector("td"));
    }

    public WebElement searchValueInColumn(String columnName, String value) {

        Integer numOfPages = getGridPagination().getTotalPageNumber();
        List<WebElement> rowHeaders = gerGridHeaders();
        int columnIndex = -1;
        for (int i = 0; i < rowHeaders.size(); i++) {
            if (rowHeaders.get(i).getText().equals(columnName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex == -1) {
            return null;
        }
        for (int pageNr = 1; pageNr < numOfPages + 1; pageNr++) {

            List<WebElement> rows = getRows();
            for (WebElement row : rows) {
                List<WebElement> cells = getCells(row);
                if (cells.get(columnIndex).getText().equals(value)) {
                    LOG.debug("[{}] found on page [{}]", value, pageNr);
                    return row;
                }
            }
            getGridPagination().goToNextPage();

        }
        return null;
    }


}

