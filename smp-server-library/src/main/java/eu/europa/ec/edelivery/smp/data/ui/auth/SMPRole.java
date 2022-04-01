package eu.europa.ec.edelivery.smp.data.ui.auth;

public enum SMPRole {

    ANONYMOUS("ANONYMOUS"),
    GROUP_ADMIN("GROUP_ADMIN"),
    SERVICE_GROUP_ADMIN("SERVICE_GROUP_ADMIN"),
    SMP_ADMIN("SMP_ADMIN"),
    SYSTEM_ADMIN("SYSTEM_ADMIN"),
    WS_SERVICE_GROUP_ADMIN("WS_SERVICE_GROUP_ADMIN"),
    WS_SMP_ADMIN("WS_SMP_ADMIN"),
    WS_SYSTEM_ADMIN("WS_SYSTEM_ADMIN");

    String code;

    SMPRole(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
