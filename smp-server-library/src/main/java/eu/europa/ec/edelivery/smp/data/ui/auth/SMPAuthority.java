package eu.europa.ec.edelivery.smp.data.ui.auth;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.europa.ec.edelivery.smp.data.ui.databind.SMPAuthorityDeserializer;
import org.springframework.security.core.GrantedAuthority;


@JsonDeserialize(using = SMPAuthorityDeserializer.class)
public class SMPAuthority implements GrantedAuthority {

    // static constants for annotations!
    public static final String S_AUTHORITY_TOKEN_SYSTEM_ADMIN = "ROLE_SYSTEM_ADMIN";
    public static final String S_AUTHORITY_TOKEN_SMP_ADMIN = "ROLE_SMP_ADMIN";
    public static final String S_AUTHORITY_TOKEN_SERVICE_GROUP_ADMIN = "ROLE_SERVICE_GROUP_ADMIN";
    public static final String S_AUTHORITY_TOKEN_ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

    // static constants for verification...
    public static final SMPAuthority S_AUTHORITY_SYSTEM_ADMIN = new SMPAuthority(SMPRole.SYSTEM_ADMIN.getCode());
    public static final SMPAuthority S_AUTHORITY_SMP_ADMIN = new SMPAuthority(SMPRole.SMP_ADMIN.getCode());
    public static final SMPAuthority S_AUTHORITY_SERVICE_GROUP = new SMPAuthority(SMPRole.SERVICE_GROUP_ADMIN.getCode());
    public static final SMPAuthority S_AUTHORITY_ANONYMOUS = new SMPAuthority(SMPRole.ANONYMOUS.getCode());

    String role;

    public SMPAuthority(String role) {
        this.role = role;
    }

    @Override
    @JsonValue
    public String getAuthority() {
        return "ROLE_" + role;
    }
}
