package eu.europa.ec.edelivery.smp.data.ui;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AccessTokenRO implements Serializable {

    private static final long serialVersionUID = 2821447495333163882L;

    private String identifier;
    private String value;
    LocalDateTime generatedOn;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getGeneratedOn() {
        return generatedOn;
    }

    public void setGeneratedOn(LocalDateTime generatedOn) {
        this.generatedOn = generatedOn;
    }
}