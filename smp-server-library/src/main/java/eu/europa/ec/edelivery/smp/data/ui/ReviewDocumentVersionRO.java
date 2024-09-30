/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;

import java.time.OffsetDateTime;
/**
 * Class represents RO for the document version for the review
 *
 * @since 5.1
 * @author Joze RIHARSIC
 */
public class ReviewDocumentVersionRO {

    private String documentId;
    private String documentVersionId;
    private String resourceId;
    private String subresourceId;
    private int version;
    private DocumentVersionStatusType currentStatus = DocumentVersionStatusType.DRAFT;
    private String resourceIdentifierValue;
    private String resourceIdentifierScheme;
    private String subresourceIdentifierValue;
    private String subresourceIdentifierScheme;
    private String target;
    private OffsetDateTime lastUpdatedOn;



    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentVersionId() {
        return documentVersionId;
    }

    public void setDocumentVersionId(String documentVersionId) {
        this.documentVersionId = documentVersionId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DocumentVersionStatusType getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(DocumentVersionStatusType currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getSubresourceId() {
        return subresourceId;
    }

    public void setSubresourceId(String subresourceId) {
        this.subresourceId = subresourceId;
    }

    public String getResourceIdentifierValue() {
        return resourceIdentifierValue;
    }

    public void setResourceIdentifierValue(String resourceIdentifierValue) {
        this.resourceIdentifierValue = resourceIdentifierValue;
    }

    public String getResourceIdentifierScheme() {
        return resourceIdentifierScheme;
    }

    public void setResourceIdentifierScheme(String resourceIdentifierScheme) {
        this.resourceIdentifierScheme = resourceIdentifierScheme;
    }

    public String getSubresourceIdentifierValue() {
        return subresourceIdentifierValue;
    }

    public void setSubresourceIdentifierValue(String subresourceIdentifierValue) {
        this.subresourceIdentifierValue = subresourceIdentifierValue;
    }

    public String getSubresourceIdentifierScheme() {
        return subresourceIdentifierScheme;
    }

    public void setSubresourceIdentifierScheme(String subresourceIdentifierScheme) {
        this.subresourceIdentifierScheme = subresourceIdentifierScheme;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public OffsetDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(OffsetDateTime lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }
}
