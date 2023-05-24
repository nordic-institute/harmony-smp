package eu.europa.ec.edelivery.smp.data.enums;

/**
 * Specifies
 *
 * Specifies sml authentication type as SML Client-Cert header, SSLCLientCert header and mTLS .
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public enum SMLAuthenticationType {

    HTTP_HEADER_STRING,
    HTTP_HEADER_CERTIFICATE,
    TLS_CLIENT_CERTIFICATE
}
