package rest;

import com.sun.jersey.api.client.ClientResponse;
import models.rest.CreateUserModel;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Generator;

public class UsersClient extends BaseRestClient {
    public UsersClient(String username, String password) {
        super(username, password);
    }

    public void createUser(String username, String role, String email) {

        CreateUserModel createUserModel = new CreateUserModel(username, true, role, email, Generator.randomAlphaNumeric(6), "default_theme", "fr");
        JSONObject usrObj = new JSONObject(createUserModel);

        JSONArray payload = new JSONArray();
        payload.put(usrObj);



        if (!isLoggedIn()) {
            try {
                refreshCookies();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String usersPath = RestServicePaths.getUsersPath(data.userId);

        ClientResponse response = jsonPUT(resource.path(usersPath), usrObj);

        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not create user", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("User: " + username + "  has been created successfully!");
    }


}
