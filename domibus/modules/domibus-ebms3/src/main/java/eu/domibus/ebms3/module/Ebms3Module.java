package eu.domibus.ebms3.module;

/* import org.apache.activemq.broker.BrokerFactory;
 import org.apache.activemq.broker.BrokerService;
 */

import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.JNDIUtil;
import eu.domibus.ebms3.workers.WorkerPool;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.modules.Module;
import org.apache.commons.httpclient.contrib.ssl.AuthSSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.log4j.Logger;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;

/**
 * [2012-09-19/safi] Commented out all references to JMS
 *
 * @author Hamid Ben Malek
 */
public class Ebms3Module implements Module {
    private static final Logger log = Logger.getLogger(Ebms3Module.class);

    public void init(final ConfigurationContext configContext, final AxisModule module) throws AxisFault {
        if (Ebms3Module.log.isDebugEnabled()) {
            Ebms3Module.log.debug("initialization..., ");
        }
        Constants.configContext = configContext;
        Constants.getAttachmentDir();

        final AxisConfiguration config = configContext.getAxisConfiguration();

        Constants.setAxisModule(module);

        this.readModules(config, module);
        this.readOtherParameters(config, module);
        this.enable2WaySSL();
        Ebms3Module.log.debug("Loading Workers...");
        this.loadWorkers();
        Ebms3Module.log.debug("Done loading Workers");
        Ebms3Module.log.debug("ebms3 Module Started.");
    }

    // shutdown the module
    public void shutdown(final ConfigurationContext configCtx) throws AxisFault {
    }

    private void readModules(final AxisConfiguration config, final AxisModule module) {
        final Parameter modulesParam = module.getParameter("Modules");
        if (modulesParam == null) {
            return;
        }
        final String mod = (String) modulesParam.getValue();
        try {
            final Parameter modParam = new Parameter(Constants.MODULES, mod);
            config.addParameter(modParam);
            Constants.engagedModules = EbUtil.parseModules(mod);
        } catch (Exception e) {
            Ebms3Module.log.error("Error occured while reading modules", e);
        }
    }

    private void enable2WaySSL() {
        try {
            URL truststore = new File(
                    JNDIUtil.getStringEnvironmentParameter(eu.domibus.common.Constants.DOMIBUS_SSL_TRUSTSTORE_PATH))
                    .toURI().toURL();

            String TRUSTSTORE_PASSWORD =
                    JNDIUtil.getStringEnvironmentParameter(eu.domibus.common.Constants.DOMIBUS_SSL_TRUSTSTORE_PASSWORD);

            URL keystore = new File(
                    JNDIUtil.getStringEnvironmentParameter(eu.domibus.common.Constants.DOMIBUS_SSL_KEYSTORE_PATH))
                    .toURI().toURL();
            String KEYSTORE_PASSWORD =
                    JNDIUtil.getStringEnvironmentParameter(eu.domibus.common.Constants.DOMIBUS_SSL_KEYSTORE_PASSWORD);


            Protocol authhttps = new Protocol("https",
                                              new AuthSSLProtocolSocketFactory(keystore, KEYSTORE_PASSWORD, truststore,
                                                                               TRUSTSTORE_PASSWORD), 443);


            Protocol.registerProtocol("https", authhttps);
        } catch (GeneralSecurityException e) {
            log.error("Security error occurred", e);
        } catch (IOException e) {
            log.error("IOException while loading Keystore/Truststore", e);
        }


    }

    private void readOtherParameters(final AxisConfiguration config, final AxisModule module) {
        try {

            final String gatewayConfigFile =
                    JNDIUtil.getStringEnvironmentParameter(Constants.GATEWAY_CONFIG_LOCATION_PARAMETER);

            final String submittedMessagesFolder =
                    JNDIUtil.getStringEnvironmentParameter(Constants.SUBMITTED_MESSAGES_FOLDER_PARAMETER);

            final String receivedMessagesFolder =
                    JNDIUtil.getStringEnvironmentParameter(Constants.RECEIVED_MESSAGES_FOLDER_PARAMETER);

            final String hostnames = JNDIUtil.getStringEnvironmentParameter(Constants.HOSTNAMES_PARAMETER);


            final Parameter gatewayParam = new Parameter(Constants.GATEWAY_CONFIG_FILE_PARAMETER, gatewayConfigFile);
            config.addParameter(gatewayParam);

            final Parameter submitParam =
                    new Parameter(Constants.SUBMITTED_MESSAGES_FOLDER_PARAMETER, submittedMessagesFolder);
            config.addParameter(submitParam);

            final Parameter receivedParam =
                    new Parameter(Constants.RECEIVED_MESSAGES_FOLDER_PARAMETER, receivedMessagesFolder);
            config.addParameter(receivedParam);

            final Parameter localParam = new Parameter(Constants.HOSTNAMES_PARAMETER, hostnames);
            config.addParameter(localParam);

        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    private void loadWorkers() {
        Ebms3Module.log.debug("workers file is: " + Constants.getWorkersFile());
        final WorkerPool pool = WorkerPool.load(Constants.getWorkersFile());
        Constants.workerPool = pool;
        if (pool == null) {
            Ebms3Module.log.debug("Could not load workers from file " + Constants.getWorkersFile());
        } else {
            pool.start();
            pool.watch(30000);
            Ebms3Module.log.debug("started the workers pool");
        }
    }

    public void engageNotify(final AxisDescription axisDescription) throws AxisFault {
    }


    public void applyPolicy(final Policy policy, final AxisDescription axisDescription) throws AxisFault {
    }

    public boolean canSupportAssertion(final Assertion assertion) {
        return true;
    }

}
