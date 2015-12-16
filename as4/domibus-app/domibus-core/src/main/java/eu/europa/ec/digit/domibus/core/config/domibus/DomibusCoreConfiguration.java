package eu.europa.ec.digit.domibus.core.config.domibus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import eu.domibus.submission.routing.AbstractRoutingCriteria;
import eu.domibus.submission.routing.ActionRoutingCriteriaFactory;
import eu.domibus.submission.routing.IRoutingCriteria;
import eu.europa.ec.digit.domibus.base.config.domibus.DomibusBaseConfiguration;
import eu.europa.ec.digit.domibus.core.config.CoreConfiguration;
import eu.europa.ec.digit.domibus.core.policy.notification.BasicNotificationPolicy;
import eu.europa.ec.digit.domibus.core.policy.notification.NotificationPolicy;
import eu.europa.ec.digit.domibus.core.util.NotificationFilter;

@Configuration
@Import({
    DomibusBaseConfiguration.class
})
public class DomibusCoreConfiguration extends CoreConfiguration {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */
    @Autowired
    private BasicNotificationPolicy basicNotificationPolicy = null;

    /* ---- Constructors ---- */

    /* ---- Configuration Beans ---- */
    @Bean(name = "notificationSelectionPolicy")
    public Map<String, NotificationPolicy> notificationSelectionPolicy() {
        Map<String, NotificationPolicy> map = new HashMap<>();
        map.put("basic", basicNotificationPolicy);
        return map;
    }

    @Bean(name = "notificationFilterList")
    public List<NotificationFilter> notificationFilterList() {
        List<NotificationFilter> notificationFilterList = new ArrayList<>();

        NotificationFilter notificationFilter = new NotificationFilter();
        IRoutingCriteria actionRoutingCriteria = new ActionRoutingCriteriaFactory().getInstance();

        // Create Basic NotificationFilter
        notificationFilter.setNotificationFacadeName("basic");

        // Add Basic Routing Criteria
        actionRoutingCriteria.setExpression("submit");
        notificationFilter.addRoutingCriteria((AbstractRoutingCriteria) actionRoutingCriteria);
        notificationFilterList.add(notificationFilter);

        return notificationFilterList;
    }
    /* ---- Getters and Setters ---- */

    public BasicNotificationPolicy getBasicNotificationPolicy() {
        return basicNotificationPolicy;
    }

    public void setBasicNotificationPolicy(BasicNotificationPolicy basicNotificationPolicy) {
        this.basicNotificationPolicy = basicNotificationPolicy;
    }

}
