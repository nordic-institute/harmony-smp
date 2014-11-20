package eu.domibus.ebms3.packaging;

import eu.domibus.common.soap.Element;
import eu.domibus.ebms3.module.Constants;
import org.apache.axis2.context.MessageContext;

/**
 * @author Hamid Ben Malek
 */
public class PayloadInfo extends Element {
    private static final long serialVersionUID = -4645523945415810689L;

    public PayloadInfo() {
        super(Constants.PAYLOAD_INFO, Constants.NS, Constants.PREFIX);
    }

    public PayloadInfo(final String partID) {
        this();
        this.addPartInfo(partID, null, null, null, null);
    }

    public PayloadInfo(final String partID, final String schemaLocation, final String description) {
        this();
        this.addPartInfo(partID, schemaLocation, description, null, null);
    }

    public PayloadInfo(final String[] partIDs) {
        this();
        if ((partIDs == null) || (partIDs.length == 0)) {
            return;
        }
        for (final String cid : partIDs) {
            this.addPartInfo(cid, null, null, null, null);
        }
    }

    public PayloadInfo(final String[] partIDs, final String soapPartCid) {
        this();
        if ((partIDs == null) || (partIDs.length == 0)) {
            return;
        }
        for (final String cid : partIDs) {
            if (!cid.equals(soapPartCid)) {
                this.addPartInfo(cid, null, null, null, null);
            }
        }
    }

    public PayloadInfo(final String[] partIDs, final String soapPartCid, final boolean hasBody) {
        this();
        if ((partIDs == null) || (partIDs.length == 0)) {
            return;
        }
        if (hasBody) {
            this.addPartInfo(null, null, null, null, null);
        }
        for (final String cid : partIDs) {
            if (!cid.equals(soapPartCid)) {
                this.addPartInfo(cid, null, null, null, null);
            }
        }
    }

    public void addPartInfo(final String partID, final String schemaLocation, final String description,
                            final String[] propertyNames, final String[] propertyValues) {
        final Element partInfo = new Element(Constants.PART_INFO, Constants.NS, Constants.PREFIX);
        if (partID != null) {
            String pId = partID;
            if (partID.startsWith("<") && partID.endsWith(">")) {
                pId = partID.substring(1, partID.length() - 1);
            }
            if (partID.startsWith("&lt;") && partID.endsWith("&gt;")) {
                pId = partID.substring(4, partID.length() - 4);
            }
            if (pId.startsWith("http://") || pId.startsWith("#")) {
                partInfo.addAttribute("href", pId);
            } else if (!pId.startsWith("cid") && !pId.startsWith("#")) {
                pId = "cid:" + pId;
                partInfo.addAttribute("href", pId);
            } else if (pId.startsWith("cid")) {
                partInfo.addAttribute("href", pId);
            }
        }

        if ((schemaLocation != null) && !"".equals(schemaLocation.trim())) {
            final Element sch = new Element(Constants.PART_INFO_SCHEMA, Constants.NS, Constants.PREFIX);
            sch.addAttribute("location", schemaLocation);
            partInfo.addChild(sch);
        }
        if (description != null) {
            final Element desc = new Element(Constants.PART_INFO_DESCR, Constants.NS, Constants.PREFIX);
            desc.setText(description);
            desc.addAttribute("xml:lang", "en-US");
            partInfo.addChild(desc);
        }

        if ((propertyNames != null) && (propertyNames.length > 0) &&
            (propertyValues != null) && (propertyValues.length > 0)) {
            final Element partProperties =
                    new Element(Constants.PART_INFO_PART_PROPERTIES, Constants.NS, Constants.PREFIX);
            for (int i = 0; i < propertyNames.length; i++) {
                final Element prop = new Element(Constants.PROPERTY, Constants.NS, Constants.PREFIX);
                prop.addAttribute("name", propertyNames[i]);
                if ((i < propertyValues.length) && (propertyValues[i] != null)) {
                    prop.setText(propertyValues[i]);
                }

                partProperties.addChild(prop);
            }
            partInfo.addChild(partProperties);
        }

        this.addChild(partInfo);
    }

    public static PayloadInfo createPayloadInfo(final MessageContext msgCtx) {
        if ((msgCtx == null) || (msgCtx.getAttachmentMap() == null)) {
            return null;
        }
        final String[] cids = msgCtx.getAttachmentMap().getAllContentIDs();
        if ((cids == null) || (cids.length == 0)) {
            return null;
        }
        return new PayloadInfo(cids);
    }
}