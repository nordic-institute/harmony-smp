package eu.europa.ec.smp.api;

/**
 * Created by gutowpa on 12/01/2017.
 */
public class MalformedIdentifierException extends IllegalArgumentException {

    private static String buildMessage(String malformedId){
        return "Malformed identifier, scheme and id should be delimited by double colon: "+malformedId;
    }

    public MalformedIdentifierException(String malformedId, Exception cause){
        super(buildMessage(malformedId), cause);
    }

    public MalformedIdentifierException(String malformedId){
        super(buildMessage(malformedId));
    }
}
