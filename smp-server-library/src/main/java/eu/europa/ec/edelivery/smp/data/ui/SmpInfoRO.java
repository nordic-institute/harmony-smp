package eu.europa.ec.edelivery.smp.data.ui;

import java.io.Serializable;
import java.util.List;

public class SmpInfoRO implements Serializable {
    private static final long serialVersionUID = -49712226560325302L;
    String version;
    boolean smlIntegrationOn;
    boolean smlParticipantMultiDomainOn;
    String ssoAuthenticationLabel;
    String contextPath;
    List<String> authTypes;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isSmlIntegrationOn() {
        return smlIntegrationOn;
    }

    public void setSmlIntegrationOn(boolean smlIntegrationOn) {
        this.smlIntegrationOn = smlIntegrationOn;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public boolean isSmlParticipantMultiDomainOn() {
        return smlParticipantMultiDomainOn;
    }

    public void setSmlParticipantMultiDomainOn(boolean smlParticipantMultidomainOn) {
        this.smlParticipantMultiDomainOn = smlParticipantMultidomainOn;
    }

    public String getSsoAuthenticationLabel() {
        return ssoAuthenticationLabel;
    }

    public void setSsoAuthenticationLabel(String ssoAuthenticationLabel) {
        this.ssoAuthenticationLabel = ssoAuthenticationLabel;
    }

    public List<String> getAuthTypes() {
        return authTypes;
    }

    public void setAuthTypes(List<String> authTypes) {
        this.authTypes = authTypes;
    }
}
