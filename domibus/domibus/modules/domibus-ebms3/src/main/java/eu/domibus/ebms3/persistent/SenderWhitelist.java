package eu.domibus.ebms3.persistent;


import eu.domibus.common.persistent.AbstractBaseEntity;

import javax.persistence.*;

/**
 * Database entity for Whitelist check. Database contains Party information and Service and Action parameter.
 * It also check if database contains "*" as wildcard.
 * Created by nowos01 on 09.05.14.
 */

@Entity
@Table(name = "TB_SENDER_WHITELIST")
@NamedQueries({@NamedQuery(name = "SenderWhitelist.findWhitelistEntry",
                           query = "SELECT COUNT (sw) from SenderWhitelist sw WHERE sw.partyId = :PARTY_ID AND sw.partyIdType = :PARTY_ID_TYPE AND (sw.service = :SERVICE OR sw.service = '*') AND (sw.action = :ACTION OR sw.action = '*')")})

public class SenderWhitelist extends AbstractBaseEntity {

    @Column(name = "PARTY_ID")
    private String partyId;

    @Column(name = "PARTY_ID_TYPE")
    private String partyIdType;

    @Column(name= "SERVICE")
    private String service;

    @Column(name= "ACTION")
    private String action;

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyIdType() {
        return partyIdType;
    }

    public void setPartyIdType(String partyIdType) {
        this.partyIdType = partyIdType;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
