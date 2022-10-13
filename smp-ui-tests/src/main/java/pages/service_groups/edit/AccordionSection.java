package pages.service_groups.edit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import pages.components.baseComponents.PageComponent;
import utils.PROPERTIES;

import java.util.ArrayList;
import java.util.List;

public class AccordionSection extends PageComponent {
	@FindBy(css = "span.mat-content > mat-panel-title")
	WebElement title;
	@FindBy(css = "span.mat-content > mat-panel-description > div")
	WebElement selectCount;
	@FindBy(css = ".mat-expansion-indicator")
	WebElement expandButton;
	@FindBy(tagName = "mat-list-option")
	List<WebElement> options;

	public AccordionSection(WebDriver driver, WebElement container) {
		super(driver);
		PageFactory.initElements(new AjaxElementLocatorFactory(container, PROPERTIES.TIMEOUT), this);
	}

	public boolean isExpanded() {
		log.info("check if expanded");
		return isVisible(options.get(0));
	}

	public String getTitle() {
		log.info("getting title text");
		waitForElementToBeVisible(title);
		return title.getText().replaceAll("\\W", "").trim();
	}

	public String getSelectedCountFullText() {
		log.info("get Selected Count Full Text");
		waitForElementToBeVisible(selectCount);
		return selectCount.getText().trim();
	}

	public Integer getSelectedCount() {
		String fullText = getSelectedCountFullText();
		return Integer.valueOf(fullText.replaceAll("\\D", ""));
	}

	public void expandSection() {
		log.info("expanding...");
		waitForElementToBeClickable(expandButton).click();
		waitForElementToBeVisible(options.get(0));
	}

	public void selectOptionWithText(String text) {
		log.info("selecting option " + text);
		for (WebElement option : options) {

			if (option.getAttribute("aria-selected").contains("true")) {
				continue;
			}

			if (option.getText().trim().equalsIgnoreCase(text)) {
				log.info("option found ... selecting");
				option.click();
				return;
			}
		}
	}

	public void selectOptionWithIndex(Integer index) {
		log.info("selecting option " + index);
		if (index >= options.size()) {
			return;
		}

		WebElement option = options.get(index);
		if (option.getAttribute("aria-selected").contains("true")) {
			return;
		}
		waitForElementToBeClickable(option).click();
		return;
	}

	public boolean optionsEnabled() {
		log.info("checking if options are enabled");
		waitForElementToBeVisible(title);
		boolean isDisabled = options.get(0).getAttribute("aria-disabled").equalsIgnoreCase("true");
		return !isDisabled;
	}

	public List<String> getOptions() {
		log.info("getting options");
		List<String> optionStr = new ArrayList<>();
		for (WebElement option : options) {
			optionStr.add(option.getText().trim());
		}
		return optionStr;
	}


}
