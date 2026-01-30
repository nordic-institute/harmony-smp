/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2025 - 2025 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.services.resource;

import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.dao.TestUtilsDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.SMPExceptionLanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the DomainResolverService class.
 * This class tests the domain resolution logic based on header and path parameters.
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
class DomainResolverServiceTest extends AbstractJunit5BaseDao {
    private static final Logger LOG = LoggerFactory.getLogger(DomainResolverServiceTest.class);
    @Autowired
    protected TestUtilsDao testUtilsDao;
    @Autowired
    ConfigurationDao configurationDao;
    @Autowired
    DomainResolverService testInstance;
    @Autowired
    SMPExceptionLanguageService smpExceptionLanguageService;


    @BeforeEach
    void setUp() {
        testUtilsDao.clearData();
        // Initialize the domains
        testUtilsDao.createDomain("firstDomain");
        testUtilsDao.createDomain("headerDomain");
        testUtilsDao.createDomain("pathDomain");
        testUtilsDao.createDomain("defaultDomain");
    }

    @ParameterizedTest
    @CsvSource({
            //desc,  headerParameter, pathParameter, expectedDomainCode, expectedException
            "'Wrong header domain', DomainNotExist,,,Invalid domain [DomainNotExist]!",
            "'Wrong header domain even if path domain is OK', DomainNotExist,pathDomain,,Invalid domain [DomainNotExist]!",
            "'Wrong header domain even if path and default domain are OK', DomainNotExist,pathDomain,defaultDomain,Invalid domain [DomainNotExist]!"
    })
    void testResolveDomainNegativeFlows(String desc, String headerParameter, String pathParameter, String dbDefaultDomainProperty, String expectedException) {
        LOG.info("Running test: [{}]", desc);
        // given
        configurationDao.setPropertyToDatabase(
                SMPPropertyEnum.DEFAULT_DOMAIN.getProperty(), dbDefaultDomainProperty);
        configurationDao.reloadPropertiesFromDatabase();
        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.resolveDomain(headerParameter, pathParameter));
        // Assert that the exception is thrown with the expected error code
        assertEquals(ErrorCode.DOMAIN_NOT_EXISTS, result.getErrorCode());
        assertThat(result.getMessage(), containsString(expectedException));
    }

    @ParameterizedTest
    @CsvSource({
            // desc, headerParameter, pathParameter, expectedDomainCode, the default domain code
            "'Return header domain', headerDomain,,headerDomain,",
            "'The header domain has priority', headerDomain,pathDomain,headerDomain,",
            "'The header domain has priority even if default domain is given', headerDomain,pathDomain,headerDomain,defaultDomain",
            "'Return path domain', ,pathDomain,pathDomain,",
            "'The path domain has priority before defaultDomain', ,pathDomain,pathDomain,defaultDomain",
            "'Returns the first domain when none is specified.', ,,firstDomain,",
            "'Returns the default domain when no header/path domain are specified.', ,,defaultDomain,defaultDomain",
            "'Returns the default domain when pathDomain is not found specified', ,NotADomainCode,defaultDomain,defaultDomain",
    })
    void testResolveDomainPositiveFlow(String desc, String headerParameter, String pathParameter, String expectedDomainCode, String dbDefaultDomainProperty) {
        LOG.info("Running test: [{}]", desc);
        // given
        configurationDao.setPropertyToDatabase(
                SMPPropertyEnum.DEFAULT_DOMAIN.getProperty(), dbDefaultDomainProperty);
        configurationDao.reloadPropertiesFromDatabase();
        // when
        DBDomain result = testInstance.resolveDomain(headerParameter, pathParameter);
        // Assert that the domain code matches the expected value
        assertEquals(expectedDomainCode, result.getDomainCode());
    }
}