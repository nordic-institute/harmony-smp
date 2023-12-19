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
package eu.europa.ec.edelivery.smp.data.ui.databind;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Class test deserialization of the SMPAuthority.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPAuthorityDeserializerTest {

    @Test
    public void deserialize() throws IOException {
        String value = "{\"status\":0,\"index\":0,\"actionMessage\":null,\"userId\":\"hsAkhiqJp1o89VZ4iBtmLnEM2vkb5FJTt0vWEUIxOw\",\"username\":\"user\",\"active\":true,\"role\":\"USER\",\"emailAddress\":\"user@mail-example.local\",\"fullName\":null,\"smpTheme\":null,\"smpLocale\":null,\"casAuthenticated\":false,\"casUserDataUrl\":null,\"passwordExpireOn\":null,\"sequentialLoginFailureCount\":0,\"lastFailedLoginAttempt\":null,\"suspendedUtil\":null,\"passwordUpdatedOn\":null,\"authorities\":[\"ROLE_USER\"],\"statusPassword\":0,\"passwordExpired\":true,\"showPasswordExpirationWarning\":false,\"forceChangeExpiredPassword\":false}";
        ObjectMapper mapper = new ObjectMapper();
        UserRO userRO = mapper.readValue(value, UserRO.class);

        assertNotNull(userRO);
        assertNotNull(userRO.getAuthorities());
        assertEquals(1, userRO.getAuthorities().size());
        assertEquals(SMPAuthority.S_AUTHORITY_USER.getAuthority(), userRO.getAuthorities().toArray(new SMPAuthority[]{})[0].getAuthority());
    }
}
