package pages.keystore;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import pages.domain.DomainGrid;
import pages.domain.DomainPage;
import utils.PROPERTIES;


public class KeyStoreEditDialog extends PageComponent {

    public KeyStoreEditDialog(WebDriver driver){
        super(driver);
        PageFactory.initElements( new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);

    }

    @FindBy(css = "#keystoreTable_id")
    private WebElement keystoreTable;

    @FindBy(xpath = "//span[text()='Import keystore']")
    private WebElement importKeystore;

    @FindBy(css = "button[mat-dialog-close]")
    private WebElement closeBtn;

    public KeyStoreImportDialog clickImportKeystore()
    {
        log.info("clicking import keystore");
        waitForElementToBeClickable(importKeystore).click();
        waitForElementToBeGone(importKeystore);
        return new KeyStoreImportDialog(driver);
    }

    public DomainPage clickCloseInKeystore(){
        log.info("clicking close btn in keystore edit dialog");
        waitForElementToBeClickable(closeBtn).click();
        waitForElementToBeGone(closeBtn);
        return new DomainPage(driver);
    }

    public KeyStoreGrid grid(){
        return new KeyStoreGrid(driver, keystoreTable);
    }

}
