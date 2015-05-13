package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "Authorization", strict = false)
public class Authorization implements Serializable {
    private static final long serialVersionUID = 746582737443013947L;

    public static final String USERNAME_TOKEN = "UsernameToken";
    public static final String SIGNATURE = "Signature";

    @Attribute
    protected String type;

    @Attribute(required = false)
    protected String username;

    @Attribute(required = false)
    protected String password;


    public Authorization(final String type, final String username, final String password) {
        if ((type != null) && type.equalsIgnoreCase(Authorization.USERNAME_TOKEN)) {
            this.type = Authorization.USERNAME_TOKEN;
        } else if ((type != null) && type.equalsIgnoreCase(Authorization.SIGNATURE)) {
            this.type = Authorization.SIGNATURE;
        }
        this.username = username;
        this.password = password;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        if ((type != null) && type.equalsIgnoreCase(Authorization.USERNAME_TOKEN)) {
            this.type = Authorization.USERNAME_TOKEN;
        } else if ((type != null) && type.equalsIgnoreCase(Authorization.SIGNATURE)) {
            this.type = Authorization.SIGNATURE;
        }
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean isUsernameToken() {
        return this.type.equalsIgnoreCase(Authorization.USERNAME_TOKEN);
    }
}