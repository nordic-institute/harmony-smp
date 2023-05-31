package domiSMPTests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import rest.DomiSMPRestClient;
import utils.MyLogger;
import utils.TestRunData;

public class BaseTest {
    public static TestRunData data = new TestRunData();
    public static DomiSMPRestClient rest = new DomiSMPRestClient();

    public WebDriver driver;
    public ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


}
