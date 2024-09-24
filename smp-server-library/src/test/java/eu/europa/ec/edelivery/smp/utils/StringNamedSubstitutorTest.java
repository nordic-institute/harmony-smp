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
package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.smp.spi.enums.TransientDocumentPropertyType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringNamedSubstitutorTest {

    private static final String  TEST_UTF8_STRING = "Test%C4%85%C3%B3%C5%BC%C4%99%C4%85%E1%BA%9E%C3%B6+Greek+%C3%80%C3%86%C3%87%C3%9F%C3%A3%C3%BF%CE%B1%CE%A9%C6%92%CE%91+char";
    // partially URL encoded service group with UTF8 characters.
    // test characters are URL encoded to "survive various development local settings  :)" to use this first url decode string!
    private static final String SERVICE_GROUP_WITH_UTF8 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">\n" +
            "    <ParticipantIdentifier scheme=\"${resource.identifier.scheme}\">${resource.identifier.value}</ParticipantIdentifier>\n" +
            "    <ServiceMetadataReferenceCollection/>\n" +
            "    <Extension>\n" +
            "        <ex:Test xmlns:ex=\"http://test.eu\">"+TEST_UTF8_STRING+"</ex:Test>\n" +
            "    </Extension>\n" +
            "</ServiceGroup>";

    @ParameterizedTest
    @CsvSource({
            "'The quick ${FOX_COLOR} fox jumps over the ${DOG_MODE} dog', " +
                    "'FOX_COLOR=red;DOG_MODE=slow', " +
                    "'The quick red fox jumps over the slow dog'",
            "'The quick ${fox_COLOR} fox jumps over the ${dog_MODE} dog', " +
                    "'FOX_COLOR=red;DOG_MODE=slow', " +
                    "'The quick red fox jumps over the slow dog'",
            "'The quick ${FOX_COLOR} fox jumps over the ${DOG_MODE} dog', " +
                    "'FOX_COLOR=red', " +
                    "'The quick red fox jumps over the ${DOG_MODE} dog'",
    })
    void resolve(String testString, String values, String expected) {
        Map<String, String> mapVal = Stream.of(values.split("\\s*;\\s*"))
                .map(s -> s.split("\\s*=\\s*"))
                .collect(HashMap::new, (m, v) -> m.put(v[0], v[1]), Map::putAll);

        String result = StringNamedSubstitutor.resolve(testString, mapVal);
        assertEquals(expected, result);
    }

    @Test
    void testTransientResolutionForResourceWithUTF8() throws UnsupportedEncodingException {
        Map<String, String> mapProperties = new HashMap<>();
        mapProperties.put(TransientDocumentPropertyType.RESOURCE_IDENTIFIER_VALUE.getPropertyName(), "value");
        mapProperties.put(TransientDocumentPropertyType.RESOURCE_IDENTIFIER_SCHEME.getPropertyName(), "scheme");
        String serviceGroupWithUt8 = URLDecoder.decode(SERVICE_GROUP_WITH_UTF8, "UTF-8");
        String testStringInMessage = URLDecoder.decode(TEST_UTF8_STRING, "UTF-8");
        // when
        System.out.println(serviceGroupWithUt8);
        String resolved = StringNamedSubstitutor.resolve(serviceGroupWithUt8, mapProperties);
        System.out.println(resolved);
        //then
        Assertions.assertThat(resolved)
                .doesNotContain(TransientDocumentPropertyType.SUBRESOURCE_IDENTIFIER_VALUE.getPropertyPlaceholder())
                .doesNotContain(TransientDocumentPropertyType.SUBRESOURCE_IDENTIFIER_SCHEME.getPropertyPlaceholder())
                .contains(testStringInMessage);
    }

    @Test
    void testTransientResolutionWithUTF8String() throws UnsupportedEncodingException {
        Map<String, String> mapProperties = new HashMap<>();
        mapProperties.put(TransientDocumentPropertyType.RESOURCE_IDENTIFIER_VALUE.getPropertyName(), "value");
        mapProperties.put(TransientDocumentPropertyType.RESOURCE_IDENTIFIER_SCHEME.getPropertyName(), "scheme");
        String serviceGroupWithUt8 = URLDecoder.decode(TEST_UTF8_STRING, "UTF-8");
        // when
        System.out.println(serviceGroupWithUt8);
        String resolved = StringNamedSubstitutor.resolve(serviceGroupWithUt8, mapProperties);
        System.out.println(resolved);
        //then
        assertEquals(serviceGroupWithUt8, resolved);
    }
}
