package rest;

import com.sun.jersey.api.client.ClientResponse;
import org.json.JSONObject;
import rest.models.DomainModel;

public class DomainClient extends BaseRestClient {

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

        String createDomainPath = RestServicePaths.getDomainPath(data.userId);

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


}
