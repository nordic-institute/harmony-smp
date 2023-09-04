package pages.administration.editDomainsPage;

import ddsl.PageWithGrid;
import ddsl.dcomponents.commonComponents.members.MembersComponent;
import org.openqa.selenium.WebDriver;

public class EditDomainsPage extends PageWithGrid {
    public EditDomainsPage(WebDriver driver) {
        super(driver);
    }

    public MembersComponent getMembersTab() {

        return new MembersComponent(driver);
    }
}
