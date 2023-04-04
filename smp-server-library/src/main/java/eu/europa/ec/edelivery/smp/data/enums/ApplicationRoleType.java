package eu.europa.ec.edelivery.smp.data.enums;

public enum ApplicationRoleType {
    USER("USER","WS_USER"),
    SYSTEM_ADMIN("SYSTEM_ADMIN","WS_USER");
    String apiName;
    String uiName;

    ApplicationRoleType(String uiName, String apiName) {
        this.apiName = apiName;
        this.uiName = uiName;
    }

    public String getAPIRole(){
        return "ROLE_" + apiName();
    }

    public String getUIRole(){
        return apiName;
    }

    public String apiName(){
        return apiName;
    }

    public String uiName(){
        return  uiName;
    }


}
