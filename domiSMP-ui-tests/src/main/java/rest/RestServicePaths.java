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

    public static String getDomainPath(String currentUserId) {

        return "/internal/rest/domain/" + currentUserId + "/create";
    }


}
