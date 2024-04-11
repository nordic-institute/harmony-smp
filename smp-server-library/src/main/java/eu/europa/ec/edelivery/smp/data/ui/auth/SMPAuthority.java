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
package eu.europa.ec.edelivery.smp.data.ui.auth;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.ui.databind.SMPAuthorityDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;


/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@JsonDeserialize(using = SMPAuthorityDeserializer.class)
public class SMPAuthority implements GrantedAuthority {

    // static constants for annotations!
    public static final String S_AUTHORITY_TOKEN_WS_SYSTEM_ADMIN = "ROLE_WS_SYSTEM_ADMIN";

    public static final String S_AUTHORITY_TOKEN_WS_USER= "ROLE_WS_USER";
    // ui
    public static final String S_AUTHORITY_TOKEN_SYSTEM_ADMIN = "ROLE_SYSTEM_ADMIN";
    public static final String S_AUTHORITY_TOKEN_USER = "ROLE_USER";

    // static constants for verification...
    public static final SMPAuthority S_AUTHORITY_SYSTEM_ADMIN = new SMPAuthority(SMPRole.SYSTEM_ADMIN.getCode());
    public static final SMPAuthority S_AUTHORITY_USER = new SMPAuthority(SMPRole.USER.getCode());
    public static final SMPAuthority S_AUTHORITY_ANONYMOUS = new SMPAuthority(SMPRole.ANONYMOUS.getCode());
    public static final SMPAuthority S_AUTHORITY_WS_USER = new SMPAuthority(SMPRole.WS_USER.getCode());
    public static final SMPAuthority S_AUTHORITY_WS_SYSTEM_ADMIN = new SMPAuthority(SMPRole.WS_SYSTEM_ADMIN.getCode());

    String role;

    private SMPAuthority(String role) {
        this.role = role;
    }

    @Override
    @JsonValue
    public String getAuthority() {
        return "ROLE_" + role;
    }

    public String getRole() {
        return role;
    }

    public static SMPAuthority getAuthorityByRoleName(String name) {
        if (StringUtils.isBlank(name)) {
            return S_AUTHORITY_ANONYMOUS;
        }

        SMPRole role = SMPRole.valueOf(name);
        return getAuthorityByRole(role);
    }

    public static SMPAuthority getAuthorityByRole(SMPRole role) {
        switch (role) {
            case USER:
                return S_AUTHORITY_USER;
            case WS_USER:
                return S_AUTHORITY_WS_USER;
            case SYSTEM_ADMIN:
                return S_AUTHORITY_SYSTEM_ADMIN;
            case WS_SYSTEM_ADMIN:
                return S_AUTHORITY_WS_SYSTEM_ADMIN;
            default:
                return S_AUTHORITY_ANONYMOUS;
        }
    }

    public static SMPAuthority getAuthorityByApplicationRole(ApplicationRoleType role) {
        switch (role) {
            case USER:
                return S_AUTHORITY_USER;
            case SYSTEM_ADMIN:
                return S_AUTHORITY_SYSTEM_ADMIN;
            default:
                return S_AUTHORITY_ANONYMOUS;
        }
    }

    @Override
    public String toString() {
        return role;
    }
}
