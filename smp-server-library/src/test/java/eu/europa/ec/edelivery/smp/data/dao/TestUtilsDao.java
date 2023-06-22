package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.ext.DBExtension;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.data.model.user.*;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static eu.europa.ec.edelivery.smp.testutil.TestDBUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Purpose of the class is to provide util to set-up test database data
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class TestUtilsDao {

    @Autowired
    UserDao userDao;
    @PersistenceContext
    protected EntityManager memEManager;
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(TestUtilsDao.class);


    DBDomain d1;
    DBDomain d2;
    DBResourceDef resourceDefSmp;
    DBSubresourceDef resourceDefSmpMetadata;
    DBResourceDef resourceDefCpp;

    DBDomainResourceDef domainResourceDefD1R1;
    DBDomainResourceDef domainResourceDefD1R2;
    DBDomainResourceDef domainResourceDefD2R1;

    DBUser user1;
    DBUser user2;
    DBUser user3;

    DBUser user4;
    DBUser user5;

    DBGroup groupD1G1;
    DBGroup groupD1G2;
    DBGroup groupD2G1;

    DBResource resourceD1G1RD1;
    DBDocument documentD1G1RD1;
    DBResource resourceD2G1RD1;
    DBDocument documentD2G1RD1;

    DBDocument documentD1G1RD1_S1;
    DBDocument documentD2G1RD1_S1 ;
    DBSubresource subresourceD1G1RD1_S1;
    DBSubresource subresourceD2G1RD1_S1;

    DBDomainMember domainMemberU1D1Admin;
    DBDomainMember domainMemberU1D2Viewer;
    DBGroupMember groupMemberU1D1G1Admin;
    DBGroupMember groupMemberU1D2G1Viewer;

    DBResourceMember resourceMemberU1R1_D2G1RD1_Admin;
    DBResourceMember resourceMemberU1R2_D2G1RD1_Viewer;

    DBResource resourcePrivateD1G1RD1;
   // DBResource resourceInternalD1G1RD1;

    DBExtension extension;

    boolean searchDataCreated = false;

    /**
     * Database can be cleaned by script before the next test; clean also the objects
     */
    public void clearData(){
        d1 = null;
        d2 = null;
        resourceDefSmp = null;
        resourceDefSmpMetadata = null;
        resourceDefCpp = null;
        domainResourceDefD1R1 = null;
        domainResourceDefD1R2 = null;
        domainResourceDefD2R1 = null;
        user1 = null;
        user2 = null;
        user3 = null;
        user4 = null;
        user5 = null;
        groupD1G1 = null;
        groupD1G2 = null;
        groupD2G1 = null;
        resourceD1G1RD1 = null;
        resourceD2G1RD1 = null;
        documentD1G1RD1 = null;
        documentD2G1RD1 = null;
        subresourceD1G1RD1_S1 = null;
        subresourceD2G1RD1_S1 = null;
        documentD1G1RD1_S1 = null;
        documentD2G1RD1_S1 = null;
        domainMemberU1D1Admin = null;
        domainMemberU1D2Viewer = null;
        groupMemberU1D1G1Admin = null;
        groupMemberU1D2G1Viewer = null;
        resourceMemberU1R1_D2G1RD1_Admin = null;
        resourceMemberU1R2_D2G1RD1_Viewer = null;

        resourcePrivateD1G1RD1 = null;
        //resourceInternalD1G1RD1 = null;

        extension = null;
        searchDataCreated = false;

    }


    /**
     * Set two domains  and register the following resourceDefinitions
     * TEST_DOMAIN_CODE_1
     * <ul>
     *  <li>TEST_RESOURCE_DEF_SMP10</li>
     *  <li>TEST_RESOURCE_DEF_CPP</li>
     *  </ul>
     * TEST_DOMAIN_CODE_2
     * <ul>
     *  <li>TEST_RESOURCE_DEF_SMP10</li>
     *  </ul>
     */
    @Transactional
    public void createResourceDefinitionsForDomains() {
        if (domainResourceDefD1R1 != null) {
            LOG.trace("eResourceDefinitionsForDomains are already initialized!");
            return;
        }
        createDomains();
        createResourceDefinitions();
        // register resourceDef to Domain
        domainResourceDefD1R1 = registerDomainResourceDefinition(d1, resourceDefSmp);
        domainResourceDefD1R2 = registerDomainResourceDefinition(d1, resourceDefCpp);
        domainResourceDefD2R1 = registerDomainResourceDefinition(d2, resourceDefSmp);

        assertNotNull(domainResourceDefD1R1.getId());
        assertNotNull(domainResourceDefD1R2.getId());
        assertNotNull(domainResourceDefD2R1.getId());
    }

    /**
     * Create resource definitions with id and URLs
     * <ul>
     *  <li>TEST_RESOURCE_DEF_SMP10</li>
     *  <li>TEST_RESOURCE_DEF_CPP</li>
     *  </ul>
     */
    @Transactional
    public void createResourceDefinitions() {
        if (resourceDefSmp != null) {
            LOG.trace("ResourceDefinitions are already initialized!");
            return;
        }
        resourceDefSmp = createResourceDefinition(TEST_RESOURCE_DEF_SMP10);
        resourceDefSmpMetadata =  createSubresourceDefinition(TEST_SUBRESOURCE_DEF_SMP10, resourceDefSmp);

        resourceDefCpp = createResourceDefinition(TEST_RESOURCE_DEF_CPP);

        assertNotNull(resourceDefSmp.getId());
        assertNotNull(resourceDefCpp.getId());
    }

    /**
     * Create resource definitions with id and URLs
     * <ul>
     *  <li>TEST_DOMAIN_CODE_1</li>
     *  <li>TEST_DOMAIN_CODE_2</li>
     *  </ul>
     */
    @Transactional
    public void createDomains() {
        if (d1 != null) {
            LOG.trace("Domains are already initialized!");
            return;
        }
        d1 = createDomain(TEST_DOMAIN_CODE_1);
        d2 = createDomain(TEST_DOMAIN_CODE_2);

        assertNotNull(d1.getId());
        assertNotNull(d1.getId());
    }

    @Transactional
    public void createUsers() {
        if (user1 != null) {
            LOG.trace("Domains are already initialized!");
            return;
        }
        user1 = createDBUserByUsername(USERNAME_1);
        DBCredential c1 = TestDBUtils.createDBCredentialForUser(user1, null, null, null);
        c1.setValue(BCrypt.hashpw(USERNAME_1_PASSWORD, BCrypt.gensalt()));
        user1.getUserCredentials().add(c1);
        user2 = createDBUserByCertificate(USER_CERT_2);
        user3 = createDBUserByUsername(USERNAME_3);
        DBCredential c3 = TestDBUtils.createDBCredentialForUserAccessToken(user3, null, null, null);
        c3.setValue(BCrypt.hashpw(USERNAME_3_AT_PASSWORD, BCrypt.gensalt()));
        c3.setName(USERNAME_3_AT);
        user3.getUserCredentials().add(c3);

        user4 = createDBUserByUsername(USERNAME_4);
        user5 = createDBUserByUsername(USERNAME_5);

        persistFlushDetach(user1);
        persistFlushDetach(user2);
        persistFlushDetach(user3);
        persistFlushDetach(user4);
        persistFlushDetach(user5);

        assertNotNull(user1.getId());
        assertNotNull(user2.getId());
        assertNotNull(user3.getId());
        assertNotNull(user4.getId());
        assertNotNull(user5.getId());
    }

    @Transactional
    public void deactivateUser(String username) {
        DBUser user = userDao.findUserByUsername(username).get();
        user.setActive(false);
        persistFlushDetach(user);
    }


    /**
     * Create domain members for
     * user1 on domain 1  as Admin
     * user1 on domain 2  as Viewer
     */
    @Transactional
    public void creatDomainMemberships() {
        if (domainMemberU1D1Admin != null) {
            LOG.trace("DomainMemberships are already initialized!");
            return;
        }
        createDomains();
        createUsers();
        domainMemberU1D1Admin = createDomainMembership(MembershipRoleType.ADMIN, user1, d1);
        domainMemberU1D2Viewer = createDomainMembership(MembershipRoleType.VIEWER, user1, d2);
    }

    @Transactional
    public void createGroupMemberships() {
        if (groupMemberU1D1G1Admin != null) {
            LOG.trace("GroupMemberships are already initialized!");
            return;
        }
        createGroups();
        createUsers();
        groupMemberU1D1G1Admin = createGroupMembership(MembershipRoleType.ADMIN, user1, groupD1G1);
        groupMemberU1D2G1Viewer = createGroupMembership(MembershipRoleType.VIEWER, user1, groupD2G1);
    }

    @Transactional
    public void createResourceMemberships() {
        if (resourceMemberU1R1_D2G1RD1_Admin != null) {
            LOG.trace("GroupMemberships are already initialized!");
            return;
        }
        createUsers();
        createResources();
        resourceMemberU1R1_D2G1RD1_Admin = createResourceMembership(MembershipRoleType.ADMIN, user1, resourceD1G1RD1);
        resourceMemberU1R2_D2G1RD1_Viewer = createResourceMembership(MembershipRoleType.VIEWER, user1, resourceD2G1RD1);
    }

    @Transactional
    public void createResourcesForSearch() {

        if (searchDataCreated) {
            LOG.trace("Search Data is already initialized!");
            return;
        }

        createUsers();
        createResourceDefinitions();

        DBDomain publicDomain = createDomain("publicDomain", VisibilityType.PUBLIC);
        DBDomain privateDomain = createDomain("privateDomain", VisibilityType.PRIVATE);

        DBDomainResourceDef publicDomainResourceDef = registerDomainResourceDefinition(publicDomain, resourceDefSmp);
        DBDomainResourceDef privateDomainResourceDef= registerDomainResourceDefinition(privateDomain, resourceDefSmp);
        // membership of the domain
        createDomainMembership(MembershipRoleType.VIEWER, user3, privateDomain);

        DBGroup pubPubGroup = createGroup("pubPubGroup", VisibilityType.PUBLIC, publicDomain);
        DBGroup pubPrivGroup = createGroup("pubPrivGroup", VisibilityType.PRIVATE, publicDomain);
        DBGroup privPubGroup = createGroup("privPubGroup", VisibilityType.PUBLIC, privateDomain);
        DBGroup privPrivGroup = createGroup("privPrivGroup", VisibilityType.PRIVATE, privateDomain);

        createGroupMembership(MembershipRoleType.VIEWER, user4, privPrivGroup);

        DBResource pubPubPubRes = createResource("pubPubPub", "1-1-1", VisibilityType.PUBLIC, publicDomainResourceDef,  pubPubGroup);
        DBResource pubPubPrivRes = createResource("pubPubPriv", "2-2-2", VisibilityType.PRIVATE, publicDomainResourceDef,  pubPubGroup);
        DBResource pubPrivPubRes = createResource("pubPrivPub", "3-3-3", VisibilityType.PUBLIC, publicDomainResourceDef,  pubPrivGroup);
        DBResource pubPrivPrivRes = createResource("pubPrivPriv", "4-4-4", VisibilityType.PRIVATE, publicDomainResourceDef,  pubPrivGroup);

        DBResource privPubPubRes = createResource("privPubPub", "5-5-5", VisibilityType.PUBLIC, privateDomainResourceDef,  privPubGroup);
        DBResource privPubPrivRes = createResource("privPubPriv", "6-6-6", VisibilityType.PRIVATE, privateDomainResourceDef,  privPubGroup);
        DBResource privPrivPubRes = createResource("privPrivPub", "7-7-7", VisibilityType.PUBLIC, privateDomainResourceDef,  privPrivGroup);
        DBResource privPrivPrivRes = createResource("privPrivPriv", "8-8-8", VisibilityType.PRIVATE, privateDomainResourceDef,  privPrivGroup);

        createResourceMembership(MembershipRoleType.ADMIN, user1, pubPubPubRes);
        createResourceMembership(MembershipRoleType.VIEWER, user2, pubPubPubRes);
        createResourceMembership(MembershipRoleType.ADMIN, user1, pubPubPrivRes);
        createResourceMembership(MembershipRoleType.VIEWER, user2, pubPubPrivRes);
        createResourceMembership(MembershipRoleType.ADMIN, user1, pubPrivPubRes);
        createResourceMembership(MembershipRoleType.VIEWER, user2, pubPrivPubRes);
        createResourceMembership(MembershipRoleType.ADMIN, user1, pubPrivPrivRes);
        createResourceMembership(MembershipRoleType.VIEWER, user2, pubPrivPrivRes);

        createResourceMembership(MembershipRoleType.ADMIN, user1, privPubPubRes);
        createResourceMembership(MembershipRoleType.VIEWER, user2, privPubPubRes);
        createResourceMembership(MembershipRoleType.ADMIN, user1, privPubPrivRes);
        createResourceMembership(MembershipRoleType.VIEWER, user2, privPubPrivRes);
        createResourceMembership(MembershipRoleType.ADMIN, user1, privPrivPubRes);
        createResourceMembership(MembershipRoleType.VIEWER, user2, privPrivPubRes);
        createResourceMembership(MembershipRoleType.ADMIN, user1, privPrivPrivRes);
        createResourceMembership(MembershipRoleType.VIEWER, user2, privPrivPrivRes);

        createResourceMembership(MembershipRoleType.VIEWER, user5, privPrivPrivRes);

        searchDataCreated = true;
    }

    @Transactional
    public DBDomainMember createDomainMembership(MembershipRoleType roleType, DBUser user, DBDomain domain){
        DBDomainMember domainMember = new DBDomainMember();
        domainMember.setRole(roleType);
        domainMember.setUser(user);
        domainMember.setDomain(domain);
        persistFlushDetach(domainMember);
        assertNotNull(domainMember.getId());
        return domainMember;
    }

    @Transactional
    public DBGroupMember createGroupMembership(MembershipRoleType roleType, DBUser user, DBGroup group){
        DBGroupMember member = new DBGroupMember();
        member.setRole(roleType);
        member.setUser(user);
        member.setGroup(group);
        persistFlushDetach(member);
        assertNotNull(member.getId());
        return member;
    }

    @Transactional
    public DBResourceMember createResourceMembership(MembershipRoleType roleType, DBUser user, DBResource resource){
        DBResourceMember member = new DBResourceMember();
        member.setRole(roleType);
        member.setUser(user);
        member.setResource(resource);
        persistFlushDetach(member);
        assertNotNull(member.getId());
        return member;
    }

    /**
     * Create resources for ids:
     * TEST_SG_ID_1, TEST_SG_ID_2
     * <ul>
     *  <li>TEST_SG_ID_1 and schema TEST_SG_SCHEMA_1 for Domain TEST_DOMAIN_CODE_1 group TEST_GROUP_A and resource Type TEST_RESOURCE_DEF_SMP10 </li>
     *  <li>TEST_SG_ID_2 and schema null for Domain TEST_DOMAIN_CODE_2 group TEST_GROUP_A and resource Type TEST_RESOURCE_DEF_SMP10 </li>
     *  </ul>
     */
    @Transactional
    public void createResources() {
        if (resourceD1G1RD1 != null) {
            LOG.trace("Domains are already initialized!");
            return;
        }
        createGroups();
        createResourceDefinitionsForDomains();
        documentD1G1RD1 = createDocument(2);
        documentD2G1RD1 = createDocument(2);
        resourceD1G1RD1 = TestDBUtils.createDBResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1);
        resourceD1G1RD1.setDocument(documentD1G1RD1);

        resourceD1G1RD1.setGroup(groupD1G1);
        resourceD1G1RD1.setDomainResourceDef(domainResourceDefD1R1);

        resourceD2G1RD1 = TestDBUtils.createDBResource(TEST_SG_ID_2, null);
        resourceD2G1RD1.setDocument(documentD2G1RD1);

        resourceD2G1RD1.setGroup(groupD2G1);
        resourceD2G1RD1.setDomainResourceDef(domainResourceDefD2R1);

        persistFlushDetach(resourceD1G1RD1);
        persistFlushDetach(resourceD2G1RD1);

        assertNotNull(resourceD1G1RD1.getId());
        assertNotNull(resourceD2G1RD1.getId());
    }

    @Transactional
    public DBResource createResource(String identifier, String schema, VisibilityType visibilityType, DBDomainResourceDef domainResourceDef, DBGroup group) {

        DBResource resource = TestDBUtils.createDBResource(identifier, schema);
        resource.setVisibility(visibilityType);
        resource.setGroup(group);
        resource.setDomainResourceDef(domainResourceDef);

        persistFlushDetach(resource);
        assertNotNull(resource.getId());
        return resource;
    }


    /**
     * Create resources with subresources  for ids:
     * <ul>
     *  <li>TEST_SG_ID_1 and schema TEST_SG_SCHEMA_1 with subresource type: TEST_SUBRESOURCE_DEF_SMP10 with id: TEST_DOC_ID_1, TEST_DOC_SCHEMA_1 </li>
     *  <li>TEST_SG_ID_2 and schema null with subresource type: TEST_SUBRESOURCE_DEF_SMP10 with id: TEST_DOC_ID_2, TEST_DOC_SCHEMA_2  </li>
     * </ul>
     */
    @Transactional
    public void createSubresources() {
        if (subresourceD1G1RD1_S1 != null) {
            LOG.trace("Subresources are already initialized!");
            return;
        }
        createResources();

        documentD1G1RD1_S1 = createDocument(2);
        documentD2G1RD1_S1 = createDocument(2);
        subresourceD1G1RD1_S1 = TestDBUtils.createDBSubresource(
                resourceD1G1RD1.getIdentifierValue(),resourceD1G1RD1.getIdentifierScheme(),
                TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        subresourceD2G1RD1_S1 = TestDBUtils.createDBSubresource(
                resourceD2G1RD1.getIdentifierValue(),resourceD2G1RD1.getIdentifierScheme(),
                TEST_DOC_ID_2, TEST_DOC_SCHEMA_2);

        subresourceD1G1RD1_S1.setDocument(documentD1G1RD1_S1);
        subresourceD2G1RD1_S1.setDocument(documentD2G1RD1_S1);

        subresourceD1G1RD1_S1.setResource(resourceD1G1RD1);
        subresourceD2G1RD1_S1.setResource(resourceD2G1RD1);

        subresourceD1G1RD1_S1.setSubresourceDef(resourceDefSmpMetadata);
        subresourceD2G1RD1_S1.setSubresourceDef(resourceDefSmpMetadata);


        persistFlushDetach(subresourceD1G1RD1_S1);
        persistFlushDetach(subresourceD2G1RD1_S1);

        assertNotNull(resourceD1G1RD1.getId());
        assertNotNull(resourceD2G1RD1.getId());
    }

    @Transactional
    public DBDocument createAndPersistDocument() {
        return createAndPersistDocument(3);
    }

    @Transactional
    public DBDocument createAndPersistDocument(int versions) {
        DBDocument document = createDocument(versions);
        persistFlushDetach(document);
        for (int i= 0; i< versions; i++ ) {
            assertNotNull(document.getDocumentVersions().get(i).getId());
        }
        // current version is the last version
        assertEquals(versions-1, document.getCurrentVersion());

        return document;
    }

    public DBDocument createDocument(int versions) {
        DBDocument document = createDBDocument();
        // add document versions to the document
        for (int i= 0; i< versions; i++ ) {
            document.addNewDocumentVersion(createDBDocumentVersion());
        }

        return document;
    }


    /**
     * Set two domains  and register the following resourceDefinitions
     * TEST_DOMAIN_CODE_1
     * <ul>
     *  <li>TEST_RESOURCE_DEF_SMP10</li>
     *  <li>TEST_RESOURCE_DEF_CPP</li>
     *  </ul>
     * TEST_DOMAIN_CODE_2
     * <ul>
     *  <li>TEST_RESOURCE_DEF_SMP10</li>
     *  </ul>
     */
    @Transactional
    public void createGroups() {
        // check if domains are already created
        if (groupD1G1 != null) {
            LOG.trace("Domains are already initialized!");
            return;
        }
        createDomains();
        groupD1G1 = createGroup(TEST_GROUP_A, VisibilityType.PUBLIC, d1);
        groupD1G2 = createGroup(TEST_GROUP_B, VisibilityType.PUBLIC, d1);
        groupD2G1 = createGroup(TEST_GROUP_A, VisibilityType.PUBLIC, d2);
    }

    @Transactional
    public DBGroup createGroup(String groupName, VisibilityType visibility, DBDomain domain){
        DBGroup group = createDBGroup(groupName, visibility);
        group.setDomain(domain);
        persistFlushDetach(group);
        assertNotNull(group.getId());

        return group;
    }

    @Transactional
    public DBExtension createExtension() {
        extension = createDBExtension(TEST_EXTENSION_IDENTIFIER);
        persistFlushDetach(extension);
        return extension;
    }

    @Transactional
    public DBDomain createDomain(String domainCode) {
     return createDomain(domainCode, VisibilityType.PUBLIC);
    }

    @Transactional
    public DBDomain createDomain(String domainCode, VisibilityType visibility) {
        DBDomain d = TestDBUtils.createDBDomain(domainCode);
        d.setVisibility(visibility);
        persistFlushDetach(d);
        return d;
    }

    @Transactional
    public DBResourceDef createResourceDefinition(String urlContextDef) {
        DBResourceDef d = TestDBUtils.createDBResourceDef(urlContextDef, urlContextDef);
        persistFlushDetach(d);
        return d;
    }

    @Transactional
    public DBSubresourceDef createSubresourceDefinition(String urlContextDef, DBResourceDef resourceDef) {
        DBSubresourceDef d = TestDBUtils.createDBSubresourceDef(urlContextDef, urlContextDef);
        d.setResourceDef(resourceDef);
        persistFlushDetach(d);
        return d;
    }

    @Transactional
    public DBDomainResourceDef registerDomainResourceDefinition(DBDomain domain, DBResourceDef resourceDef) {
        DBDomainResourceDef domainResourceDef = new DBDomainResourceDef();

        domainResourceDef.setDomain(domain);
        domainResourceDef.setResourceDef(resourceDef);
        persistFlushDetach(domainResourceDef);
        return domainResourceDef;
    }

    @Transactional
    public <E> void persistFlushDetach(E entity) {
        LOG.debug("Persist entity: [{}]", entity);
        memEManager.persist(entity);
        memEManager.flush();
        memEManager.detach(entity);
    }

    @Transactional
    public <E> E merge(E entity) {
        LOG.debug("merge entity: [{}]", entity);
        return memEManager.merge(entity);
    }

    public DBDomain getD1() {
        return d1;
    }

    public DBDomain getD2() {
        return d2;
    }

    public DBResourceDef getResourceDefSmp() {
        return resourceDefSmp;
    }

    public DBResourceDef getResourceDefCpp() {
        return resourceDefCpp;
    }

    public DBDomainResourceDef getDomainResourceDefD1R1() {
        return domainResourceDefD1R1;
    }

    public DBDomainResourceDef getDomainResourceDefD1R2() {
        return domainResourceDefD1R2;
    }

    public DBDomainResourceDef getDomainResourceDefD2R1() {
        return domainResourceDefD2R1;
    }

    public DBUser getUser1() {
        return user1;
    }

    public DBUser getUser2() {
        return user2;
    }

    public DBUser getUser3() {
        return user3;
    }

    public DBUser getUser4() {
        return user4;
    }

    public DBUser getUser5() {
        return user5;
    }

    public DBGroup getGroupD1G1() {
        return groupD1G1;
    }

    public DBGroup getGroupD1G2() {
        return groupD1G2;
    }

    public DBGroup getGroupD2G1() {
        return groupD2G1;
    }

    public DBResource getResourceD1G1RD1() {
        return resourceD1G1RD1;
    }

    public DBSubresourceDef getResourceDefSmpMetadata() {
        return resourceDefSmpMetadata;
    }

    public DBDocument getDocumentD1G1RD1_S1() {
        return documentD1G1RD1_S1;
    }

    public DBDocument getDocumentD2G1RD1_S1() {
        return documentD2G1RD1_S1;
    }

    public DBSubresource getSubresourceD1G1RD1_S1() {
        return subresourceD1G1RD1_S1;
    }

    public DBSubresource getSubresourceD2G1RD1_S1() {
        return subresourceD2G1RD1_S1;
    }

    public DBResource getResourceD2G1RD1() {
        return resourceD2G1RD1;
    }

    public DBDocument getDocumentD1G1RD1() {
        return documentD1G1RD1;
    }

    public DBDocument getDocumentD2G1RD1() {
        return documentD2G1RD1;
    }

    public DBExtension getExtension() {
        return extension;
    }

    public DBDomainMember getDomainMemberU1D1Admin() {
        return domainMemberU1D1Admin;
    }

    public DBDomainMember getDomainMemberU1D2Viewer() {
        return domainMemberU1D2Viewer;
    }
}
