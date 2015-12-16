package eu.europa.ec.digit.domibus.common.log;

public enum LogEvent {

	/* ---- Constants ---- */

    BUS_SEND_MESSAGE_REQUEST ("BUS-001", "Send message request received."),
    BUS_RETRIEVE_MESSAGE_REQUEST ("BUS-002","Retrieve message request received."),
    BUS_SUBMIT_MESSAGE_SUCCESSFUL ("BUS-003", "Message successfully submitted."),
    BUS_SUBMIT_MESSAGE_FAILED ("BUS-004", "Error while submitting the message. %s"),
    BUS_RETRIEVE_MESSAGE_SUCCESSFUL ("BUS-005", "Message successfully retrieved."),
    BUS_RETRIEVE_MESSAGE_FAILED ("BUS-006", "Error while retrieving the message. %s"),
    BUS_SERVICE_CALL ("BUS-007", "Calling service %s"),
    BUS_TYPE_MATCH ("BUS-008", "The type of the message matches %s"),
    BUS_CONVERSION_SUCCESSFUL ("BUS-009", "Conversion to %s succeeded"),
    BUS_CONVERSION_FAILED ("BUS-010", "Conversion of %s to %s failed due to: %s"),
    BUS_SUBMISSION_SUCCESSFUL ("BUS-011", "Message submission succeeded, %s"),
    BUS_SUBMISSION_FAILED ("BUS-012", "Message submission failed due to: %s"),
    BUS_RETRIEVAL_SUCCESSFUL ("BUS-013", "Message retrieval succeeded"),
    BUS_RETRIEVAL_FAILED ("BUS-014", "Message retrieval failed due to: %s"),
    BUS_INVALID_QUEUED_MESSAGE ("BUS-015", "Invalid message received in the queue: %s"),
    BUS_NOTIFY_MESSAGE_FAILED ("BUS-016", "Notifying the message failed, %s"),
    BUS_NOTIFY_MESSAGE_SUCCESSFUL ("BUS-017", "Message %s successfully notified to %s");

	/* ---- Instance Variables ---- */

	private String code = null;
	private String message = null;

	/* ---- Constructors ---- */

	private LogEvent(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/* ---- Business Methods ---- */

	public String format(String... params) {
		if (params != null) {
			try {
				return String.format(this.message, (Object[]) params);
			} catch (Exception e) {
				return this.message;
			}
		} else {
			return this.message;
		}
	}

	/* ---- Getters and Setters ---- */

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


}
