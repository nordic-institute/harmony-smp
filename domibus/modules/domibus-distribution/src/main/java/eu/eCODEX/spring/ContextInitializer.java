package eu.eCODEX.spring;


import eu.domibus.common.util.JNDIUtil;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.io.File;
import java.util.Properties;

public class ContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

    private static final Logger LOGGER = Logger.getLogger(ContextInitializer.class);

    @Override
    public void initialize(ConfigurableWebApplicationContext configurableWebApplicationContext) {
        Properties props = new Properties();
        String contextDir = JNDIUtil.getStringEnvironmentParameter("domibus.module.spring.context.folder");
        String absolutePath = null;
        try {
            absolutePath = new File(contextDir).getCanonicalPath();
        } catch (Exception e) {
            LOGGER.error("Problem occured when attempting to read the file: " + contextDir, e);
        }
        props.setProperty("domibus.module.spring.context.folder.absolute", absolutePath);
        StringPropertySource propSource = new StringPropertySource(props, "directorySource");
        configurableWebApplicationContext.getEnvironment().getPropertySources().addFirst(propSource);
    }

}

