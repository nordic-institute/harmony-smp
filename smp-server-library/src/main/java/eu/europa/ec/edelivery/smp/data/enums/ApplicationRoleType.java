package eu.europa.ec.edelivery.smp.data.enums;

import eu.europa.ec.edelivery.smp.data.ui.auth.SMPRole;

public enum ApplicationRoleType {
    USER(SMPRole.USER, SMPRole.WS_USER),
    SYSTEM_ADMIN(SMPRole.SYSTEM_ADMIN, SMPRole.WS_SYSTEM_ADMIN);


    SMPRole apiRole;
    SMPRole uiRole;

    ApplicationRoleType(SMPRole uiRole, SMPRole apiRole) {
        this.uiRole = uiRole;
        this.apiRole = apiRole;
    }

    public String getAPIRole() {
        return "ROLE_" + apiName();
    }

    public String getUIRole() {
        return "ROLE_" + uiName();
    }

    public String apiName() {
        return apiRole.getCode();
    }

    public String uiName() {
        return uiRole.getCode();
    }


}
