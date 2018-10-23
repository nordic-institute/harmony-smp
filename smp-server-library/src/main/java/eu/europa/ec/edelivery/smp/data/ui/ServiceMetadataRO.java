package eu.europa.ec.edelivery.smp.data.ui;


import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class ServiceMetadataRO extends BaseRO {


    private static final long serialVersionUID = 67944640449327185L;
    private Long id;
    String documentIdentifier;
    String documentIdentifierScheme;
    Long serviceGroupDomainId;
   // Long domainId;
    String smlSubdomain;
    String domainCode;
    private int xmlContentStatus = EntityROStatus.PERSISTED.getStatusNumber();
    String xmlContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceGroupDomainId() {
        return serviceGroupDomainId;
    }

    public void setServiceGroupDomainId(Long serviceGroupDomainId) {
        this.serviceGroupDomainId = serviceGroupDomainId;
    }
/*
    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }
*/
    public String getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(String documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public String getDocumentIdentifierScheme() {
        return documentIdentifierScheme;
    }

    public void setDocumentIdentifierScheme(String documentIdentifierScheme) {
        this.documentIdentifierScheme = documentIdentifierScheme;
    }

    public String getSmlSubdomain() {
        return smlSubdomain;
    }

    public void setSmlSubdomain(String smlSubdomain) {
        this.smlSubdomain = smlSubdomain;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public int getXmlContentStatus() {
        return xmlContentStatus;
    }

    public void setXmlContentStatus(int xmlContentStatus) {
        this.xmlContentStatus = xmlContentStatus;
    }
}