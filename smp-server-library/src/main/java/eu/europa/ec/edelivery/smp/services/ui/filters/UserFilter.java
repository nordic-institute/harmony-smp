package eu.europa.ec.edelivery.smp.services.ui.filters;

import java.util.List;

public class UserFilter {
    List<String> roleList;

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roles) {
        this.roleList = roles;
    }
}
