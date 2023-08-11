package ddsl.dcomponents.Grid;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SmallGrid extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(SmallGrid.class);
    protected static final By gridHeadersLocator = By.cssSelector("table thead th");
    protected static final By gridRowsLocator = By.cssSelector("table tbody tr");
    private final WebElement parentElement;


    public SmallGrid(WebDriver driver, WebElement parentElement) {
        super(driver);
        PageFactory.initElements(driver, this);
        this.parentElement = parentElement;
        wait.forElementToBeVisible(parentElement);


    }

    public GridPagination getGridPagination() {
        return new GridPagination(driver, parentElement);
    }

    public List<WebElement> getGridHeaders() {
        return parentElement.findElements(gridHeadersLocator);
    }

    public List<WebElement> getRows() {
        return parentElement.findElements(gridRowsLocator);
    }

    public List<WebElement> getCells(WebElement row) {
        return row.findElements(By.cssSelector("td"));
    }

    public WebElement searchValueInColumn(String columnName, String value) {

        Integer numOfPages = getGridPagination().getTotalPageNumber();
        List<WebElement> rowHeaders = getGridHeaders();
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

