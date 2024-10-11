package ddsl.dcomponents.Grid;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class GridWithoutPagination extends DComponent {

    protected static final By gridHeadersLocator = By.cssSelector("table thead th");
    protected static final By gridRowsLocator = By.cssSelector("table tbody tr");
    private final static Logger LOG = LoggerFactory.getLogger(GridWithoutPagination.class);
    private final WebElement parentElement;


    public GridWithoutPagination(WebDriver driver, WebElement parentElement) {
        super(driver);
        PageFactory.initElements(driver, this);
        this.parentElement = parentElement;

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

    public void searchAndDoubleClickElementInColumn(String columnName, String value) {

        wait.forXMillis(100);
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


        List<WebElement> rows = getRows();
        for (WebElement row : rows) {
            List<WebElement> cells = getCells(row);
            WebElement currentCell = cells.get(columnIndex);
            if (currentCell.getText().equalsIgnoreCase(value)) {
                isElementPresent = true;
                LOG.debug("Value: " + value + " has been found");
                //Double Click on top right corner of element to prevent clicking on tooltip
                Actions act = new Actions(driver);
                act.moveToElement(currentCell, currentCell.getSize().getWidth() - 1, 0)
                        .doubleClick()
                        .perform();


                return;
            }
        }
        LOG.error("Value " + value + " has not been found in the grid");
    }

    public WebElement searchAndGetPrecedentSiblingElementInColumn(String columnName, String value) {

        wait.forXMillis(100);
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
        List<WebElement> rows = getRows();
        for (WebElement row : rows) {
            List<WebElement> cells = getCells(row);
            WebElement currentCell = cells.get(columnIndex);
            if (currentCell.getText().equals(value)) {
                LOG.debug("[{}] found", value);
                return currentCell.findElement(By.xpath("preceding-sibling::*"));
            }
        }
        return null;
    }

    public WebElement searchAndGetFollowingSiblingElementInColumn(String columnName, String value) {

        wait.forXMillis(100);
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
        List<WebElement> rows = getRows();
        for (WebElement row : rows) {
            List<WebElement> cells = getCells(row);
            WebElement currentCell = cells.get(columnIndex);
            if (currentCell.getText().equals(value)) {
                LOG.debug("[{}] found", value);
                return currentCell.findElement(By.xpath("following-sibling::*"));
            }
        }
        return null;
    }

}
