package eu.europa.ec.edelivery.smp.data.ui;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */

public class ResourceDefinitionRO extends BaseRO {
    private static final long serialVersionUID = 9008583888835630013L;

    String resourceId;
    private String identifier;
    private String name;
    private String description;
    private String mimeType;
    private String urlSegment;

    private List<SubresourceDefinitionRO> subresourceDefinitions = new ArrayList<>();

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getUrlSegment() {
        return urlSegment;
    }

    public void setUrlSegment(String urlSegment) {
        this.urlSegment = urlSegment;
    }

    public List<SubresourceDefinitionRO> getSubresourceDefinitions() {
        return subresourceDefinitions;
    }
}
