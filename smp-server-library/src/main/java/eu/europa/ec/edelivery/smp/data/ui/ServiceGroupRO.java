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
@Table(name = "smp_service_group")
public class ServiceGroupRO implements Serializable {


    @EmbeddedId
    ServiceGroupROId serviceGroupROId;

    @Column(name = "domainId")
    private String domain;

    public ServiceGroupROId getServiceGroupROId() {
        return serviceGroupROId;
    }

    public void setServiceGroupROId(ServiceGroupROId serviceGroupROId) {
        this.serviceGroupROId = serviceGroupROId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }



    @Embeddable
    @ToString
    @EqualsAndHashCode
    public static class ServiceGroupROId implements Serializable {
        @Column(name = "businessIdentifier")
        private String participantId;
        @Column(name = "businessIdentifierScheme")
        private String participantSchema;

        public ServiceGroupROId(){

        }

        public ServiceGroupROId(String participantId, String participantSchema) {
            this.participantId = participantId;
            this.participantSchema = participantSchema;
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

        public void setParticipantSchema(String participanSchema) {
            this.participantSchema = participanSchema;
        }

    }
}
