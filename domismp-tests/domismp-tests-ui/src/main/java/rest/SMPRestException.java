package rest;

import com.sun.jersey.api.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMPRestException extends Exception {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public SMPRestException(String message, ClientResponse response) {
        super(String.format("%s \n %s \n %s \n",
                message,
                "STATUS = " + response.getStatus(),
                "CONTENT = " + response.getEntity(String.class)));
        log.error(String.format("%s \n %s \n %s \n",
                message,
                "STATUS = " + response.getStatus(),
                "CONTENT = " + response.getEntity(String.class)));
    }

    public SMPRestException(String message, int responseStatus, String responseContent) {
        super(String.format("%s \n %s \n %s \n",
                message,
                "STATUS = " + responseStatus,
                "CONTENT = " + responseContent));
        log.error(String.format("%s \n %s \n %s \n",
                message,
                "STATUS = " + responseStatus,
                "CONTENT = " + responseContent));
    }

}
