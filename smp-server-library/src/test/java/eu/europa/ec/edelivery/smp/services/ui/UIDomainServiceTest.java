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
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.services.AbstractServiceTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class UIDomainServiceTest extends AbstractServiceTest {

    @Autowired
    UIDomainService testInstance;
    @Autowired
    DomainDao domainDao;

    @Before
    public void prepareDatabase() {
        testUtilsDao.clearData();
        testUtilsDao.createResourceDefinitionsForDomains();
    }

    @Test
    public void getAllDomains() {
        List<DomainRO> domainROS = testInstance.getAllDomains();
        assertEquals(2, domainROS.size());
    }

    @Test
    public void updateDomainData() {

        DomainRO domainRO = new DomainRO();
        domainRO.setDomainCode("NewCode");
        domainRO.setVisibility(VisibilityType.INTERNAL);
        domainRO.setSignatureKeyAlias("NewAlias");
        domainRO.setDefaultResourceTypeIdentifier("New ID");
        DBDomain domain = testUtilsDao.getD1();
        testInstance.updateBasicDomainData(domain.getId(), domainRO);
        DBDomain result = domainDao.find(domain.getId());

        assertEquals(domainRO.getDomainCode(), result.getDomainCode());
        assertEquals(domainRO.getVisibility(), result.getVisibility());
        assertEquals(domainRO.getSignatureKeyAlias(), result.getSignatureKeyAlias());
        assertEquals(domainRO.getDefaultResourceTypeIdentifier(), result.getDefaultResourceTypeIdentifier());
    }

    @Test
    public void updateSMLDomainData() {

        DomainRO domainRO = new DomainRO();
        domainRO.setSmlSubdomain("New SmlSubdomain");
        domainRO.setSmlSmpId("NewSmlSmpId");
        domainRO.setSmlClientKeyAlias("NewClientKeyAlias");
        domainRO.setSmlClientCertAuth(false);
        DBDomain domain = testUtilsDao.getD1();
        testInstance.updateDomainSmlIntegrationData(domain.getId(), domainRO);
        DBDomain result = domainDao.find(domain.getId());

        assertEquals(domainRO.getSmlSubdomain(), result.getSmlSubdomain());
        assertEquals(domainRO.getSmlSmpId(), result.getSmlSmpId());
        assertEquals(domainRO.getSmlClientKeyAlias(), result.getSmlClientKeyAlias());
        assertEquals(domainRO.isSmlClientCertAuth(), result.isSmlClientCertAuth());
    }

    @Test
    public void updateDomainResourceListClear() {
        DBDomain testDomain = testUtilsDao.getD1();
        DomainRO domainRO = testInstance.getDomainData(testDomain.getId());
        assertFalse(domainRO.getResourceDefinitions().isEmpty());
        testInstance.updateResourceDefDomainList(testDomain.getId(), Collections.emptyList());

        DomainRO result = testInstance.getDomainData(testDomain.getId());
        assertTrue(result.getResourceDefinitions().isEmpty());
    }

    @Test
    public void updateDomainResourceListAddNew() {
        DBDomain testDomain = testUtilsDao.getD2();
        DomainRO domainRO = testInstance.getDomainData(testDomain.getId());
        String restDef2 = testUtilsDao.getResourceDefCpp().getIdentifier();

        List<String> existingList = domainRO.getResourceDefinitions();
        assertFalse(existingList.contains(restDef2));
        existingList.add(restDef2);
        testInstance.updateResourceDefDomainList(testDomain.getId(), existingList);

        DomainRO result = testInstance.getDomainData(testDomain.getId());
        assertTrue(result.getResourceDefinitions().contains(restDef2));
    }

    @Test
    public void deleteDomain() {
        DBDomain domain = testUtilsDao.getD1();
        DBDomain test = domainDao.find(domain.getId());
        assertNotNull(test);
        testInstance.deleteDomain(domain.getId());

        DBDomain result = domainDao.find(domain.getId());
        assertNull(result);
    }

}
