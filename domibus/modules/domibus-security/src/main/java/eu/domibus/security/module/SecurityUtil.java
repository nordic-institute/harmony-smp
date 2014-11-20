package eu.domibus.security.module;

import eu.domibus.common.util.JNDIUtil;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisModule;

/**
 * @author Hamid Ben Malek
 */
public class SecurityUtil {
    public static final String SECURITY_CONFIG_FILE = "SecurityConfigFile";
    public static final String POLICIES_FOLDER = "PoliciesFolder";
    public static final String KEYS_FOLDER = "KeysFolder";
    //	public static final String SECCONFIG_TIMEOUT = "SecurityConfigFileCheckTimeout";
    public static final int SECCONFIG_DEFAULT_TIMEOUT = 30 * 1000;

    public static final String SECURITY = "SECURITY";

    public static ConfigurationContext configContext;
    public static AxisModule module;

    public static String getSecurityConfig() {
        return JNDIUtil.getStringEnvironmentParameter(Constants.CONFIG_FILE_PARAMETER);
    }

    public static String getPoliciesFolder() {
        return JNDIUtil.getStringEnvironmentParameter(Constants.POLICIES_FOLDER_PARAMETER);
    }

    //	public static String getKeysFolder() {
    //		return JNDIUtil
    //				.getStringEnvironmentParameter(Constants.KEYS_FOLDER_PARAMETER);
    //	}
}