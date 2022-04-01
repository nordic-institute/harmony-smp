package eu.europa.ec.edelivery.smp.data.ui;

import java.io.Serializable;


/**
 * Password change request
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class PasswordChangeRO implements Serializable  {
    String currentPassword;
    String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
