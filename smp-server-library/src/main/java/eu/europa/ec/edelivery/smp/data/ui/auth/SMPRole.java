package eu.europa.ec.edelivery.smp.data.ui.auth;

public enum SMPRole {

    ANONYMOUS("ANONYMOUS"),
    USER("USER"),
    WS_USER("WS_USER"),
    SYSTEM_ADMIN("SYSTEM_ADMIN"),
    WS_SYSTEM_ADMIN("WS_SYSTEM_ADMIN")
  ;

    String code;

    SMPRole(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
