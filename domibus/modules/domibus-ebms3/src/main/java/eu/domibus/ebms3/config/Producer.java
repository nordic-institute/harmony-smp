package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "Producer", strict = false)
@Embeddable
public class Producer {


    @Attribute(required = false)
    @Column(name = "NAME")
    protected String name;

    @ElementList(inline = true)
    @JoinColumn(name = "PRODUCER_PK")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected Set<Party> parties = new HashSet<Party>();

    @Element(name = "Role", required = false)
    @Column(name = "Role")
    protected String role;

    public Producer() {
    }

    public Producer(final String name, final Collection<Party> parties, final String role) {
        this.name = name;
        this.parties.clear();
        this.parties.addAll(parties);
        this.role = role;
    }

    public void addParty(final String type, final String partyId) {
        final Party p = new Party(type, partyId);
        this.parties.add(p);
    }

    public void addParty(final Party party) {
        this.parties.add(party);
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Collection<Party> getParties() {
        return this.parties;
    }

    public void setParties(final Set<Party> parties) {
        this.parties = parties;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(final String role) {
        this.role = role;
    }
}