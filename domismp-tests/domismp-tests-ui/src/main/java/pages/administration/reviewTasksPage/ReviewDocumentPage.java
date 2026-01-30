package pages.administration.reviewTasksPage;

import ddsl.commonPages.commonDocumentPage.CommonBaseDocumentPage;
import ddsl.dcomponents.ConfirmationDialog;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ReviewDocumentPage extends CommonBaseDocumentPage {
    @FindBy(id = "reviewApprove_id")
    private WebElement approveBtn;

    @FindBy(id = "reviewReject_id")
    private WebElement rejectBtn;

    public ReviewDocumentPage(WebDriver driver) {
        super(driver);
    }

    public void clickOnApproveAndConfirm() {
        weToDButton(approveBtn).click();
        new ConfirmationDialog(driver).confirm();
    }

    public void clickOnARejectAndConfirm() {
        weToDButton(rejectBtn).click();
        new ConfirmationDialog(driver).confirm();
    }


}
