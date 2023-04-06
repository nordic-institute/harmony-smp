package eu.europa.ec.edelivery.smp.data.ui;


import java.util.ArrayList;
import java.util.List;

/**
 * Lighter (without administration walues) ServiceGroup object for searching service group and its metadata.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

public class ServiceGroupSearchRO extends BaseRO {


    private static final long serialVersionUID = 9008583888835630016L;
    private Long id;
    private String participantIdentifier;
    private String participantScheme;
    private List<ServiceMetadataRO> lstServiceMetadata = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParticipantIdentifier() {
        return participantIdentifier;
    }

    public void setParticipantIdentifier(String participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
    }

    public String getParticipantScheme() {
        return participantScheme;
    }

    public void setParticipantScheme(String participantScheme) {
        this.participantScheme = participantScheme;
    }

    public List<ServiceMetadataRO> getServiceMetadata() {
        return lstServiceMetadata;
    }
}
