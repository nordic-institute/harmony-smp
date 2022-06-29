package eu.europa.ec.edelivery.smp.data.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Public SmpInfoRO properties.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class SmpInfoRO implements Serializable {
    private static final long serialVersionUID = -49712226560325302L;
    private String version;
    private String ssoAuthenticationLabel;
    private String ssoAuthenticationURI;
    private String contextPath;
    private List<String> authTypes = new ArrayList<>();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getSsoAuthenticationLabel() {
        return ssoAuthenticationLabel;
    }

    public void setSsoAuthenticationLabel(String ssoAuthenticationLabel) {
        this.ssoAuthenticationLabel = ssoAuthenticationLabel;
    }

    public String getSsoAuthenticationURI() {
        return ssoAuthenticationURI;
    }

    public void setSsoAuthenticationURI(String ssoAuthenticationURI) {
        this.ssoAuthenticationURI = ssoAuthenticationURI;
    }

    public List<String> getAuthTypes() {
        return authTypes;
    }

    public void addAuthTypes(List<String> authTypes) {
        this.authTypes.addAll(authTypes);
    }
}
