package eu.europa.ec.edelivery.smp.data.ui.enums;

/**
 * Enumeration of the alert types. The enumeration defines the mail template
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public enum AlertTypeEnum {
    TEST_ALERT("test_mail.ftl"),
    CREDENTIALS_IMMINENT_EXPIRATION("credentials_imminent_expiration.ftl"),
    CREDENTIALS_EXPIRED("credentials_expired.ftl"),
    ACCOUNT_SUSPENDED("account_suspended.ftl"),
    ;


    private final String template;

    AlertTypeEnum(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
