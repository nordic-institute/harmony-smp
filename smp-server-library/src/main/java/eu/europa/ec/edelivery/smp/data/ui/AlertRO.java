package eu.europa.ec.edelivery.smp.data.ui;


import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;

import java.time.OffsetDateTime;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class AlertRO extends BaseRO {

    private static final long serialVersionUID = -9018583888835630560L;

    // session id
    private String sid;
    private Boolean processed;
    private OffsetDateTime processedTime;
    private AlertTypeEnum alertType;
    private OffsetDateTime reportingTime;
    private AlertStatusEnum alertStatus;
    private AlertLevelEnum alertLevel;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
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
}
