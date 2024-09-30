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

import java.io.Serializable;
import java.util.*;

/**
 * @since 5.1
 * @author Sebastian-Ion TINCU
 */
public class ResourceFilterOptionsResult implements Serializable {

    private static final long serialVersionUID = 6677275164291128366L;

    // The set of all the available domain codes
    private Set<String> availableDomains = new LinkedHashSet<>();

    // The set of all the available document types
    private Set<String> availableDocumentTypes = new LinkedHashSet<>();

    public ResourceFilterOptionsResult(List<String> domainCodes, List<String> documentTypes) {
        this.availableDomains.addAll(new TreeSet<>(domainCodes));
        this.availableDocumentTypes.addAll(new TreeSet<>(documentTypes));
    }

    public Set<String> getAvailableDomains() {
        return availableDomains;
    }

    public Set<String> getAvailableDocumentTypes() {
        return availableDocumentTypes;
    }
}
