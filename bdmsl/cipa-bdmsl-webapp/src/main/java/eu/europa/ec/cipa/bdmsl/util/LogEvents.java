package eu.europa.ec.cipa.bdmsl.util;

import eu.europa.ec.cipa.common.logging.ILogEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feriaad on 18/06/2015.
 */
@Component
public class LogEvents implements ILogEvent {

    private List<LogEvent> eventList = new ArrayList<LogEvent>();

    public static final String SEC_CONNECTION_ATTEMPT = "SEC-001";
    public static final String SEC_AUTHORIZED_ACCESS = "SEC-002";
    public static final String SEC_UNAUTHORIZED_ACCESS = "SEC-003";
    public static final String SEC_REVOKED_CERTIFICATE = "SEC-004";
    public static final String SEC_UNKNOWN_CERTIFICATE = "SEC-005";
    public static final String SEC_CERTIFICATE_EXPIRED = "SEC-006";
    public static final String SEC_CERTIFICATE_NOT_YET_VALID = "SEC-007";

    public static final String BUS_AUTHENTICATION_ERROR = "BUS-001";
    public static final String BUS_SMP_CREATED = "BUS-002";
    public static final String BUS_SMP_CREATION_FAILED = "BUS-003";
    public static final String BUS_SMP_READ = "BUS-004";
    public static final String BUS_SMP_READ_FAILED = "BUS-005";
    public static final String BUS_SMP_DELETED = "BUS-006";
    public static final String BUS_SMP_DELETION_FAILED = "BUS-007";
    public static final String BUS_SMP_UPDATED = "BUS-008";
    public static final String BUS_SMP_UPDATE_FAILED = "BUS-009";
    public static final String BUS_PARTICIPANT_CREATED = "BUS-010";
    public static final String BUS_PARTICIPANT_CREATION_FAILED = "BUS-011";
    public static final String BUS_PARTICIPANT_LIST_CREATED = "BUS-012";
    public static final String BUS_PARTICIPANT_LIST_CREATION_FAILED = "BUS-013";
    public static final String BUS_PARTICIPANT_DELETED = "BUS-014";
    public static final String BUS_PARTICIPANT_DELETION_FAILED = "BUS-015";
    public static final String BUS_PARTICIPANT_LIST_DELETED = "BUS-016";
    public static final String BUS_PARTICIPANT_LIST_DELETION_FAILED = "BUS-017";
    public static final String BUS_PARTICIPANT_LIST = "BUS-018";
    public static final String BUS_PARTICIPANT_LIST_FAILED = "BUS-019";
    public static final String BUS_PREPARE_TO_MIGRATE_SUCCESS = "BUS-020";
    public static final String BUS_PREPARE_TO_MIGRATE_FAILED = "BUS-021";
    public static final String BUS_MIGRATE_SUCCESS = "BUS-022";
    public static final String BUS_MIGRATE_FAILED = "BUS-023";
    public static final String BUS_LIST_ALL_PARTICIPANT_SUCCESS = "BUS-024";
    public static final String BUS_LIST_ALL_PARTICIPANT_FAILED = "BUS-025";
    public static final String BUS_CERTIFICATE_CHANGED = "BUS-026";
    public static final String BUS_CERTIFICATE_CHANGE_FAILED = "BUS-027";
    public static final String BUS_CNAME_RECORD_FOR_PARTICIPANT_CREATED = "BUS-028";
    public static final String BUS_NAPTR_RECORD_FOR_PARTICIPANT_CREATED = "BUS-029";
    public static final String BUS_CNAME_RECORD_FOR_SMP_CREATED = "BUS-030";
    public static final String BUS_A_RECORD_FOR_SMP_CREATED = "BUS-031";
    public static final String BUS_CERTIFICATE_CHANGE_JOB_SUCCESS = "BUS-032";
    public static final String BUS_CERTIFICATE_CHANGE_JOB_FAILED = "BUS-033";
    public static final String BUS_CONFIGURATION_ERROR = "BUS-034";

    public LogEvents() {
        // security logs
        eventList.add(new LogEvent(CATEGORY_SECURITY, SEC_CONNECTION_ATTEMPT, "The host %s attempted to access %s without any certificate"));
        eventList.add(new LogEvent(CATEGORY_SECURITY, SEC_AUTHORIZED_ACCESS, "The host %s has been granted access to %s with roles %s"));
        eventList.add(new LogEvent(CATEGORY_SECURITY, SEC_UNAUTHORIZED_ACCESS, "The host %s has been refused access to %s"));
        eventList.add(new LogEvent(CATEGORY_SECURITY, SEC_REVOKED_CERTIFICATE, "The certificate is revoked : %s"));
        eventList.add(new LogEvent(CATEGORY_SECURITY, SEC_UNKNOWN_CERTIFICATE, "The root certificate of the client certificate is unknown in the database. It means that the certificate is accepted at transport level (SSL) but refused at application level. %s"));
        eventList.add(new LogEvent(CATEGORY_SECURITY, SEC_CERTIFICATE_EXPIRED, "Certificate is not valid at the current date %s. Certificate valid from %s to %s"));
        eventList.add(new LogEvent(CATEGORY_SECURITY, SEC_CERTIFICATE_NOT_YET_VALID, "Certificate is not yet valid at the current date %s. Certificate valid from %s to %s"));

        // business logs
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_AUTHENTICATION_ERROR, "Technical error while authentication process"));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_CONFIGURATION_ERROR, "Error while configuring the application."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_SMP_CREATED, "The SMP was successfully created: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_SMP_CREATION_FAILED, "The SMP couldn't be created: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_SMP_READ, "The following SMP was read: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_SMP_READ_FAILED, "The SMP couldn't be read: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_SMP_DELETED, "The SMP and all its participants were successfully deleted: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_SMP_DELETION_FAILED, "The SMP couldn't be deleted: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_SMP_UPDATED, "The SMP was successfully updated: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_SMP_UPDATE_FAILED, "The SMP couldn't be updated: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PARTICIPANT_CREATED, "The participant was successfully created: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PARTICIPANT_CREATION_FAILED, "The participant couldn't be created: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PARTICIPANT_LIST_CREATED, "These participants have been successfully created: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PARTICIPANT_LIST_CREATION_FAILED, "The list of participants couldn't be created: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PARTICIPANT_DELETED, "The participant was successfully deleted: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PARTICIPANT_DELETION_FAILED, "The participant couldn't be deleted: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PARTICIPANT_LIST_DELETED, "These participants have been successfully deleted: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PARTICIPANT_LIST_DELETION_FAILED, "The list of participants couldn't be deleted: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PARTICIPANT_LIST, "The participants of SMP %s have been successfully listed."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PARTICIPANT_LIST_FAILED, "The participants of SMP %s couldn't be listed."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PREPARE_TO_MIGRATE_SUCCESS, "The prepare to migrate service was successfully called for participant: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_PREPARE_TO_MIGRATE_FAILED, "The prepare to migrate service failed for participant: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_MIGRATE_SUCCESS, "The call to migrate service was successfully called for participant: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_MIGRATE_FAILED, "The call to migrate service failed for participant: %s."));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_LIST_ALL_PARTICIPANT_SUCCESS, "The call to the list service succeeded"));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_LIST_ALL_PARTICIPANT_FAILED, "The call to the list service failed"));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_CERTIFICATE_CHANGED, "The new certificate was successfully planned for change for current certificate: %s"));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_CERTIFICATE_CHANGE_FAILED, "The certificate change failed for current certificate: %s"));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_CNAME_RECORD_FOR_PARTICIPANT_CREATED, "The following CNAME record has been added to the DNS for the participant %s : %s"));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_NAPTR_RECORD_FOR_PARTICIPANT_CREATED, "The following NAPTR record has been added to the DNS for the participant %s : %s"));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_CNAME_RECORD_FOR_SMP_CREATED, "The following CNAME record has been added to the DNS for the SMP %s : %s"));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_A_RECORD_FOR_SMP_CREATED, "The following A record has been added to the DNS for the SMP %s : %s"));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_CERTIFICATE_CHANGE_JOB_SUCCESS, "The CertificateChangeJob ran successfully. %s certificates have been migrated"));
        eventList.add(new LogEvent(CATEGORY_BUSINESS, BUS_CERTIFICATE_CHANGE_JOB_FAILED, "The CertificateChangeJob failed."));
    }

    public String getMessage(String code) {
        for (LogEvent logEvent : eventList) {
            if (logEvent.getCode().equals(code)) {
                return logEvent.getMessage();
            }
        }
        return null;
    }


    static class LogEvent {
        String category;
        String code;
        String message;

        public LogEvent(String category, String code, String message) {
            this.category = category;
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LogEvent)) return false;

            LogEvent logEvent = (LogEvent) o;

            if (category != null ? !category.equals(logEvent.category) : logEvent.category != null) return false;
            if (code != null ? !code.equals(logEvent.code) : logEvent.code != null) return false;
            return !(message != null ? !message.equals(logEvent.message) : logEvent.message != null);

        }

        @Override
        public int hashCode() {
            int result = category != null ? category.hashCode() : 0;
            result = 31 * result + (code != null ? code.hashCode() : 0);
            result = 31 * result + (message != null ? message.hashCode() : 0);
            return result;
        }
    }
}
