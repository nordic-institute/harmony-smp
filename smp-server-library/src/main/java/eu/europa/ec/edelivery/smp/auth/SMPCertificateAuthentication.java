/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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

package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
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
