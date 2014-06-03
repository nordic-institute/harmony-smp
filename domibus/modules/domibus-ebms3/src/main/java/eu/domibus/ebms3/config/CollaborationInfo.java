package eu.domibus.ebms3.config;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "CollaborationInfo", strict = false)
public class CollaborationInfo implements java.io.Serializable {
    private static final long serialVersionUID = -5593316571234520737L;

    @Element(name = "Service")
    protected Service service;

    @Element(name = "Action")
    protected String action;

    public CollaborationInfo() {
    }

    public CollaborationInfo(final Service service, final String action) {
        this.service = service;
        this.action = action;
    }

    public Service getService() {
        return service;
    }

    public void setService(final Service service) {
        this.service = service;
    }

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof CollaborationInfo)) {
            return false;
        }
        final CollaborationInfo c = (CollaborationInfo) obj;
        return action.equalsIgnoreCase(c.getAction()) && service.equals(c.getService());
    }
}