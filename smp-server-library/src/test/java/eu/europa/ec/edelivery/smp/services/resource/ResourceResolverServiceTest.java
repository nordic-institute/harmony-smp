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
package eu.europa.ec.edelivery.smp.services.resource;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.TEST_DOC_SCHEMA_2;
import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration(classes = {ResourceResolverService.class, ConversionTestConfig.class})
public class ResourceResolverServiceTest extends AbstractServiceIntegrationTest {


    @Autowired
    protected ResourceResolverService testInstance;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
    }

    @Test
    public void tesValidateRequestDataInvalid() {

        List<Object[]> faileTestData = Arrays.asList(
                new Object[]{new ResourceRequest(null, null, null, null), "Resource Location vector coordinates must not be null"},
                new Object[]{new ResourceRequest(null, null, Collections.emptyList(), null), "Resource Location vector coordinates must not be null"},
                new Object[]{new ResourceRequest(null, null, Arrays.asList("1", "2", "3", "4", "5", "6"), null), "More than max. count (5) of Resource Location vector coordinates!"},
                new Object[]{new ResourceRequest(null, null, Arrays.asList("1", "2", "3"), null), "Can not resolve resource for unknown domain!"}
        );

        for (Object[] testData : faileTestData) {
            ResourceRequest req = (ResourceRequest) testData[0];
            String expectedMessage = (String) testData[1];

            SMPRuntimeException runtimeException = assertThrows(SMPRuntimeException.class, () -> testInstance.validateRequestData(req));
            MatcherAssert.assertThat(runtimeException.getMessage(), CoreMatchers.containsString(expectedMessage));
        }
    }

    @Test
    public void testResolveAndAuthorizeRequestForResource() {
        // given
        SMPUserDetails user = new SMPUserDetails(null, null, null);
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        ResourceRequest req = createResourceRequest(resource);
        req.setAuthorizedDomain(testUtilsDao.getD1());

        // when
        ResolvedData result = testInstance.resolveAndAuthorizeRequest(user, req);
        // then
        assertNotNull(result);
        assertEquals(resource, result.getResource());
        assertNull(result.getSubresource());
    }

    @Test
    public void testResolveAndAuthorizeRequestForSubresource() {
        // given
        SMPUserDetails user = new SMPUserDetails(null, null, null);
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DBSubresource subresource = createSubresource(resource, testUtilsDao.getSubresourceDefSmpMetadata());

        // create request for subresource
        ResourceRequest req = createResourceRequest(subresource);
        req.setAuthorizedDomain(subresource.getResource().getDomainResourceDef().getDomain());

        // when
        ResolvedData result = testInstance.resolveAndAuthorizeRequest(user, req);

        // then
        assertNotNull(result);
        assertEquals(subresource.getResource(), result.getResource());
        assertEquals(subresource, result.getSubresource());
    }

    public DBSubresource createSubresource(DBResource resource, DBSubresourceDef subresourceDef) {
        DBDocument doc = testUtilsDao.createDocument(1, resource.getIdentifierValue(), resource.getIdentifierScheme());
        DBSubresource subresource = TestDBUtils.createDBSubresource(
                resource.getIdentifierValue(), resource.getIdentifierScheme(),
                UUID.randomUUID().toString(), TEST_DOC_SCHEMA_2);
        subresource.setDocument(doc);
        subresource.setSubresourceDef(subresourceDef);
        subresource.setResource(resource);
        testUtilsDao.persistFlushDetach(subresource);

        return subresource;
    }


    public static ResourceRequest createResourceRequest(DBResource resource) {
        return new ResourceRequest(ResourceAction.READ, null, Collections.singletonList(resource.getIdentifierScheme() + "::" + resource.getIdentifierValue()), null);
    }

    public static ResourceRequest createResourceRequest(DBSubresource subresource) {
        DBResource res = subresource.getResource();
        return new ResourceRequest(ResourceAction.READ, null,

                Arrays.asList((StringUtils.isNotBlank(res.getIdentifierScheme()) ? res.getIdentifierScheme() + "::" : "") + subresource.getResource().getIdentifierValue(),
                        subresource.getSubresourceDef().getUrlSegment(),
                        (StringUtils.isNotBlank(subresource.getIdentifierScheme()) ? subresource.getIdentifierScheme() + "::" : "") + subresource.getIdentifierValue()),
                null);
    }
}

