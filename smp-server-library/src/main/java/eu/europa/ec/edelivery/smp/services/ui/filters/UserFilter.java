package eu.europa.ec.edelivery.smp.services.ui.filters;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBUser;

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
