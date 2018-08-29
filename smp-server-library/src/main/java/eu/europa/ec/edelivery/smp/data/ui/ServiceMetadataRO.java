package eu.europa.ec.edelivery.smp.data.ui;


import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Entity
@Table(name = "smp_service_metadata")
public class ServiceMetadataRO implements Serializable {


    @EmbeddedId
    ServiceMetadataROId serviceMetadataROId;

    public ServiceMetadataROId getServiceMetadataROId() {
        return serviceMetadataROId;
    }

    public void setServiceMetadataROId(ServiceMetadataROId serviceMetadataROId) {
        this.serviceMetadataROId = serviceMetadataROId;
    }

    @Embeddable
    @ToString
    @EqualsAndHashCode
    public static class ServiceMetadataROId implements Serializable {
        @Column(name = "businessIdentifier")
        private String participantId;
        @Column(name = "businessIdentifierScheme")
        private String participantSchema;
        @Column(name = "documentIdentifierScheme")
        private String documentIdScheme;
        @Column(name = "documentIdentifier")
        private String documentIdValue;

        public ServiceMetadataROId() {
        }

        public ServiceMetadataROId(String participantId, String participantSchema, String documentIdScheme, String documentIdValue) {
            this.participantId = participantId;
            this.participantSchema = participantSchema;
            this.documentIdScheme = documentIdScheme;
            this.documentIdValue = documentIdValue;
        }

        public String getParticipantId() {
            return participantId;
        }

        public void setParticipantId(String participantId) {
            this.participantId = participantId;
        }

        public String getParticipantSchema() {
            return participantSchema;
        }

        public void setParticipantSchema(String participantSchema) {
            this.participantSchema = participantSchema;
        }

        public String getDocumentIdScheme() {
            return documentIdScheme;
        }

        public void setDocumentIdScheme(String documentIdScheme) {
            this.documentIdScheme = documentIdScheme;
        }

        public String getDocumentIdValue() {
            return documentIdValue;
        }

        public void setDocumentIdValue(String documentIdValue) {
            this.documentIdValue = documentIdValue;
        }
    }
}
