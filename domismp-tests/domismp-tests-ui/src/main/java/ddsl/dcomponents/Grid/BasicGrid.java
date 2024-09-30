package ddsl.dcomponents.Grid;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BasicGrid extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(BasicGrid.class);

    @FindBy(css = "datatable-header div.datatable-row-center datatable-header-cell")
    protected List<WebElement> gridHeaders;
    @FindBy(css = "datatable-body-row > div.datatable-row-center.datatable-row-group")
    protected List<WebElement> gridRows;
    protected ArrayList<String> headerTxt = new ArrayList<String>();


    public BasicGrid(WebDriver driver, WebElement container) {
        super(driver);

        LOG.info("Loading basic grid");
        wait.forXMillis(500);
        PageFactory.initElements(new DefaultElementLocatorFactory(container), this);

        for (int i = 0; i < gridHeaders.size(); i++) {
            headerTxt.add(gridHeaders.get(i).getText().trim());
        }

    }

    public void selectRow(int rowNumber) {
        LOG.info("selecting row with number ... " + rowNumber);
        wait.forXMillis(500);
        if (rowNumber >= gridRows.size()) {
            return;
        }
        gridRows.get(rowNumber).click();
        wait.forXMillis(500);
    }

    public void doubleClickRow(String fieldName) {
        gridRows.forEach(row -> {
                    if (row.getText().startsWith(fieldName)) {
                        Actions action = new Actions(driver);
                        action.doubleClick(row).perform();
                    }
                }

        );
        wait.forXMillis(500);
    }

    public String getValue(String fieldName) {
        String fieldValue = null;
        for (WebElement row : gridRows) {
            if (row.getText().startsWith(fieldName)) {
                fieldValue = row.getText().replace(fieldName, "").trim();

            }
        }
        return fieldValue;

    }

    public void scrollRow(int index) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView();", gridRows.get(index));
        wait.forXMillis(500);
    }

}
