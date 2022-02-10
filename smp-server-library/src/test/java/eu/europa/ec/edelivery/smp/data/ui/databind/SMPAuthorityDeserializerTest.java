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
        String value = "{\"username\":\"smp\",\"password\":null,\"emailAddress\":null,\"authorities\":[\"ROLE_SMP_ADMIN\"],\"active\":true,\"role\":\"SMP_ADMIN\",\"id\":8,\"certificate\":null,\"statusPassword\":0,\"passwordExpired\":true}";
        ObjectMapper mapper = new ObjectMapper();
        UserRO userRO = mapper.readValue(value, UserRO.class);

        assertNotNull(userRO);
        assertNotNull(userRO.getAuthorities());
        assertEquals(userRO.getAuthorities().size(), 1);
        assertEquals(SMPAuthority.S_AUTHORITY_SMP_ADMIN.getAuthority(), userRO.getAuthorities().toArray(new SMPAuthority[]{})[0].getAuthority());

    }
}