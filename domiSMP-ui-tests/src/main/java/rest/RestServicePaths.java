package rest;

public class RestServicePaths {
    private static final String CONTEXT_PATH_PUBLIC = "/public/rest/";
    private static final String CONTEXT_PATH_INTERNAL = "/internal/rest/";
    private static final String CONTEXT_PATH_EDIT = "/edit/rest/";
    private static final String CONTEXT_PATH_INTERNAL_USER = CONTEXT_PATH_INTERNAL + "user";
    private static final String CONTEXT_PATH_INTERNAL_DOMAIN = CONTEXT_PATH_INTERNAL + "domain";
    public static final String LOGIN = CONTEXT_PATH_PUBLIC +"security/authentication";
    public static final String CONNECTED =CONTEXT_PATH_PUBLIC + "security/user";

    private RestServicePaths() {
    }

    public static String getUsersPath(String currentUserId) {

        return CONTEXT_PATH_INTERNAL_USER + "/" + currentUserId + "/create";
    }

    public static String getChangePasswordPath(String currentUserId, String forUserId) {

        return CONTEXT_PATH_INTERNAL_USER + "/" + currentUserId + "/change-password-for/" + forUserId;
    }

    //Domains paths

    public static String getCreateDomainPath(String currentUserId) {

        return CONTEXT_PATH_INTERNAL_DOMAIN +"/" + currentUserId + "/create";
    }

    public static String getDomainAddMemberPath(String currentUserId, String domainId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/domain/" + domainId + "/member/put";
    }

    public static String getAddResourcePath(String currentUserId, String domainId) {

       return CONTEXT_PATH_INTERNAL_DOMAIN +"/" + currentUserId +"/" +domainId + "/update-resource-types";
        //return String.format("/internal/rest/domain/%userId/%domainId/update-resource-types", currentUserId, domainId);
    }


    //Groups
    public static String getCreateGroupPath(String currentUserId, String domainId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/domain/" + domainId + "/group/create";
    }

    public static String getGroupAddMemberPath(String currentUserId, String domainId, String groupId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/domain/" + domainId + "/group/" + groupId + "/member/put";
    }

}
