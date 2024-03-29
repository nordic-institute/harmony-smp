package utils;

import ddsl.enums.ApplicationRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.Properties;


public class TestRunData {
    public static SimpleDateFormat UI_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public static SimpleDateFormat UI_DATE_FORMAT2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssz");
    public static SimpleDateFormat CSV_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static SimpleDateFormat REST_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static SimpleDateFormat REST_JMS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public String userId;
    static Properties prop = new Properties();
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public TestRunData() {
        if (prop.isEmpty()) {
            loadTestData();
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private void loadTestData() {
        try {
            String filename = System.getProperty("propertiesFile");
            FileInputStream stream = new FileInputStream(filename);
            prop.load(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, String> getUser(String role) {

        HashMap<String, String> toReturn = new HashMap<>();

        toReturn.put("username", prop.getProperty(role + ".username"));
        toReturn.put("pass", prop.getProperty(role + ".password"));

        return toReturn;
    }


    public HashMap<String, String> getAdminUser() {
        return getUser(ApplicationRoles.SYSTEM_ADMIN);
    }

    public String getNewPassword() {
        return prop.getProperty("new.password");
    }

    public String getUiBaseUrl() {
        String url = prop.getProperty("UI_BASE_URL");
        log.debug(url);
        return url;
    }

    public Integer getTIMEOUT() {
        return Integer.valueOf(prop.getProperty("SHORT_TIMEOUT_SECONDS"));
    }

    public Duration getTIMEOUTinDuration() {
        return Duration.ofMinutes(Long.parseLong((prop.getProperty("SHORT_TIMEOUT_SECONDS"))));
    }

    public Integer getLongWait() {
        return Integer.valueOf(prop.getProperty("LONG_TIMEOUT_SECONDS"));
    }

    public Duration getLongWaitInDuration() {
        return Duration.ofMinutes(Long.parseLong(prop.getProperty("LONG_TIMEOUT_SECONDS")));
    }


    public String getReportsFolder() {
        return prop.getProperty("reports.folder");
    }

    public boolean isHeadless() {
        try {
            return Boolean.parseBoolean(prop.getProperty("headless"));
        } catch (Exception e) {
            log.debug("e = " + e);
            return false;
        }
    }

    public String getProxyAddress() {
        return prop.getProperty("proxyAddress");
    }

    public String getChromeDriverPath() {
        return prop.getProperty("webdriver.chrome.driver");
    }

    public String getFirefoxDriverPath() {
        return prop.getProperty("webdriver.gecko.driver");
    }

    public String getRunBrowser() {
        return System.getProperty("runBrowser");
    }


    public String downloadFolderPath() {
        return System.getProperty("user.dir") + File.separator + "downloadFiles";
    }

}
