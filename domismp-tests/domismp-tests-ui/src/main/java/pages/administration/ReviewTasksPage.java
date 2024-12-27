package pages.administration;

import ddsl.DomiSMPPage;
import ddsl.dcomponents.Grid.BasicGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the Review tasks. This contains the locators of the page and the methods for the behaviour of the page
 */
public class ReviewTasksPage extends DomiSMPPage {
    @FindBy(id = "searchTable")
    private WebElement propertyTableContainer;

    public ReviewTasksPage(WebDriver driver) {
        super(driver);
    }

    private BasicGrid getGrid() {
        return new BasicGrid(driver, propertyTableContainer);
    }

}
