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
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.CREATE, eventSourceType, details);
        dbDocumentVersion.addNewDocumentVersionEvent(dbEvent);
        dbDocumentVersion.setStatus(DocumentVersionStatusType.DRAFT);
        if (publish) {
            LOG.debug("Creating And Publish event for document version");
            publishDocumentVersion(dbDocumentVersion, eventSourceType);
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
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.RETIRE, eventSourceType, details);
        dbDocumentVersion.addNewDocumentVersionEvent(dbEvent);
        dbDocumentVersion.setStatus(DocumentVersionStatusType.RETIRED);
    }

    /**
     * Method sets document version status to published and adds Publish event to the document version list of events
     *
     * @param dbDocumentVersion document version to be published
     * @param eventSourceType   event source type
     */
    public void publishDocumentVersion(DBDocumentVersion dbDocumentVersion, EventSourceType eventSourceType) {
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.PUBLISH, eventSourceType, null);
        dbDocumentVersion.addNewDocumentVersionEvent(dbEvent);
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
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.REJECT, eventSourceType, message);
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
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.APPROVE, eventSourceType, message);
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
        DBDocumentVersionEvent dbEvent = createDocumentVersionEvent(DocumentVersionEventType.REQUEST_REVIEW, eventSourceType, null);
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
                                                             EventSourceType eventSourceType,
                                                             String details) {

        SMPUserDetails userDetails = SessionSecurityUtils.getSessionUserDetails();
        DBDocumentVersionEvent dbEvent = new DBDocumentVersionEvent();

        if (userDetails != null && userDetails.getUser() != null) {
            dbEvent.setUsername(userDetails.getUser().getUsername());
        } else {
            LOG.debug("User details not found for event creation ");
        }

        dbEvent.setEventOn(OffsetDateTime.now());
        dbEvent.setEventType(eventType);
        dbEvent.setEventSourceType(eventSourceType);
        dbEvent.setDetails(details);
        return dbEvent;
    }

}
