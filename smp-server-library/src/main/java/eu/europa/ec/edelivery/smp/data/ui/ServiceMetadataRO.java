package eu.europa.ec.edelivery.smp.data.ui;


import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class ServiceMetadataRO extends BaseRO {


    private static final long serialVersionUID = 9008583888835630018L;
    private Long id;
    String documentIdentifier;
    String documentIdentifierScheme;
    String smlSubdomain;
    String domainCode;

    String subresourceDefUrlSegment;
    private int xmlContentStatus = EntityROStatus.PERSISTED.getStatusNumber();
    String xmlContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(String documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public String getSubresourceDefUrlSegment() {
        return subresourceDefUrlSegment;
    }

    public void setSubresourceDefUrlSegment(String subresourceDefUrlSegment) {
        this.subresourceDefUrlSegment = subresourceDefUrlSegment;
    }

    public String getDocumentIdentifierScheme() {

        return StringUtils.isEmpty(documentIdentifierScheme)?null: documentIdentifierScheme;
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
