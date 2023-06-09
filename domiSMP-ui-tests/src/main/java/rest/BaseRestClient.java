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
    protected TestRunData data = new TestRunData();

    protected Client client = Client.create();
    public WebResource resource = client.resource(data.getUiBaseUrl());

    protected List<NewCookie> cookies;
    protected String token;
    protected String username;
    protected String password;
    protected String newPassword;

    public BaseRestClient(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public BaseRestClient() {
        this.username = data.getAdminUser().get("username");
        this.password = data.getAdminUser().get("pass");
    }

    //	---------------------------------------Default request methods -------------------------------------------------
    protected ClientResponse requestPUT(WebResource resource, String params, String type) {

        if (!isLoggedIn()) {
            try {
                refreshCookies();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        WebResource.Builder builder = decorateBuilder(resource);

        return builder.type(type).put(ClientResponse.class, params);
    }

    protected ClientResponse requestPUT(WebResource resource, JSONObject body, String type) {

        if (!isLoggedIn()) {
            log.info("User is not loggedin");
            try {
                refreshCookies();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        WebResource.Builder builder = decorateBuilder(resource);

        return builder.type(type).put(ClientResponse.class, body.toString());
    }


    protected ClientResponse jsonPUT(WebResource resource, String params) {
        return requestPUT(resource, params, MediaType.APPLICATION_JSON);
    }

    protected ClientResponse jsonPUT(WebResource resource, JSONObject body) {
        return requestPUT(resource, body, MediaType.APPLICATION_JSON);
    }

    protected ClientResponse requestPOST(WebResource resource, String params, String type) {


        WebResource.Builder builder = decorateBuilder(resource);

        return builder.type(type).post(ClientResponse.class, params);
    }

    // -------------------------------------------- Login --------------------------------------------------------------

    protected WebResource.Builder decorateBuilder(WebResource resource) {

        WebResource.Builder builder = resource.getRequestBuilder();

        if (null != cookies) {
            log.debug("");
            for (NewCookie cookie : cookies) {
                builder = builder.cookie(new Cookie(cookie.getName(), cookie.getValue(), "/", ""));
                log.debug("cookie " + cookie + " is added to the builder");
            }
        }
        if (null != token) {
            builder = builder.header("X-XSRF-TOKEN", token);
        }

        return builder;
    }
    public List<NewCookie> login() throws SMPRestException {
        log.debug("Rest client using to login: " + this.username);
        HashMap<String, String> params = new HashMap<>();
        params.put("username", this.username);
        params.put("password", this.password);

        ClientResponse response = resource.path(RestServicePaths.LOGIN).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, new JSONObject(params).toString());

        JSONObject responseBody = new JSONObject(response.getEntity(String.class));
        // extract userId to be used in the Paths of the requests

        data.setUserId((String) responseBody.get("userId"));
        log.debug("Last Userid is " + data.getUserId());

        if (response.getStatus() == 200) {
            return response.getCookies();
        }
        throw new SMPRestException("Login failed", response);

    }
    private String extractToken() {
        String mytoken = null;
        for (NewCookie cookie : cookies) {
            if (StringUtils.equalsIgnoreCase(cookie.getName(), "XSRF-TOKEN")) {
                mytoken = cookie.getValue();
            }
        }
        return mytoken;
    }
    public void refreshCookies() throws Exception {
        if (isLoggedIn()) {
            return;
        }
        cookies = login();
        if (null != cookies) {
            token = extractToken();
        } else {
            throw new Exception("Could not login, tests will not be able to generate necessary data!");
        }

        if (null == token) {
            throw new Exception("Could not obtain XSRF token, tests will not be able to generate necessary data!");
        }
    }
    public boolean isLoggedIn() {
        WebResource.Builder builder = decorateBuilder(resource.path(RestServicePaths.CONNECTED));
        int response = builder.get(ClientResponse.class).getStatus();
        log.debug("Connected endpoint returns: " + response);
        log.debug("UserID is: " + data.getUserId());
        return (!(response == 401) && !data.getUserId().isEmpty());
    }

}
