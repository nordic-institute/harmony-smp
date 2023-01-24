package pages.users;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.components.baseComponents.PaginationControls;
import pages.components.grid.BasicGrid;

import java.util.ArrayList;
import java.util.List;

public class UsersGrid extends BasicGrid {
	public UsersGrid(WebDriver driver, WebElement container) {
		super(driver, container);
	}

	public List<UserRowInfo> getRows() {
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

	public boolean isUserListed(String username) {
		PaginationControls pagination = new PaginationControls(driver);
		pagination.skipToFirstPage();
		List<UserRowInfo> rows;
		int count = 0;
		do {
			if (count != 0) {
				pagination.goToNextPage();
			}
			rows =getRows();
			for (UserRowInfo row : rows) {
				if (row.getUsername().equalsIgnoreCase(username)) {
					return true;
				}
			}
			count++;
		}
		while (pagination.hasNextPage());
		return false;
	}

	public int scrollToUser(String username) {
		PaginationControls pagination = new PaginationControls(driver);
		pagination.skipToFirstPage();
		List<UserRowInfo> rows;
		int count = 0;
		do {
			if(count!=0){
				pagination.goToNextPage();
			}
			rows = getRows();
			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i).getUsername().equalsIgnoreCase(username)) {
					return i;
				}
			}
			count++;
		} while (pagination.hasNextPage());
		return -1;
	}

	public int scrollToUserWithRole(String role) {
		PaginationControls pagination = new PaginationControls(driver);
		pagination.skipToFirstPage();
		List<UserRowInfo> rows;
		int count = 0;

		do {
			if(count!=0){
				pagination.goToNextPage();
			}
			rows = getRows();
			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i).getRole().equalsIgnoreCase(role)) {
					return i;
				}
			}
			count++;

		    } while (pagination.hasNextPage());
		      return -1;
	     }
	}



