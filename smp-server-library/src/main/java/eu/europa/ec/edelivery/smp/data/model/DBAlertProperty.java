package eu.europa.ec.edelivery.smp.data.model;

import eu.europa.ec.edelivery.smp.data.dao.utils.ColumnDescription;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Audited
@Table(name = "SMP_ALERT_PROPERTY")
public class DBAlertProperty extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SMP_ALERT_PROP_SEQ")
    @GenericGenerator(name = "SMP_ALERT_PROP_SEQ", strategy = "native")
    @Column(name = "ID")
    @ColumnDescription(comment = "Unique alert property id")
    Long id;

    @NotNull
    @Column(name = "PROPERTY_NAME")
    protected String property;

    @Column(name = "PROPERTY_VALUE", length = CommonColumnsLengths.MAX_MEDIUM_TEXT_LENGTH)
    private String value;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "FK_ALERT_ID")
    private DBAlert alert;

    public DBAlertProperty() {
    }

    public DBAlertProperty(String property, String value, DBAlert alert) {
        this.property = property;
        this.value = value;
        this.alert = alert;
    }

    public DBAlertProperty(String property, OffsetDateTime value, DBAlert alert) {
        this.property = property;
        this.alert = alert;
        setDateTime(value);
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public DBAlert getAlert() {
        return alert;
    }

    public void getAlert(DBAlert alert) {
        this.alert = alert;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Transient
    public void setDateTime(OffsetDateTime date) {
        setValue(date == null ? null : date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    @Transient
    public OffsetDateTime getDateTime() {
        return StringUtils.isBlank(value) ? null : OffsetDateTime.parse(getValue(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
