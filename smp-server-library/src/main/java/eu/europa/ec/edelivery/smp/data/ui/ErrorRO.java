package eu.europa.ec.edelivery.smp.data.ui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * @author Sebastian-Ion TINCU
 * @since 4.0.1
 */
public class ErrorRO implements Serializable {

    protected String message;

    public ErrorRO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonIgnore
    public int getContentLength() {
        try {
            return new ObjectMapper().writeValueAsString(this).length();
        } catch (JsonProcessingException e) {
            return -1;
        }
    }
}