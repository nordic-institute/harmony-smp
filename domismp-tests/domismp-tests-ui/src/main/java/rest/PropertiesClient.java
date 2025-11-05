package rest;

import com.sun.jersey.api.client.ClientResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Rest client for group actions
 */
public class PropertiesClient extends BaseRestClient {
    private final static Logger LOG = LoggerFactory.getLogger(PropertiesClient.class);

    public JSONObject getProperty(String propertyName) throws Exception {
        startSession();


        HashMap<String, String> params = new HashMap<>();
        params.put("property", propertyName);
        ClientResponse clientResponse = requestGET(resource.path(RestServicePaths.getPropertyPath()), params);
        if (clientResponse.getStatus() != 200) {
            throw new Exception("Could not get properties ");
        }
        log.debug("Getting property value for " + propertyName);
        return new JSONObject(clientResponse.getEntity(String.class)).getJSONArray("serviceEntities").getJSONObject(0);

    }

    public void setPropertyValue(JSONObject propertyObject) throws Exception {
        startSession();
        JSONArray list = new JSONArray();
        list.put(propertyObject);

        ClientResponse clientResponse = jsonPUT(resource.path(RestServicePaths.getPropertyPath()), list.toString());
        if (clientResponse.getStatus() != 200) {
            throw new Exception("Could not get properties ");
        }
    }

}
