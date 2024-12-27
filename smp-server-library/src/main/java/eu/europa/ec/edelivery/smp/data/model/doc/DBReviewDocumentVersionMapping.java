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
package eu.europa.ec.edelivery.smp.data.model.doc;

import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;

/**
 * Class represents the document version for the review. It is used with query
 * to get all review tasks for the user
 *
 * @author Joze RIHARSIC
 * @since 5.1
 */
public class DBReviewDocumentVersionMapping {

    private Long documentId;
    private Long documentVersionId;
    private Long resourceId;
    private Long subresourceId;
    private int version;
    private DocumentVersionStatusType status = DocumentVersionStatusType.DRAFT;
    private String resourceIdentifierValue;
    private String resourceIdentifierScheme;
    private String subresourceIdentifierValue;
    private String subresourceIdentifierScheme;
    private String target;
    private OffsetDateTime lastUpdatedOn;

    public DBReviewDocumentVersionMapping() {
    }

    public DBReviewDocumentVersionMapping(
            Long id,
            Long documentId,
            Long resourceId,
            Long subresourceId,
            int version,
            String status,
            String resourceIdentifierValue,
            String resourceIdentifierScheme,
            String subresourceIdentifierValue,
            String subresourceIdentifierScheme,
            String target,
            OffsetDateTime lastUpdatedOn) {
        this.documentId = documentId;
        this.documentVersionId = id;
        this.resourceId = resourceId;
        this.subresourceId = subresourceId;
        this.version = version;
        this.status = StringUtils.isNotBlank(status) ? DocumentVersionStatusType.valueOf(status) : null;
        this.resourceIdentifierValue = resourceIdentifierValue;
        this.resourceIdentifierScheme = resourceIdentifierScheme;
        this.subresourceIdentifierValue = subresourceIdentifierValue;
        this.subresourceIdentifierScheme = subresourceIdentifierScheme;
        this.target = target;
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getDocumentVersionId() {
        return documentVersionId;
    }

    public void setDocumentVersionId(Long documentVersionId) {
        this.documentVersionId = documentVersionId;
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

    public OffsetDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(OffsetDateTime lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DocumentVersionStatusType getStatus() {
        return status;
    }

    public void setStatus(DocumentVersionStatusType status) {
        this.status = status;
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
}
