package eu.europa.ec.cipa.common.exception;

/**
 * Created by feriaad on 12/06/2015.
 */
public abstract class TechnicalException extends Exception implements ICipaException {

    /**
     * UUID for the serialization.
     */
    private static final long serialVersionUID = -2888388417094351293L;

    /**
     * Exception code
     */
    private int code;


    public TechnicalException(int code, String message) {
        super(message);
        this.code = code;
    }

    public TechnicalException(int code, String message, Throwable t) {
        super(message, t);
        this.code = code;
    }

    public TechnicalException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return "[ERR-" + code + "] " + super.getMessage();
    }
}
