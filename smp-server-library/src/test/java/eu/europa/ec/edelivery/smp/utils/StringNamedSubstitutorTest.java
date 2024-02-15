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
package eu.europa.ec.edelivery.smp.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringNamedSubstitutorTest {


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
}
