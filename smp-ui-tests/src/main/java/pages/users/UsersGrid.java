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
		boolean end = false;

		PaginationControls pagination = new PaginationControls(driver);
		pagination.skipToFirstPage();

		do {
			List<UserRowInfo> rows = getRows();

			for (UserRowInfo row : rows) {
				if (row.getUsername().equalsIgnoreCase(username)) {
					return true;
				}
			}

			try {
				pagination.goToNextPage();
			} catch (Exception e) {
			}
		} while (pagination.hasNextPage());

		return false;
	}

	public int scrollToUser(String username) {

		PaginationControls pagination = new PaginationControls(driver);
		pagination.skipToFirstPage();

		do {

			List<UserRowInfo> rows = getRows();
			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i).getUsername().equalsIgnoreCase(username)) {
					return i;
				}
			}
			try {
				pagination.goToNextPage();
			} catch (Exception e) {
			}

		} while (pagination.hasNextPage());

		return -1;
	}

	public int scrollToUserWithRole(String role) {
		PaginationControls pagination = new PaginationControls(driver);
		pagination.skipToFirstPage();

		do {

			List<UserRowInfo> rows = getRows();
			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i).getRole().equalsIgnoreCase(role)) {
					return i;
				}
			}

			try {
				pagination.goToNextPage();
			} catch (Exception e) {
			}

		} while (pagination.hasNextPage());

		return -1;
	}


}
