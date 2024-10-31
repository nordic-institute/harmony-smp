package pages.search;

import ddsl.dcomponents.DComponent;
import ddsl.dcomponents.Grid.GridPagination;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;


public class ResourcePageGrid extends DComponent {
    protected static final By gridHeadersLocator = By.cssSelector("datatable-header div.datatable-row-center datatable-header-cell");
    protected static final By gridRowsLocator = By.cssSelector("datatable-body-row > div.datatable-row-center.datatable-row-group");
    private final static Logger LOG = LoggerFactory.getLogger(ResourcePageGrid.class);
    private final WebElement parentElement;

    public ResourcePageGrid(WebDriver driver, WebElement parentElement) {
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
        return row.findElements(By.cssSelector("datatable-body-cell"));
    }

    public void searchAndClickElementInColumn(String columnName, String value) {

        wait.forXMillis(100);
        int numOfPages;
        try {
            numOfPages = getGridPagination().getTotalPageNumber();
        } catch (Exception e) {
            LOG.debug("No pagination found");
            numOfPages = 1;
        }
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
            throw new NoSuchElementException("Column not found");
        }
        boolean isElementPresent = false;
        for (int pageNr = 0; pageNr < numOfPages + 1; pageNr++) {

            List<WebElement> rows = getRows();
            for (WebElement row : rows) {
                List<WebElement> cells = getCells(row);
                WebElement currentCell = cells.get(columnIndex);
                if (currentCell.getText().equals(value)) {
                    LOG.debug("[{}] found on page [{}]", value, pageNr);
                    isElementPresent = true;
                    currentCell.click();
                }
            }
            if (isElementPresent) {
                return;
            }
            if (numOfPages > 1) {
                getGridPagination().goToNextPage();
            }
        }
        if (!isElementPresent) {
            throw new NoSuchElementException("Value [" + value + "] was not found in the grid");

        }

    }

    public void openSubresource(String resourceColumn, String resourceValue, String columnNameSubresouce, String valueSubresource) {

        wait.forXMillis(100);
        int numOfPages;
        try {
            numOfPages = getGridPagination().getTotalPageNumber();
        } catch (Exception e) {
            LOG.debug("No pagination found");
            numOfPages = 1;
        }
        List<WebElement> rowHeaders = getGridHeaders();
        int columnIndex = -1;
        for (int i = 0; i < rowHeaders.size(); i++) {
            if (rowHeaders.get(i).getText().equals(resourceColumn)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex == -1) {
            LOG.error("No element found");
            throw new NoSuchElementException("Column not found");
        }
        boolean isElementPresent = false;
        for (int pageNr = 0; pageNr < numOfPages + 1; pageNr++) {

            List<WebElement> rows = getRows();
            for (WebElement row : rows) {
                List<WebElement> cells = getCells(row);
                WebElement currentCell = cells.get(columnIndex);
                if (currentCell.getText().equals(resourceValue)) {
                    LOG.debug("[{}] found on page [{}]", resourceValue, pageNr);
                    isElementPresent = true;
                    currentCell.click();
                    openURLSubresouce(row, columnNameSubresouce, valueSubresource);
                }
            }
            if (isElementPresent) {
                return;
            }
            if (numOfPages > 1) {
                getGridPagination().goToNextPage();
            }
        }
        if (!isElementPresent) {
            throw new NoSuchElementException("Value [" + resourceValue + "] was not found in the grid");

        }

    }

    private void openURLSubresouce(WebElement resourceRow, String columnName, String value) {
        WebElement parentRowElement = resourceRow.findElement(By.xpath("../following-sibling::*[1]"));
        List<WebElement> rowHeaders = parentRowElement.findElements(gridHeadersLocator);
        int columnIndex = -1;
        for (int i = 0; i < rowHeaders.size(); i++) {
            if (rowHeaders.get(i).getText().equals(columnName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex == -1) {
            LOG.error("No element found");
            throw new NoSuchElementException("Column not found");
        }
        boolean isElementPresent = false;
        List<WebElement> rows = parentRowElement.findElements(gridRowsLocator);
        for (WebElement row : rows) {
            List<WebElement> cells = getCells(row);
            WebElement currentCell = cells.get(columnIndex);
            if (currentCell.getText().equals(value)) {
                LOG.debug("[{}] found on page", value);
                isElementPresent = true;
                WebElement urlCell = cells.get(2);
                urlCell.findElement(By.cssSelector("a")).click();
            }
        }
        if (isElementPresent) {
            return;
        }

        if (!isElementPresent) {
            throw new NoSuchElementException("Value [" + value + "] was not found in the grid");

        }

    }
}
