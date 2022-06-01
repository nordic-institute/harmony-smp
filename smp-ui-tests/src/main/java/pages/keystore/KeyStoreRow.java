package pages.keystore;

import pages.domain.DomainRow;

public class KeyStoreRow {

    private String alias;

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    private String certificateId;

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    @Override
    public String toString() {
        return "KeyStoreRow{" +
                "alias='" + alias + '\'' +
                ", certificateId='" + certificateId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyStoreRow row = (KeyStoreRow) o;

        if (alias != null ? !alias.equals(row.alias) : row.alias != null) return false;
        return certificateId != null ? certificateId.equals(row.certificateId) : row.certificateId == null;
    }
}
