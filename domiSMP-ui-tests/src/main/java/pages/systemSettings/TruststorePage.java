package pages.systemSettings;

import ddsl.CommonCertificatePage;
import ddsl.dcomponents.Grid.SmallGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TruststorePage extends CommonCertificatePage {
    /**
     * Page object for the Truststorepage. This contains the locators of the page and the methods for the behaviour of the page
     */

    @FindBy(id = "custom-file-upload")
    private WebElement uploadInput;

    public TruststorePage(WebDriver driver) {
        super(driver);
    }

    public SmallGrid getCertificateGrid() {
        return new SmallGrid(driver, rightPanel);
    }
    public String addCertificateAndReturnAlias(String filePath) {
        uploadInput.sendKeys(filePath);
        String certificateAlias = getAlertMessageAndClose();

        Pattern pattern = Pattern.compile("(?<=alias \\[)(.*?)(?=\\])");
        Matcher matcher = pattern.matcher(certificateAlias);
        if (matcher.find()) {
            String result = matcher.group(1);
            return result;
        }
        return null;
    }


}
