package eu.europa.ec.digit.domibus.common.exception;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Base class for subclassing other exception classes
 *
 * @author Vincent Dijkstra
 *
 */
public abstract class DomibusException extends RuntimeException {

	/* ---- Constants ---- */

	private static final long serialVersionUID = 201503061425L;

	/**
	 * Pattern of the beginning of error message. Example:
	 * message.internal.company.error.
	 */
	protected static final String START_MSG_KEY_PATTERN = "message\\.[a-z]+\\.[a-z]+\\.(error|warn|info)\\.";

	/**
	 * Pattern of the end of error message. Example .100, .001, .123
	 */
	protected static final String END_MSG_KEY_PATTERN = "\\.([0-9]{3})";

	/* ---- Instance Variables ---- */

	private final String id = UUID.randomUUID().toString();

	private String messageKey;
	private Object[] messageParameters = null;
	private Locale locale;

	/* ---- Constructors ---- */

	public DomibusException() {
		// default constructor
	}

	/**
	 * @param messageKey
	 * @param messageParameters
	 */
	public DomibusException(String messageKey, Object... messageParameters) {
		super(messageKey);
		validateMessageKey(messageKey);
		this.messageKey = messageKey;
		this.messageParameters = messageParameters;
	}

	/**
	 * @param messageKey
	 * @param throwable
	 * @param messageParameters
	 */
	public DomibusException(String messageKey, Throwable throwable,
			Object... messageParameters) {
		super(messageKey, throwable);
		validateMessageKey(messageKey);
		this.messageKey = messageKey;
		this.messageParameters = messageParameters;
	}

	public DomibusException(String messageKey, Throwable throwable, Locale locale,
			Object... messageParameters) {
		super(messageKey, throwable);
		validateMessageKey(messageKey);
		this.messageKey = messageKey;
		this.messageParameters = messageParameters;
		this.locale = locale;
	}

	/* ---- Business Methods ---- */

	protected abstract Pattern getMessageKeyPattern();

	/**
	 * Validates a message code based on the START_MSG_KEY_PATTERN and
	 * END_MSG_KEY_PATTERN patters.
	 *
	 * @param msgKey
	 * @throws IllegalArgumentException
	 */
	private void validateMessageKey(String msgKey)
			throws IllegalArgumentException {
		if (msgKey == null) {
			throw new IllegalArgumentException(
					"The message key cannot be null.");
		}
		if (!getMessageKeyPattern().matcher(msgKey).matches()) {
			throw new IllegalArgumentException(
					"Invalid message key provided, it should match the pattern "
							+ getMessageKeyPattern().pattern());
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)
				.append("messageKey", messageKey != null ? messageKey : StringUtils.EMPTY)
				.append("messageParams", messageParameters != null ? messageParameters.toString() : StringUtils.EMPTY)
				.append("locale", locale != null ? locale.toString() : StringUtils.EMPTY)
				.toString();
	}

	/* ---- Getters and Setters ---- */

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public Object[] getMessageParameters() {
		return messageParameters;
	}

	public void setMessageParameters(Object[] messageParameters) {
		this.messageParameters = messageParameters;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}