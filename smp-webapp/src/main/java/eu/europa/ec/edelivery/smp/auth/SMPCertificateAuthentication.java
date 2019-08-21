/**
 * (C) Copyright 2018 - European Commission | CEF eDelivery
 * <p>
 * Licensed under the EUPL, Version 1.2 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * \BDMSL\bdmsl-parent-pom\LICENSE-EUPL-v1.2.pdf or https://joinup.ec.europa.eu/sites/default/files/custom-page/attachment/eupl_v1.2_en.pdf
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SMPCertificateAuthentication implements Authentication {

    PreAuthenticatedCertificatePrincipal principal;
    DBUser dbUser;

    List<GrantedAuthority> listAuthorities = new ArrayList<>();
    boolean isAuthenticated;
    private static final int SERIAL_PADDING_SIZE =16;


    public SMPCertificateAuthentication(PreAuthenticatedCertificatePrincipal principal, List<GrantedAuthority> listAuthorities, DBUser user) {
        this.principal = principal;
        this.listAuthorities.addAll(listAuthorities);
        this.dbUser = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return listAuthorities;
    }

    @Override
    public Object getCredentials() {
        return this.principal!=null?this.principal.getCredentials():null;
    }

    @Override
    public Object getDetails() {
        return this.principal;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        isAuthenticated = b;
    }

    @Override
    public String getName() {
        return principal.getName(SERIAL_PADDING_SIZE);
    }

    @Override
    public String toString() {
        return getName();
    }


}
