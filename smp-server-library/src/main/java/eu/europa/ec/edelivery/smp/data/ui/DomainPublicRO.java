package eu.europa.ec.edelivery.smp.data.ui;


/**
 * Domain resource object containing only public data
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class DomainPublicRO extends BaseRO  {

    private static final long serialVersionUID = 9008583888835630007L;

    String domainCode;
    String smlSubdomain;

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


}
