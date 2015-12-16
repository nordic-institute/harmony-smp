package eu.europa.ec.digit.domibus.common.exception;

import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;

/**
 * Validation exception which indicates that an object is found invalid.
 *
 * @author Vincent Dijkstra
 */
public class DomibusValidationException extends DomibusBusinessException {

	/* ---- Constants ---- */
	private static final long serialVersionUID = 201503061459L;
	private static final String TYPE = "validation";

	private static final Pattern MESSAGE_KEY_PATTERN = Pattern
		.compile(START_MSG_KEY_PATTERN + TYPE + END_MSG_KEY_PATTERN);

	/* ---- Instance Variables ---- */
	private Errors errors;

	/* ---- Constructors ---- */

	/**
	 * @param messageKey
	 * @param messageParams
	 */
	public DomibusValidationException(String messageKey, Object... messageParams) {
		super(messageKey, messageParams);
	}

	/**
	 * @param messageKey
	 * @param throwable
	 * @param messageParameters
	 */
	public DomibusValidationException(String messageKey, Throwable throwable,
			Object... messageParameters) {
		super(messageKey, throwable, messageParameters);
	}

	/**
	 * @param messageKey
	 * @param throwable
	 * @param locale
	 * @param messageParameters
	 */
	public DomibusValidationException(String messageKey, Throwable throwable, Locale locale,
			Object... messageParameters) {
		super(messageKey, throwable, messageParameters);
	}

	public DomibusValidationException(Errors errors) {
		this.errors = errors;
	}

	/* ---- Business Methods ---- */

	/* ---- Getters and Setters ---- */

	@Override
	protected Pattern getMessageKeyPattern() {
		return MESSAGE_KEY_PATTERN;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}
