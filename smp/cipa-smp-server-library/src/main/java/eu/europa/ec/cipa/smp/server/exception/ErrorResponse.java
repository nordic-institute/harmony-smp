package eu.europa.ec.cipa.smp.server.exception;

/**
 * Created by migueti on 16/01/2017.
 */
public class ErrorResponse {
    public enum BusinessCode {
        XSD_INVALID("XSD_INVALID"),
        MISSING_FIELD("MISSING_FIELD"),
        WRONG_FIELD("WRONG_FIELD"),
        OUT_OF_RANGE("OUT_OF_RANGE"),
        UNAUTHOR_FIELD("UNAUTHOR_FIELD"),
        FORMAT_ERROR("FORMAT_ERROR"),
        OTHER_ERROR("OTHER_ERROR"),
        UNAUTHORIZED("UNAUTHORIZED"),
        NOT_FOUND("NOT_FOUND"),
        TECHNICAL("TECHNICAL");

        private final String name;

        BusinessCode(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }
}
