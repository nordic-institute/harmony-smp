package eu.eCODEX.spring;


import eu.domibus.common.util.JNDIUtil;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class ContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {
    @Override
    public void initialize(ConfigurableWebApplicationContext configurableWebApplicationContext) {
        Properties props = new Properties();
        String contextDir = JNDIUtil.getStringEnvironmentParameter("domibus.module.spring.context.folder");
        String absolutePath = null;
        try {
            absolutePath = new File(contextDir).getCanonicalPath();
            System.out.println(absolutePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        props.setProperty("domibus.module.spring.context.folder.absolute", absolutePath);
        StringPropertySource propSource = new StringPropertySource(props, "directorySource");
        configurableWebApplicationContext.getEnvironment().getPropertySources().addFirst(propSource);
    }

}

