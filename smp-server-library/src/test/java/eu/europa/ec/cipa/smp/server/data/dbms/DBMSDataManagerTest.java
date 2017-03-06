/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.smp.server.data.dbms;

import com.helger.commons.annotations.DevelopersNote;
import com.helger.web.http.basicauth.BasicAuthClientCredentials;
import eu.europa.ec.cipa.smp.server.AbstractTest;
import eu.europa.ec.cipa.smp.server.conversion.ServiceMetadataConverter;
import eu.europa.ec.cipa.smp.server.data.dbms.model.*;
import eu.europa.ec.cipa.smp.server.errors.exceptions.NotFoundException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnknownUserException;
import eu.europa.ec.cipa.smp.server.security.BCryptPasswordHash;
import eu.europa.ec.cipa.smp.server.util.IdentifierUtils;
import eu.europa.ec.cipa.smp.server.util.SMPDBUtils;
import eu.europa.ec.cipa.smp.server.util.XmlTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
// @Ignore
// ("Cannot be enabled by default, because it would fail without the correct configuration")
@DevelopersNote ("You need to adjust your local config.properties file to run this test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class DBMSDataManagerTest extends AbstractTest {
    private static final String PARTICIPANT_IDENTIFIER_SCHEME = CommonColumnsLengths.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME;
  private static final String DOCUMENT_SCHEME = CommonColumnsLengths.DEFAULT_DOCUMENT_TYPE_IDENTIFIER_SCHEME;
  private static final String PROCESS_SCHEME = CommonColumnsLengths.DEFAULT_PROCESS_IDENTIFIER_SCHEME;

  private static final String PARTICIPANT_IDENTIFIER1 = "0010:599900000000A";
  private static final String PARTICIPANT_IDENTIFIER2 = "0010:599900000000B";

  private static final String TEST_DOCTYPE_ID = "doc1";
  private static final String TEST_PROCESS_ID = "bis4";

  private static final String USERNAME = "peppol_user";
  private static final String PASSWORD = "Test1234";
  private static final String ADMIN_USERNAME = "the_admin";
  private static final String NOADMIN_USERNAME = "CN=SMP_1000000181,O=DIGIT,C=DK:123456789";
  private static final String NOADMIN_PASSWORD = "123456789";
  private static final String NOADMIN_PASSWORD_HASH = BCryptPasswordHash.hashPassword(NOADMIN_PASSWORD);
    public static final BasicAuthClientCredentials ADMIN_CREDENTIALS = new BasicAuthClientCredentials(ADMIN_USERNAME, null);

    private static final String CERTIFICATE = "VGhpcyBpcyBzdXJlbHkgbm90IGEgdmFsaWQgY2VydGlmaWNhdGUsIGJ1dCBpdCBo\n"
                                            + "YXMgbW9yZSB0aGFuIDY0IGNoYXJhY3RlcnM=";
  private static final String ADDRESS = "http://test.eu/accesspoint.svc";
    private static final boolean REQUIRE_SIGNATURE = true;
    private static final String MINIMUM_AUTH_LEVEL = "1";
    private static final Date ACTIVATION_DATE = GregorianCalendar.getInstance().getTime();
    private static final String DESCRIPTION = "description123";
    private static final String DESCRIPTION_2 = "new description";
    private static final Date EXPIRATION_DATE = GregorianCalendar.getInstance().getTime();
    private static final String TECH_CONTACT = "fake@peppol.eu";
    private static final String TECH_INFO = "http://fake.peppol.eu/";
    private static final String TRANSPORT_PROFILE = "bdxr-transport-ebms3-as4";

  private static final ParticipantIdentifierType PARTY_ID = new ParticipantIdentifierType(PARTICIPANT_IDENTIFIER1,"iso6523-actorid-upis");
    private static final ParticipantIdentifierType SERVICEGROUP_ID = PARTY_ID;
    private static final DocumentIdentifier DOCTYPE_ID = new DocumentIdentifier(TEST_DOCTYPE_ID, DOCUMENT_SCHEME);
  private static final BasicAuthClientCredentials CREDENTIALS = new BasicAuthClientCredentials (USERNAME, PASSWORD);

  private ServiceGroup m_aServiceGroup;
  private ServiceMetadata m_aServiceMetadata;
  private String m_sServiceMetadata;
  private boolean isServiceGroupToDelete = false;
  private boolean isServiceMetadataToDelete = false;

  @Before
  public void beforeTest () throws Throwable {
    createOrUpdatedDBUser(NOADMIN_USERNAME, NOADMIN_PASSWORD_HASH, false);
    createOrUpdatedDBUser(ADMIN_USERNAME, null, true);

    final ExtensionType aExtension = SMPDBUtils.getAsExtensionSafe("<root><any>value</any></root>");
    assertNotNull (aExtension);
    assertNotNull (aExtension.getAny ());

    final ObjectFactory aObjFactory = new ObjectFactory ();
    m_aServiceGroup = aObjFactory.createServiceGroup ();
    m_aServiceGroup.setParticipantIdentifier (PARTY_ID);

    // Be sure to delete if it exists.
    try {
      s_aDataMgr.deleteServiceGroup (SERVICEGROUP_ID, CREDENTIALS);
    }
    catch (final Exception ex) {}

    // Create a new one
    s_aDataMgr.saveServiceGroup (m_aServiceGroup, CREDENTIALS);

    m_aServiceMetadata = aObjFactory.createServiceMetadata ();
    final ServiceInformationType aServiceInformation = aObjFactory.createServiceInformationType ();
    aServiceInformation.setDocumentIdentifier (DOCTYPE_ID);
    aServiceInformation.setParticipantIdentifier (PARTY_ID);
    aServiceInformation.getExtensions().add(aExtension);
    {
      final ProcessListType processList = aObjFactory.createProcessListType ();
      {
        final ProcessType process = aObjFactory.createProcessType ();
        process.setProcessIdentifier (new ProcessIdentifier(TEST_PROCESS_ID, PROCESS_SCHEME));
        process.getExtensions().add(aExtension);
        {
          final ServiceEndpointList serviceEndpointList = aObjFactory.createServiceEndpointList ();
          {
            final EndpointType endpoint = aObjFactory.createEndpointType ();
            endpoint.setCertificate (CERTIFICATE.getBytes());
            endpoint.setEndpointURI(ADDRESS);
            endpoint.setMinimumAuthenticationLevel (MINIMUM_AUTH_LEVEL);
            endpoint.setRequireBusinessLevelSignature (REQUIRE_SIGNATURE);
            endpoint.setServiceActivationDate (ACTIVATION_DATE);
            endpoint.setServiceDescription (DESCRIPTION);
            endpoint.setServiceExpirationDate (EXPIRATION_DATE);
            endpoint.getExtensions().add(aExtension);
            endpoint.setTechnicalContactUrl (TECH_CONTACT);
            endpoint.setTechnicalInformationUrl (TECH_INFO);
            endpoint.setTransportProfile (TRANSPORT_PROFILE);
            serviceEndpointList.getEndpoints().add (endpoint);
          }
          process.setServiceEndpointList (serviceEndpointList);
        }
        processList.getProcesses().add (process);
      }
      aServiceInformation.setProcessList (processList);
    }
    m_aServiceMetadata.setServiceInformation (aServiceInformation);
    m_sServiceMetadata = XmlTestUtils.marshall(m_aServiceMetadata);
  }

  private final void createOrUpdatedDBUser(String username, String password, boolean isAdmin) throws Throwable {
      DBUser aDBUser = s_aDataMgr.getCurrentEntityManager().find(DBUser.class, username);

      if(aDBUser == null){
          aDBUser = new DBUser();
          aDBUser.setUsername(username);
          aDBUser.setPassword(password);
          aDBUser.setAdmin(isAdmin);
          s_aDataMgr.getCurrentEntityManager().persist(aDBUser);
      }else{
          aDBUser.setPassword(password);
          aDBUser.setAdmin(isAdmin);
          s_aDataMgr.getCurrentEntityManager().merge(aDBUser);
      }
  }

  @Test
  public void testCreateServiceGroup () throws Throwable {
    m_aServiceGroup.getParticipantIdentifier ().setValue (PARTICIPANT_IDENTIFIER2);

    s_aDataMgr.deleteServiceGroup(PARTY_ID, ADMIN_CREDENTIALS);

    boolean bNewServiceGroupCreated = s_aDataMgr.saveServiceGroup (m_aServiceGroup, CREDENTIALS);

    final ParticipantIdentifierType aParticipantIdentifier2 = new ParticipantIdentifierType(PARTICIPANT_IDENTIFIER2, "iso6523-actorid-upis");
    final ServiceGroup result = s_aDataMgr.getServiceGroup (aParticipantIdentifier2);
    assertTrue(bNewServiceGroupCreated);
    assertNotNull (result);

    assertNull (result.getServiceMetadataReferenceCollection ());
    assertEquals (PARTICIPANT_IDENTIFIER_SCHEME, result.getParticipantIdentifier ().getScheme ());
    assertTrue (IdentifierUtils.areParticipantIdentifierValuesEqual (PARTICIPANT_IDENTIFIER2,
                                                                     result.getParticipantIdentifier ().getValue ()));
  }

    @Test
    public void testUpdateServiceByAdmin () throws Throwable {
        //given
        s_aDataMgr.deleteServiceGroup(PARTY_ID, ADMIN_CREDENTIALS);

        ServiceGroup sg = new ServiceGroup();
        sg.setParticipantIdentifier(PARTY_ID);
        boolean bNewServiceGroupCreated = s_aDataMgr.saveServiceGroup (m_aServiceGroup, CREDENTIALS);
        ServiceGroup serviceGroup = s_aDataMgr.getServiceGroup(PARTY_ID);
        assertNotNull(serviceGroup.getExtensions());
        assertEquals(0,serviceGroup.getExtensions().size());

        //when
        ExtensionType extension = new ExtensionType();
        extension.setExtensionID("the id");
        sg.getExtensions().add(0, extension);
        boolean bNewServiceGroupUpdated = s_aDataMgr.saveServiceGroup(sg, ADMIN_CREDENTIALS);

        //then
        assertTrue(bNewServiceGroupCreated);
        assertFalse(bNewServiceGroupUpdated);
        ServiceGroup newGroup = s_aDataMgr.getServiceGroup(PARTY_ID);
        assertEquals(1, newGroup.getExtensions().size());
        assertEquals("the id", newGroup.getExtensions().get(0).getExtensionID());
    }

    @Test
    public void testDeleteServiceGroupByAdmin () throws Throwable {
        //given
        ServiceGroup sg = new ServiceGroup();
        sg.setParticipantIdentifier(PARTY_ID);
        s_aDataMgr.saveServiceGroup (m_aServiceGroup, CREDENTIALS);

        //when
        s_aDataMgr.deleteServiceGroup(PARTY_ID, ADMIN_CREDENTIALS);

        //then
        assertNull(s_aDataMgr.getServiceGroup(PARTY_ID));
    }

    @Test
  public void testCreateServiceGroupInvalidPassword () throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials (USERNAME, "WRONG_PASSWORD");

    m_aServiceGroup.getParticipantIdentifier ().setValue (PARTICIPANT_IDENTIFIER2);
    try {
        s_aDataMgr.saveServiceGroup (m_aServiceGroup, aCredentials);
        fail ();
    }
    catch (final UnauthorizedException ex) {}
  }

  @Test
  public void testCreateServiceGroupUnknownUser () throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials ("Unknown_User", PASSWORD);

    m_aServiceGroup.getParticipantIdentifier ().setValue (PARTICIPANT_IDENTIFIER2);
    try {
        s_aDataMgr.saveServiceGroup (m_aServiceGroup, aCredentials);
        fail ();
    }
    catch (final UnknownUserException ex) {}
  }

  @Test
  public void testDeleteServiceGroup () throws Throwable {
      s_aDataMgr.deleteServiceGroup (SERVICEGROUP_ID, CREDENTIALS);
      assertNull (s_aDataMgr.getServiceGroup (SERVICEGROUP_ID));
  }

  @Test
  public void testDeleteServiceGroupUnknownID () throws Throwable {
    final ParticipantIdentifierType aServiceGroupID2 = new ParticipantIdentifierType(PARTICIPANT_IDENTIFIER2, "iso6523-actorid-upis");
    try {
        s_aDataMgr.deleteServiceGroup (aServiceGroupID2, CREDENTIALS);
    }
    catch (final NotFoundException ex) {}
    assertNull (s_aDataMgr.getServiceGroup (aServiceGroupID2));
  }

  @Test
  public void testDeleteServiceGroupUnknownUser () throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials ("Unknown_User", PASSWORD);
    try {
        s_aDataMgr.deleteServiceGroup (SERVICEGROUP_ID, aCredentials);
        s_aDataMgr.getCurrentEntityManager().getTransaction().commit();
        fail ();
    }
    catch (final UnknownUserException ex) {}
  }

  @Test
  public void testDeleteServiceGroupWrongPass () throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials (USERNAME, "WrongPassword");
    try {
        s_aDataMgr.deleteServiceGroup (SERVICEGROUP_ID, aCredentials);
        fail ();
    }
    catch (final UnauthorizedException ex) {}
  }

  @Test
  public void testCreateServiceMetadata() throws Throwable {
    // Save to DB
    boolean bNewServiceMetadataCreated = s_aDataMgr.saveService(SERVICEGROUP_ID, DOCTYPE_ID, m_sServiceMetadata, CREDENTIALS);

    // Retrieve from DB
    final String docDBServiceMetadata = s_aDataMgr.getService (SERVICEGROUP_ID, DOCTYPE_ID);
    final ServiceMetadata aDBServiceMetadata = ServiceMetadataConverter.unmarshal(docDBServiceMetadata);
    assertNotNull (aDBServiceMetadata);

    final ProcessListType aOrigProcessList = m_aServiceMetadata.getServiceInformation ().getProcessList ();
    assertEquals (1, aOrigProcessList.getProcesses().size ());
    final ProcessType aOrigProcess = aOrigProcessList.getProcesses().get (0);
    assertEquals (1, aOrigProcess.getServiceEndpointList ().getEndpoints().size ());
    final EndpointType aOrigEndpoint = aOrigProcess.getServiceEndpointList ().getEndpoints().get (0);

    final ProcessType aDBProcess = aDBServiceMetadata.getServiceInformation ().getProcessList ().getProcesses().get (0);
    final EndpointType aDBEndpoint = aDBProcess.getServiceEndpointList ().getEndpoints().get (0);

    assertTrue(bNewServiceMetadataCreated);
    assertTrue (IdentifierUtils.areIdentifiersEqual (m_aServiceMetadata.getServiceInformation ()
                                                                       .getDocumentIdentifier (),
                                                     aDBServiceMetadata.getServiceInformation ()
                                                                       .getDocumentIdentifier ()));
    assertTrue (IdentifierUtils.areIdentifiersEqual (m_aServiceMetadata.getServiceInformation ()
                                                                       .getParticipantIdentifier (),
                                                     aDBServiceMetadata.getServiceInformation ()
                                                                       .getParticipantIdentifier ()));
    assertTrue (IdentifierUtils.areIdentifiersEqual (aOrigProcess.getProcessIdentifier (),
                                                     aDBProcess.getProcessIdentifier ()));
    assertArrayEquals (aOrigEndpoint.getCertificate (), aDBEndpoint.getCertificate ());
    assertEquals (aOrigEndpoint.getMinimumAuthenticationLevel (), aDBEndpoint.getMinimumAuthenticationLevel ());
    assertEquals (aOrigEndpoint.getServiceDescription (), aDBEndpoint.getServiceDescription ());
    assertEquals (aOrigEndpoint.getTechnicalContactUrl (), aDBEndpoint.getTechnicalContactUrl ());
    assertEquals (aOrigEndpoint.getTechnicalInformationUrl (), aDBEndpoint.getTechnicalInformationUrl ());
    assertEquals (aOrigEndpoint.getTransportProfile (), aDBEndpoint.getTransportProfile ());
    assertEquals (aOrigEndpoint.getEndpointURI(), aDBEndpoint.getEndpointURI());

    isServiceMetadataToDelete = true;
  }

  @Test(expected = NotFoundException.class)
  public void testCreateServiceMetadataNonExistingServiceGroup() throws Throwable {
      final ParticipantIdentifierType serviceGroupNonExisting = new ParticipantIdentifierType("nonexisting","iso6523-actorid-upis");
      s_aDataMgr.saveService(serviceGroupNonExisting, DOCTYPE_ID, m_sServiceMetadata, CREDENTIALS);
  }

  @Test(expected = NotFoundException.class)
  public void testCreateServiceMetadataByAdminNonExistingServiceGroup() throws Throwable {
      final ParticipantIdentifierType serviceGroupNonExisting = new ParticipantIdentifierType("nonexisting","iso6523-actorid-upis");
      s_aDataMgr.saveService(serviceGroupNonExisting, DOCTYPE_ID, m_sServiceMetadata, ADMIN_CREDENTIALS);
  }

  @Test(expected = NotFoundException.class)
  public void testCreateServiceMetadataByNoAdminNonExistingServiceGroup() throws Throwable {
      final ParticipantIdentifierType serviceGroupNonExisting = new ParticipantIdentifierType("nonexisting","iso6523-actorid-upis");
      final BasicAuthClientCredentials noAdminCredentials = new BasicAuthClientCredentials (NOADMIN_USERNAME, NOADMIN_PASSWORD);
      s_aDataMgr.saveService(serviceGroupNonExisting, DOCTYPE_ID, m_sServiceMetadata, noAdminCredentials);
  }

  @Test
  public void testCreateServiceMetadataRedirect() throws Throwable {
      final String PARTICIPANT_IDENTIFIER3 = "0010:599900000000C";
      final ParticipantIdentifierType PARTY_ID3 = new ParticipantIdentifierType(PARTICIPANT_IDENTIFIER3,"iso6523-actorid-upis");
      final ObjectFactory aObjFactory = new ObjectFactory ();
      m_aServiceGroup = aObjFactory.createServiceGroup ();
      m_aServiceGroup.setParticipantIdentifier (PARTY_ID3);

      // Be sure to delete if it exists.
      try {
          s_aDataMgr.deleteServiceGroup (PARTY_ID3, CREDENTIALS);
      }
      catch (final Exception ex) {}

      // Create a new one
      s_aDataMgr.saveServiceGroup (m_aServiceGroup, CREDENTIALS);

      ServiceMetadata m_aServiceMetadataRedirect = aObjFactory.createServiceMetadata();
      RedirectType redirect = aObjFactory.createRedirectType();
      redirect.setCertificateUID("certificateUID");
      redirect.setHref("href");

      ExtensionType extension = aObjFactory.createExtensionType();
      extension.setExtensionAgencyID("agencyId");
      extension.setExtensionAgencyName("agencyName");
      extension.setExtensionAgencyURI("uri");
      extension.setExtensionID("id");
      extension.setExtensionName("name");
      extension.setExtensionReason("reason");
      extension.setExtensionReasonCode("reasonCode");
      extension.setExtensionVersionID("versionId");

      redirect.getExtensions().add(extension);
      m_aServiceMetadataRedirect.setRedirect(redirect);

      String m_sServiceMetadataRedirect = XmlTestUtils.marshall(m_aServiceMetadataRedirect);

      // Save to DB
      boolean bNewServiceMetadataCreated = s_aDataMgr.saveService(PARTY_ID3, DOCTYPE_ID, m_sServiceMetadataRedirect, CREDENTIALS);

      // Retrieve from DB
      assertTrue(bNewServiceMetadataCreated);
      final String docDBServiceMetadata = s_aDataMgr.getService (PARTY_ID3, DOCTYPE_ID);
      final ServiceMetadata aDBServiceMetadata = ServiceMetadataConverter.unmarshal(docDBServiceMetadata);
      assertNotNull (aDBServiceMetadata);

      RedirectType redirectDB = aDBServiceMetadata.getRedirect();
      assertNotNull(redirectDB);
      assertEquals(redirect.getHref(), redirectDB.getHref());
      assertEquals(redirect.getCertificateUID(), redirectDB.getCertificateUID());
      ExtensionType extensionDB = redirectDB.getExtensions().get(0);
      assertNotNull(extensionDB);
      assertEquals(extension.getExtensionAgencyID(), extensionDB.getExtensionAgencyID());
      assertEquals(extension.getExtensionAgencyName(), extensionDB.getExtensionAgencyName());
      assertEquals(extension.getExtensionAgencyURI(), extensionDB.getExtensionAgencyURI());
      assertEquals(extension.getExtensionID(), extensionDB.getExtensionID());
      assertEquals(extension.getExtensionName(), extensionDB.getExtensionName());
      assertEquals(extension.getExtensionReason(), extensionDB.getExtensionReason());
      assertEquals(extension.getExtensionReasonCode(), extensionDB.getExtensionReasonCode());
      assertEquals(extension.getExtensionVersionID(), extensionDB.getExtensionVersionID());
  }

    @Test
    public void testUpdateServiceMetadataByAdmin() throws Throwable {
        // given
        s_aDataMgr.deleteService(PARTY_ID, DOCTYPE_ID, ADMIN_CREDENTIALS);
        boolean bNewServiceMetadataCreated = s_aDataMgr.saveService(PARTY_ID, DOCTYPE_ID, m_sServiceMetadata, CREDENTIALS);
        String strMetadata = s_aDataMgr.getService(PARTY_ID, DOCTYPE_ID);
        ServiceMetadata metadata = ServiceMetadataConverter.unmarshal(strMetadata);
        EndpointType endpoint = metadata.getServiceInformation().getProcessList().getProcesses().get(0).getServiceEndpointList().getEndpoints().get(0);
        assertEquals(DESCRIPTION, endpoint.getServiceDescription());

        //when
        m_sServiceMetadata = m_sServiceMetadata.replaceAll(DESCRIPTION, DESCRIPTION_2);
        boolean bNewServiceMetadataUpdated = s_aDataMgr.saveService(PARTY_ID, DOCTYPE_ID, m_sServiceMetadata, ADMIN_CREDENTIALS );

        //then
        assertTrue(bNewServiceMetadataCreated);
        assertFalse(bNewServiceMetadataUpdated);
        String strNewMetadata = s_aDataMgr.getService(PARTY_ID, DOCTYPE_ID);
        ServiceMetadata newMetadata = ServiceMetadataConverter.unmarshal(strNewMetadata);
        EndpointType newEndpoint = newMetadata.getServiceInformation().getProcessList().getProcesses().get(0).getServiceEndpointList().getEndpoints().get(0);
        assertEquals(DESCRIPTION_2, newEndpoint.getServiceDescription());
    }

    @Test
    public void testDeleteServiceMetadataByAdmin() throws Throwable {
        // given
        boolean bNewServiceMetadataCreated = s_aDataMgr.saveService(PARTY_ID, DOCTYPE_ID, m_sServiceMetadata, CREDENTIALS);

        //when
        s_aDataMgr.deleteService(PARTY_ID, DOCTYPE_ID, ADMIN_CREDENTIALS);

        //then
        assertTrue(bNewServiceMetadataCreated);
        assertNull(s_aDataMgr.getService(PARTY_ID, DOCTYPE_ID));
    }

  @Test
  public void testCreateServiceMetadataUnknownUser() throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials ("Unknown_User", PASSWORD);
    try {
        s_aDataMgr.saveService(PARTY_ID, DOCTYPE_ID, m_sServiceMetadata, aCredentials);
        fail ();
    }
    catch (final UnknownUserException ex) {}
  }

  @Test
  public void testCreateServiceMetadataWrongPass() throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials (USERNAME, "WrongPassword");
    try {
        s_aDataMgr.saveService(PARTY_ID, DOCTYPE_ID, m_sServiceMetadata, aCredentials);
        s_aDataMgr.getCurrentEntityManager().getTransaction().commit();
        fail ();
    }
    catch (final UnauthorizedException ex) {}
  }

  @Test
  public void testPrintServiceMetadata() throws Throwable {
    // Ensure something is present :)
      isServiceMetadataToDelete = true;
      s_aDataMgr.saveService(SERVICEGROUP_ID, DOCTYPE_ID, m_sServiceMetadata, CREDENTIALS);
      System.out.println (s_aDataMgr.getService (SERVICEGROUP_ID, DOCTYPE_ID));
  }

  @Test
  public void testDeleteServiceMetadata() throws Throwable {
    // Ensure something is present :)
    s_aDataMgr.saveService(SERVICEGROUP_ID, DOCTYPE_ID, m_sServiceMetadata, CREDENTIALS);

    // First deletion succeeds
    s_aDataMgr.deleteService (SERVICEGROUP_ID, DOCTYPE_ID, CREDENTIALS);
    try {
      // Second deletion fails
      s_aDataMgr.deleteService (SERVICEGROUP_ID, DOCTYPE_ID, CREDENTIALS);
      fail ();
    }
    catch (final NotFoundException ex) {}
  }

  @Test
  public void testDeleteServiceMetadataUnknownUser () throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials ("Unknown_User", PASSWORD);
    try {
      s_aDataMgr.deleteService (SERVICEGROUP_ID, DOCTYPE_ID, aCredentials);
      fail ();
    }
    catch (final UnknownUserException ex) {}
  }

  @Test
  public void testDeleteServiceMetadataWrongPass () throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials (USERNAME, "WrongPassword");
    try {
      s_aDataMgr.deleteService (SERVICEGROUP_ID, DOCTYPE_ID, aCredentials);
      fail ();
    }
    catch (final UnauthorizedException ex) {}
  }

  @Test(expected = UnauthorizedException.class )
  public void testDeleteServiceMetadataNullPass () throws Throwable {
     final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials (USERNAME, null);
     s_aDataMgr.deleteService (SERVICEGROUP_ID, DOCTYPE_ID, aCredentials);
     fail ();
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteServiceMetadataNonExistingServiceGroup() throws Throwable {
      final ParticipantIdentifierType serviceGroupNonExisting = new ParticipantIdentifierType("nonexisting","iso6523-actorid-upis");
      s_aDataMgr.deleteService(serviceGroupNonExisting, DOCTYPE_ID, CREDENTIALS);
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteServiceMetadataByAdminNonExistingServiceGroup() throws Throwable {
      final ParticipantIdentifierType serviceGroupNonExisting = new ParticipantIdentifierType("nonexisting","iso6523-actorid-upis");
      s_aDataMgr.deleteService(serviceGroupNonExisting, DOCTYPE_ID, ADMIN_CREDENTIALS);
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteServiceMetadataByNoAdminNonExistingServiceGroup() throws Throwable {
      final ParticipantIdentifierType serviceGroupNonExisting = new ParticipantIdentifierType("nonexisting","iso6523-actorid-upis");
      final BasicAuthClientCredentials noAdminCredentials = new BasicAuthClientCredentials (NOADMIN_USERNAME, NOADMIN_PASSWORD);
      s_aDataMgr.deleteService(serviceGroupNonExisting, DOCTYPE_ID, noAdminCredentials);
  }

  @Test
  public void testSaveServiceGroup() throws Throwable {
      // # Authentication #
      BasicAuthClientCredentials auth =CREDENTIALS;

      // # Delete after leaving test #
      isServiceGroupToDelete = true;

      // # Save ServiceGroup #
      String participantId = PARTICIPANT_IDENTIFIER2 + "654987";
      m_aServiceGroup = createServiceGroup(participantId);
      s_aDataMgr.deleteServiceGroup(PARTY_ID, ADMIN_CREDENTIALS);

      //when
      boolean bNewServiceGroupCreated = s_aDataMgr.saveServiceGroup(m_aServiceGroup, auth);

      //then
      ServiceGroup result = s_aDataMgr.getServiceGroup(m_aServiceGroup.getParticipantIdentifier());

      // # Check ServiceGroup after save #
      assertNotNull(result);
      assertNull(result.getServiceMetadataReferenceCollection());
      assertEquals(PARTICIPANT_IDENTIFIER_SCHEME, result.getParticipantIdentifier().getScheme());
      assertTrue(IdentifierUtils.areParticipantIdentifierValuesEqual(participantId,
              result.getParticipantIdentifier().getValue()));
      // # Check Ownership #
      assertNotNull( s_aDataMgr.getCurrentEntityManager().find(DBOwnership.class, new DBOwnershipID(auth.getUserName(), m_aServiceGroup.getParticipantIdentifier())));
      assertTrue(bNewServiceGroupCreated);
  }

  @Test(expected = UnauthorizedException.class)
  public void testSaveServiceGroupByCertificateNotOwner() throws Throwable {
    String participantId = PARTICIPANT_IDENTIFIER2 + "951842";
    String certificateIdentifierHeader = NOADMIN_USERNAME;
    ServiceGroup serviceGroup = createServiceGroup(participantId);

    s_aDataMgr.saveServiceGroup(serviceGroup, CREDENTIALS);

    BasicAuthClientCredentials auth = new BasicAuthClientCredentials(certificateIdentifierHeader, "100password");
    s_aDataMgr.saveServiceGroup(serviceGroup, auth);
  }

  @Test(expected = UnknownUserException.class)
  public void testSaveServiceGroupByCertificateNotFound() throws Throwable {
    String certificateIdentifierHeader = "CN=SMP_123456789,O=DIGIT,C=PT:123456789";

    ServiceGroup serviceGroup = createServiceGroup(certificateIdentifierHeader);
    BasicAuthClientCredentials auth = new BasicAuthClientCredentials(certificateIdentifierHeader, "100password");
    s_aDataMgr.saveServiceGroup(serviceGroup, auth);
  }

  @Test(expected = UnknownUserException.class)
  public void testSaveServiceGroupByUserNotFound() throws Throwable {
    String participantId = PARTICIPANT_IDENTIFIER2 + "123456789";
    ServiceGroup serviceGroup = createServiceGroup(participantId);

    BasicAuthClientCredentials auth = new BasicAuthClientCredentials ("123456789", PASSWORD);
    s_aDataMgr.saveServiceGroup(serviceGroup, auth);
  }

  @Test
  public void verifyUser() throws Throwable {
    BasicAuthClientCredentials auth = CREDENTIALS;
    DBUser user = s_aDataMgr._verifyUser(auth);
    assertNotNull(user);
    assertNotNull(user.getUsername(),CREDENTIALS.getUserName());
    assertNotNull(user.getPassword(),CREDENTIALS.getPassword());
  }

  @Test(expected = UnauthorizedException.class)
  public void verifyUserNullPassword() throws Throwable {
       BasicAuthClientCredentials auth = new BasicAuthClientCredentials(CREDENTIALS.getUserName(), null);
       DBUser user = s_aDataMgr._verifyUser(auth);
  }

  @Test(expected = UnknownUserException.class)
  public void verifyUserNotFound() throws Throwable {
        BasicAuthClientCredentials auth = new BasicAuthClientCredentials("UserNotFound", null);
        DBUser user = s_aDataMgr._verifyUser(auth);
  }

  @Test(expected = UnauthorizedException.class)
  public void verifyUserWrongPassword() throws Throwable {
       BasicAuthClientCredentials auth = new BasicAuthClientCredentials(CREDENTIALS.getUserName(), "WrongPass");
       DBUser user = s_aDataMgr._verifyUser(auth);
  }

  @Test
  public void verifyUserNullAllowed() throws Throwable {
      // # Look for user
      DBUser aDBUser = s_aDataMgr.getCurrentEntityManager().find(DBUser.class, CREDENTIALS.getUserName());
      assertNotNull(aDBUser);

      // # Set password to null and save
      aDBUser.setPassword(null);
      s_aDataMgr.getCurrentEntityManager().merge(aDBUser);

      // # Check if password is null
      aDBUser = s_aDataMgr.getCurrentEntityManager().find(DBUser.class, CREDENTIALS.getUserName());
      assertNull(aDBUser.getPassword());

      // # Validate authentication with null password in the request and database
      BasicAuthClientCredentials auth = new BasicAuthClientCredentials(CREDENTIALS.getUserName(),null);
      DBUser user = s_aDataMgr._verifyUser(auth);
      assertNotNull(user);

      aDBUser.setPassword(CREDENTIALS.getPassword());
      s_aDataMgr.getCurrentEntityManager().merge(aDBUser);
  }

  @Test(expected = NullPointerException.class)
  public void verifyNullUser() throws Throwable {
        BasicAuthClientCredentials auth = new BasicAuthClientCredentials(null, "WrongPass");
        DBUser user = s_aDataMgr._verifyUser(auth);
  }

  private ServiceGroup createServiceGroup(String participantId) {
    final ObjectFactory aObjFactory = new ObjectFactory();
    ServiceGroup m_aServiceGroup = aObjFactory.createServiceGroup();
    m_aServiceGroup.setParticipantIdentifier(PARTY_ID);
    m_aServiceGroup.getParticipantIdentifier().setValue(participantId);

    return m_aServiceGroup;
  }

  @After
  public void deleteServiceGroup() throws Throwable {
      if (isServiceGroupToDelete) {
          DBServiceGroup aDBServiceGroup = s_aDataMgr.getCurrentEntityManager().find(DBServiceGroup.class, new DBServiceGroupID(m_aServiceGroup.getParticipantIdentifier()));
          if (aDBServiceGroup != null) {
              s_aDataMgr.getCurrentEntityManager().remove(aDBServiceGroup);
              isServiceGroupToDelete = false;
          }
      }
  }

    @After
    public void deleteServiceMetadata() throws Throwable {
        if (isServiceMetadataToDelete) {
            DBServiceMetadata serviceMetadata = s_aDataMgr.getCurrentEntityManager().find(DBServiceMetadata.class, new DBServiceMetadataID(SERVICEGROUP_ID, DOCTYPE_ID));
            if (serviceMetadata != null) {
                s_aDataMgr.getCurrentEntityManager().remove(serviceMetadata);
                isServiceMetadataToDelete = false;
            }
        }
    }

  @After
  public void deleteUser() throws Throwable {
      String[] usernames = new String[]{PARTICIPANT_IDENTIFIER2 + "654987",NOADMIN_USERNAME, ADMIN_USERNAME};
      for(String username : Arrays.asList(usernames)){
        DBUser aDBUser = s_aDataMgr.getCurrentEntityManager().find(DBUser.class, username);
        if(aDBUser != null) {
            s_aDataMgr.getCurrentEntityManager().remove(aDBUser);
        }
      }
    }
}