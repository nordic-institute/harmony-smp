package eu.europa.ec.edelivery.smp.data.ui.enums;

/**
 * Enumeration of the alert types. The enumeration defines the mail template
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public enum AlertTypeEnum {
    TEST_ALERT("test_mail.ftl"),
    CREDENTIAL_IMMINENT_EXPIRATION("credential_imminent_expiration.ftl"),
    CREDENTIAL_EXPIRED("credential_expired.ftl"),
    CREDENTIAL_SUSPENDED("credential_suspended.ftl"),
    CREDENTIAL_VERIFICATION_FAILED("credential_verification_failed.ftl"),
    ;

    private final String template;

    AlertTypeEnum(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
