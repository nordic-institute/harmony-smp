/*
 * 
 */
package eu.domibus.backend.spring;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * The Class BackendSpringBeanAutowiringSupport.
 */
public class BackendSpringBeanAutowiringSupport extends SpringBeanAutowiringSupport {

    /**
     * The spring injected.
     */
    private boolean springInjected = false;

    /**
     * Inits the.
     */
    public synchronized void init() {
        if (!springInjected) {
            final ApplicationContext cc = eu.domibus.backend.module.BackendModule.getContext();

            if (cc != null) {
                final AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
                bpp.setBeanFactory(cc.getAutowireCapableBeanFactory());
                bpp.processInjection(this);
            }

            springInjected = true;
        }
    }
}
