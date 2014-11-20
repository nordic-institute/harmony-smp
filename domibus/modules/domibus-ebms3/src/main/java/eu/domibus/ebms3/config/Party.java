package eu.domibus.ebms3.config;

import eu.domibus.common.persistent.AbstractBaseEntity;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "PartyId", strict = false)
@Entity
@Table(name = "TB_PARTY")
public class Party extends AbstractBaseEntity {


    @Text
    @Column(name = "PARTY_ID")
    protected String partyId;
    @Attribute(required = false)
    @Column(name = "TYPE")
    protected String type;

    public Party() {
    }

    public Party(final String type, final String partyId) {
        this.type = type;
        this.partyId = partyId;
    }

    public String getPartyId() {
        return this.partyId;
    }

    public void setPartyId(final String partyId) {
        this.partyId = partyId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return this.partyId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if ((obj == null) || !(obj instanceof Party)) {
            return false;
        }
        final Party p = (Party) obj;
        return this.partyId.equals(p.getPartyId()) &&
               (((this.type == null) && (p.type == null)) || ((this.type != null) && this.type.equals(p.type)));
    }
}