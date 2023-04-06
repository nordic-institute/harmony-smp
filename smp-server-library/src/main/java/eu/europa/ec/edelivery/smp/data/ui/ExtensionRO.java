package eu.europa.ec.edelivery.smp.data.ui;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */

public class ExtensionRO extends BaseRO {

    private static final long serialVersionUID = 9008583888835630009L;

    String extensionId;
    private String version;
    private String name;
    private String description;
    private String implementationName;
    private List<ResourceDefinitionRO> resourceDefinitions = new ArrayList<>();

    public String getExtensionId() {
        return extensionId;
    }

    public void setExtensionId(String extensionId) {
        this.extensionId = extensionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public String getImplementationName() {
        return implementationName;
    }

    public void setImplementationName(String implementationName) {
        this.implementationName = implementationName;
    }

    public List<ResourceDefinitionRO> getResourceDefinitions() {
        return resourceDefinitions;
    }
}
