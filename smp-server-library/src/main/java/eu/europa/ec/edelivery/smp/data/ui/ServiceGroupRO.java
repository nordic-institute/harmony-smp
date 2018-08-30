package eu.europa.ec.edelivery.smp.data.ui;


import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Entity
@Table(name = "smp_service_group")
public class ServiceGroupRO implements Serializable {


    private static final long serialVersionUID = -7523221767041516157L;
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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceGroupRO that = (ServiceGroupRO) o;
        return Objects.equals(serviceGroupROId, that.serviceGroupROId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serviceGroupROId);
    }

    @Embeddable
    public static class ServiceGroupROId implements Serializable {
        private static final long serialVersionUID = 7895751676689305736L;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ServiceGroupROId that = (ServiceGroupROId) o;
            return Objects.equals(participantId, that.participantId) &&
                    Objects.equals(participantSchema, that.participantSchema);
        }

        @Override
        public int hashCode() {

            return Objects.hash(participantId, participantSchema);
        }
    }
}
