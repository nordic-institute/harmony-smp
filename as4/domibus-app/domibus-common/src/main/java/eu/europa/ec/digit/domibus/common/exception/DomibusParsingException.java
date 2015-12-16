package eu.europa.ec.digit.domibus.common.exception;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Business exception which indicates that an object is found invalid.
 *
 * @author Vincent Dijkstra
 */
public class DomibusParsingException extends DomibusBusinessException {

    /* ---- Constants ---- */
    private static final long serialVersionUID = 201503061205L;

    private static final String TYPE = "parsing";
	private static final Pattern MESSAGE_KEY_PATTERN = Pattern
			.compile(START_MSG_KEY_PATTERN + TYPE + END_MSG_KEY_PATTERN);

    /* ---- Instance Variables ---- */

    /* ---- Constructors ---- */

	/**
	 * @param messageKey
	 * @param messageParams
	 */
	public DomibusParsingException(String messageKey, Object... messageParams) {
		super(messageKey, messageParams);
	}

	/**
	 * @param messageKey
	 * @param throwable
	 * @param messageParameters
	 */
	public DomibusParsingException(String messageKey, Throwable throwable,
			Object... messageParameters) {
		super(messageKey, throwable, messageParameters);
	}

	/**
	 * @param messageKey
	 * @param throwable
	 * @param locale
	 * @param messageParameters
	 */
	public DomibusParsingException(String messageKey, Throwable throwable, Locale locale,
			Object... messageParameters) {
		super(messageKey, throwable, messageParameters);
	}

    /* ---- Business Methods ---- */

    /* ---- Getters and Setters ---- */

    @Override
	protected Pattern getMessageKeyPattern() {
		return MESSAGE_KEY_PATTERN;
	}
}
