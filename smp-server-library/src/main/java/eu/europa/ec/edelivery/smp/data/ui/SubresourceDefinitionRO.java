package eu.europa.ec.edelivery.smp.data.ui;


/**
 * @author Joze Rihtarsic
 * @since 5.0
 */

public class SubresourceDefinitionRO extends BaseRO {
    private static final long serialVersionUID = 9008583888835630025L;

    String resourceId;
    private String identifier;
    private String name;
    private String description;
    private String mimeType;
    String urlSegment;

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
}
