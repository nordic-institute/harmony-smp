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
package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.user.DBResourceMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;

/**
 * Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

public abstract class AbstractResourceDaoTest extends AbstractBaseDao {
    @Autowired
    ResourceDao testInstance;

    @Autowired
    DomainDao domainDao;

    @Autowired
    DomainResourceDefDao domainResourceDefDao;

    @Autowired
    GroupDao groupDao;

    @Autowired
    ResourceDefDao resourceDefDao;

    @Autowired
    UserDao userDao;

    @Autowired
    ResourceMemberDao resourceMemberDao;


    @BeforeEach
    public void prepareDatabase() {
        testUtilsDao.clearData();
        // setup initial data!
        testUtilsDao.createResourceDefinitionsForDomains();
        testUtilsDao.createGroups();
        testUtilsDao.createUsers();
        testInstance.clearPersistenceContext();
    }

    public DBResource createAndSaveNewResource() {
        return createAndSaveNewResource(TEST_DOMAIN_CODE_1, TEST_GROUP_A, TestConstants.TEST_SG_ID_1, TestConstants.TEST_SG_SCHEMA_1, TEST_RESOURCE_DEF_SMP10_URL);
    }

    private DBResource createAndSaveNewResource(String domain, String group, String participantId, String participantSchema, String resourceDefSeg) {
        return createAndSaveNewResource(domain, group, participantId, participantSchema, resourceDefSeg,  null);
    }

    @Transactional
    public DBResource createAndSaveNewResource(String domainCode, String group, String participantId, String participantSchema, String resourceDefSeg, DBUser usr) {
        Optional<DBGroup> optGroup = groupDao.getGroupByNameAndDomainCode(group, domainCode);
        Optional<DBDomainResourceDef> optDomainResourceDef = domainResourceDefDao
                .getResourceDefConfigurationForDomainCodeAndResourceDefCtx(domainCode, resourceDefSeg);
        DBResource sg = TestDBUtils.createDBResource(participantId, participantSchema);
        sg.setGroup(optGroup.get());
        sg.setDomainResourceDef(optDomainResourceDef.get());

        if (usr != null) {
            sg.getMembers().add(new DBResourceMember(sg, usr));
        }
        //sg.addDomain(d);
        testInstance.persistFlushDetach(sg);
        return sg;
    }

    public void createAndSaveNewServiceGroups(int iCount, String domain,String group, String participant, String resourceDefSeg) {
        createAndSaveNewServiceGroups(iCount, domain, group, participant, resourceDefSeg, null);
    }

    @Transactional
    public void createAndSaveNewServiceGroups(int iCount, String domain, String group, String participant, String resourceDefSeg, DBUser usr) {
        int i = 0;
        while (i++ < iCount) {
            createAndSaveNewResource(domain, group, participant + ":" + i, TestConstants.TEST_SG_SCHEMA_1, resourceDefSeg, usr);
        }
    }

    @Transactional
    public DBResource createAndSaveNewServiceGroupWithMetadata() {

        DBDomain d = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
        DBResource sg = TestDBUtils.createDBResource();
      /*  sg.addDomain(d);
        DBSubresource md = TestDBUtils.createDBSubresource(TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        sg.getResourceDomains().get(0).addServiceMetadata(md);
        testInstance.persistFlushDetach(sg);

       */
        return sg;
    }

    @Transactional
    public DBResource createAndSaveNewServiceGroupWithUsers() {
        DBUser u1 = userDao.findUserByUsername(USERNAME_1).get();
        DBUser u2 = userDao.findUserByCertificateId(USER_CERT_2).get();

        DBDomain d = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
        DBResource sg = TestDBUtils.createDBResource();
       // sg.addDomain(d);
        //sg.getUsers().add(u1);
        //sg.getUsers().add(u2);
        testInstance.update(sg);
        return sg;
    }

    @Transactional
    public void update(DBResource sg) {
        testInstance.update(sg);
    }

}
