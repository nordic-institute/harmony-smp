package eu.domibus.ebms3.consumers;

import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.JNDIUtil;
import eu.domibus.ebms3.module.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.log4j.Logger;
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

    static ServletContext application;
    static ConfigurationContext configCtx;

    public static GatewayConfig gatewayConfig;
    private static final Logger LOG = Logger.getLogger(Gateway.class);


    private static GatewayConfig getGatewayConfig() {
        if (Gateway.gatewayConfig != null) {
            return Gateway.gatewayConfig;
        }
        final File source = new File(Gateway.getGatewayConfigFile());

        final Persister serializer = new Persister();
        try {
            Gateway.gatewayConfig = serializer.read(GatewayConfig.class, source);
        } catch (Exception e) {
            throw new ConfigurationException("could not read gateway configuration file at " + source.getAbsolutePath(),
                                             e);
        }

        return Gateway.gatewayConfig;
    }

    private static String getGatewayConfigFile() {
        return JNDIUtil.getStringEnvironmentParameter(Constants.GATEWAY_CONFIG_FILE_PARAMETER);
    }


    @Override
    public void init(final ServletConfig config) throws ServletException {
        Gateway.application = config.getServletContext();
        try {
            final Persister serializer = new Persister();
            final String confFile = Gateway.getGatewayConfigFile();
            if ((confFile == null) || "".equals(confFile.trim())) {
                return;
            }
            final File source = new File(confFile);
            if (!source.exists()) {
                return;
            }
            Gateway.gatewayConfig = serializer.read(GatewayConfig.class, source);
        } catch (Exception ex) {
            Gateway.LOG.error("Exception during deserialization", ex);
        }
    }

    public void pull() {
        if (Gateway.getGatewayConfig() == null) {
            return;
        }
        final Consumption c = Gateway.getGatewayConfig().getMatchingConsumption();
        if (c != null) {
            final List<ConsumerInfo> consumers = c.getConsumers();
            if ((consumers != null) && !consumers.isEmpty()) {
                final ConsumerInfo cons = consumers.get(0);
                final EbConsumer consumer = cons.createInstance();
                if (consumer != null) {
                    Gateway.LOG.info("Gateway is about to call consumer.pull()");
                    consumer.pull();
                }
            }
        }
    }

    @Override
    public void setParameters(Map<String, String> parameters) {

    }

    public void push() {
        if (Gateway.getGatewayConfig() == null) {
            throw new ConfigurationException("Could not find the configuration file for the Gateway: gateway.xml");

        }
        final Consumption c = Gateway.getGatewayConfig().getMatchingConsumption();
        if (c != null) {
            Gateway.LOG.trace("Gateway found matching consumption");
            final List<ConsumerInfo> consumers = c.getConsumers();
            if ((consumers != null) && !consumers.isEmpty()) {
                for (final ConsumerInfo cons : consumers) {
                    final EbConsumer consumer = cons.createInstance();
                    if (consumer != null) {
                        Gateway.LOG.trace("Gateway is about to call consumer.push()");
                        consumer.push();
                    }
                }
            }
        }
    }
}