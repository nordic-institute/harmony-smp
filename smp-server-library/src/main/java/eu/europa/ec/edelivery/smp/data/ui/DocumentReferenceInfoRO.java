/*-
 * #START_LICENSE#
 * smp-webapp
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

import java.io.Serial;
import java.io.Serializable;

/**
 * The entity DocumentReferenceInfoRO is object to transfer document reference info to UI
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
public class DocumentReferenceInfoRO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9008583888835630042L;

    private Boolean sharingEnabled;
    private Long referencedByCount;
    private Boolean referencedDocumentExists;
    private String referenceUrlPath;

    public Boolean getSharingEnabled() {
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

    public Boolean getReferencedDocumentExists() {
        return referencedDocumentExists;
    }

    public void setReferencedDocumentExists(Boolean referencedDocumentExists) {
        this.referencedDocumentExists = referencedDocumentExists;
    }

    public String getReferenceUrlPath() {
        return referenceUrlPath;
    }

    public void setReferenceUrlPath(String referenceUrlPath) {
        this.referenceUrlPath = referenceUrlPath;
    }
}
