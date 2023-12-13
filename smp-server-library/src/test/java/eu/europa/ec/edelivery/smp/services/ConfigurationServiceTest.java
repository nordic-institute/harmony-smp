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
package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SSO_CAS_SMP_USER_DATA_URL_PATH;
import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.SSO_CAS_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ConfigurationServiceTest {

    ConfigurationDao configurationDaoMock = mock(ConfigurationDao.class);
    ConfigurationService testInstance = new ConfigurationService(configurationDaoMock);

    @Test
    public void testGetCasUserDataURL() throws MalformedURLException {
        String casUrl = "http://test:123/path";
        String casUserDataPath = "userdata/data.hsp";
        doReturn(new URL(casUrl)).when(configurationDaoMock).getCachedPropertyValue(SSO_CAS_URL);
        doReturn(casUserDataPath).when(configurationDaoMock).getCachedPropertyValue(SSO_CAS_SMP_USER_DATA_URL_PATH);

        URL result = testInstance.getCasUserDataURL();
        assertNotNull(result);
        // expected - the same server but different context path
        assertEquals("http://test:123/" + casUserDataPath, result.toString());
    }
}
