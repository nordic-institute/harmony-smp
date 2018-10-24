package eu.europa.ec.edelivery.smp.data.model;


public class DBDomainDeleteValidation {

    Long id;
    String domainCode;
    String smlSubdomain;
    Integer count;

    public DBDomainDeleteValidation() {
    }

    public DBDomainDeleteValidation(Long id, String domainCode, String smlSubdomain,  Integer count) {
        this.id = id;
        this.domainCode = domainCode;
        this.smlSubdomain = smlSubdomain;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
