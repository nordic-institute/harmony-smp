package eu.domibus.logging.appender;

import eu.domibus.logging.persistent.LoggerMessage;
import eu.domibus.logging.persistent.LoggerMessageDAO;
import eu.domibus.logging.persistent.MessageInfo;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A custom appender for logging into a database via JPA
 *
 * @author Stefan Mueller
 * @author Tim Nowosadtko
 * @date 07-13-2012
 */
public class DomibusAppender extends AppenderSkeleton implements Appender {

    private static final Logger log = Logger.getLogger(DomibusAppender.class);
    private LoggerMessageDAO lmd;
    // Me comment

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
     * @param event Event to log
     */
    protected void append(final LoggingEvent event) {
        if (event.getMessage() instanceof MessageInfo) {
            if (this.lmd == null) {
                this.lmd = new LoggerMessageDAO();
            }

            DomibusAppender.log.debug("Logelement: Message");
            final LoggerMessage lm = new LoggerMessage((MessageInfo) event.getMessage());
            this.lmd.persist(lm);
        }
    }

}