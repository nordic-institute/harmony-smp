
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
    private Boolean hasCurrentUserReviewPermission = false;
    private Boolean reviewEnabled = false;
    private Long status;
    private String visibility;
    private String statusMessage;
    private String documentReferenceInfo;


    public Boolean getHasCurrentUserReviewPermission() {
        return hasCurrentUserReviewPermission;
    }

    public void setHasCurrentUserReviewPermission(Boolean hasCurrentUserReviewPermission) {
        this.hasCurrentUserReviewPermission = hasCurrentUserReviewPermission;
    }

    public Boolean getReviewEnabled() {
        return reviewEnabled;
    }

    public void setReviewEnabled(Boolean reviewEnabled) {
        this.reviewEnabled = reviewEnabled;
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

    public static ResourceModel generatePublicResource() {
        ResourceModel resourceModel = new ResourceModel();
        resourceModel.resourceTypeIdentifier = StringUtils.lowerCase(Utils.randomEnum(getAllEnumValues(ResourceTypes.class)).getName());
        resourceModel.identifierValue = ("AUT_resourceIdentifier_" + Generator.randomAlphaNumericValue(4)).toLowerCase();
        resourceModel.identifierScheme = Generator.randomAlphabeticalValue(4).toLowerCase() + "-" + Generator.randomAlphaNumericValue(4).toLowerCase() + "-" + Generator.randomAlphaNumericValue(4).toLowerCase();
        resourceModel.visibility = "PUBLIC";
        resourceModel.reviewEnabled = false;
        return resourceModel;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getDocumentReferenceInfo() {
        return documentReferenceInfo;
    }

    public void setDocumentReferenceInfo(String documentReferenceInfo) {
        this.documentReferenceInfo = documentReferenceInfo;
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

    public static ResourceModel generatePublicResource(ResourceTypes resourceType) {
        ResourceModel resourceModel = new ResourceModel();
        resourceModel.identifierScheme = StringUtils.lowerCase(Generator.randomAlphabeticalValue(3) + "-" + Generator.randomAlphaNumericValue(3) + "-" + Generator.randomAlphaNumericValue(3));
        resourceModel.identifierValue = "AUT_resIden_" + Generator.randomAlphaNumericValue(4).toLowerCase();
        resourceModel.smlRegistered = false;
        resourceModel.resourceTypeIdentifier = StringUtils.lowerCase(resourceType.getName());
        resourceModel.visibility = "PUBLIC";
        return resourceModel;
    }

    public static ResourceModel generatePrivateResource(ResourceTypes resourceType) {
        ResourceModel resourceModel = new ResourceModel();
        resourceModel.identifierScheme = StringUtils.lowerCase(Generator.randomAlphabeticalValue(3) + "-" + Generator.randomAlphaNumericValue(3) + "-" + Generator.randomAlphaNumericValue(3));
        resourceModel.identifierValue = "AUT_resIden_" + Generator.randomAlphaNumericValue(4).toLowerCase();
        resourceModel.smlRegistered = false;
        resourceModel.resourceTypeIdentifier = StringUtils.lowerCase(resourceType.getName());
        resourceModel.visibility = "PRIVATE";
        return resourceModel;
    }

    public static ResourceModel generatePublicResourceWithReview(ResourceTypes resourceType) {
        ResourceModel resourceModel = new ResourceModel();
        resourceModel.identifierScheme = StringUtils.lowerCase(Generator.randomAlphabeticalValue(3) + "-" + Generator.randomAlphaNumericValue(3) + "-" + Generator.randomAlphaNumericValue(3));
        resourceModel.identifierValue = "AUT_resIden_" + Generator.randomAlphaNumericValue(4).toLowerCase();
        resourceModel.smlRegistered = false;
        resourceModel.resourceTypeIdentifier = StringUtils.lowerCase(resourceType.getName());
        resourceModel.visibility = "PUBLIC";
        resourceModel.reviewEnabled = true;
        return resourceModel;
    }


}
