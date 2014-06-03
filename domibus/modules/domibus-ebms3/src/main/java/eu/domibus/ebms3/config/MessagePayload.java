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
@Root(name = "Message", strict = false)
public class MessagePayload implements java.io.Serializable {
    private static final long serialVersionUID = -1827366571062220737L;

    @Attribute(required = false)
    protected String label;

    @Attribute(required = false)
    protected String maxSize;

    @Element(name = "SoapBodySchema", required = false)
    protected String soapBodySchema;

    @ElementList(inline = true, required = false)
    protected List<Part> parts = new ArrayList<Part>();

    public MessagePayload() {
    }

    public MessagePayload(final String label, final String maxSize, final List<Part> parts) {
        this.label = label;
        this.parts = parts;
        this.maxSize = maxSize;
    }

    public void addPart(final String cid, final String mimeType, final String schemaLocation, final String desc) {
        final Part p = new Part(cid, mimeType, schemaLocation, desc);
        parts.add(p);
    }

    public void addPart(final Part part) {
        parts.add(part);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(final String maxSize) {
        this.maxSize = maxSize;
    }

    public String getSoapBodySchema() {
        return soapBodySchema;
    }

    public void setSoapBodySchema(final String soapBodySchema) {
        this.soapBodySchema = soapBodySchema;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(final List<Part> parts) {
        this.parts = parts;
    }

    /* Needed to serialize objects to Flex UI */
    public Part[] getPartsArray() {
        if (parts == null) {
            return null;
        }
        final Part[] res = new Part[parts.size()];
        int i = 0;
        for (final Part p : parts) {
            res[i] = p;
            i++;
        }
        return res;
    }

    public void setPartsArray(final Part[] list) {
        if (list == null || list.length == 0) {
            if (parts != null && parts.size() > 0) {
                parts.clear();
            }
            return;
        }
        if (parts == null) {
            parts = new ArrayList<Part>();
        }
        if (parts.size() > 0) {
            parts.clear();
        }
        for (final Part p : list) {
            addPart(p);
        }
    }
}