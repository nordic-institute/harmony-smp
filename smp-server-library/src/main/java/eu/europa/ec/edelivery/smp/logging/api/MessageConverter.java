package eu.europa.ec.edelivery.smp.logging.api;

import org.slf4j.Marker;

/**
 * @author Cosmin Baciu (Domibus 3.3+)
 * @since 4.1
 */
public interface MessageConverter {

  String getMessage(Marker marker, MessageCode key, Object... args);

}
