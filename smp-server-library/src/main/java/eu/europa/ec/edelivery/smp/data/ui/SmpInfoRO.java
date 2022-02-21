package eu.europa.ec.edelivery.smp.data.ui;

import java.io.Serializable;

public class SmpInfoRO implements Serializable {
    private static final long serialVersionUID = -49712226560325302L;
    String version;
    boolean smlIntegrationOn;
    boolean smlParticipantMultiDomainOn;
    boolean ssoAuthentication;
    String ssoAuthenticationLabel;
    String contextPath;

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

    public boolean isSsoAuthentication() {
        return ssoAuthentication;
    }

    public void setSsoAuthentication(boolean ssoAuthentication) {
        this.ssoAuthentication = ssoAuthentication;
    }

    public String getSsoAuthenticationLabel() {
        return ssoAuthenticationLabel;
    }

    public void setSsoAuthenticationLabel(String ssoAuthenticationLabel) {
        this.ssoAuthenticationLabel = ssoAuthenticationLabel;
    }
}
