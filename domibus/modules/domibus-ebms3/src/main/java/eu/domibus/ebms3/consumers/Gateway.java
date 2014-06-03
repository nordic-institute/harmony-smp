package eu.domibus.ebms3.consumers;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.log4j.Logger;
import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.JNDIUtil;
import eu.domibus.ebms3.module.Constants;
import org.simpleframework.xml.core.Persister;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author Hamid Ben Malek
 */
public class Gateway extends HttpServlet implements EbConsumer {

    static ServletContext application = null;
    static ConfigurationContext configCtx = null;

    public static GatewayConfig gatewayConfig;
    private static final Logger log = Logger.getLogger(Gateway.class);

    //	public static ConfigurationContext getConfigurationContext() {
    //		if (configCtx != null)
    //			return configCtx;
    //		if (application == null)
    //			return null;
    //		configCtx = (ConfigurationContext) application
    //				.getAttribute(AxisServlet.CONFIGURATION_CONTEXT);
    //		return configCtx;
    //	}

    private static GatewayConfig getGatewayConfig() {
        if (gatewayConfig != null) {
            return gatewayConfig;
        }
        final File source = new File(getGatewayConfigFile());

        final Persister serializer = new Persister();
        try {
            gatewayConfig = serializer.read(GatewayConfig.class, source);
        } catch (Exception e) {
            throw new ConfigurationException("could not read gateway configuration file at " + source.getAbsolutePath(),
                                             e);
        }

        return gatewayConfig;
    }

    private static String getGatewayConfigFile() {
        return JNDIUtil.getStringEnvironmentParameter(Constants.GATEWAY_CONFIG_FILE_PARAMETER);
    }

    //TODO: is this really needed?
    private Map<String, String> parameters;

    //protected Map<String, String> parameters;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        application = config.getServletContext();
        try {
            final Persister serializer = new Persister();
            final String confFile = getGatewayConfigFile();
            if (confFile == null || confFile.trim().equals("")) {
                return;
            }
            final File source = new File(confFile);
            if (!source.exists()) {
                return;
            }
            gatewayConfig = serializer.read(GatewayConfig.class, source);
        } catch (Exception ex) {
            log.error("Exception during deserialization", ex);
        }
    }

    public void pull() {
        if (getGatewayConfig() == null) {
            return;
        }
        final Consumption c = getGatewayConfig().getMatchingConsumption();
        if (c != null) {
            final List<ConsumerInfo> consumers = c.getConsumers();
            if (consumers != null && consumers.size() > 0) {
                final ConsumerInfo cons = consumers.get(0);
                final EbConsumer consumer = cons.createInstance();
                if (consumer != null) {
                    log.info("Gateway is about to call consumer.pull()");
                    consumer.pull();
                }
            }
        }
    }

    public void push() {
        if (getGatewayConfig() == null) {
            log.info("Could not find the configuration file for the Gateway: gateway.xml");
            return;
        }
        final Consumption c = getGatewayConfig().getMatchingConsumption();
        if (c != null) {
            log.debug("Gateway found matching consumption");
            final List<ConsumerInfo> consumers = c.getConsumers();
            if (consumers != null && consumers.size() > 0) {
                for (final ConsumerInfo cons : consumers) {
                    final EbConsumer consumer = cons.createInstance();
                    if (consumer != null) {
                        log.info("Gateway is about to call consumer.push()");
                        consumer.push();
                    }
                }
            }
        }
    }

    public void setParameters(Map<String, String> properties) {
        this.parameters = properties;
    }
}