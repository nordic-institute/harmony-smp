package ddsl.dcomponents.Grid;

import ddsl.dcomponents.DComponent;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class SmallGrid extends DComponent {
    @FindBy(css = "datatable-header div.datatable-row-center datatable-header-cell")
    protected List<WebElement> gridHeaders;
    @FindBy(css = "datatable-body-row > div.datatable-row-center.datatable-row-group")
    protected List<WebElement> gridRows;

    public SmallGrid(WebDriver driver) {
        super(driver);
    }

    public String getFirstValue() {
        return gridRows.get(0).getText();
    }
}

