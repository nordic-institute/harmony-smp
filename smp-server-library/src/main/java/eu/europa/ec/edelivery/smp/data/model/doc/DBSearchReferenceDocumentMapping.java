package eu.europa.ec.edelivery.smp.data.model.doc;

import eu.europa.ec.edelivery.smp.data.enums.DocumentReferenceType;

import java.util.StringJoiner;

/**
 * Class represents the document reference mapping. It is used with query
 * to get all reference documents for target resource
 */
public class DBSearchReferenceDocumentMapping {

    private Long documentId;
    private Long resourceId;
    private Long subresourceId;

    private DocumentReferenceType referenceType;

    private String documentName;
    private String resourceValue;
    private String resourceScheme;
    private String subresourceValue;
    private String subresourceScheme;

    private String domainCode;
    private String resourceDefUrlSegment;
    private String subresourceDefUrlSegment;

    public DBSearchReferenceDocumentMapping(Long documentId,
                                            Long resourceId,
                                            String documentName,
                                            String resourceValue,
                                            String resourceScheme,
                                            String domainCode,
                                            String resourceDefUrlSegment) {
        referenceType = DocumentReferenceType.RESOURCE;
        this.documentId = documentId;
        this.resourceId = resourceId;
        this.documentName = documentName;
        this.resourceValue = resourceValue;
        this.resourceScheme = resourceScheme;
        this.domainCode = domainCode;
        this.resourceDefUrlSegment = resourceDefUrlSegment;
    }

    public DBSearchReferenceDocumentMapping(Long documentId,
                                            Long resourceId,
                                            Long subresourceId,
                                            String documentName,
                                            String resourceValue,
                                            String resourceScheme,
                                            String subresourceValue,
                                            String subresourceScheme,
                                            String domainCode,
                                            String resourceDefUrlSegment,
                                            String subresourceDefUrlSegment) {
        referenceType = DocumentReferenceType.SUBRESOURCE;
        this.documentId = documentId;
        this.resourceId = resourceId;
        this.subresourceId = subresourceId;
        this.documentName = documentName;
        this.resourceValue = resourceValue;
        this.resourceScheme = resourceScheme;
        this.subresourceValue = subresourceValue;
        this.subresourceScheme = subresourceScheme;
        this.domainCode = domainCode;
        this.resourceDefUrlSegment = resourceDefUrlSegment;
        this.subresourceDefUrlSegment = subresourceDefUrlSegment;
    }


    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getSubresourceId() {
        return subresourceId;
    }

    public void setSubresourceId(Long subresourceId) {
        this.subresourceId = subresourceId;
    }

    public DocumentReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(DocumentReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getResourceValue() {
        return resourceValue;
    }

    public void setResourceValue(String resourceValue) {
        this.resourceValue = resourceValue;
    }

    public String getResourceScheme() {
        return resourceScheme;
    }

    public void setResourceScheme(String resourceScheme) {
        this.resourceScheme = resourceScheme;
    }

    public String getSubresourceValue() {
        return subresourceValue;
    }

    public void setSubresourceValue(String subresourceValue) {
        this.subresourceValue = subresourceValue;
    }

    public String getSubresourceScheme() {
        return subresourceScheme;
    }

    public void setSubresourceScheme(String subresourceScheme) {
        this.subresourceScheme = subresourceScheme;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getResourceDefUrlSegment() {
        return resourceDefUrlSegment;
    }

    public void setResourceDefUrlSegment(String resourceDefUrlSegment) {
        this.resourceDefUrlSegment = resourceDefUrlSegment;
    }

    public String getSubresourceDefUrlSegment() {
        return subresourceDefUrlSegment;
    }

    public void setSubresourceDefUrlSegment(String subresourceDefUrlSegment) {
        this.subresourceDefUrlSegment = subresourceDefUrlSegment;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DBSearchReferenceDocumentMapping.class.getSimpleName() + "[", "]")
                .add("documentId=" + documentId)
                .add("resourceId=" + resourceId)
                .add("subresourceId=" + subresourceId)
                .add("resourceValue='" + resourceValue + "'")
                .add("resourceScheme='" + resourceScheme + "'")
                .add("subresourceValue='" + subresourceValue + "'")
                .add("subresourceScheme='" + subresourceScheme + "'")
                .toString();
    }
}
