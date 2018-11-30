package pages.components.messageArea;

public class AlertMessage {

	String message;
	boolean isError;

	public AlertMessage(String message, boolean isError) {
		this.message = message;
		this.isError = isError;
	}

	public String getMessage() {
		return message.replaceAll("×", "").trim();
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean error) {
		isError = error;
	}
}
