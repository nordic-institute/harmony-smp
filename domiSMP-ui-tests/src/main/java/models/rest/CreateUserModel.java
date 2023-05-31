package models.rest;


public class CreateUserModel {

    private String userId;
    private String username;
    private boolean active;
    private String role;
    private String emailAddress;
    private String fullName;
    private String smpTheme;
    private String smpLocale;

    public CreateUserModel(String username, boolean active, String role, String emailAddress, String fullName, String smpTheme, String smpLocale) {
        this.username = username;
        this.active = active;
        this.role = role;
        this.emailAddress = emailAddress;
        this.fullName = fullName;
        this.smpTheme = smpTheme;
        this.smpLocale = smpLocale;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String email) {
        this.emailAddress = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSmpTheme() {
        return smpTheme;
    }

    public void setSmpTheme(String smpTheme) {
        this.smpTheme = smpTheme;
    }


    public String getSmpLocale() {
        return smpLocale;
    }

    public void setSmpLocale(String smpLocale) {
        this.smpLocale = smpLocale;
    }
}

