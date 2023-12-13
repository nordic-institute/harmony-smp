/*-
 * #%L
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #L%
 */
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
