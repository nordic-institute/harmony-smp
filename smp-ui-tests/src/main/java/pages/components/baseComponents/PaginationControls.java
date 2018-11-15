package pages.components.baseComponents;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.GenericSelect;
import pages.components.baseComponents.PageComponent;

import java.util.List;

public class PaginationControls extends PageComponent {


	public PaginationControls(WebDriver driver) {
		super(driver);
		
		log.info("initiating pagination controls!");
		PageFactory.initElements( driver, this);
		
		pageSizeSelect = new GenericSelect(driver, pageSizeSelectContainer);
	}

	@FindBy(css = "li.pages")
	List<WebElement> pgLinks;

	@FindBy(css = "li.pages.active")
	WebElement activePageLnk;
	
	@FindBy(css = "datatable-footer > div > datatable-pager > ul > li:nth-child(1)")
	WebElement skipFirstLnk;
	
	@FindBy(css = "datatable-footer > div > datatable-pager > ul > li:nth-last-child(1)")
	WebElement skipLastLnk;
	
	@FindBy(css = "datatable-footer > div > datatable-pager > ul > li:nth-last-child(2)")
	WebElement nextPageLnk;
	
	@FindBy(css = "datatable-footer > div > datatable-pager > ul > li:nth-child(2)")
	WebElement prevPageLnk;
	
	
	@FindBy(id = "pagesize_id")
	WebElement pageSizeSelectContainer;
	public GenericSelect pageSizeSelect;
	
	@FindBy(css = "datatable-footer > div > div.page-count")
	WebElement pageCount;

	
	public boolean hasNextPage(){
		return !("disabled".equalsIgnoreCase(nextPageLnk.getAttribute("class")));
	}
	
	public int getExpectedNoOfPages(){
		
		log.info("getting expected number of pages");
		
		int noOfItems = getTotalItems();
		int itemsPerPg = Integer.valueOf(pageSizeSelect.getSelectedValue());
		
		return (int) Math.ceil((double)noOfItems/itemsPerPg);
	}
	
	public int getNoOfItemsOnLastPg(){
		
		log.info("getting expected number of items on last page");
		
		int noOfItems = getTotalItems();
		int itemsPerPg = Integer.valueOf(pageSizeSelect.getSelectedValue());
		
		return noOfItems%itemsPerPg;
	}
	
	public int getItemsPerPage(){
		int itemsPerPg = Integer.valueOf(pageSizeSelect.getSelectedValue());
		
		log.info("selected value for items per page is .. " + itemsPerPg);
		
		return itemsPerPg;
	}
	
	
	
	
	public boolean isPaginationPresent(){
		log.info("checking if pagination is present on page");
		return (activePageLnk.isDisplayed());
	}
	
//	if pagination is not present we return 1 by default
	public Integer getActivePage(){
		
		log.info("getting active page number");
		
		if(!activePageLnk.isDisplayed()){return 1;}
		return Integer.valueOf(activePageLnk.getText().trim());
	}
	
	public void goToPage(int pgNo){
		
		log.info("going to page .. " + pgNo);
		
		for (WebElement pgLink : pgLinks) {
			if(Integer.valueOf(pgLink.getText().trim()) == pgNo){
				pgLink.click();
				PageFactory.initElements(driver, this);
				return;
			}
		}
	}
	
	
	public void skipToFirstPage(){
		log.info("skip to FIRST page of results");
		waitForElementToBeClickable(skipFirstLnk).click();
		PageFactory.initElements(driver, this);
	}
	
	public void skipToLastPage(){
		log.info("skip to last page of results");
		waitForElementToBeClickable(skipLastLnk);
		skipLastLnk.click();
		PageFactory.initElements(driver, this);
	}
	
	public void goToNextPage(){
		log.info("going to next page");
		nextPageLnk.click();
		PageFactory.initElements(driver, this);
	}
	
	public void goToPrevPage(){
		log.info("going to prev page");
		prevPageLnk.click();
		PageFactory.initElements(driver, this);
	}
	
	
	public int getTotalItems() {
		
		log.info("getting total number of items to be displayed");
		
		String raw = pageCount.getText().trim();
		if(raw.contains("total")){
			String[] splits = raw.split("/");
			for (String split : splits) {
				if(split.contains("total")){
					String total = split.replaceAll("\\D", "");
					return Integer.valueOf(total);
				}
			}
		}
		return 0;
	}
	
	public Integer getNoOfSelectedItems(){
		
		log.info("getting number of selected items in grid");
		
		String raw = pageCount.getText().trim();
		if(raw.contains("selected")){
			String[] splits = raw.split("/");
			for (String split : splits) {
				if(split.contains("selected")){
					String selected = split.replaceAll("\\D", "");
					return Integer.valueOf(selected);
				}
			}
		}
		return null;
	}
	
	
}
