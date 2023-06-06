package domiSMPTests;

import org.openqa.selenium.WebDriver;
import rest.DomiSMPRestClient;
import utils.TestRunData;

public class BaseTest {
    public static TestRunData data = new TestRunData();
    public static DomiSMPRestClient rest = new DomiSMPRestClient();
    public WebDriver driver;


}
