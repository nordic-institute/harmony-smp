/*
 * 
 */
package eu.domibus.backend.db.model;

import javax.persistence.*;

/**
 * The Class EbmsPayload.
 */
@Entity
@Table(name = "TB_EBMS_PAYLOAD")
public class Payload {

    @Id
    @TableGenerator(name = "TABLE_GEN_PAYLOAD", table = "SEQUENCE_TABLE", pkColumnName = "SEQ_NAME",
                    valueColumnName = "SEQ_COUNT", pkColumnValue = "PAYLOAD_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_PAYLOAD")
    @Column(name = "ID_PAYLOAD")
    private Integer idPayload;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MESSAGE")
    private Message message;


    @Column(name = "FILE_NAME", length = 256)
    private String fileName;


    @Column(name = "PAYLOAD_ID", length = 256)
    private String payloadId;


    @Column(name = "BODYLOAD")
    private boolean bodyload;


    @Column(name = "CONTENT_TYPE", length = 256)
    private String contentType;


    public Payload() {
    }


    public Payload(final Integer idPayload) {
        this.idPayload = idPayload;
    }


    public Payload(final Integer idPayload, final Message message, final String fileName) {
        this.idPayload = idPayload;
        this.message = message;
        this.fileName = fileName;
    }


    public Integer getIdPayload() {
        return this.idPayload;
    }


    public void setIdPayload(final Integer idPayload) {
        this.idPayload = idPayload;
    }


    public Message getMessage() {
        return this.message;
    }


    public void setMessage(final Message message) {
        this.message = message;
    }


    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the payloadId.
     *
     * @return the payloadId
     */
    public String getPayloadId() {
        return this.payloadId;
    }

    /**
     * Sets the payloadId
     *
     * @param payloadId the new payloadId
     */
    public void setPayloadId(final String payloadId) {
        this.payloadId = payloadId;
    }

    /**
     * Gets the bodyload.
     *
     * @return the bodyload
     */
    public boolean getBodyload() {
        return this.bodyload;
    }

    /**
     * Sets the bodyload.
     *
     * @param bodyload the new bodyload
     */
    public void setBodyload(final boolean bodyload) {
        this.bodyload = bodyload;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "EbmsPayload [idPayload=" + idPayload + ", message=" + message + ", fileName=" + fileName +
               ", payloadId=" +
               payloadId + ", bodyload=" + bodyload + ", contentType=" + contentType + "]";
    }
}