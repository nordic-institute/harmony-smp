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

	public boolean isUserListed(String username) {
		boolean end = false;

		UsersPage page = new UsersPage(driver);
		page.pagination.skipToFirstPage();

		while (!end) {
			page = new UsersPage(driver);
			List<UserRowInfo> rows = page.grid().getRows();

			for (UserRowInfo row : rows) {
				if (row.getUsername().equalsIgnoreCase(username)) {
					return true;
				}
			}

			if (page.pagination.hasNextPage()) {
				page.pagination.goToNextPage();
			} else {
				end = true;
			}
		}

		return false;
	}

	public int scrollToUser(String username) {

		UsersPage page = new UsersPage(driver);
		page.pagination.skipToFirstPage();

		boolean end = false;
		while (!end) {
			page = new UsersPage(driver);

			List<UserRowInfo> rows = page.grid().getRows();
			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i).getUsername().equalsIgnoreCase(username)) {
					return i;
				}
			}

			if (page.pagination.hasNextPage()) {
				page.pagination.goToNextPage();
			} else {
				end = true;
			}
		}

		return -1;
	}

	public int scrollToUserWithRole(String role) {
		UsersPage page = new UsersPage(driver);
		page.pagination.skipToFirstPage();

		boolean end = false;
		while (!end) {
			page = new UsersPage(driver);

			List<UserRowInfo> rows = page.grid().getRows();
			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i).getRole().equalsIgnoreCase(role)) {
					return i;
				}
			}

			if (page.pagination.hasNextPage()) {
				page.pagination.goToNextPage();
			} else {
				end = true;
			}
		}

		return -1;
	}
	
	
}
