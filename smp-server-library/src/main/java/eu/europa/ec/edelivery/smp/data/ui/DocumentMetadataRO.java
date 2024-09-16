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

import java.util.ArrayList;
import java.util.List;

/**
 * 'DocumentMetadataRO' represents the metadata of a document.
 *
 * @author Joze RIHTARSIC
 * @since 5.1
 */
public class DocumentMetadataRO extends BaseRO {
    private static final long serialVersionUID = 9008583888835630040L;

    String name;
    String mimeType;
    String referenceDocumentId;
    Integer publishedVersion;
    List<Integer> allVersions;
    Boolean sharingEnabled = Boolean.FALSE;

    public String getReferenceDocumentId() {
        return referenceDocumentId;
    }

    public void setReferenceDocumentId(String referenceDocumentId) {
        this.referenceDocumentId = referenceDocumentId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Integer getPublishedVersion() {
        return publishedVersion;
    }

    public void setPublishedVersion(Integer publishedVersion) {
        this.publishedVersion = publishedVersion;
    }

    public List<Integer> getAllVersions() {
        if (allVersions == null) {
            allVersions = new ArrayList<>();
        }
        return allVersions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSharingEnabled() {
        return sharingEnabled;
    }

    public void setSharingEnabled(Boolean sharingEnabled) {
        this.sharingEnabled = sharingEnabled;
    }
}
