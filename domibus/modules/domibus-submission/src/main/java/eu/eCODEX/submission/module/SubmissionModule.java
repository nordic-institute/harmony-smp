package eu.eCODEX.submission.module;

import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.JNDIUtil;
import eu.eCODEX.submission.Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.modules.Module;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;
import org.springframework.context.ApplicationContext;

import java.io.File;

/**
 * Class required by Axis2. Does not do anything for this module.
 */
public class SubmissionModule implements Module {

    private static ApplicationContext context;

    @Override
    public void init(final ConfigurationContext configurationContext, final AxisModule axisModule) throws AxisFault {

        final String webserviceFolder = JNDIUtil.getStringEnvironmentParameter(Constants.SUBMISSION_WEBSERVICE_FOLDER);
        final File dir = new File(webserviceFolder);
        if (!(dir.exists() && dir.isDirectory())) {
            throw new ConfigurationException("Webservice folder: " + webserviceFolder + " not found");
        }
        for (final File f : dir.listFiles()) {
            if (f.getName().endsWith(".aar")) {
                final AxisServiceGroup serviceGroup = DeploymentEngine.loadServiceGroup(f, configurationContext);
                final AxisConfiguration axiConfiguration = configurationContext.getAxisConfiguration();
                axiConfiguration.addServiceGroup(serviceGroup);
            }
        }
    }

    @Override
    public void engageNotify(final AxisDescription axisDescription) throws AxisFault {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canSupportAssertion(final Assertion assertion) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void applyPolicy(final Policy policy, final AxisDescription axisDescription) throws AxisFault {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void shutdown(final ConfigurationContext configurationContext) throws AxisFault {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public static ApplicationContext getContext() {
        return context;
    }
}
