package rest;

import com.sun.jersey.api.client.ClientResponse;
import org.json.JSONObject;
import rest.models.CreateUserModel;

public class UserClient extends BaseRestClient {
    public UserClient(String username, String password) {
        super(username, password);
    }

    public JSONObject createUser(CreateUserModel user) {

        JSONObject usrObj = new JSONObject(user);

        if (!isLoggedIn()) {
            try {
                refreshCookies();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String usersPath = RestServicePaths.getUsersPath(data.userId);

        ClientResponse response = jsonPUT(resource.path(usersPath), usrObj);
        JSONObject responseBody = new JSONObject(response.getEntity(String.class));
        // extract userId to be used in the Paths of the requests

        String forUserId = (String) responseBody.get("userId");

        //Set password for user
        changePassword(forUserId, data.getNewPassword());

        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not create user", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("User: " + user.getUsername() + "  has been created successfully!");
        return responseBody;

    }

    public JSONObject changePassword(String forUserId, String newPassword) {


        String changePasswordPath = RestServicePaths.getChangePasswordPath(data.userId, forUserId);
        JSONObject passwordChangeBody = new JSONObject();
        passwordChangeBody.put("currentPassword", password);
        passwordChangeBody.put("newPassword", newPassword);


        ClientResponse response = jsonPUT(resource.path(changePasswordPath), passwordChangeBody);
        JSONObject responseBody = new JSONObject(response.getEntity(String.class));
        return responseBody;

    }


}
