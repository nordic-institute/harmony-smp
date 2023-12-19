/*-
 * #START_LICENSE#
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
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.data.ui;


import java.util.ArrayList;
import java.util.List;

/**
 * Lighter (without administration walues) ServiceGroup object for searching service group and its metadata.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

public class ServiceGroupSearchRO extends BaseRO {


    private static final long serialVersionUID = 9008583888835630016L;
    private Long id;

    private String domainCode;
    private String resourceDefUrlSegment;
    private String participantIdentifier;
    private String participantScheme;
    private final List<ServiceMetadataRO> lstServiceMetadata = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParticipantIdentifier() {
        return participantIdentifier;
    }

    public void setParticipantIdentifier(String participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
    }

    public String getParticipantScheme() {
        return participantScheme;
    }

    public void setParticipantScheme(String participantScheme) {
        this.participantScheme = participantScheme;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getResourceDefUrlSegment() {
        return resourceDefUrlSegment;
    }

    public void setResourceDefUrlSegment(String resourceDefUrlSegment) {
        this.resourceDefUrlSegment = resourceDefUrlSegment;
    }

    public List<ServiceMetadataRO> getServiceMetadata() {
        return lstServiceMetadata;
    }
}
