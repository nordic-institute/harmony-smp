package pages;

import ddsl.PageWithGrid;
import ddsl.dcomponents.Grid.SmallGrid;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TruststorePage extends PageWithGrid {
    /**
     * Page object for the Truststorepage. This contains the locators of the page and the methods for the behaviour of the page
     */

    //TODO work in progress
    @FindBy(id = "custom-file-upload")
    private WebElement uploadInput;
    @FindBy(id = "publicKeyType_id")
    private WebElement publicKeyTypeLbl;
    @FindBy(id = "alias_id")
    private WebElement aliasIdLbl;
    @FindBy(id = "certificateId_id")
    private WebElement smpCertificateId;
    @FindBy(id = "subject_id")
    private WebElement subjectName;


    public TruststorePage(WebDriver driver) {
        super(driver);
    }

    public SmallGrid getGrid() {
        return new SmallGrid(driver, dataPanel);
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
