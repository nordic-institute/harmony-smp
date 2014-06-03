package eu.domibus.ebms3.packaging;

import org.apache.axiom.om.OMElement;
import eu.domibus.common.soap.Element;
import eu.domibus.ebms3.module.Constants;

/**
 * @author Hamid Ben Malek
 */
public class Receipt extends Element {
    private static final long serialVersionUID = -2001342201298347565L;

    public Receipt() {
        super(Constants.RECEIPT, Constants.NS, Constants.PREFIX);
    }

    public Receipt(final OMElement[] references) {
        setReferences(references);
    }

    public Receipt(final OMElement nonRepudiationInformation) {
        this();
        if (nonRepudiationInformation != null) {
            addChild(nonRepudiationInformation);
        }
    }

    public void setReferences(final OMElement[] references) {
        final Element nri = new Element(Constants.NON_REPUDIATION_INFORMATION, Constants.ebbpNS, Constants.ebbp_PREFIX);
        for (final OMElement ref : references) {
            final Element mpn =
                    new Element(Constants.MESSAGE_PART_NR_INFORMATION, Constants.ebbpNS, Constants.ebbp_PREFIX);
            mpn.addChild(ref);
            nri.addChild(mpn);
        }
        addChild(nri);
    }
}