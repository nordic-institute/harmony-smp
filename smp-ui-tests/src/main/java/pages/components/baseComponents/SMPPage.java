package pages.components.baseComponents;

import org.openqa.selenium.WebDriver;
import pages.components.Sidebar;
import pages.components.messageArea.AlertArea;

public class SMPPage extends PageComponent {

	public SMPPage(WebDriver driver) {
		super(driver);
	}

	public AlertArea alertArea = new AlertArea(driver);

	public Sidebar sidebar = new Sidebar(driver);

	public Header pageHeader = new Header(driver);

	public void refreshPage() {
		driver.navigate().refresh();
		try {
			new SMPPage(driver).pageHeader.waitForTitleToBe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
