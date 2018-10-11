package eu.europa.ec.edelivery.smp.auth;

public enum SMPRole {

    SMP_ADMIN("SMP_ADMIN"),
    SERVICE_GROUP_ADMIN("SERVICE_GROUP_ADMIN"),
    SYSTEM_ADMIN("SYSTEM_ADMIN");




    String code;
    SMPRole(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }



}
