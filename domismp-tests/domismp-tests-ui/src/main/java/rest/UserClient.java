package rest;

import com.sun.jersey.api.client.ClientResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.models.UserModel;
import utils.TestRunData;

/**
 * Rest client for user actions
 */
public class UserClient extends BaseRestClient {
    private final static Logger LOG = LoggerFactory.getLogger(UserClient.class);


    public UserClient(String username, String password) {
        super(username, password);
    }
    public JSONObject createUser(UserModel user) {

        JSONObject usrObj = new JSONObject(user);
        startSession();
        String usersPath = RestServicePaths.getUsersPath(TestRunData.getInstance().getUserId());
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
        LOG.debug("User: " + user.getUsername() + "  has been created successfully!");
        return responseBody;

    }

    public JSONObject changePassword(String forUserId, String newPassword) {

        String changePasswordPath = RestServicePaths.getChangePasswordPath(TestRunData.getInstance().getUserId(), forUserId);
        JSONObject passwordChangeBody = new JSONObject();
        passwordChangeBody.put("currentPassword", password);
        passwordChangeBody.put("newPassword", newPassword);
        ClientResponse response = jsonPUT(resource.path(changePasswordPath), passwordChangeBody);

        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not create user", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.debug(" Password was changed successfully!");

        return new JSONObject(response.getEntity(String.class));
    }


}
