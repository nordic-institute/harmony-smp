package eu.europa.ec.digit.domibus.common.exception;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * This class is used to represent a business logic rule not followed by the
 * client application.
 * <p>
 * A business exception is an exception from which the client could recover,
 * therefore sub-classing this exception might be useful.
 * </p>
 *
 * @author Vincent Dijkstra
 */
public class DomibusBusinessException extends DomibusException {

	/* ---- Constants ---- */
	private static final long serialVersionUID = 201503061500L;

	private static final String TYPE = "business";
	private static final Pattern MESSAGE_KEY_PATTERN = Pattern
			.compile(START_MSG_KEY_PATTERN + TYPE + END_MSG_KEY_PATTERN);

	/* ---------- Constructors ---------- */

	public DomibusBusinessException() {
		// default constructor
	}

	/**
	 * @param messageKey
	 * @param messageParams
	 */
	public DomibusBusinessException(String messageKey, Object... messageParams) {
		super(messageKey, messageParams);
	}

	/**
	 * @param messageKey
	 * @param throwable
	 * @param messageParameters
	 */
	public DomibusBusinessException(String messageKey, Throwable throwable,
			Object... messageParameters) {
		super(messageKey, throwable, messageParameters);
	}

	/**
	 * @param messageKey
	 * @param throwable
	 * @param locale
	 * @param messageParameters
	 */
	public DomibusBusinessException(String messageKey, Throwable throwable, Locale locale,
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
