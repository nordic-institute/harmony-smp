package eu.europa.ec.smp.api.exceptions;

/**
 * Thrown when used identifier does not fulfill requirements specified in OASIS SMP specs:
 * http://docs.oasis-open.org/bdxr/bdx-smp/v1.0/bdx-smp-v1.0.html
 * 
 * Created by gutowpa on 12/01/2017.
 */
public class MalformedIdentifierException extends IllegalArgumentException {

    private static String buildMessage(String malformedId){
        return "Malformed identifier, scheme and id should be delimited by double colon: "+malformedId;
    }

    public MalformedIdentifierException(String malformedId, Exception cause){
        super(buildMessage(malformedId), cause);
    }
}
