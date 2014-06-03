package eu.domibus.common.util;

import org.apache.log4j.Logger;
import eu.domibus.common.exceptions.ConfigurationException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Properties;

public class JNDIUtil {
    public static final String JNDI_ENV_PREFIX = "java:comp/env/";
    private static final Logger log = Logger.getLogger(JNDIUtil.class);
    private static final Context ctx;
    public static Properties props;

    static {
        InitialContext temp = null;
        try {
            temp = new InitialContext();
        } catch (NamingException e) {
            throw new ConfigurationException("No JNDI context found, configuration parameters can not be loaded", e);
        }
        ctx = temp;
    }

    /**
     * Returns string parameter see {@link JNDIUtil#getEnvironmentParameter(String)}
     *
     * @param parameter the name of jndi property to get
     * @return the string value associated to the parameter
     */
    public static String getStringEnvironmentParameter(final String parameter) {
        return (String) getEnvironmentParameter(parameter);
    }

    /**
     * Returns boolean parameter see {@link JNDIUtil#getEnvironmentParameter(String)}
     *
     * @param parameter the name of jndi property to get
     * @return the boolean value associated to the parameter
     */
    public static Boolean getBooleanEnvironmentParameter(final String parameter) {
        return (Boolean) getEnvironmentParameter(parameter);
    }

    /**
     * Gives access to jndi values defined in environment
     *
     * @param parameter the name of jndi property to get
     * @return the value associated to the parameter
     */
    public static Object getEnvironmentParameter(final String parameter) {

        try {
            final String initCtxProvider = (String) ctx.getEnvironment().get(Context.INITIAL_CONTEXT_FACTORY);
            String prefix = "";

            // Weblogic does not allow to define configuration variable in the
            // JNDI so we must load from a file
            if (initCtxProvider != null && initCtxProvider.contains("weblogic")) {
                if (props == null) {
                    props = new Properties();
                    props.load(JNDIUtil.class.getClassLoader().getResourceAsStream("weblogic.properties"));
                }
                return props.getProperty(parameter);

            } else {
                // assume application was deployed in tomcat
                prefix = JNDI_ENV_PREFIX;
            }

            return (ctx.lookup(prefix + parameter));
        } catch (NamingException e) {
            throw new ConfigurationException("Parameter " + parameter + " can not be found in the JNDI context", e);
        } catch (IOException e) {
            throw new ConfigurationException("Parameter " + parameter + " can not be found in the Init properties", e);
        }
    }
}
