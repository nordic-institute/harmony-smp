package eu.europa.ec.edelivery.smp.data.ui;



import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */


public class ServiceMetadataRO implements Serializable {


    private static final long serialVersionUID = 67944640449327185L;
    private String participantId;
    private String participantSchema;
    private String documentIdScheme;
    private String documentIdValue;


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
