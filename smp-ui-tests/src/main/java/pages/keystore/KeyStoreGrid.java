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

public class KeyStoreGrid extends BasicGrid {

    public KeyStoreGrid(WebDriver driver, WebElement container) {
        super(driver,container);
        log.info("Loading KeyStoreGrid");
        waitForRowsToLoad();
        PageFactory.initElements(new DefaultElementLocatorFactory(container), this);
    }


   private By cellSelector = By.cssSelector("#keystoreTable_id datatable-body-cell");



    public ConfirmationDialog deleteKeyStore(int rowNum) {
        WebElement gridRow = gridRows.get(rowNum);
        List<WebElement> cells = gridRow.findElements(cellSelector);
        WebElement deleteButton = cells.get(2).findElement(By.cssSelector("button[mattooltip='Delete certificate']"));
        waitForElementToBeClickable(deleteButton).click();
        return new ConfirmationDialog(driver);
    }
}
