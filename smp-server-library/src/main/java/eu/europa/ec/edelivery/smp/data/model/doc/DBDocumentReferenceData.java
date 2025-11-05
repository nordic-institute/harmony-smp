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

/**
 * The entityDBDocumentReferenceData is object to map results from sql query to gather document reference data
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
public class DBDocumentReferenceData {

    private Long documentId;
    private Boolean sharingEnabled;
    private Long referencedByCount;
    private Long referencedDocumentId;
    private String referenceUrlPath;

    public DBDocumentReferenceData(Long documentId, Boolean sharingEnabled, Long referencedByCount, Long referencedDocumentId, String referenceUrlPath) {
        this.documentId = documentId;
        this.sharingEnabled = sharingEnabled;
        this.referencedByCount = referencedByCount;
        this.referencedDocumentId = referencedDocumentId;
        this.referenceUrlPath = referenceUrlPath;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Boolean isSharingEnabled() {
        return sharingEnabled;
    }

    public void setSharingEnabled(Boolean sharingEnabled) {
        this.sharingEnabled = sharingEnabled;
    }

    public Long getReferencedByCount() {
        return referencedByCount;
    }

    public void setReferencedByCount(Long referencedByCount) {
        this.referencedByCount = referencedByCount;
    }

    public Long getReferencedDocumentId() {
        return referencedDocumentId;
    }

    public void setReferencedDocumentId(Long referencedDocumentId) {
        this.referencedDocumentId = referencedDocumentId;
    }

    public String getReferenceUrlPath() {
        return referenceUrlPath;
    }

    public void setReferenceUrlPath(String referenceUrlPath) {
        this.referenceUrlPath = referenceUrlPath;
    }
}
