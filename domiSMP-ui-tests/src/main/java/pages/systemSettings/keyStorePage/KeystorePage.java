package pages.systemSettings.keyStorePage;

import ddsl.CommonCertificatePage;
import org.openqa.selenium.WebDriver;


public class KeystorePage extends CommonCertificatePage {

    public KeystorePage(WebDriver driver) {
        super(driver);
    }

    public KeyStoreImportDialog clickImportkeyStoreBtn() throws Exception {
        weToDButton(addBtn).click();
        return new KeyStoreImportDialog(driver);
    }
}
