package pages.keystore;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;

public class KeyStoreImportDialog extends PageComponent{
    protected Logger log = Logger.getLogger(this.getClass());
    public KeyStoreImportDialog(WebDriver driver) {
            super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, PROPERTIES.TIMEOUT), this);
              }
    @FindBy(xpath = "//span[text()='Import']")
    private WebElement importBtn;

    @FindBy(css = "keystore-import-dialog button[mat-dialog-close]")
    private WebElement cancelBtn;

    @FindBy(css = "button[mat-dialog-close]")
    private WebElement closeBtn;

    @FindBy(css = "#keystore-file-upload")
    private WebElement chooseKeystore;

    @FindBy(css = "#keystoreFilename")
    private WebElement keyStoreFileInput;

    @FindBy(css = "#password_id")
    private WebElement passwordInput;
    
    public KeyStoreEditDialog clickImportBtn()
    {
        log.info("clicking import btn ");
        waitForElementToBeClickable(importBtn).click();
        waitForElementToBeGone(importBtn);
        return new KeyStoreEditDialog(driver);
        
    }
    public KeyStoreEditDialog clickCancelBtn(){
        log.info("clicking import keystore");
        waitForElementToBeClickable(cancelBtn).click();
        waitForElementToBeGone(cancelBtn);
        return new KeyStoreEditDialog(driver);
    }

    public KeyStoreEditDialog clickCloseBtn(){
        log.info("clicking close btn");
        waitForElementToBeClickable(closeBtn).click();
        waitForElementToBeGone(closeBtn);
        return new KeyStoreEditDialog(driver);
    }
    public void chooseKeystoreFile(String relativePath) {
       //String path =new File("src/main/resources/keystore/keystore_dummy1.jks").getAbsolutePath() ;
        String path =new File(relativePath).getAbsolutePath() ;
        chooseKeystore.sendKeys(path);
    }
    public void fillPassword(String password)
    {
        waitForElementToBeVisible(passwordInput).sendKeys(password);
    }

    public String getKeyStoreFileName() {
        return keyStoreFileInput.getAttribute("value");
    }
}

