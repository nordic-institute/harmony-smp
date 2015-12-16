package eu.europa.ec.digit.domibus.base.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public abstract class NotificationEndpointPolicy<T, R> {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */
	
	@Autowired
	protected Environment environment = null;

	/* ---- Constructors ---- */

	/* ---- Business Methods ---- */

	public abstract R submit(T messageObject);
	
	/* ---- Getters and Setters ---- */

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

}
