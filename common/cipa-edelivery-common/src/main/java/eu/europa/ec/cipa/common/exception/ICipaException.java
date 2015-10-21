package eu.europa.ec.cipa.common.exception;

/**
 * Common interface for business and technical exceptions
 */
public interface ICipaException {

    String getMessage();

    int getCode();

    /**
     * Setter for the exception code
     *
     * @param code the exception code
     */
    void setCode(int code);

}
