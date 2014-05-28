package eu.europa.ec.cipa.dispatcher.util;

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
        if (requestingHost.equals("158.169.9.13"))
            return new PasswordAuthentication(user, password.toCharArray());
        else
            return null;
    }
}