package eu.europa.ec.edelivery.smp.data.ui;

import java.io.Serializable;

/**
 * @author Sebastian-Ion TINCU
 * @since 4.0.1
 */
public class LoginRO implements Serializable {

    private static final long serialVersionUID = 9008583888835630010L;

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
