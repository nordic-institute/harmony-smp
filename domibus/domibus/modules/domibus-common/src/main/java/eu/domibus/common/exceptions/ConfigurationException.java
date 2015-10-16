package eu.domibus.common.exceptions;

public class ConfigurationException extends RuntimeException {

    public ConfigurationException() {
        super();

    }

    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);

    }

    public ConfigurationException(final String message) {
        super(message);
    }

    public ConfigurationException(final Throwable cause) {
        super(cause);

    }


}
