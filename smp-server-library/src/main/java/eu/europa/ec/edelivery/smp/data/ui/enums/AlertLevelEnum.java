package eu.europa.ec.edelivery.smp.data.ui.enums;

/**
 * Alert level enumeration defining 3  levels
 *  high - Critical system or security alerts
 *  medium - Alerts ( Alerts notified to users  by mail: as password locks)
 *  low - notification alerts (Alerts are not send by mail.)
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public enum AlertLevelEnum {
    HIGH,
    MEDIUM,
    LOW
}