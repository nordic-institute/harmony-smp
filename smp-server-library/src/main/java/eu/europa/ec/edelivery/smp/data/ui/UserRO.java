package eu.europa.ec.edelivery.smp.data.ui;


import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

import static eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths.MAX_USERNAME_LENGTH;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Entity
@Table(name = "smp_user")
public class UserRO implements Serializable {


    private static final long serialVersionUID = -4971552086560325302L;
    @Id
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "isadmin")
    private boolean isAdmin;

    public UserRO(){

    }

    public UserRO(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRO userRO = (UserRO) o;
        return Objects.equals(username, userRO.username);
    }

    @Override
    public int hashCode() {

        return Objects.hash(username);
    }
}
