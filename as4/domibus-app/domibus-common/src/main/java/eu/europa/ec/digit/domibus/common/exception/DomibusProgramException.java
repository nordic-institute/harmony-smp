package eu.europa.ec.digit.domibus.common.exception;

import java.util.Locale;
import java.util.regex.Pattern;


/**
 * This class is used to represent an unrecoverable EPC problem. Programming exceptions are
 * typically used for situations where you arrive at an unexpected state. For example, a
 * variable doesn't have a value where you expected one. Certain conditions are not
 * as you would have expected.
 * <p>
 * This class is <tt>final</tt> because it should be used in scenarios where clients cannot
 * recover from the error. Therefore there's no need to subclass this exception.
 * </p>
 *
 * @author Vincent Dijkstra
 */
public final class DomibusProgramException extends DomibusException {

	/* ---- Constants ---- */
    private static final long serialVersionUID = 201502080922L;

    private static final String TYPE = "program";
	private static final Pattern MESSAGE_KEY_PATTERN = Pattern
			.compile(START_MSG_KEY_PATTERN + TYPE + END_MSG_KEY_PATTERN);

    /* ---- Constructors ---- */

    public DomibusProgramException(String messageKey, Object... messageParams) {
		super(messageKey, messageParams);
	}

	public DomibusProgramException(String messageKey, Throwable throwable,
			Object... messageParameters) {
		super(messageKey, throwable, messageParameters);
	}

	public DomibusProgramException(String messageKey, Throwable throwable, Locale locale,
			Object... messageParameters) {
		super(messageKey, throwable, locale, messageParameters);
	}

    /* ---- Business Methods ---- */

    /* ---- Getters and Setters ---- */

    @Override
	protected Pattern getMessageKeyPattern() {

    	return MESSAGE_KEY_PATTERN;
	}
}
