package eu.europa.ec.edelivery.smp.data.ui;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */

public class DomainResourceDefinitionRO extends BaseRO {
    private static final long serialVersionUID = 9008583888835630027L;

    String domainResourceId;
    private String resourceId;
    private String resourceIdentifier;

    public String getDomainResourceId() {
        return domainResourceId;
    }

    public void setDomainResourceId(String domainResourceId) {
        this.domainResourceId = domainResourceId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(String resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }
}
