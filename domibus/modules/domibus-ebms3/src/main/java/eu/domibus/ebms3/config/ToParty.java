package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "ToPartyInfo", strict = false)
public class ToParty implements Serializable {
    private static final long serialVersionUID = -5593316571068880737L;

    @Attribute(required = false)
    protected String name;

    @ElementList(inline = true)
    protected List<Party> parties = new ArrayList<Party>();

    @Element(name = "Role", required = false)
    protected String role;

    public ToParty() {
    }

    public ToParty(final String name, final List<Party> parties, final String role) {
        this.name = name;
        this.parties = parties;
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

    public List<Party> getParties() {
        return this.parties;
    }

    public void setParties(final List<Party> parties) {
        this.parties = parties;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public Party[] getPartiesArray() {
        if (this.parties == null) {
            return null;
        }
        final Party[] res = new Party[this.parties.size()];
        int i = 0;
        for (final Party p : this.parties) {
            res[i] = p;
            i++;
        }
        return res;
    }

    public void setPartiesArray(final Party[] list) {
        if ((list == null) || (list.length == 0)) {
            if ((this.parties != null) && !this.parties.isEmpty()) {
                this.parties.clear();
            }
            return;
        }
        if (this.parties == null) {
            this.parties = new ArrayList<Party>();
        }
        if (!this.parties.isEmpty()) {
            this.parties.clear();
        }
        for (final Party p : list) {
            this.addParty(p);
        }
    }
}