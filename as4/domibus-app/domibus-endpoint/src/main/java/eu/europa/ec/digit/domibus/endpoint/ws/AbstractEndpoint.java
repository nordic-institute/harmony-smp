package eu.europa.ec.digit.domibus.endpoint.ws;

import javax.annotation.PostConstruct;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;


public abstract class AbstractEndpoint extends SpringBeanAutowiringSupport {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */
	
	/**
	 * Note that this post construction is needed for running on JBOSS 7.X. Normally extending
	 * the SpringBeanAutowiringSupport is sufficient to inject the Spring configuration in the
	 * endpoint.
	 */
    @PostConstruct
    public void init() {
    	SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

	/* ---- Getters and Setters ---- */

}
