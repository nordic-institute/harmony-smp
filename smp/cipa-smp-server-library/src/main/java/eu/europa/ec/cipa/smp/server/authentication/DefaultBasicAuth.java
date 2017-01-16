package eu.europa.ec.cipa.smp.server.authentication;

import com.helger.web.http.basicauth.BasicAuthClientCredentials;

/**
 * Created by rodrfla on 16/01/2017.
 */
public class DefaultBasicAuth extends BasicAuthClientCredentials {

    private boolean isServiceGroupOwner;

    public DefaultBasicAuth(String username, boolean isServiceGroupOwner) {
        super(username);
        this.isServiceGroupOwner = isServiceGroupOwner;
    }

    public DefaultBasicAuth(String username, String password, boolean isServiceGroupOwner) {
        super(username, password);
        this.isServiceGroupOwner = isServiceGroupOwner;
    }

    public boolean isServiceGroupOwner() {
        return isServiceGroupOwner;
    }
}
