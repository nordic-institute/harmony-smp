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

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class DocumentVersionRO implements Serializable {
    private static final long serialVersionUID = 9008583888835630039L;

    private int version;
    private DocumentVersionStatusType versionStatus;
    private OffsetDateTime createdOn;
    private OffsetDateTime lastUpdatedOn;


    List<DocumentVersionEventRO> documentVersionEvents = new ArrayList<>();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DocumentVersionStatusType getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(DocumentVersionStatusType versionStatus) {
        this.versionStatus = versionStatus;
    }

    public List<DocumentVersionEventRO> getDocumentVersionEvents() {
        if (documentVersionEvents == null) {
            documentVersionEvents = new ArrayList<>();
        }
        return documentVersionEvents;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public OffsetDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(OffsetDateTime lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }
}
