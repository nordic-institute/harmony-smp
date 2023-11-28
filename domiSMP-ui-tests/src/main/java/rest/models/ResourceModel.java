
package rest.models;

import ddsl.enums.ResourceTypes;
import org.apache.commons.lang3.StringUtils;
import utils.Generator;
import utils.Utils;

import static utils.Utils.getAllEnumValues;


public class ResourceModel {

    private Object actionMessage;
    private String identifierScheme;
    private String identifierValue;
    private Long index;
    private String resourceId;
    private String resourceTypeIdentifier;
    private Boolean smlRegistered;
    private Long status;
    private String visibility;

    public static ResourceModel generatePublicResource() {
        ResourceModel resourceModel = new ResourceModel();
        resourceModel.resourceTypeIdentifier = StringUtils.lowerCase(Utils.randomEnum(getAllEnumValues(ResourceTypes.class)).getName());
        resourceModel.identifierValue = ("AUT_resourceIdentifier_" + Generator.randomAlphaNumericValue(4)).toLowerCase();
        resourceModel.identifierScheme = Generator.randomAlphabeticalValue(4).toLowerCase() + "-" + Generator.randomAlphaNumericValue(4).toLowerCase() + "-" + Generator.randomAlphaNumericValue(4).toLowerCase();
        resourceModel.visibility = "PUBLIC";
        return resourceModel;
    }

    public String getIdentifierScheme() {
        return identifierScheme;
    }

    public void setIdentifierScheme(String identifierScheme) {
        this.identifierScheme = identifierScheme;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceTypeIdentifier() {
        return resourceTypeIdentifier;
    }

    public void setResourceTypeIdentifier(String resourceTypeIdentifier) {
        this.resourceTypeIdentifier = resourceTypeIdentifier;
    }

    public void setActionMessage(Object actionMessage) {
        this.actionMessage = actionMessage;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public void setSmlRegistered(Boolean smlRegistered) {
        this.smlRegistered = smlRegistered;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public static ResourceModel generatePublicResourceUnregisteredToSML() {
        ResourceModel resourceModel = new ResourceModel();
        resourceModel.identifierScheme = StringUtils.lowerCase(Generator.randomAlphabeticalValue(3) + "-" + Generator.randomAlphaNumericValue(3) + "-" + Generator.randomAlphaNumericValue(3));
        resourceModel.identifierValue = "AUT_resIden_" + Generator.randomAlphaNumericValue(4).toLowerCase();
        resourceModel.smlRegistered = false;
        resourceModel.resourceTypeIdentifier = StringUtils.lowerCase(Utils.randomEnum(ResourceTypes.values()).getName());
        resourceModel.visibility = "PUBLIC";
        return resourceModel;
    }


}
