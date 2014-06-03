package eu.domibus.logging.level;

import org.apache.log4j.Level;


/**
 * This Class defines a custom loglevel for messages. The level is 10 above info
 *
 * @author Stefan Mueller
 * @author Tim Nowosadtko
 * @date 07-13-2012
 */
@SuppressWarnings("serial")
public class Message extends Level {

    public static final int MESSAGE_INT = INFO_INT + 10;

    public static final Level MESSAGE = new Message(MESSAGE_INT, "MESSAGE", 6);

    protected Message(final int level, final String levelStr, final int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }


    public static Level toLevel(final String level) {
        if (level != null && level.toUpperCase().equals("MESSAGE")) {
            return MESSAGE;
        }
        return toLevel(level, Level.INFO);
    }


    public static Level toLevel(final int value) {
        if (value == MESSAGE_INT) {
            return MESSAGE;
        }
        return toLevel(value, Level.INFO);
    }


    public static Level toLevel(final int value, final Level defaultLevel) {
        if (value == MESSAGE_INT) {
            return MESSAGE;
        }
        return Level.toLevel(value, defaultLevel);
    }


    public static Level toLevel(final String level, final Level defaultLevel) {
        if (level != null && level.toUpperCase().equals("MESSAGE")) {
            return MESSAGE;
        }
        return Level.toLevel(level, defaultLevel);
    }

}
