package pages.systemSettings;

import ddsl.CommonCertificatePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Page object for the Truststorepage. This contains the locators of the page and the methods for the behaviour of the page
 */
public class TruststorePage extends CommonCertificatePage {
    @FindBy(id = "custom-file-upload")
    private WebElement uploadInput;

    public TruststorePage(WebDriver driver) {
        super(driver);
    }
    public String addCertificateAndReturnAlias(String filePath) {
        uploadInput.sendKeys(filePath);
        String certificateAlias = getAlertMessageAndClose();
        String regex =  "\\[[^\\]]*\\].*\\[([^\\]]+)\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(certificateAlias);
        if (matcher.find()) {
            return matcher.group(1);
        }
     else {
        throw new NullPointerException("No alias found in the message: "+certificateAlias);
    }
    }


}
