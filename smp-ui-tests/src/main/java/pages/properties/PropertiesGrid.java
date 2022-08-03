package pages.properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.components.grid.BasicGrid;
import pages.properties.PropertyRowInfo;
import pages.users.UserRowInfo;

import java.util.ArrayList;
import java.util.List;

public class PropertiesGrid extends BasicGrid {


    public PropertiesGrid(WebDriver driver, WebElement container) {
        super(driver, container);
    }
    public List<PropertyRowInfo> getRows() {
        List<PropertyRowInfo> rowInfos = new ArrayList<>();

        for (WebElement gridRow : gridRows) {
            List<WebElement> cells = gridRow.findElements(By.tagName("datatable-body-cell"));
            PropertyRowInfo rowInfo = new PropertyRowInfo();
            rowInfo.setPropertyname(cells.get(0).getText().trim());
            rowInfo.setPropertyvalue(cells.get(1).getText().trim());
            rowInfos.add(rowInfo);
        }
        return rowInfos;
    }

    public Boolean rowContainPropertyName(String property){
        List<PropertyRowInfo> rows = getRows();
        int i = rows.size();
        Boolean bool=rows.size() == 1 && rows.get(i-1).getPropertyname().equalsIgnoreCase(property);

        return bool;

    }
}
