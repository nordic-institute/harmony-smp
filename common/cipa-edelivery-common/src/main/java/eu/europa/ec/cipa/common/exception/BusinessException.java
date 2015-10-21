package eu.europa.ec.cipa.common.exception;

/**
 * Created by feriaad on 12/06/2015.
 */
public abstract class BusinessException extends Exception implements ICipaException {

    /**
     * UUID for the serialization.
     */
    private static final long serialVersionUID = -2888388417094351293L;

    /**
     * Exception code
     */
    private int code;


    /**
     * constructor for the business exception
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String message, Throwable t) {
        super(message, t);
        this.code = code;
    }

    public BusinessException(Throwable cause) {
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
