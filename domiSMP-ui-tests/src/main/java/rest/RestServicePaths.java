package rest;

public class RestServicePaths {
    public static final String LOGIN = "/public/rest/security/authentication";
    public static final String CONNECTED = "/public/rest/security/user";

    private RestServicePaths() {
    }

    public static String getUsersPath(String currentUserId) {

        return "/internal/rest/user/" + currentUserId + "/create";
    }

    public static String getChangePasswordPath(String currentUserId, String forUserId) {

        return "/internal/rest/user/" + currentUserId + "/change-password-for/" + forUserId;
    }

    //Domains paths

    public static String getCreateDomainPath(String currentUserId) {

        return "/internal/rest/domain/" + currentUserId + "/create";
    }

    public static String getDomainAddMemberPath(String currentUserId, String domainId) {

        return "/edit/rest/" + currentUserId + "/domain/" + domainId + "/member/put";
    }

    public static String getAddResourcePath(String currentUserId, String domainId) {
        return String.format("/internal/rest/domain/%userId/%domainId/update-resource-types", currentUserId, domainId);
    }


}
