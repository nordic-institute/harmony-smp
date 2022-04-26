package eu.europa.ec.edelivery.smp.data.ui.enums;

/**
 * Enumeration of the alert types. The enumeration defines the mail template
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public enum AlertTypeEnum {
    USER_ACCOUNT_SUSPENDED("account_disabled.ftl"),
    CERT_IMMINENT_EXPIRATION("cert_imminent_expiration.ftl"),
    CERT_EXPIRED("cert_expired.ftl"),
    USER_LOGIN_FAILURE("login_failure.ftl"),
    USER_ACCOUNT_DISABLED("account_disabled.ftl"),
    USER_ACCOUNT_ENABLED("account_enabled.ftl"),
    PLUGIN_USER_LOGIN_FAILURE("login_failure.ftl"),
    PLUGIN_USER_ACCOUNT_DISABLED("account_disabled.ftl"),
    PLUGIN_USER_ACCOUNT_ENABLED("account_enabled.ftl"),
    PASSWORD_IMMINENT_EXPIRATION("password_imminent_expiration.ftl"),
    PASSWORD_EXPIRED("password_expired.ftl"),
    PLUGIN_PASSWORD_IMMINENT_EXPIRATION("password_imminent_expiration.ftl"),
    PLUGIN_PASSWORD_EXPIRED("password_expired.ftl"),
    PLUGIN("plugin.ftl");

    private final String template;

    AlertTypeEnum(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
