package eu.europa.ec.edelivery.smp;

public enum SMPRole {

    SMP_ADMIN("ROLE_SMP_ADMIN"),
    SERVICE_GROUP_ADMIN("ROLE_SERVICE_GROUP_ADMIN"),
    SYSTEM_ADMIN("ROLE_SYSTEM_ADMIN");

    // static constants for annotations!
    public static final String S_ROLE_SYSTEM_ADMIN = "ROLE_SYSTEM_ADMIN";
    public static final String S_ROLE_SMP_ADMIN = "ROLE_SMP_ADMIN";
    public static final String S_ROLE_SERVICE_GROUP_ADMIN = "ROLE_SERVICE_GROUP_ADMIN";


    String code;
    SMPRole(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }



}
