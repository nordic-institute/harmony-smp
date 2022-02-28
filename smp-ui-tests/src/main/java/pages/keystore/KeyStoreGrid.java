package pages.keystore;


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import pages.components.ConfirmationDialog;
import pages.components.baseComponents.PageComponent;
import pages.components.grid.BasicGrid;
import pages.domain.DomainRow;

import java.util.ArrayList;
import java.util.List;

public class KeyStoreGrid extends PageComponent {

    public KeyStoreGrid(WebDriver driver, WebElement container) {
        super(driver);
        log.info("Loading KeyStoreGrid");
        waitForRowsToLoad();
        PageFactory.initElements(new DefaultElementLocatorFactory(container), this);
        for (int i = 0; i < gridHeaders.size(); i++) {
            headerTxt.add(gridHeaders.get(i).getText().trim());
        }
    }

    @FindBy(css = "#keystoreTable_id datatable-header div.datatable-row-center datatable-header-cell")
    protected List<WebElement> gridHeaders;

    @FindBy(css = "#keystoreTable_id datatable-body-row > div.datatable-row-center.datatable-row-group")
    protected List<WebElement> gridRows;

    protected ArrayList<String> headerTxt = new ArrayList<String>();

    private By cellSelector = By.cssSelector("#keystoreTable_id datatable-body-cell");

//    public List<KeyStoreRow> getKeyStoreRowsInfo() {
//        log.info("getting all the rows information");
//        List<KeyStoreRow> rowInfos = new ArrayList<>();
//
//        for (WebElement gridRow : gridRows) {
//            List<WebElement> cells = gridRow.findElements(cellSelector);
//
//            KeyStoreRow row = new KeyStoreRow();
//
//            for (int i = 0; i < headerTxt.size(); i++) {
//                switch (headerTxt.get(i)) {
//                    case "Alias":
//                        row.setAlias(cells.get(i).getText().trim());
//                        break;
//                    case "Certificate id":
//                        row.setCertificateId(cells.get(i).getText().trim());
//                        break;
//                }
//            }
//            rowInfos.add(row);
//        }
//
//        return rowInfos;
//    }

    public void doubleClickRow(int rowNumber) {
        log.info("double clicking row ... " + rowNumber);
        waitForXMillis(500);
        if (rowNumber >= gridRows.size()) {
            return;
        }
        Actions action = new Actions(driver);
        action.doubleClick(gridRows.get(rowNumber)).perform();
    }

    public int getColumnsNo() {
        log.info("getting number of columns");
        return gridHeaders.size();
    }

    public int getRowsNo() {
        return gridRows.size();
    }

    /*public boolean isKeyStoreCreated(int rowNum, String keyStoreName) {
        WebElement gridRow = gridRows.get(rowNum);
        List<WebElement> cells = gridRow.findElements(cellSelector);
        for (int i = 0; i < headerTxt.size(); i++) {
            if (headerTxt.get(i).equals("Alias")) {
                if (cells.get(i).getText().trim().contains(keyStoreName)) {
                    return true;
                }
            }
        }
        return false;
    }*/

    public ConfirmationDialog deleteKeyStore(int rowNum) {
        WebElement gridRow = gridRows.get(rowNum);
        List<WebElement> cells = gridRow.findElements(cellSelector);
        WebElement deleteButton = cells.get(2).findElement(By.cssSelector("button[mattooltip='Delete certificate']"));
        waitForElementToBeClickable(deleteButton).click();
        return new ConfirmationDialog(driver);
    }
}
