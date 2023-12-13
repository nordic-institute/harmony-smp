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
package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentVersion;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Purpose of class is to test database data. Class is created as a bean so that
 * annotated the method with @Transactional go through the proxy and no transaction is opened!
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class DBAssertion {

    @Autowired
    protected ResourceDao serviceGroupDao;

    @Transactional
    public void assertServiceGroupForOnlyDomain(String partId, String partSchema, String domainCode) {

        Optional<DBResource> optRes = serviceGroupDao.findServiceGroup(partId, partSchema);

        // then
        assertTrue(optRes.isPresent());
        DBResource dbServiceGroup = optRes.get();
        assertEquals(partId, dbServiceGroup.getIdentifierValue());
        assertEquals(partSchema, dbServiceGroup.getIdentifierScheme());
    }

    @Transactional
    public void assertServiceGroupExtensionEqual(String partId, String partSchema, byte[] expectedExt) {
        byte[] ext = getExtensionForServiceGroup(partId, partSchema);
        assertArrayEquals(expectedExt, ext);
    }

    @Transactional
    public byte[] getExtensionForServiceGroup(String partId, String partSchema) {
        DBResource sg = serviceGroupDao.findServiceGroup(partId, partSchema).get();
        DBDocumentVersion currentVersion = sg.getDocument() != null && !sg.getDocument().getDocumentVersions().isEmpty() ?
                sg.getDocument().getDocumentVersions().stream().filter(res -> res.getVersion() == sg.getDocument().getCurrentVersion()).findFirst().orElse(null) : null;
        return currentVersion != null ? currentVersion.getContent() : null;
    }

    @Transactional
    public Optional<DBResource> findAndInitServiceGroup(String partId, String partSchema) {
        Optional<DBResource> sg = serviceGroupDao.findServiceGroup(partId, partSchema);
        if (sg.isPresent()) {
/*
            sg.get().getExtension();
            sg.get().getUsers().size();
            sg.get().getResourceDomains().size();
            for (DBDomainResourceDef d : sg.get().getResourceDomains()) {
                d.getDomain().getDomainCode();
                d.getSubresourcesList().size();

            }*/

        }
        return sg;
    }
}
