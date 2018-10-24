package eu.europa.ec.edelivery.smp.data.model;


import org.hibernate.envers.Audited;

import javax.persistence.*;


public class DBUserDeleteValidation {

    Long id;
    String username;
    String certificateId;
    Integer count;

    public DBUserDeleteValidation() {
    }

    public DBUserDeleteValidation(Long id, String username, String certificateId, Integer count) {
        this.id = id;
        this.username = username;
        this.certificateId = certificateId;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer iCount) {
        this.count = iCount;
    }
}
