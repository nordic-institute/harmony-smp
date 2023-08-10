package eu.europa.ec.edelivery.smp.data.ui;

import java.util.ArrayList;
import java.util.List;

public class KeystoreImportResult {

    String errorMessage;
    List<CertificateRO> addedCertificates = new ArrayList<>();

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<CertificateRO> getAddedCertificates() {
        return addedCertificates;
    }
}
