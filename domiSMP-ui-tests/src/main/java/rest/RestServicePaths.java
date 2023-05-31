package rest;

import utils.TestRunData;

public class RestServicePaths {
    private RestServicePaths() {
    }

    public static final String LOGIN = "/public/rest/security/authentication";
    public static final String CONNECTED = "/public/rest/security/user";


    public static String getUsersPath(String currentUserId) {

        return "/internal/rest/user/" + currentUserId + "/create";
    }

}
