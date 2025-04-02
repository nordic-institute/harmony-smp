package ddsl.commonPages.commonAlertPage;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.AlertModel;

import java.util.ArrayList;
import java.util.List;

public class CommonAlertsGrid extends DComponent {

    protected static final By gridHeadersLocator = By.cssSelector("datatable-header-cell.datatable-header-cell");
    protected static final By gridRowsLocator = By.cssSelector("datatable-body datatable-row-wrapper datatable-body-row");
    private final static Logger LOG = LoggerFactory.getLogger(CommonAlertsGrid.class);
    private final WebElement parentElement;

    public CommonAlertsGrid(WebDriver driver, WebElement parentElement) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
        this.parentElement = parentElement;
    }

    private List<WebElement> getGridHeaders() {
        return parentElement.findElements(gridHeadersLocator);
    }

    private List<WebElement> getRows() {
        return parentElement.findElements(gridRowsLocator);
    }

    private List<WebElement> getCells(WebElement row) {
        return row.findElements(By.cssSelector("datatable-body-cell"));
    }


    private List<WebElement> getAllAlertsByValue(String columnNameToSearch, String valueToSearch) {
        wait.forXMillis(data.getWaitTimeoutShortMilliseconds());
        Integer numOfPages = 1;
        List<WebElement> rowsContainingSearchedValue = new ArrayList<WebElement>();
        List<WebElement> rowHeaders = getGridHeaders();
        int columnIndex = -1;

        for (int i = 0; i < rowHeaders.size(); i++) {
            if (rowHeaders.get(i).getText().equals(columnNameToSearch)) {
                columnIndex = i;
                if (columnIndex != -1) {
                    break;
                }
            }
        }

        if (columnIndex == -1) {
            LOG.debug("Search value was not found in the grid");
            return null;
        }
        for (int pageNr = 1; pageNr < numOfPages + 1; pageNr++) {

            List<WebElement> rows = getRows();
            for (WebElement row : rows) {
                List<WebElement> cells = getCells(row);
                WebElement currentCell = cells.get(columnIndex);
                if (currentCell.getText().equals(valueToSearch)) {
                    rowsContainingSearchedValue.add(row);
                }
            }
        }
        return rowsContainingSearchedValue;

    }

    private AlertModel mapGridElementsToAlertModel(WebElement alertElementRow) {
        AlertModel alertModel = new AlertModel();
        List<WebElement> cells = getCells(alertElementRow);
        alertModel.setAlertDate(cells.get(0).getText());
        alertModel.setAlertLevel(cells.get(1).getText());
        alertModel.setForUser(cells.get(2).getText());
        alertModel.setCredentialType(cells.get(3).getText());
        alertModel.setAlertType(cells.get(4).getText());
        alertModel.setAlertStatus(cells.get(5).getText());
        alertModel.setStatusDesc(cells.get(6).getText());
        return alertModel;
    }

    public List<AlertModel> getAllAlertsByUsername(String username) {
        List<AlertModel> userAlerts = new ArrayList<>();
        List<WebElement> alertsElementsRows = getAllAlertsByValue("For User", username);
        for (WebElement alertElementRow : alertsElementsRows) {
            userAlerts.add(mapGridElementsToAlertModel(alertElementRow));
        }
        return userAlerts;
    }

    public List<AlertModel> getAllAlerts() {
        List<AlertModel> userAlerts = new ArrayList<>();
        List<WebElement> alertsElementsRows = getAllAlertsByValue("Status desc.", "");
        for (WebElement alertElementRow : alertsElementsRows) {
            userAlerts.add(mapGridElementsToAlertModel(alertElementRow));
        }
        return userAlerts;
    }

    public CommonAlertDetailDialog doubleClickMostRecentAlertByAlertType(String username, String alertType) {
        List<WebElement> alerts = getAllAlertsByValue("For User", username);
        WebElement alertElement = alerts.stream().filter(el -> el.getText().contains(alertType)).findFirst().get();
        Actions action = new Actions(driver);
        action.doubleClick(alertElement).perform();
        return new CommonAlertDetailDialog(driver);
    }
}
