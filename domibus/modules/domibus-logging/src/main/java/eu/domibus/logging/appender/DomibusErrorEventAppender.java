package eu.domibus.logging.appender;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import eu.domibus.logging.persistent.LoggerEvent;
import eu.domibus.logging.persistent.LoggerEventDAO;

/**
 * A custom appender for logging into a database via hibernate
 *
 * @author Stefan Mueller
 * @author Tim Nowosadtko
 * @date 07-13-2012
 */
public class DomibusErrorEventAppender extends AppenderSkeleton implements Appender {

    private LoggerEventDAO led;

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    /**
     * This method stores custom and standard logevents into the
     * database. In case of the cuastom logevent MESSAGE it expects a
     * message of type MessageInfo
     *
     * @param event
     */
    protected void append(final LoggingEvent event) {
        if (led == null) {
            led = new LoggerEventDAO();
        }
        final LoggerEvent le = new LoggerEvent(event);
        led.persist(le);
    }
}