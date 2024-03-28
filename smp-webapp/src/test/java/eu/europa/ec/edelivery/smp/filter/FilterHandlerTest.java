/*-
 * #START_LICENSE#
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.Parser;

import java.net.URLEncoder;

/**
 * @author Sebastian-Ion TINCU
 * @since 5.1
 */
public class FilterHandlerTest {

    private static final Logger LOG = LoggerFactory.getLogger(FilterHandlerTest.class);

    private Parser<?> filterHandlerParser;

    @BeforeEach
    public void setup() {
        filterHandlerParser = new FilterHandler().getParser(null, null);

    }

    @ParameterizedTest
    @CsvSource({
            "'%','\\%'",        // %
            "'\\','\\\\'",      // \
            "'_','\\_'",        // _
            "'''','\\'''",      // '
            "'\"','\\\"'",      // "
            "'[','\\['",        // [
            "']','\\]'",        // ]
    })
    public void getEscapedDecodedFilterValue(String unencodedFilterValue, String expectedParsedFilterValue) throws Exception {
        // GIVEN
        String encodedFilterValue = URLEncoder.encode(unencodedFilterValue);
        LOG.info("Testing if the original filter value [{}], encoded as [{}] can be decoded successfully to [{}] " +
                "having its SQL query parameters escaped", unencodedFilterValue, encodedFilterValue, expectedParsedFilterValue);

        // WHEN
        Object result = filterHandlerParser.parse(encodedFilterValue, null);

        // THEN
        Assertions.assertEquals(expectedParsedFilterValue, result,
                "Should have parsed the [" + encodedFilterValue + "] correctly to [" + expectedParsedFilterValue
                        + "], decoding the original filter value and escaping any special SQL query characters that are to be used in a LIKE statement" );
    }
}