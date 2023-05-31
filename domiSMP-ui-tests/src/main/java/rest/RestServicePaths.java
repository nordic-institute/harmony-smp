package rest;

import utils.TestRunData;

public class RestServicePaths {
    public static final String LOGIN = "/public/rest/security/authentication";
    public static final String CONNECTED = "/public/rest/security/user";
    protected TestRunData data = new TestRunData();

    public String getUsersPath() {

        return "/internal/rest/user/" + data.getUserId() + "/create";
    }

}
