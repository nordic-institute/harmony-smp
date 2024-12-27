package rest.models;

public class MemberModel {
    private String fullName;
    private String memberOf;
    private String roleType;
    private String memberId;
    private String username;

    public boolean isHasPermissionReview() {
        return hasPermissionReview;
    }

    public void setHasPermissionReview(boolean hasPermissionReview) {
        this.hasPermissionReview = hasPermissionReview;
    }

    private boolean hasPermissionReview = false;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(String memberOf) {
        this.memberOf = memberOf;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
