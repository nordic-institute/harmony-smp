package ddsl.dobjects;

import ddsl.dcomponents.ConfirmationDialog;
import ddsl.dcomponents.DComponent;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DMatSelect extends DComponent {
    /**
     * Generic wrapper for select element.
     */
    private final static Logger LOG = LoggerFactory.getLogger(ConfirmationDialog.class);

    public WebElement selectContainer;
    protected List<String> optionIDs = new ArrayList<String>();

    @FindBy(css = "[class*=\"select-arrow\"]")
    protected WebElement expandBtn;

    private By options = By.cssSelector("div > mat-option");
    private By selectedOption = By.cssSelector("[class*=\"-select-value\"]");

    public DMatSelect(WebDriver driver, WebElement container) {
        super(driver);
        wait.forXMillis(100);
        PageFactory.initElements(new AjaxElementLocatorFactory(container, data.getWaitTimeShort()), this);

        this.selectContainer = container;
//        try {
//            if (isDisplayed() && isEnabled()) {
//                weToDButton(expandBtn).click();
//                extractOptionIDs();
//                contract();
//            }
//        } catch (Exception e) {
//        }
    }

    public boolean isDisplayed() throws Exception {
        try {
            return weToDButton(expandBtn).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEnabled() {
//		aria-disabled="true"
        boolean isEnabled = true;
        try {
            isEnabled = !Boolean.valueOf((selectContainer).getAttribute("aria-disabled"));
        } catch (Exception e) {
        }

        return isEnabled;
    }

    private void extractOptionIDs() throws Exception {
        wait.forAttributeToContain(selectContainer, "aria-owns", "mat-option");
        String[] ids = selectContainer.getAttribute("aria-owns").split(" ");
        optionIDs.addAll(Arrays.asList(ids));

        // log.debug(optionIDs.size() + " option ids identified : " + optionIDs);
    }

//    private void expand() throws Exception {
//        try {
//            weToDButton(expandBtn).click();
//            wait.forAttributeNotEmpty(selectContainer, "aria-owns");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void contract() throws Exception {
        try {
            wait.forXMillis(200);
            selectContainer.sendKeys(Keys.ESCAPE);
            wait.forXMillis(200);

        } catch (Exception e) {
        }
    }

//	-----------------------------------------------------------------

    protected List<DObject> getOptionElements() throws Exception {
        weToDButton(expandBtn).click();

        List<DObject> optionObj = new ArrayList<>();

        for (int i = 0; i < optionIDs.size(); i++) {
            String optionId = optionIDs.get(i);
            WebElement option = driver.findElement(By.id(optionId));
            optionObj.add(new DObject(driver, option));
        }
        return optionObj;
    }

    public boolean selectOptionByText(String text) throws Exception {
        LOG.debug("selecting option by text: " + text);

        if (StringUtils.isEmpty(text)) {
            return false;
        }

        List<DObject> optionObj = getOptionElements();
        if (optionObj.size() == 0) {
            LOG.warn("select has no options ");
        }

        for (DObject dObject : optionObj) {
            if (StringUtils.equalsIgnoreCase(dObject.getText(), text)) {
                dObject.click();
                return true;
            }
        }

        return false;
    }

    public boolean deselectOptionByText(String text) throws Exception {
        LOG.debug("selecting option by text: " + text);

        if (StringUtils.isEmpty(text)) {
            return false;
        }

        List<DObject> optionObj = getOptionElements();
        if (optionObj.size() == 0) {
            LOG.warn("select has no options ");
        }

        for (DObject dObject : optionObj) {
            if (StringUtils.equalsIgnoreCase(dObject.getText(), text)) {
                dObject.click();
                if (dObject.getAttribute("aria-selected").equals(true)) {
                    return true;
                }
            }
        }

        return false;
    }


    public List<String> getOptionsTexts() throws Exception {
        List<String> texts = new ArrayList<>();
        List<DObject> options = getOptionElements();

        for (int i = 0; i < options.size(); i++) {
            texts.add(options.get(i).getText());
        }
        contract();
        return texts;
    }

}
