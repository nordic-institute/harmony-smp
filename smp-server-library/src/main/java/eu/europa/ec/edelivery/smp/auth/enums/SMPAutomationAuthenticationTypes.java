package eu.europa.ec.edelivery.smp.auth.enums;

/**
 *  Authentication types for application accounts supporting automated application functionalities. The application accounts
 *  are used for SMP web-service integrations.
 *
 *  Supported authentication types
 *   - PASSWORD: the application username/password (Note:automation-user authentication is different than ui-user
 *               password and it can be used only for web-services!).
 *   - CERTIFICATE: certificate authentication username/password.,
 *
 *  @author Joze Rihtarsic
 *  @since 4.2
 */
public enum SMPAutomationAuthenticationTypes {
    PASSWORD,
    CERTIFICATE;
}