package pages.users;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.components.grid.BasicGrid;

import java.util.ArrayList;
import java.util.List;

public class UsersGrid extends BasicGrid {
	public UsersGrid(WebDriver driver, WebElement container) {
		super(driver, container);
	}
	
	public List<UserRowInfo> getRows(){
		List<UserRowInfo> rowInfos = new ArrayList<>();
		
		for (WebElement gridRow : gridRows) {
			List<WebElement> cells = gridRow.findElements(By.tagName("datatable-body-cell"));
			UserRowInfo rowInfo = new UserRowInfo();
			rowInfo.setUsername(cells.get(0).getText().trim());
			rowInfo.setCertificate(cells.get(1).getText().trim());
			rowInfo.setRole(cells.get(2).getText().trim());
			rowInfos.add(rowInfo);
		}
		return rowInfos;
	}
	
	
	
}
