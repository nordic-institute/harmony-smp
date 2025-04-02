package ddsl.commonPages.commonAlertPage;

import ddsl.dcomponents.DComponent;
import ddsl.dobjects.DButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import rest.models.AlertModel;

public class CommonAlertDetailDialog extends DComponent {
    @FindBy(css = "tr.mat-mdc-row:nth-child(1) > td:nth-child(2)")
    private WebElement alertDateLbl;
    @FindBy(css = "tr.mat-mdc-row:nth-child(2) > td:nth-child(2)")
    private WebElement alertLevelLbl;
    @FindBy(css = "tr.mat-mdc-row:nth-child(3) > td:nth-child(2)")
    private WebElement forUserLbl;
    @FindBy(css = "tr.mat-mdc-row:nth-child(4) > td:nth-child(2)")
    private WebElement credentialTypeLbl;
    @FindBy(css = "tr.mat-mdc-row:nth-child(5) > td:nth-child(2)")
    private WebElement alertTypeLbl;
    @FindBy(css = "tr.mat-mdc-row:nth-child(6) > td:nth-child(2)")
    private WebElement alertStatusLbl;
    @FindBy(css = "tr.mat-mdc-row:nth-child(7) > td:nth-child(2)")
    private WebElement statusDescLbl;
    @FindBy(id = "nobuttondialog_id")
    private WebElement closeBtn;

    public CommonAlertDetailDialog(WebDriver driver) {

        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, data.getWaitTimeShort()), this);
    }

    public AlertModel getAlertDetails() {
        AlertModel alertModel = new AlertModel();
        alertModel.setAlertDate(alertDateLbl.getText());
        alertModel.setAlertLevel(alertLevelLbl.getText());
        alertModel.setForUser(forUserLbl.getText());
        alertModel.setCredentialType(credentialTypeLbl.getText());
        alertModel.setAlertType(alertTypeLbl.getText());
        alertModel.setAlertStatus(alertStatusLbl.getText());
        alertModel.setStatusDesc(statusDescLbl.getText());
        return alertModel;
    }

    public DButton getCloseBtn() {
        return weToDButton(closeBtn);
    }
}
