package pages.systemSettings.keyStorePage;

import ddsl.dcomponents.DComponent;
import ddsl.enums.KeyStoreTypes;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyStoreImportDialog extends DComponent {
    /**
     * This is the page object for the Keystore import dialog. It contains the webelements and the methods specific to the dialog.
     */
    private final static Logger LOG = LoggerFactory.getLogger(KeyStoreImportDialog.class);

    @FindBy(id = "keystore-file-upload")
    private WebElement importKeyStoreInput;
    @FindBy(id = "keystoreFilename")
    private WebElement keyStoreFileNameLbl;
    @FindBy(id = "keystoretype_id")
    private WebElement keyStoreTypeDdl;
    @FindBy(id = "password_id")
    private WebElement passwordIdInput;
    @FindBy(css = "mat-dialog-actions button:first-of-type")
    private WebElement importBtn;

    public KeyStoreImportDialog(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);

    }

    public void addCertificate(String filepath, KeyStoreTypes keyStoreTypes, String password) {
        try {
            importKeyStoreInput.sendKeys(filepath);
            weToDSelect(keyStoreTypeDdl).selectValue(keyStoreTypes.toString());
            weToDInput(passwordIdInput).fill(password);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clickImport() {
        try {
            if (importBtn.isEnabled()) {
                weToDButton(importBtn).click();
            }
        } catch (Exception e) {
            LOG.error("Could not press Import Keystore button", e);
            throw new RuntimeException(e);
        }
    }
}

