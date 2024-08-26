package pages.systemSettings.keyStorePage;

import ddsl.CommonCertificatePage;
import ddsl.dcomponents.ConfirmationDialog;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the Keystore page. This contains the locators of the page and the methods for the behaviour of the page
 */
public class KeystorePage extends CommonCertificatePage {
    public KeystorePage(WebDriver driver) {
        super(driver);
    }

    public KeyStoreImportDialog clickImportkeyStoreBtn(){
        weToDButton(addBtn).click();
        return new KeyStoreImportDialog(driver);
    }

    public void deleteandConfirm(){
        weToDButton(deleteBtn).click();
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(driver);
        confirmationDialog.confirm();
    }

}
