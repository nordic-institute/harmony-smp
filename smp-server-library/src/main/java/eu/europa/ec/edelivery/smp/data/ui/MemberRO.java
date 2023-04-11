package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;

public class MemberRO {

    String memberId;
    String username;
    String memberOf;
    String fullName;
    MembershipRoleType roleType;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(String memberOf) {
        this.memberOf = memberOf;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public MembershipRoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(MembershipRoleType roleType) {
        this.roleType = roleType;
    }
}
