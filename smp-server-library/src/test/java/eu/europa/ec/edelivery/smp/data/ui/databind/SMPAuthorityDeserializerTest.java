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
    @Ignore
    public void deserialize() throws IOException {
        String value = "{\"username\":\"smp\",\"password\":null,\"emailAddress\":null,\"authorities\":[\"ROLE_USER\"],\"active\":true,\"role\":\"ROLE_USER\",\"certificate\":null,\"statusPassword\":0,\"passwordExpired\":true}";
        ObjectMapper mapper = new ObjectMapper();
        UserRO userRO = mapper.readValue(value, UserRO.class);

        assertNotNull(userRO);
        assertNotNull(userRO.getAuthorities());
        assertEquals(1, userRO.getAuthorities().size());
        assertEquals(SMPAuthority.S_AUTHORITY_USER.getAuthority(), userRO.getAuthorities().toArray(new SMPAuthority[]{})[0].getAuthority());

    }
}
