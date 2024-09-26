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


import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionEventType;
import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;
import eu.europa.ec.edelivery.smp.data.enums.EventSourceType;

import java.time.OffsetDateTime;

/**
 * Document version event. The event entity allows user to track
 * changes in the document version.
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
public class DocumentVersionEventRO extends BaseRO {

    private static final long serialVersionUID = 9008583888835630037L;

    private DocumentVersionEventType eventType = DocumentVersionEventType.CREATE;
    private DocumentVersionStatusType documentVersionStatus = DocumentVersionStatusType.DRAFT;
    private OffsetDateTime eventOn;
    private String username;
    private EventSourceType eventSourceType = EventSourceType.OTHER;
    private String details;


    public DocumentVersionEventType getEventType() {
        return eventType;
    }

    public void setEventType(DocumentVersionEventType eventType) {
        this.eventType = eventType;
    }

    public OffsetDateTime getEventOn() {
        return eventOn;
    }

    public void setEventOn(OffsetDateTime eventOn) {
        this.eventOn = eventOn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public EventSourceType getEventSourceType() {
        return eventSourceType;
    }

    public void setEventSourceType(EventSourceType eventSourceType) {
        this.eventSourceType = eventSourceType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }


    public DocumentVersionStatusType getDocumentVersionStatus() {
        return documentVersionStatus;
    }

    public void setDocumentVersionStatus(DocumentVersionStatusType status) {
        this.documentVersionStatus = status;
    }
}
