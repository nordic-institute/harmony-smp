package eu.europa.ec.edelivery.smp.data.ui;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */


public class ServiceGroupRO extends BaseRO {


    private static final long serialVersionUID = -7523221767041516157L;
    private String participantIdentifier;
    private String participantScheme;
    private boolean smlRegistered = false;
    private List<ServiceMetadataRO> lstServiceMetadata = new ArrayList<>();



    private String domain;


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public boolean isSmlRegistered() {
        return smlRegistered;
    }

    public void setSmlRegistered(boolean smlRegistered) {
        this.smlRegistered = smlRegistered;
    }


    public List<ServiceMetadataRO> getServiceMetadata() {
        return lstServiceMetadata;
    }

}
