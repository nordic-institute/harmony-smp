package eu.europa.ec.edelivery.smp.data.ui;

import com.fasterxml.jackson.annotation.JsonFormat;
import eu.europa.ec.edelivery.smp.utils.SMPConstants;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AccessTokenRO implements Serializable {

    private static final long serialVersionUID = 2821447495333163882L;

    private String identifier;
    private String value;
    @JsonFormat(pattern = SMPConstants.JSON_DATETIME_ISO)
    LocalDateTime generatedOn;
    @JsonFormat(pattern = SMPConstants.JSON_DATETIME_ISO)
    LocalDateTime expireOn;

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

    public LocalDateTime getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(LocalDateTime expireOn) {
        this.expireOn = expireOn;
    }
}