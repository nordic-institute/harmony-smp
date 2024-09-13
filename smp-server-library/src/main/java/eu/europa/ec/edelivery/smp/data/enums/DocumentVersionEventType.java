package eu.europa.ec.edelivery.smp.data.enums;

/**
 * Document version event types. The event status allows user to track
 * changes in the document version.
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
public enum DocumentVersionEventType {
    CREATE,
    UPDATE,
    PUBLISH,
    REQUEST_REVIEW,
    RETIRE,
    APPROVE,
    REJECT,
    ERROR
}
