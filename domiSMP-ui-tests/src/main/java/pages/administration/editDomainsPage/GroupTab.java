package pages.administration.editDomainsPage;

import ddsl.dcomponents.commonComponents.subcategoryTab.SubcategoryTabComponent;
import org.openqa.selenium.WebDriver;

public class GroupTab extends SubcategoryTabComponent {
    public GroupTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public CreateGroupDetailsDialog create() {
        return new CreateGroupDetailsDialog(driver);
    }
}
