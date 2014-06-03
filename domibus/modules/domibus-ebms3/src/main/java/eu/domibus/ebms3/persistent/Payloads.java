package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractBaseEntity;
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
@Root(name = "Payloads")
@Entity
@Table(name = "TB_PAYLOADS")
public class Payloads extends AbstractBaseEntity {


    @Element(name = "BodyPayload", required = false)
    @JoinColumn(name = "BODY_PAYLOAD_ID")
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private EbmsPayload bodyPayload;

    @ElementList(required = false, inline = true)
    @JoinColumn(name = "PAYLOAD_ID")
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    protected Set<EbmsPayload> payloads = new HashSet<EbmsPayload>();

    public String getBodyPayload() {
        if (this.bodyPayload == null) {
            return null;
        }
        return this.bodyPayload.getFile();
    }

    public void setBodyPayload(final EbmsPayload bodyPayload) {
        this.bodyPayload = bodyPayload;
    }

    public void setBodyPayload(final String payloadURI) {
        this.bodyPayload = new EbmsPayload();
        this.bodyPayload.setFile(payloadURI);
    }

    public void setBodyPayload(final String cid, final String payloadURI) {
        this.bodyPayload = new EbmsPayload();
        this.bodyPayload.setCid(cid);
        this.bodyPayload.setFile(payloadURI);
    }

    public String getBodyPayloadCID() {
        if (this.bodyPayload == null) {
            return null;
        }

        return this.bodyPayload.getCid();
    }

    public void setBodyPayloadCID(final String cid) {
        if ((cid == null) || "".equals(cid.trim())) {
            return;
        }

        if (this.bodyPayload == null) {
            this.bodyPayload = new EbmsPayload();
        }

        this.bodyPayload.setCid(cid);
    }

    public boolean isBodyPayloadCompressed() {
        if (this.bodyPayload == null) {
            return false;
        }

        return this.bodyPayload.isCompressed();
    }

    public void setBodyPayloadCompressed(final boolean compressed) {
        this.bodyPayload.setCompressed(compressed);
    }

    public String getBodyPayloadDescription() {
        if (this.bodyPayload == null) {
            return null;
        }

        return this.bodyPayload.getDescription();
    }

    public void setBodyPayloadDescription(final String description) {
        if ((description == null) || "".equals(description.trim())) {
            return;
        }

        if (this.bodyPayload == null) {
            this.bodyPayload = new EbmsPayload();
        }

        this.bodyPayload.setDescription(description);
    }

    public PartProperties getBodyPayloadPartProperties() {
        if (this.bodyPayload == null) {
            return null;
        }

        return this.bodyPayload.getPartProperties();
    }

    public void setBodyPayloadPartProperties(final PartProperties partProperties) {
        if (partProperties == null) {
            return;
        }

        if (this.bodyPayload == null) {
            this.bodyPayload = new EbmsPayload();
        }

        this.bodyPayload.setPartProperties(partProperties);
    }

    public String getBodyPayloadSchemaLocation() {
        if (this.bodyPayload == null) {
            return null;
        }

        return this.bodyPayload.getSchemaLocation();
    }

    public void setBodyPayloadSchemaLocation(final String schemaLocation) {
        if ((schemaLocation == null) || "".equals(schemaLocation.trim())) {
            return;
        }

        if (this.bodyPayload == null) {
            this.bodyPayload = new EbmsPayload();
        }

        this.bodyPayload.setSchemaLocation(schemaLocation);
    }

    public Collection<EbmsPayload> getPayloads() {
        return this.payloads;
    }

    public void setPayloads(final Set<EbmsPayload> payloads) {
        this.payloads = payloads;
    }

    public void addPayload(final String cid, final String payloadURI) {
        final EbmsPayload p = new EbmsPayload();
        p.setCid(cid);
        p.setFile(payloadURI);
        this.payloads.add(p);
    }

    public String getPayload(final String cid) {
        if ((this.payloads == null) || (this.payloads.size() <= 0)) {
            return null;
        }
        for (final EbmsPayload p : this.payloads) {
            if (p.getCid().equalsIgnoreCase(cid)) {
                return p.getFile();
            }
        }
        return null;
    }

    public String getCID(final String payload) {
        if ((this.payloads == null) || (this.payloads.size() <= 0)) {
            return null;
        }
        for (final EbmsPayload p : this.payloads) {
            if (p.getFile().equalsIgnoreCase(payload)) {
                return p.getCid();
            }
        }
        return null;
    }

    public void setCID(final String cid, final String payloadFie) {
        if ((cid == null) || "".equals(cid.trim()) || (payloadFie == null)) {
            return;
        }
        if (this.payloads == null) {
            this.payloads = new HashSet<EbmsPayload>();
        }
        for (final EbmsPayload p : this.payloads) {
            if (p.getFile().equalsIgnoreCase(payloadFie)) {
                p.setCid(cid);
                return;
            }
        }
        final EbmsPayload p = new EbmsPayload();
        p.setCid(cid);
        p.setFile(payloadFie);
        this.payloads.add(p);
    }

    public boolean isCompressed(final String fileName) {
        if ((fileName == null) || "".equals(fileName.trim())) {
            return false;
        }
        if ((this.payloads == null) || (this.payloads.isEmpty())) {
            return false;
        }
        for (final EbmsPayload p : this.payloads) {
            if (p.getFile().equals(fileName) && p.isCompressed()) {
                return true;
            }
        }
        return false;
    }

    public String getDescription(final String payloadFile) {
        for (final EbmsPayload p : this.payloads) {
            if (p.getFile().equals(payloadFile)) {
                return p.getDescription();
            }
        }
        return null;
    }

    public PartProperties getPartProperties(final String payloadFile) {
        for (final EbmsPayload p : this.payloads) {
            if (p.getFile().equals(payloadFile)) {
                return p.getPartProperties();
            }
        }
        return null;
    }

    public String getSchemaLocation(final String payloadFile) {
        for (final EbmsPayload p : this.payloads) {
            if (p.getFile().equals(payloadFile)) {
                return p.getSchemaLocation();
            }
        }
        return null;
    }
}