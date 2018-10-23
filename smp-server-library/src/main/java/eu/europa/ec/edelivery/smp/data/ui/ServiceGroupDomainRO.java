package eu.europa.ec.edelivery.smp.data.ui;


/**
 * @author Joze Rihtarsic
 * @since 4.1
 */


public class ServiceGroupDomainRO extends BaseRO {


    private static final long serialVersionUID = -7111221767041516157L;
    private Long id;
    private Long domainId;
    String domainCode;
    String smlSubdomain;
    boolean smlRegistered;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getSmlSubdomain() {
        return smlSubdomain;
    }

    public void setSmlSubdomain(String smlSubdomain) {
        this.smlSubdomain = smlSubdomain;
    }

    public boolean isSmlRegistered() {
        return smlRegistered;
    }

    public void setSmlRegistered(boolean registered) {
        this.smlRegistered = registered;
    }
}
