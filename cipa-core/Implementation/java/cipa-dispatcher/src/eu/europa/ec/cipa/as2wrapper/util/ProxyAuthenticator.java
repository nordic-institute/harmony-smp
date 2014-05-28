package eu.europa.ec.cipa.as2wrapper.util;

import java.net.Authenticator;
import java.net.PasswordAuthentication;


public class ProxyAuthenticator extends Authenticator
{

    private String user, password;

    public ProxyAuthenticator(String user, String password)
    {
        this.user = user;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication()
    {
        String requestingHost = getRequestingHost();
        if (requestingHost.equals("158.169.9.13"))                                 //TODO: analyze if it's intereesting to offer this proxyauthenticator to the users, and how to make it configurable (currently configurable in jetty.xml, and I think it should stay there)
            return new PasswordAuthentication(user, password.toCharArray());
        else
            return null;
    }
}