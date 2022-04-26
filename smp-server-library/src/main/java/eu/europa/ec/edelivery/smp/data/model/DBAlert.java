package eu.europa.ec.edelivery.smp.data.model;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertLevelEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertStatusEnum;
import eu.europa.ec.edelivery.smp.data.ui.enums.AlertTypeEnum;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

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
@NamedQueries({
        @NamedQuery(name = "DBAlert.updateProcess", query = "UPDATE DBAlert a set a.processed=:PROCESSED where a.id=:ALERT_ID"),
})
public class DBAlert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_ALERT_SEQ")
    @GenericGenerator(name = "SMP_ALERT_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique alert id")
    Long id;

    @Column(name = "PROCESSED")
    private Boolean processed;

    @Column(name = "PROCESSED_TIME")
    private LocalDateTime processedTime;

    @Column(name = "ALERT_TYPE")
    @Enumerated(EnumType.STRING)
    @NotNull
    private AlertTypeEnum alertType;

    @Column(name = "REPORTING_TIME")
    private LocalDateTime reportingTime;

    @Column(name = "ALERT_STATUS")
    @Enumerated(EnumType.STRING)
    @NotNull
    private AlertStatusEnum alertStatus;

    @Column(name = "ALERT_LEVEL")
    @Enumerated(EnumType.STRING)
    @NotNull
    private AlertLevelEnum alertLevel;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public LocalDateTime getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(LocalDateTime processedTime) {
        this.processedTime = processedTime;
    }

    public AlertTypeEnum getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertTypeEnum alertType) {
        this.alertType = alertType;
    }

    public LocalDateTime getReportingTime() {
        return reportingTime;
    }

    public void setReportingTime(LocalDateTime reportingTime) {
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

    @PreUpdate
    @PrePersist
    public void prePersistUpdate() {
        if (processed == null) {
            processed = Boolean.FALSE;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DBAlert dbAlert = (DBAlert) o;
        return Objects.equals(id, dbAlert.id) &&
                alertType == dbAlert.alertType &&
                reportingTime.equals(dbAlert.reportingTime) &&
                alertLevel == dbAlert.alertLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, alertType, reportingTime, alertStatus, alertLevel);
    }
}
