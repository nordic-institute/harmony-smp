package rest;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.TestRunData;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import java.util.HashMap;
import java.util.List;

public class BaseRestClient {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected TestRunData data =  TestRunData.getInstance();

    protected Client client = Client.create();
    public WebResource resource = client.resource(data.getUiBaseUrl());

    protected List<NewCookie> cookies;
    protected String token;
    protected String username;
    protected String password;

    public BaseRestClient(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public BaseRestClient() {
        this.username = data.getAdminUser().get("username");
        this.password = data.getAdminUser().get("password");
    }

    //	---------------------------------------Default request methods -------------------------------------------------
    protected ClientResponse requestPUT(WebResource resource, JSONObject body, String type) {

        startSession();
        WebResource.Builder builder = decorateBuilder(resource);

        return builder.type(type).put(ClientResponse.class, body.toString());
    }

    protected ClientResponse requestPUT(WebResource resource, String body, String type) {

        startSession();
        WebResource.Builder builder = decorateBuilder(resource);

        return builder.type(type).put(ClientResponse.class, body);
    }

    protected ClientResponse jsonPUT(WebResource resource, JSONObject body) {
        return requestPUT(resource, body, MediaType.APPLICATION_JSON);
    }

    protected ClientResponse jsonPUT(WebResource resource, String body) {
        return requestPUT(resource, body, MediaType.APPLICATION_JSON);
    }

    protected ClientResponse requestPOST(WebResource resource, String body) {
        startSession();
        WebResource.Builder builder = decorateBuilder(resource);

        return builder.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, body);
    }

    // -------------------------------------------- Login --------------------------------------------------------------

    protected WebResource.Builder decorateBuilder(WebResource resource) {

        WebResource.Builder builder = resource.getRequestBuilder();
        cookies = TestRunData.getInstance().getCookies();
        if (null != cookies) {
            for (NewCookie cookie : cookies) {
                builder = builder.cookie(new Cookie(cookie.getName(), cookie.getValue(), "/", ""));
            }
        } else {
            log.error("No cookie are present in the builder");
        }
        if (null != TestRunData.getInstance().getXSRFToken()) {
            builder = builder.header("X-XSRF-TOKEN", TestRunData.getInstance().getXSRFToken());
        }

        return builder;
    }

    private void createNewSession() throws Exception {
        log.debug("Rest client using to login: " + this.username);
        HashMap<String, String> params = new HashMap<>();
        params.put("username", this.username);
        params.put("password", this.password);

        ClientResponse response = resource.path(RestServicePaths.LOGIN).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, new JSONObject(params).toString());
        JSONObject responseBody = new JSONObject(response.getEntity(String.class));

        if (response.getStatus() == 200) {
            // extract userId to be used in the Paths of the requests
            data.setUserId((String) responseBody.get("userId"));
            log.debug(String.format("UserID: %s is stored!", TestRunData.getInstance().getUserId()));

            data.setCookies(response.getCookies());
            log.debug("Cookies are stored!");

            if (null != TestRunData.getInstance().getCookies()) {
                token = extractToken();
            } else {
                throw new Exception("Could not login, COOKIES are not found!");
            }
        } else {
            throw new SMPRestException("Login failed", response);

        }

    }

    private String extractToken() {
        String mytoken = null;
        for (NewCookie cookie : TestRunData.getInstance().getCookies()) {
            if (StringUtils.equalsIgnoreCase(cookie.getName(), "XSRF-TOKEN")) {
                mytoken = cookie.getValue();

            }
        }
        data.setXSRFToken(mytoken);
        log.debug("XSRF-Token " + mytoken + " has been stored!");
        return mytoken;
    }

    private boolean isLoggedIn() {

        WebResource.Builder builder = decorateBuilder(resource.path(RestServicePaths.CONNECTED));
        int response = builder.get(ClientResponse.class).getStatus();

        if (response != 200) {
            log.debug("Connected endpoint returns " + response);

        }
        return (!(response == 401));
    }

    public void startSession() {
        if (!isLoggedIn()) {
            try {
                createNewSession();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
