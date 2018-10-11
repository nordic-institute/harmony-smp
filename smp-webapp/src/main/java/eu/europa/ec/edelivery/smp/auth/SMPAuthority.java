package eu.europa.ec.edelivery.smp.auth;

import org.springframework.security.core.GrantedAuthority;


public class SMPAuthority implements GrantedAuthority {

    // static constants for annotations!
    public static final String S_AUTHORITY_SYSTEM_ADMIN = "ROLE_SYSTEM_ADMIN";
    public static final String S_AUTHORITY_SMP_ADMIN = "ROLE_SMP_ADMIN";
    public static final String S_AUTHORITY_SERVICE_GROUP_ADMIN = "ROLE_SERVICE_GROUP_ADMIN";


    String role;

    public SMPAuthority(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return "ROLE_"+role;
    }
}
