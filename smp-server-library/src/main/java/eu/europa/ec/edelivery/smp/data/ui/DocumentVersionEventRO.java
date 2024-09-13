package eu.europa.ec.edelivery.smp.data.ui;


import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionEventType;
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
}
