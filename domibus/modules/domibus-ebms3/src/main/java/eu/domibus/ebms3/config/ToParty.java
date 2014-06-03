package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "ToPartyInfo", strict = false)
public class ToParty implements java.io.Serializable {
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
        parties.add(p);
    }

    public void addParty(final Party party) {
        parties.add(party);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(final List<Party> parties) {
        this.parties = parties;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public Party[] getPartiesArray() {
        if (parties == null) {
            return null;
        }
        final Party[] res = new Party[parties.size()];
        int i = 0;
        for (final Party p : parties) {
            res[i] = p;
            i++;
        }
        return res;
    }

    public void setPartiesArray(final Party[] list) {
        if (list == null || list.length == 0) {
            if (parties != null && parties.size() > 0) {
                parties.clear();
            }
            return;
        }
        if (parties == null) {
            parties = new ArrayList<Party>();
        }
        if (parties.size() > 0) {
            parties.clear();
        }
        for (final Party p : list) {
            addParty(p);
        }
    }
}