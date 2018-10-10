package eu.europa.ec.edelivery.smp.data.ui;





import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class UserRO implements Serializable {


    private static final long serialVersionUID = -4971552086560325302L;
    private String username;
    private String password;
    private String email;
    LocalDateTime passwordChanged;
    private boolean active = true;
    private String role;

    public UserRO(){

    }


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(LocalDateTime passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
