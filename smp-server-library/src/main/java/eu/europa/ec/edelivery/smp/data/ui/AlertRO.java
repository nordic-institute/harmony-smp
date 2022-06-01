package eu.europa.ec.edelivery.smp.data.ui;


import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class AlertRO extends BaseRO {

    private static final long serialVersionUID = -9018583888835630560L;

    // session id
    private String sid;
    private String username;
    private String mailTo;
    private OffsetDateTime processedTime;
    private AlertTypeEnum alertType;
    private OffsetDateTime reportingTime;
    private AlertStatusEnum alertStatus;
    private String alertStatusDesc;
    private AlertLevelEnum alertLevel;
    private Map<String, String> alertDetails = new HashMap<>();

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getAlertStatusDesc() {
        return alertStatusDesc;
    }

    public void setAlertStatusDesc(String alertStatusDesc) {
        this.alertStatusDesc = alertStatusDesc;
    }

    public OffsetDateTime getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(OffsetDateTime processedTime) {
        this.processedTime = processedTime;
    }

    public AlertTypeEnum getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertTypeEnum alertType) {
        this.alertType = alertType;
    }

    public OffsetDateTime getReportingTime() {
        return reportingTime;
    }

    public void setReportingTime(OffsetDateTime reportingTime) {
        this.reportingTime = reportingTime;
    }

    public AlertStatusEnum getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(AlertStatusEnum alertStatus) {
        this.alertStatus = alertStatus;
    }

    public AlertLevelEnum getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(AlertLevelEnum alertLevel) {
        this.alertLevel = alertLevel;
    }

    public Map<String, String> getAlertDetails() {
        return alertDetails;
    }
}
