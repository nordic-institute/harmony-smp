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

import eu.europa.ec.edelivery.smp.data.enums.DocumentReferenceType;

/**
 *
 *
 * @since 5.0
 * @author Joze RIHTARSIC
 */
public class SearchReferenceDocumentRO extends BaseRO {

    private static final long serialVersionUID = 9008583888835630041L;

    private String documentId;
    private String resourceId;
    private String subresourceId;

    private DocumentReferenceType referenceType;
    private String referenceUrl;
    private String documentName;
    private String resourceValue;
    private String resourceScheme;
    private String subresourceValue;
    private String subresourceScheme;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getSubresourceId() {
        return subresourceId;
    }

    public void setSubresourceId(String subresourceId) {
        this.subresourceId = subresourceId;
    }


    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
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
}
