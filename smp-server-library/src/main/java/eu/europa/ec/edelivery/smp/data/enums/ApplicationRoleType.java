/*-
 * #%L
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.data.enums;

import eu.europa.ec.edelivery.smp.data.ui.auth.SMPRole;

public enum ApplicationRoleType {
    USER(SMPRole.USER, SMPRole.WS_USER),
    SYSTEM_ADMIN(SMPRole.SYSTEM_ADMIN, SMPRole.WS_SYSTEM_ADMIN);


    SMPRole apiRole;
    SMPRole uiRole;

    ApplicationRoleType(SMPRole uiRole, SMPRole apiRole) {
        this.uiRole = uiRole;
        this.apiRole = apiRole;
    }

    public String getAPIRole() {
        return "ROLE_" + apiName();
    }

    public String getUIRole() {
        return "ROLE_" + uiName();
    }

    public String apiName() {
        return apiRole.getCode();
    }

    public String uiName() {
        return uiRole.getCode();
    }


}
