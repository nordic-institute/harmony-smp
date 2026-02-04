package ddsl.dcomponents.Grid;

import ddsl.dcomponents.DComponent;
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

public class MatBasicGrid extends DComponent {
    private final static Logger LOG = LoggerFactory.getLogger(MatBasicGrid.class);

    @FindBy(css = "mat-table mat-header-cell")
    protected List<WebElement> gridHeaders;
    @FindBy(css = "mat-table mat-row")
    protected List<WebElement> gridRows;
    @FindBy(css = "smp-table mat-cell")
    protected List<WebElement> gridCells;
    protected ArrayList<String> headerTxt = new ArrayList<>();


    public MatBasicGrid(WebDriver driver, WebElement container) {
        super(driver);

        LOG.info("Loading basic grid");
        wait.forXMillis(data.getWaitTimeoutShortMilliseconds());
        PageFactory.initElements(new DefaultElementLocatorFactory(container), this);

        for (WebElement gridHeader : gridHeaders) {
            headerTxt.add(gridHeader.getText().trim());
        }

    }

    public void doubleClickRow(String fieldName) {
        gridRows.forEach(row -> {
                    if (row.getText().startsWith(fieldName)) {
                        Actions action = new Actions(driver);
                        action.doubleClick(row).perform();
                    }
                }

        );
        wait.forXMillis(data.getWaitTimeoutShortMilliseconds());
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
}
