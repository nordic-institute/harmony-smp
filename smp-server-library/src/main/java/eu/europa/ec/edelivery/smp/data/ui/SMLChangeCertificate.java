package eu.europa.ec.edelivery.smp.data.ui;

import java.time.OffsetDateTime;

public class SMLChangeCertificate {

    String certificateAlias;

    OffsetDateTime changeDateTime;

    public String getCertificateAlias() {
        return certificateAlias;
    }

    public void setCertificateAlias(String certificateAlias) {
        this.certificateAlias = certificateAlias;
    }

    public OffsetDateTime getChangeDateTime() {
        return changeDateTime;
    }

    public void setChangeDateTime(OffsetDateTime changeDateTime) {
        this.changeDateTime = changeDateTime;
    }
}
