package eu.domibus.ebms3.module;

/* import org.apache.activemq.broker.BrokerFactory;
 import org.apache.activemq.broker.BrokerService;
 */

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.modules.Module;
import org.apache.log4j.Logger;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;
import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.JNDIUtil;
import eu.domibus.ebms3.workers.WorkerPool;

import java.util.ArrayList;
import java.util.List;

/**
 * [2012-09-19/safi] Commented out all references to JMS
 *
 * @author Hamid Ben Malek
 */
public class Ebms3Module implements Module {
    private static final Logger log = Logger.getLogger(Ebms3Module.class);
    protected final List<PullWorker> pullers = new ArrayList<PullWorker>();

    public Ebms3Module() {
    }

    public void init(final ConfigurationContext configContext, final AxisModule module) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug("initialization..., ");
        }
        Constants.configContext = configContext;
        Constants.getAttachmentDir();

        final AxisConfiguration config = configContext.getAxisConfiguration();

        Constants.setAxisModule(module);

        readModules(config, module);
        readOtherParameters(config, module);

        log.debug("Loading Workers...");
        loadWorkers();
        log.debug("Done loading Workers");
        log.debug("ebms3 Module Started.");
    }

    // shutdown the module
    public void shutdown(final ConfigurationContext configCtx) throws AxisFault {
        // submitWorker.terminate();
        // sender.terminate();
        try {
            // broker.stop();
            if (pullers != null && pullers.size() > 0) {
                for (final PullWorker puller : pullers) {
                    puller.terminate();
                }
            }

        } catch (Exception ex) {
            log.error("Error occured during shutdown of Ebms3Module", ex);
        }
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
            log.error("Error occured while reading modules", e);
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

    /**
     * construct a database store manager (dbAdapter) object and store it as a
     * parameter in AxisConfiguration ... so that other handlers and any module
     * can access it:
     */
    private DbStore loadDataStore(final AxisConfiguration config, final AxisModule module) {


        final DbStore store = new DbStore();
        // try {
        // Parameter storeParam = new Parameter(Constants.STORE, store);
        // config.addParameter(storeParam);
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }
        return store;
    }

    private void loadWorkers() {
        log.debug("workers file is: " + Constants.getWorkersFile());
        final WorkerPool pool = WorkerPool.load(Constants.getWorkersFile());
        Constants.workerPool = pool;
        if (pool == null) {
            log.debug("Could not load workers from file " + Constants.getWorkersFile());
        } else {
            pool.start();
            pool.watch(30000);
            log.debug("started the workers pool");
        }
    }

	/*
     * private void startActiveMQ() { try { broker =
	 * BrokerFactory.createBroker("xbean:activemq.xml"); broker.start();
	 * broker.waitUntilStarted(); } catch(Exception ex) { ex.printStackTrace();
	 * } }
	 */

    public void engageNotify(final AxisDescription axisDescription) throws AxisFault {
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
