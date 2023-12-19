/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
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
    private static final long serialVersionUID = 9008583888835630022L;
    private String version;
    private String ssoAuthenticationLabel;
    private String ssoAuthenticationURI;
    private String contextPath;
    private final List<String> authTypes = new ArrayList<>();

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
