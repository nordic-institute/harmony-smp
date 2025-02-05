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
package eu.europa.ec.edelivery.smp.services.resource;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionEventType;
import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;
import eu.europa.ec.edelivery.smp.data.enums.EventSourceType;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentVersion;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentVersionEvent;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

/**
 * Service for document version events
 *
 * @author Joze RIHTARSIC
 * @since 5.1
 */
@Service
public class DocumentVersionService {
    protected static final SMPLogger LOG = SMPLoggerFactory.getLogger(DocumentVersionService.class);
    public static final String DOCUMENT_VERSION_INITIALIZED_BY_GROUP_ADMIN = "Create and publish resource by group admin";

    /**
     * Create document version initialized by group admin. This is used when group admin creates and publishes resource.
     *
     * @param eventSourceType
     * @return DBDocumentVersion initialized by group admin
     */
    public DBDocumentVersion initializeDocumentVersionByGroupAdmin(EventSourceType eventSourceType) {

        return createDocumentVersionForCreate(eventSourceType, DOCUMENT_VERSION_INITIALIZED_BY_GROUP_ADMIN, true);
    }


    /**
     * Create document version
     *
     * @param eventSourceType
     * @param details
     * @return
     */
    public DBDocumentVersion createDocumentVersionForCreate(EventSourceType eventSourceType,
                                                            String details, boolean publish) {

        DBDocumentVersion dbDocumentVersion = new DBDocumentVersion();
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.CREATE,
                DocumentVersionStatusType.DRAFT,
                eventSourceType, details);
        dbDocumentVersion.addNewDocumentVersionEvent(dbEvent);
        dbDocumentVersion.setStatus(DocumentVersionStatusType.DRAFT);
        if (publish) {
            LOG.debug("Creating And Publish event for document version");
            publishDocumentVersion(dbDocumentVersion, eventSourceType, false);
        }
        return dbDocumentVersion;
    }

    /**
     * Method sets document version status to retired and adds retire event to the document version list of events
     *
     * @param dbDocumentVersion document version to be retired
     * @param eventSourceType   event source type
     * @param details           details of the event
     */
    public void retireDocumentVersion(DBDocumentVersion dbDocumentVersion, EventSourceType eventSourceType, String details) {
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.RETIRE,
                DocumentVersionStatusType.RETIRED, eventSourceType, details);
        dbDocumentVersion.addNewDocumentVersionEvent(dbEvent);
        dbDocumentVersion.setStatus(DocumentVersionStatusType.RETIRED);
    }

    /**
     * Method sets document version status to published and adds Publish event to the document version list of events
     *
     * @param dbDocumentVersion    document version to be published
     * @param eventSourceType      event source type
     * @param addFirstPublishEvent if true, the event will be added to fist place in the list of events (because the list is in reverse order it is
     *                             necessary to add it to the first place when adding single event, but when adding multiple events, the order should be reversed)
     */
    public void publishDocumentVersion(DBDocumentVersion dbDocumentVersion, EventSourceType eventSourceType, boolean addFirstPublishEvent) {
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.PUBLISH,
                DocumentVersionStatusType.PUBLISHED, eventSourceType, null);
        dbDocumentVersion.addNewDocumentVersionEvent(dbEvent, addFirstPublishEvent);
        dbDocumentVersion.setStatus(DocumentVersionStatusType.PUBLISHED);
    }

    /**
     * Method sets document version status to approved and add new event to list of events
     * It also submits request mails to the review requesters
     *
     * @param dbDocumentVersion document version to be resource administrators
     * @param eventSourceType   event source type
     * @param message           message to be sent to the resource administrators
     */
    public void rejectDocumentVersion(DBDocumentVersion dbDocumentVersion, EventSourceType eventSourceType, String message) {
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.REJECT,
                DocumentVersionStatusType.REJECTED, eventSourceType, message);
        dbDocumentVersion.addNewDocumentVersionEvent(dbEvent);
        dbDocumentVersion.setStatus(DocumentVersionStatusType.REJECTED);
    }

    /**
     * Method sets document version status to approved and add new event to list of events
     * It also submits request mails to the review requesters
     *
     * @param dbDocumentVersion document version to be resource administrators
     * @param eventSourceType   event source type
     * @param message           message to be sent to the resource administrators
     */
    public void approveDocumentVersion(DBDocumentVersion dbDocumentVersion, EventSourceType eventSourceType, String message) {
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.APPROVE,
                DocumentVersionStatusType.APPROVED, eventSourceType, message);
        dbDocumentVersion.addNewDocumentVersionEvent(dbEvent);
        dbDocumentVersion.setStatus(DocumentVersionStatusType.APPROVED);
    }

    /**
     * Method sets document version status to under_review and add new event to list of events
     * It also submits request mails to the reviewers
     *
     * @param dbDocumentVersion document version to be reviewed
     * @param eventSourceType   event source type
     */
    public void requestReviewDocumentVersion(DBDocumentVersion dbDocumentVersion, EventSourceType eventSourceType) {
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.REQUEST_REVIEW,
                DocumentVersionStatusType.UNDER_REVIEW,
                eventSourceType, null);
        dbDocumentVersion.addNewDocumentVersionEvent(dbEvent);
        dbDocumentVersion.setStatus(DocumentVersionStatusType.UNDER_REVIEW);
    }


    /**
     * Create document version event
     *
     * @param eventType
     * @param eventSourceType
     * @param details
     * @return
     */
    public DBDocumentVersionEvent createDocumentVersionEvent(DocumentVersionEventType eventType,
                                                             DocumentVersionStatusType statusType,
                                                             EventSourceType eventSourceType,
                                                             String details) {

        SMPUserDetails userDetails = SessionSecurityUtils.getSessionUserDetails();
        DBDocumentVersionEvent dbEvent = new DBDocumentVersionEvent();

        if (userDetails != null && userDetails.getUser() != null) {
            dbEvent.setUsername(userDetails.getUser().getUsername());
        } else {
            LOG.debug("User details not found for event creation ");
        }
        dbEvent.setStatus(statusType);
        dbEvent.setEventOn(OffsetDateTime.now());
        dbEvent.setEventType(eventType);
        dbEvent.setEventSourceType(eventSourceType);
        dbEvent.setDetails(details);
        return dbEvent;
    }

}
