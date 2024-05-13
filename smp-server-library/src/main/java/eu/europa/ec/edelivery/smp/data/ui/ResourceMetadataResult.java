package eu.europa.ec.edelivery.smp.data.ui;

import java.io.Serializable;
import java.util.*;

/**
 * @since 5.1
 * @author Sebastian-Ion TINCU
 */
public class ResourceMetadataResult implements Serializable {

    private static final long serialVersionUID = 6677275164291128366L;

    // The set of all the available domain codes
    private Set<String> availableDomains = new LinkedHashSet<>();

    // The set of all the available document types
    private Set<String> availableDocumentTypes = new LinkedHashSet<>();

    public ResourceMetadataResult(List<String> domainCodes, List<String> documentTypes) {
        this.availableDomains.addAll(new TreeSet<>(domainCodes));
        this.availableDocumentTypes.addAll(new TreeSet<>(documentTypes));
    }

    public Set<String> getAvailableDomains() {
        return availableDomains;
    }

    public Set<String> getAvailableDocumentTypes() {
        return availableDocumentTypes;
    }
}
