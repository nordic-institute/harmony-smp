package eu.europa.ec.digit.domibus.core.util;

import java.util.ArrayList;
import java.util.List;

import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import eu.domibus.submission.routing.AbstractRoutingCriteria;


public class NotificationFilter {

	/* ---- Constants ---- */

	/* ---- Instance Variables ---- */

    private List<AbstractRoutingCriteria> routingCriterias = new ArrayList<>();
    private String notificationFacadeName;

    /* ---- Constructors ---- */

    public NotificationFilter() {
        routingCriterias = new ArrayList<AbstractRoutingCriteria>();
    }

    /* ---- Business Methods ---- */

    public void addRoutingCriteria(AbstractRoutingCriteria routingCriteria) {
        this.routingCriterias.add(routingCriteria);
    }

    public boolean matches(UserMessage message) {
        for (AbstractRoutingCriteria routingCriteria : routingCriterias) {
               if (!routingCriteria.matches(message)) {
                   return false;
               }
        }
        return true;
    }

    /* ---- Getters and Setters ---- */

    public List<AbstractRoutingCriteria> getRoutingCriterias() {
        return routingCriterias;
    }

    public void setRoutingCriterias(List<AbstractRoutingCriteria> routingCriterias) {
        this.routingCriterias = routingCriterias;
    }

    public String getNotificationFacadeName() {
        return notificationFacadeName;
    }

    public void setNotificationFacadeName(String notificationFacadeName) {
        this.notificationFacadeName = notificationFacadeName;
    }

}
