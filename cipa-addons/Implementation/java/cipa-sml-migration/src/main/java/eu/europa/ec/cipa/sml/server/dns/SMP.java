package eu.europa.ec.cipa.sml.server.dns;

/**
 * Created by feriaad on 20/10/2015.
 */
public class SMP {
    private String smpId;

    private String logicalAddress;

    private String physicalAddress;

    private String username;

    public String getSmpId() {
        return smpId;
    }

    public void setSmpId(String smpId) {
        this.smpId = smpId;
    }

    public String getLogicalAddress() {
        return logicalAddress;
    }

    public void setLogicalAddress(String logicalAddress) {
        this.logicalAddress = logicalAddress;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
