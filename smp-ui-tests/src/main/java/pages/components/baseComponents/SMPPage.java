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

	public void refreshPage(){
		driver.navigate().refresh();
	}

	public void screenshotPage(){
//		try {
//			File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
//
//			OutputStream out = new FileOutputStream(new File("screenshot.png"));
//
//
//
//			scrFile.createNewFile();
//			out.write(scrFile);
//			out.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}




}
