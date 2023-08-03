package eu.europa.ec.edelivery.smp.data.model;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Database table containing update data
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Entity
@Audited
@Table(name = "SMP_ALERT")
@org.hibernate.annotations.Table(appliesTo = "SMP_ALERT", comment = "SMP alerts")
public class DBAlert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_ALERT_SEQ")
    @GenericGenerator(name = "SMP_ALERT_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique alert id")
    Long id;

    @Column(name = "PROCESSED_TIME")
    private OffsetDateTime processedTime;

    @Column(name = "ALERT_TYPE")
    @Enumerated(EnumType.STRING)
    @NotNull
    private AlertTypeEnum alertType;

    @Column(name = "REPORTING_TIME")
    private OffsetDateTime reportingTime;

    @Column(name = "ALERT_STATUS")
    @Enumerated(EnumType.STRING)
    @NotNull
    private AlertStatusEnum alertStatus;

    @Column(name = "ALERT_STATUS_DESC", length = CommonColumnsLengths.MAX_MEDIUM_TEXT_LENGTH)
    private String alertStatusDesc;

    @Column(name = "ALERT_LEVEL")
    @Enumerated(EnumType.STRING)
    @NotNull
    private AlertLevelEnum alertLevel;

    @Column(name = "MAIL_SUBJECT", length = CommonColumnsLengths.MAX_MEDIUM_TEXT_LENGTH)
    private String mailSubject;
    @Column(name = "MAIL_TO", length = CommonColumnsLengths.MAX_MEDIUM_TEXT_LENGTH)
    private String mailTo;

    @Column(name = "FOR_USERNAME", length = CommonColumnsLengths.MAX_USERNAME_LENGTH)
    private String username;


    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "property")
    @MapKeyEnumerated
    private Map<String, DBAlertProperty> properties = new HashMap<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAlertStatusDesc() {
        return alertStatusDesc;
    }

    public void setAlertStatusDesc(String alertStatusDesc) {
        this.alertStatusDesc = alertStatusDesc;
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

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addProperty(final String key, final String value) {
        properties.put(key, new DBAlertProperty(key, value, this));
    }

    public void addProperty(final String key, final OffsetDateTime value) {
        properties.put(key, new DBAlertProperty(key, value, this));
    }

    public Map<String, DBAlertProperty> getProperties() {
        return properties;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DBAlert{");
        sb.append("id=").append(id);
        sb.append(", username=").append(username);
        sb.append(", processedTime=").append(processedTime);
        sb.append(", alertType=").append(alertType);
        sb.append(", reportingTime=").append(reportingTime);
        sb.append(", alertStatus=").append(alertStatus);
        sb.append(", alertLevel=").append(alertLevel);
        sb.append(", properties=").append(String.join(",", properties.keySet()));
        sb.append('}');
        return sb.toString();
    }
}
