package rest;

public class RestServicePaths {
    private RestServicePaths() {
    }

    public static final String LOGIN = "/public/rest/security/authentication";
    public static final String CONNECTED = "/public/rest/security/user";


    public static String getUsersPath(String currentUserId) {

        return "/internal/rest/user/" + currentUserId + "/create";
    }

    public static String getChangePasswordPath(String currentUserId, String forUserId) {

        return "/internal/rest/user/" + currentUserId + "/change-password-for/" + forUserId;
    }


}
