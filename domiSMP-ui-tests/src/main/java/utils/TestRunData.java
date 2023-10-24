package utils;

import ddsl.enums.ApplicationRoles;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.NewCookie;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Singleton class for handling environment properties. Properties are returned in following precedence order:
 * System property, test.properties.path, default value
 */
public class TestRunData {
    protected static final Logger LOG = LoggerFactory.getLogger(TestRunData.class);

    /**
     * Enum for the environment test properties
     */
    public enum TestEnvironmentProperty {
        PROPERTIES_PATH("test.properties.path", "./test.properties", "Path to init properties. example: ./src/test/resources/test.properties"),
        WEBDRIVER_PATH("test.webdriver.path", null, "Webdriver path"),
        WEBDRIVER_TYPE("test.webdriver.type", "chrome", "Webdriver type: chrome, firefox"),
        WEBDRIVER_HEADLESS("test.webdriver.headless", "false", "Run Webdriver headless. Default is false"),
        APPLICATION_UI_URL("test.application.ui.url", "http://localhost:8080/smp/ui/", "Application UI url"),
        SML_URL("test.sml.url", "http://localhost:8982/edelivery-sml/listDNS", "Webdriver type: chrome, gecko, edge"),
        REPORT_FOLDER("test.reports.folder", "./reports/", "Reports folder"),
        TIMEOUT_LONG("test.timeout.long", "15", "Long timeout in seconds"),
        TIMEOUT_SHORT("test.timeout.short", "5", "Short timeout in seconds"),
        ADMIN_USERNAME("test.user.SYSTEM_ADMIN.username", "system", "Username with admin/system role"),
        ADMIN_PASSWORD("test.user.SYSTEM_ADMIN.password", "123456", "Password for username with admin/system role. User is used for setting up the test data"),
        USER_USERNAME("test.user.USER.username", "user", "Username with user role (not admin). User is used for basic tests"),
        USER_PASSWORD("test.user.USER.password", "123456", "Password for username with user role"),

        TEST_DATA_PASSWORD_DEFAULT("test.data.password.default", "QW!@QW!@qw12qw12", "Default password when creating new users"),
        TEST_DATA_PASSWORD_NEW("test.data.password.new", "Test1234!Test1234!", "New Password when changing users password "),
        ;

        String propertyName;
        String defaultValue;
        String description;

        TestEnvironmentProperty(String propertyName, String defaultValue, String description) {
            this.propertyName = propertyName;
            this.defaultValue = defaultValue;
            this.description = description;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("TestEnvironmentProperty {");
            sb.append("propertyName='").append(propertyName).append('\'');
            sb.append(", defaultValue='").append(defaultValue).append('\'');
            sb.append(", description='").append(description).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }


    public static SimpleDateFormat UI_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public static SimpleDateFormat UI_DATE_FORMAT2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssz");
    public static SimpleDateFormat CSV_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static SimpleDateFormat REST_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static SimpleDateFormat REST_JMS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static Properties prop = new Properties();
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    public String XSRFToken;
    public List<NewCookie> cookies;
    private String userId;


    private static TestRunData instance;

    private TestRunData() {
        if (prop.isEmpty()) {
            loadTestData();
        }
    }

    public static TestRunData getInstance() {
        if (instance == null) {
            instance = new TestRunData();
        }
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public List<NewCookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<NewCookie> cookies) {
        this.cookies = cookies;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private void loadTestData() {

        String filename = getPropertyValue(TestEnvironmentProperty.PROPERTIES_PATH);
        if (StringUtils.isNotBlank(filename)) {
            File file = new File(filename);
            if (!file.exists()) {
                LOG.warn("File [{}] does not exist", file.getAbsolutePath());
                return;
            }
            LOG.debug("Loading properties from [{}]", filename);
            try (FileInputStream stream = new FileInputStream(file)) {
                prop.load(stream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public HashMap<String, String> getUser(String role) {

        HashMap<String, String> toReturn = new HashMap<>();

        toReturn.put("username", prop.getProperty("test.user." + role + ".username"));
        toReturn.put("password", prop.getProperty("test.user." + role + ".password"));

        return toReturn;
    }


    public HashMap<String, String> getAdminUser() {
        return getUser(ApplicationRoles.SYSTEM_ADMIN);
    }

    public String getAdminUsername() {
        return getUser(ApplicationRoles.SYSTEM_ADMIN).get("username");
    }

    public String getDefaultPassword() {
        return getPropertyValue(TestEnvironmentProperty.TEST_DATA_PASSWORD_DEFAULT);
    }

    public String getNewPassword() {
        return getPropertyValue(TestEnvironmentProperty.TEST_DATA_PASSWORD_NEW);
    }

    public String getUiBaseUrl() {
        String url = getPropertyValue(TestEnvironmentProperty.APPLICATION_UI_URL);
        LOG.debug("UI URL [{}]", url);
        return url;
    }

    public String getSMLUrl() {
        String url = getPropertyValue(TestEnvironmentProperty.SML_URL);
        LOG.debug("SML URL [{}]", url);
        return url;
    }

    public Integer getWaitTimeShort() {
        String intValue = getPropertyValue(TestEnvironmentProperty.TIMEOUT_SHORT);
        return Integer.valueOf(intValue);
    }

    public Duration getWaitDurationShort() {
        return Duration.ofSeconds(getWaitTimeShort());
    }

    public Integer getWaitTimeLong() {
        String intValue = getPropertyValue(TestEnvironmentProperty.TIMEOUT_LONG);
        return Integer.valueOf(intValue);
    }

    public Duration getWaitDurationLong() {
        return Duration.ofSeconds(getWaitTimeLong());
    }


    public String getReportsFolder() {
        return getPropertyValue(TestEnvironmentProperty.REPORT_FOLDER);
    }

    public boolean enableHeadlessStart() {
        return Boolean.parseBoolean(getPropertyValue(TestEnvironmentProperty.WEBDRIVER_HEADLESS));
    }

    public String getProxyAddress() {
        return prop.getProperty("proxyAddress");
    }

    public String getWebDriverPath() {
        return getPropertyValue(TestEnvironmentProperty.WEBDRIVER_PATH);
    }


    public String getWebDriverType() {
        return getPropertyValue(TestEnvironmentProperty.WEBDRIVER_TYPE);
    }


    public String downloadFolderPath() {
        return System.getProperty("user.dir") + File.separator + "downloadFiles";
    }

    public String getPropertyValue(TestEnvironmentProperty propertyName) {
        String value = System.getProperty(propertyName.getPropertyName(), prop.getProperty(propertyName.getPropertyName(), propertyName.getDefaultValue()));
       // LOG.debug("Get Property [{}] value: [{}]", propertyName.getPropertyName(), value);
        return value;
    }

    public String getXSRFToken() {
        return XSRFToken;
    }

    public void setXSRFToken(String xsrfToken) {
        this.XSRFToken = xsrfToken;
    }
}
