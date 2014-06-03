package eu.domibus.ebms3.packaging;

import org.apache.axiom.om.OMElement;
import eu.domibus.common.soap.Element;
import eu.domibus.common.util.JNDIUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.module.Constants;

import java.text.MessageFormat;
import java.util.Iterator;

/**
 * @author Hamid Ben Malek
 */
public class PartyInfo extends Element {
    private static final long serialVersionUID = 2306154359641479706L;
    protected Element from = new Element(Constants.FROM, Constants.NS, Constants.PREFIX);
    protected Element to = new Element(Constants.TO, Constants.NS, Constants.PREFIX);

    public PartyInfo() {
        super(Constants.PARTY_INFO, Constants.NS, Constants.PREFIX);
        if (from == null) {
            from = new Element(Constants.FROM, Constants.NS, Constants.PREFIX);
        }
        if (to == null) {
            to = new Element(Constants.TO, Constants.NS, Constants.PREFIX);
        }
        addChild(from);
        addChild(to);
    }

    public PartyInfo(final String fromPartyID, final String toPartyID) {
        this();
        addFromParty(fromPartyID, null, null);
        addToParty(toPartyID, null, null);
    }

    public PartyInfo(final String[] fromPartyIDs, final String[] toPartyIDs) {
        this();
        addFromParties(fromPartyIDs, null);
        addToParties(toPartyIDs, null);
    }

    public void addFromParty(final String partyId, final String partyIdType, final String role) {
        if (partyId == null) {
            return;
        }
        final Element p = new Element(Constants.PARTY_ID, Constants.NS, Constants.PREFIX);
        if (partyIdType != null && !partyIdType.trim().equals("")) {
            p.setText(partyId);
            p.addAttribute("type", partyIdType);
        } else {
            p.setText(partyId);
            if (!JNDIUtil.getBooleanEnvironmentParameter(Constants.ENFORCE_1_3_COMPATIBILITY)) {
                p.setAttribute("type", eu.domibus.ebms3.module.Constants.ECODEX_PARTY_ID_URI_VALUE);
            }
        }
        from.addChild(p);
        if (role != null && !role.trim().equals("")) {
            final Element r = new Element(Constants.ROLE, Constants.NS, Constants.PREFIX);
            r.setText(role);
            from.addChild(r);
        }
    }

    public void addFromParties(final String[] partyIds, final String role) {
        if (partyIds == null || partyIds.length == 0) {
            return;
        }
        for (final String partyId : partyIds) {
            addFromParty(partyId, null, null);
        }
        if (role != null && !role.trim().equals("")) {
            final Element r = new Element(Constants.ROLE, Constants.NS, Constants.PREFIX);
            r.setText(role);
            from.addChild(r);
        }
    }

    public void addFromParties(final String[] partyIds, final String[] types, final String role) {
        if (partyIds == null || partyIds.length == 0) {
            return;
        }
        if (types != null && types.length > 0) {
            for (int i = 0; i < Math.min(partyIds.length, types.length); i++) {
                addFromParty(partyIds[i], types[i], null);
            }
            if (partyIds.length > types.length) {
                for (int j = types.length; j < partyIds.length; j++) {
                    addFromParty(partyIds[j], null, null);
                }
            }
        } else {
            for (final String partyId : partyIds) {
                addFromParty(partyId, null, null);
            }
        }
        if (role != null && !role.trim().equals("")) {
            final Element r = new Element(Constants.ROLE, Constants.NS, Constants.PREFIX);
            r.setText(role);
            from.addChild(r);
        }
    }

    // Similar methods for the to element:
    public void addToParty(final String partyId, final String partyIdType, final String role) {
        if (partyId == null) {
            return;
        }
        final Element p = new Element(Constants.PARTY_ID, Constants.NS, Constants.PREFIX);
        if (partyIdType != null && !partyIdType.trim().isEmpty()) {
            p.setText(partyId);
            p.addAttribute("type", partyIdType);
        } else {
            if (!JNDIUtil.getBooleanEnvironmentParameter(Constants.ENFORCE_1_3_COMPATIBILITY)) {
                p.setText(MessageFormat.format(eu.domibus.ebms3.module.Constants.PARTY_ID_MESSAGE_FORMAT, partyId));
            }
        }
        to.addChild(p);
        if (role != null && !role.trim().equals("")) {
            final Element r = new Element(Constants.ROLE, Constants.NS, Constants.PREFIX);
            r.setText(role);
            to.addChild(r);
        }
    }

    public void addToParties(final String[] partyIds, final String role) {
        if (partyIds == null || partyIds.length == 0) {
            return;
        }
        for (final String partyId : partyIds) {
            addToParty(partyId, null, null);
        }
        if (role != null && !role.trim().equals("")) {
            final Element r = new Element(Constants.ROLE, Constants.NS, Constants.PREFIX);
            r.setText(role);
            to.addChild(r);
        }
    }

    public void addToParties(final String[] partyIds, final String[] types, final String role) {
        if (partyIds == null || partyIds.length == 0) {
            return;
        }
        if (types != null && types.length > 0) {
            for (int i = 0; i < Math.min(partyIds.length, types.length); i++) {
                addToParty(partyIds[i], types[i], null);
            }
            if (partyIds.length > types.length) {
                for (int j = types.length; j < partyIds.length; j++) {
                    addToParty(partyIds[j], null, null);
                }
            }
        } else {
            for (final String partyId : partyIds) {
                addToParty(partyId, null, null);
            }
        }
        if (role != null && !role.trim().equals("")) {
            final Element r = new Element(Constants.ROLE, Constants.NS, Constants.PREFIX);
            r.setText(role);
            to.addChild(r);
        }
    }

    public String getFromRole() {
        //return Util.getGrandChildValue(from, Constants.ROLE);
        return XMLUtil.getGrandChildValue(from.getElement(), Constants.ROLE);
    }

    public void setFromRole(final String role) {
        if (role == null || role.trim().equals("")) {
            return;
        }
        final Iterator it = from.getChildElements();
        boolean found = false;
        while (it != null && it.hasNext()) {
            final OMElement e = (OMElement) it.next();
            if (e.getLocalName().equals(Constants.ROLE)) {
                e.setText(role);
                found = true;
            }
        }
        if (!found) {
            final Element r = new Element(Constants.ROLE, Constants.NS, Constants.PREFIX);
            r.setText(role);
            from.addChild(r);
        }
    }

    public String getToRole() {
        //return Util.getGrandChildValue(to, Constants.ROLE);
        return XMLUtil.getGrandChildValue(to.getElement(), Constants.ROLE);
    }

    public void setToRole(final String role) {
        if (role == null || role.trim().equals("")) {
            return;
        }
        final Iterator it = to.getChildElements();
        boolean found = false;
        while (it != null && it.hasNext()) {
            final OMElement e = (OMElement) it.next();
            if (e.getLocalName().equals(Constants.ROLE)) {
                e.setText(role);
                found = true;
            }
        }
        if (!found) {
            final Element r = new Element(Constants.ROLE, Constants.NS, Constants.PREFIX);
            r.setText(role);
            to.addChild(r);
        }
    }
}