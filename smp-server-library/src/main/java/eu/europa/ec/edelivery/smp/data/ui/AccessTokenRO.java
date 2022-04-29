package eu.europa.ec.edelivery.smp.data.ui;

import com.fasterxml.jackson.annotation.JsonFormat;
import eu.europa.ec.edelivery.smp.utils.SMPConstants;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class AccessTokenRO implements Serializable {

    private static final long serialVersionUID = 2821447495333163882L;

    private String identifier;
    private String value;
    @JsonFormat(pattern = SMPConstants.JSON_DATETIME_ISO)
    OffsetDateTime generatedOn;
    @JsonFormat(pattern = SMPConstants.JSON_DATETIME_ISO)
    OffsetDateTime expireOn;

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

    public OffsetDateTime getGeneratedOn() {
        return generatedOn;
    }

    public void setGeneratedOn(OffsetDateTime generatedOn) {
        this.generatedOn = generatedOn;
    }

    public OffsetDateTime getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(OffsetDateTime expireOn) {
        this.expireOn = expireOn;
    }
}