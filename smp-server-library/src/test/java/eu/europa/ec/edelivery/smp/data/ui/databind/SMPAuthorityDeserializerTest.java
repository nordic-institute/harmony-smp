package eu.europa.ec.edelivery.smp.data.ui.databind;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import org.junit.Ignore;
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
