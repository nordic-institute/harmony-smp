package eu.europa.ec.digit.domibus.core.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import eu.domibus.submission.routing.AbstractRoutingCriteria;
import eu.domibus.submission.routing.ActionRoutingCriteriaFactory;
import eu.domibus.submission.routing.IRoutingCriteria;
import eu.europa.ec.digit.domibus.base.config.BaseServiceConfiguration;
import eu.europa.ec.digit.domibus.core.util.NotificationFilter;

@Configuration
@Import({
	BaseServiceConfiguration.class
})
public class DomibusCoreTestConfiguration extends CoreConfiguration {

	/* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    /* ---- Constructors ---- */

    /* ---- Configuration Beans ---- */

	@Bean (name = "notificationFilterList")
	public List<NotificationFilter> notificationFilterList() {
		List<NotificationFilter> notificationFilterList = new ArrayList<>();

		// Create NotificationFilter
		NotificationFilter notificationFilter = new NotificationFilter();
		notificationFilter.setNotificationFacadeName("default");

		// Add Routing Criteria
		IRoutingCriteria actionRoutingCriteria = new ActionRoutingCriteriaFactory().getInstance();
        actionRoutingCriteria.setExpression("SendMessage");
        notificationFilter.addRoutingCriteria((AbstractRoutingCriteria)actionRoutingCriteria);

        notificationFilterList.add(notificationFilter);
		return notificationFilterList;
	}

    /* ---- Getters and Setters ---- */


}
