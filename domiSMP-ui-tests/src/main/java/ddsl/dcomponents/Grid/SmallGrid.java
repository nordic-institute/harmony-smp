package ddsl.dcomponents.Grid;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class SmallGrid extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(SmallGrid.class);
    protected static final By gridHeadersLocator = By.cssSelector("table thead th");
    protected static final By gridRowsLocator = By.cssSelector("table tbody tr");
    private final WebElement parentElement;


    public SmallGrid(WebDriver driver, WebElement parentElement) {
        super(driver);
        PageFactory.initElements(driver, this);
        this.parentElement = parentElement;

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

    public WebElement searchAndGetElementInColumn(String columnName, String value) {

        wait.forXMillis(100);
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
                WebElement currentCell = cells.get(columnIndex);
                if (currentCell.getText().equals(value)) {
                    LOG.debug("[{}] found on page [{}]", value, pageNr);
                    return currentCell;
                }
            }
            getGridPagination().goToNextPage();

        }
        return null;
    }

    public Boolean isValuePresentInColumn(String columnName, String value) {

        wait.forXMillis(100);
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
            return false;
        }
        for (int pageNr = 1; pageNr < numOfPages + 1; pageNr++) {

            List<WebElement> rows = getRows();
            for (WebElement row : rows) {
                List<WebElement> cells = getCells(row);
                WebElement currentCell = cells.get(columnIndex);
                if (currentCell.getText().equals(value)) {
                    LOG.debug("[{}] found on page [{}]", value, pageNr);
                    return true;
                }
            }
            getGridPagination().goToNextPage();

        }
        return false;
    }

    public String getColumnValueForSpecificRow(String columnNameToSearch, String valueToSearch, String searchedColumnValue) {
        wait.forXMillis(100);
        Integer numOfPages = getGridPagination().getTotalPageNumber();
        List<WebElement> rowHeaders = getGridHeaders();
        int columnIndex = -1;
        int searchedColumnIndex = -1;

        for (int i = 0; i < rowHeaders.size(); i++) {
            if (rowHeaders.get(i).getText().equals(columnNameToSearch)) {
                columnIndex = i;
            } else if (rowHeaders.get(i).getText().equals(searchedColumnValue)) {
                searchedColumnIndex = i;
            }
            if (columnIndex != -1 && searchedColumnIndex != -1) {
                break;
            }
        }

        if (columnIndex == -1 || searchedColumnIndex == -1) {
            LOG.debug("Search value was not found in the grid");
            return null;
        }
        for (int pageNr = 1; pageNr < numOfPages + 1; pageNr++) {

            List<WebElement> rows = getRows();
            for (WebElement row : rows) {
                List<WebElement> cells = getCells(row);
                WebElement currentCell = cells.get(columnIndex);
                if (currentCell.getText().equals(valueToSearch)) {
                    LOG.debug("[{}] found on page [{}]", valueToSearch, pageNr);
                    return cells.get(searchedColumnIndex).getText();
                }
            }
            getGridPagination().goToNextPage();

        }
        return null;
    }
    public void searchAndClickElementInColumn(String columnName, String value) {

        wait.forXMillis(100);
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
            LOG.error("No element found");
        }
        boolean isElementPresent = false;
        for (int pageNr = 1; pageNr < numOfPages + 1; pageNr++) {

            List<WebElement> rows = getRows();
            for (WebElement row : rows) {
                List<WebElement> cells = getCells(row);
                WebElement currentCell = cells.get(columnIndex);
                if (currentCell.getText().equals(value)) {
                    LOG.debug("[{}] found on page [{}]", value, pageNr);
                    isElementPresent = true;
                    currentCell.click();
                    break;
                }
            }
            if (isElementPresent) {
                break;
            }
            getGridPagination().goToNextPage();
        }
        if (!isElementPresent) {
            throw new NoSuchElementException("Value [" + value + "] was not found in the grid");

        }

    }


}

