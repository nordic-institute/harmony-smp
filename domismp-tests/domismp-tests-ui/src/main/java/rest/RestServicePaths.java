package rest;

public class RestServicePaths {
    private static final String CONTEXT_PATH_PUBLIC = "/public/rest/";
    private static final String CONTEXT_PATH_INTERNAL = "/internal/rest/";
    private static final String CONTEXT_PATH_EDIT = "/edit/rest/";
    private static final String CONTEXT_PATH_INTERNAL_USER = CONTEXT_PATH_INTERNAL + "user";
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

        return CONTEXT_PATH_INTERNAL + currentUserId + "/domain/create";
    }

    public static String getDomainAddMemberPath(String currentUserId, String domainId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/domain/" + domainId + "/member/put";
    }

    public static String getAddResourcePath(String currentUserId, String domainId) {

       return CONTEXT_PATH_INTERNAL +"/" + currentUserId +"/domain/" +domainId + "/update-resource-types";
    }


    //Groups
    public static String getCreateGroupPath(String currentUserId, String domainId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/domain/" + domainId + "/group/create";
    }

    public static String getUpdateGroupPath(String currentUserId, String domainId, String groupId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/domain/" + domainId + "/group/" + groupId + "/update";
    }

    public static String getGroupAddMemberPath(String currentUserId, String domainId, String groupId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/domain/" + domainId + "/group/" + groupId + "/member/put";
    }

    //Resources

    public static String getCreateResourcePath(String currentUserId, String domainId, String groupId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/domain/" + domainId + "/group/" + groupId + "/resource/create";
    }

    public static String getResourceAddMemberPath(String currentUserId, String domainId, String groupId, String resourceId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/domain/" + domainId + "/group/" + groupId + "/resource/" + resourceId + "/member/put";
    }

    public static String getResourceMembers(String currentUserId, String domainId, String groupId, String resourceId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/domain/" + domainId + "/group/" + groupId + "/resource/" + resourceId + "/member";
    }

    public static String getResourceUpdatePath(String currentUserId, String domainId, String groupId, String resourceId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/domain/" + domainId + "/group/" + groupId + "/resource/" + resourceId + "/update";
    }

    public static String getDocumentPath(String currentUserId, String resourceId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/resource/" + resourceId + "/document";
    }

    public static String getReviewRequestPath(String currentUserId, String resourceId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/resource/" + resourceId + "/document/review-request";
    }

    public static String getReviewRejectPath(String currentUserId, String resourceId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/resource/" + resourceId + "/document/review-reject";
    }

    public static String getReviewApprovePath(String currentUserId, String resourceId) {

        return CONTEXT_PATH_EDIT + currentUserId + "/resource/" + resourceId + "/document/review-approve";
    }
    //Keystore
    public static String getKeystorePath(String currentUserId) {

        return CONTEXT_PATH_INTERNAL + "keystore/" + currentUserId;
    }

    //Properties

    public static String getPropertyPath() {

        return CONTEXT_PATH_INTERNAL + "property";


    }
}
