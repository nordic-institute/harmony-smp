package rest;

import com.sun.jersey.api.client.ClientResponse;
import org.json.JSONObject;
import rest.models.DomainModel;
import utils.TestRunData;

public class DomainClient extends BaseRestClient {

    /**
     * Rest client for domain actions
     */
    public DomainClient() {
        super();
    }

    public JSONObject createDomain(DomainModel domainModel) {

        JSONObject domainJson = new JSONObject(domainModel);

        if (!isLoggedIn()) {
            try {
                createSession();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String createDomainPath = RestServicePaths.getCreateDomainPath(TestRunData.getUserId());

        ClientResponse response = jsonPUT(resource.path(createDomainPath), domainJson);
        JSONObject responseBody = new JSONObject(response.getEntity(String.class));
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not create domain", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("Domain: " + domainModel.getDomainCode() + "  has been created successfully!");
        return responseBody;

    }


    public JSONObject addMembersToDomain(String domainId, String username, String roleType) {

        JSONObject membersJson = new JSONObject();
        membersJson.put("memberOf", "DOMAIN");
        membersJson.put("username", username);
        membersJson.put("roleType", roleType);


        if (!isLoggedIn()) {
            try {
                createSession();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String addMemberPath = RestServicePaths.getDomainAddMemberPath(TestRunData.getUserId(), domainId);

        ClientResponse response = jsonPUT(resource.path(addMemberPath), membersJson);
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not create domain", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("Member: " + username + " has been added!");
        return membersJson;
    }

}
