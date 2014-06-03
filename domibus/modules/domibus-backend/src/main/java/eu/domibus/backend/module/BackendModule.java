/*
 * 
 */
package eu.domibus.backend.module;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisDescription;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.modules.Module;
import org.apache.log4j.Logger;
import org.apache.neethi.Assertion;
import org.apache.neethi.Policy;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

/**
 * The Class BackendModule.
 */
public class BackendModule implements Module {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(BackendModule.class);

    /**
     * The context.
     */
    private static ApplicationContext context;

    /* (non-Javadoc)
     * @see org.apache.axis2.modules.Module#init(org.apache.axis2.context.ConfigurationContext, org.apache.axis2.description.AxisModule)
     */
    @Override
    public void init(final ConfigurationContext configContext, final AxisModule module) throws AxisFault {
        log.debug("Initializing Backend Module");

        Constants.module = module;

        if (context == null) {
            try {
                final PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();

                final Properties properties = new Properties();

                properties.setProperty(eu.domibus.common.Constants.PERSISTENCE_UNIT, Constants.getPersistenceUnit());
                properties.setProperty(eu.domibus.common.Constants.DOMIBUS_PERSISTENCE_PROPERTIES,
                                       Constants.getDomibusPersistenceProperties());
                properties.setProperty(eu.domibus.backend.module.Constants.DELETE_MESSAGES_CRON_PROPERTY_KEY,
                                       Constants.getDeleteMessagesCron());

                configurer.setProperties(properties);

                final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
                context.addBeanFactoryPostProcessor(configurer);
                context.setConfigLocation("/backendApplicationContext.xml");
                context.refresh();

                BackendModule.context = context;
            } catch (Throwable throwable) {
                log.error("Backend Module failed loading Spring", throwable);

                throw new AxisFault("Error launching Spring Framework", throwable);
            }
        }

        log.debug("Backend Module Started");
    }

    /* (non-Javadoc)
     * @see org.apache.axis2.modules.Module#shutdown(org.apache.axis2.context.ConfigurationContext)
     */
    @Override
    public void shutdown(final ConfigurationContext configCtx) throws AxisFault {
    }

    /* (non-Javadoc)
     * @see org.apache.axis2.modules.Module#engageNotify(org.apache.axis2.description.AxisDescription)
     */
    @Override
    public void engageNotify(final AxisDescription axisDescription) throws AxisFault {
    }

    /* (non-Javadoc)
     * @see org.apache.axis2.modules.Module#applyPolicy(org.apache.neethi.Policy, org.apache.axis2.description.AxisDescription)
     */
    @Override
    public void applyPolicy(final Policy policy, final AxisDescription axisDescription) throws AxisFault {
    }

    /* (non-Javadoc)
     * @see org.apache.axis2.modules.Module#canSupportAssertion(org.apache.neethi.Assertion)
     */
    @Override
    public boolean canSupportAssertion(final Assertion assertion) {
        return true;
    }

    /**
     * Gets the context.
     *
     * @return the context
     */
    public static ApplicationContext getContext() {
        return context;
    }
}