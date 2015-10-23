package eu.europa.ec.cipa.dispatcher.exception;

public class DispatcherTechnicalErrorException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public DispatcherTechnicalErrorException() {
		super();
	}

	public DispatcherTechnicalErrorException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DispatcherTechnicalErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public DispatcherTechnicalErrorException(String message) {
		super(message);
	}

	public DispatcherTechnicalErrorException(Throwable cause) {
		super(cause);
	}

}
