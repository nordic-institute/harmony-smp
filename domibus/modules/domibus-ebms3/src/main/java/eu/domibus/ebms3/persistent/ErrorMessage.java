package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractBaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * This table keeps track of all ebMS 3 Errors.
 *
 * @author Thorsten Niedzwetzki
 */
@Entity
@Table(name = "TB_ERROR_MESSAGE")
public class ErrorMessage extends AbstractBaseEntity implements Serializable {
    private static final long serialVersionUID = -7459100291606253888L;

    private static final String SITE_LOCAL = "Local";
    private static final String SITE_REMOTE = "Remote";
    private static final String FLOW_IN = "In";
    private static final String FLOW_OUT = "Out";

    @Column(name = "ORIGIN")
    private String origin;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "ERROR_CODE")
    private String errorCode;

    @Column(name = "SEVERITY")
    private String severity;

    @Column(name = "REF_TO_MESSAGE_IN_ERROR")
    private String refToMessageInError;

    @Column(name = "SHORT_DESCRIPTION")
    private String shortDescription;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ERROR_DETAIL")
    private String errorDetail;

    @Column(name = "SITE")
    private String site;

    @Column(name = "FLOW")
    private String flow;

    @Column(name = "APPEARANCE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date appearance;

    @Column(name = "DELIVERED")
    private boolean delivered;

    @Column(name = "TO_URL")
    private String toURL;

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(final String origin) {
        this.origin = origin;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(final String severity) {
        this.severity = severity;
    }

    public String getRefToMessageInError() {
        return refToMessageInError;
    }

    public void setRefToMessageInError(final String refToMessageInError) {
        this.refToMessageInError = refToMessageInError;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(final String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(final String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getSite() {
        return site;
    }

    public void setSite(final String site) {
        this.site = site;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(final String flow) {
        this.flow = flow;
    }

    public Date getAppearance() {
        return appearance;
    }

    public void setAppearance(final Date appearance) {
        this.appearance = appearance;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(final boolean delivered) {
        this.delivered = delivered;
    }

    public String getToURL() {
        return toURL;
    }

    public void setToURL(final String toURL) {
        this.toURL = toURL;
    }

    @Transient
    public boolean isLocal() {
        return SITE_LOCAL.equalsIgnoreCase(getSite());
    }

    @Transient
    public boolean isRemote() {
        return SITE_REMOTE.equalsIgnoreCase(getSite());
    }

    @Transient
    public void setLocal() {
        setSite(SITE_LOCAL);
    }

    @Transient
    public void setRemote() {
        setSite(SITE_REMOTE);
    }

    @Transient
    public void setInFlow() {
        setFlow(FLOW_IN);
    }

    @Transient
    public void setOutFlow() {
        setFlow(FLOW_OUT);
    }

    @Transient
    public boolean isInFlow() {
        return FLOW_IN.equalsIgnoreCase(getFlow());
    }

    @Transient
    public boolean isOutFlow() {
        return FLOW_OUT.equalsIgnoreCase(getFlow());
    }

}