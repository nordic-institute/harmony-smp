package rest;

import com.sun.jersey.api.client.ClientResponse;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.TestRunData;

/**
 * Rest client for group actions
 */
public class KeystoreClient extends BaseRestClient {
    private final static Logger LOG = LoggerFactory.getLogger(KeystoreClient.class);

    public JSONArray getAllKeystores() {
        startSession();
        String createResourcePath = RestServicePaths.getKeystorePath(TestRunData.getInstance().getUserId());
        ClientResponse response = requestGet(resource.path(createResourcePath));
        if (response.getStatus() != 200) {
            try {
                throw new SMPRestException("Could not create resource!", response);
            } catch (SMPRestException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.debug("Resource have been added!");
        String rawStringResponse = response.getEntity(String.class);
        return new JSONArray(rawStringResponse);
    }

}
