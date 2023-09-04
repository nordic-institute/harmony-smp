package rest;

import com.sun.jersey.api.client.ClientResponse;
import org.json.JSONObject;
import rest.models.DomainModel;

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
                refreshCookies();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String createDomainPath = RestServicePaths.getCreateDomainPath(data.userId);

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


    public JSONObject AddMembersToDomain(String domainId, String username, String roleType) {

        JSONObject json = new JSONObject();
        json.put("memberOf", "DOMAIN");
        json.put("username", username);
        json.put("roleType", roleType);


        JSONObject membersJson = new JSONObject(json);

        if (!isLoggedIn()) {
            try {
                refreshCookies();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String addMemberPath = RestServicePaths.getDomainAddMemberPath(data.userId, domainId);

        ClientResponse response = jsonPUT(resource.path(addMemberPath), membersJson);
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not create domain", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("Domain: " + "" + "  has been created successfully!");
        return membersJson;
    }

}
