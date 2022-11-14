package eu.europa.ec.edelivery.smp.data.ui.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;

import java.io.IOException;

/**
 * Class for deserialize the SMPAuthority to string.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPAuthorityDeserializer extends StdDeserializer<SMPAuthority> {

    public SMPAuthorityDeserializer() {
        super(SMPAuthority.class);
    }

    @Override
    public SMPAuthority deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String text = node.asText();
        return SMPAuthority.getAuthorityByRoleName(text.substring("ROLE_".length()));
    }
}
