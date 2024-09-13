package eu.europa.ec.edelivery.smp.data.enums;

/**
 * Document version event source types. The event source can be UI, REST API,
 * Automatic cron or any other custom plugin.
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
public enum EventSourceType {
    UI,
    REST_API,
    CRON,
    PLUGIN,
    OTHER
}
