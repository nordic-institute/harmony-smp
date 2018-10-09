package eu.europa.ec.edelivery.smp.data.ui;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

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
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "isadmin")
    private boolean isAdmin;

    public UserRO(){

    }

    public UserRO(String userName, String password, boolean isAdmin) {
        this.userName = userName;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        return Objects.equals(userName, userRO.userName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userName);
    }
}
