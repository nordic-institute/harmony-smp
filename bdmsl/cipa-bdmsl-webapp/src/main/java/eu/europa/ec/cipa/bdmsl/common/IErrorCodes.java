package eu.europa.ec.cipa.bdmsl.common;

/**
 * Created by feriaad on 16/06/2015.
 */
public interface IErrorCodes {

    int SMP_NOT_FOUND_ERROR = 100;

    int UNAUTHORIZED_ERROR = 101;

    int CERTIFICATE_AUTHENTICATION_ERROR = 102;

    int ROOT_CERTIFICATE_ALIAS_NOT_FOUND_ERROR = 103;

    int CERTIFICATE_REVOKED_ERROR = 104;

    int GENERIC_TECHNICAL_ERROR = 105;

    int BAD_REQUEST_ERROR = 106;

    int DNS_CLIENT_ERROR = 107;

    int SIG0_ERROR = 108;

    int BAD_CONFIGURATION_ERROR = 109;

    int PARTICIPANT_NOT_FOUND_ERROR = 110;

    int MIGRATION_NOT_FOUND_ERROR = 111;

    int DUPLICATE_PARTICIPANT_ERROR = 112;

    int SMP_DELETE_ERROR = 113;

    int MIGRATION_PLANNED_ERROR = 114;
}
