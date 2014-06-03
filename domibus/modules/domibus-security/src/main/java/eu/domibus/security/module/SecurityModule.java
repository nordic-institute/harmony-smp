package eu.domibus.security.module;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.modules.Module;
import org.apache.log4j.Logger;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;
import eu.domibus.common.util.JNDIUtil;

import java.util.Timer;

/**
 * @author Hamid Ben Malek
 */
public class SecurityModule implements Module {
    private final static Logger log = Logger.getLogger(SecurityModule.class);


    public void init(final ConfigurationContext configContext, final AxisModule module) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("initialization..., ");
        }


        // Load the security config file, usually domibus/config/security-config.xml
        Configuration.loadSecurityConfiguration();
        // Reload the security config file if its file timestamp changes
        final int timeout = getSecurityConfigFileCheckTimeout(module);
        //    System.out.println(SecurityUtil.SECCONFIG_TIMEOUT + " = " + timeout + " ms");
        new Timer().schedule(new ConfigurationAutoUpdate(), timeout, timeout);
        log.debug("Domibus-Security module started");
    }

    private int getSecurityConfigFileCheckTimeout(final AxisModule module) {
        return Integer.parseInt(JNDIUtil.getStringEnvironmentParameter(Constants.SECURITY_RELOAD_PARAMETER));

    }


    public void engageNotify(final AxisDescription axisDescription) throws AxisFault {
    }

    public void shutdown(final ConfigurationContext configCtx) throws AxisFault {
    }

    public String[] getPolicyNamespaces() {
        return null;
    }

    public void applyPolicy(final Policy policy, final AxisDescription axisDescription) throws AxisFault {
    }

    public boolean canSupportAssertion(final Assertion assertion) {
        return true;
    }
}