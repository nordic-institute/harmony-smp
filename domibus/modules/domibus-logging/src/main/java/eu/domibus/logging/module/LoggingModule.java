package eu.domibus.logging.module;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.modules.Module;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;

/**
 * This Class represents the starting point of this module and is used to
 * initialize the database connection
 *
 * @author Stefan Mueller
 * @author Tim Nowosadtko
 * @date 07-13-2012
 */
public class LoggingModule implements Module {

    public void init(final ConfigurationContext configContext, final AxisModule module) throws AxisFault {


    }

    public void engageNotify(final AxisDescription axisDescription) throws AxisFault {
    }

    // shutdown the module
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