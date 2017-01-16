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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;

import eu.europa.ec.cipa.smp.server.conversion.ServiceMetadataConverter;
import eu.europa.ec.cipa.smp.server.data.dbms.model.*;
import eu.europa.ec.cipa.smp.server.util.DefaultHttpHeader;
import eu.europa.ec.cipa.smp.server.util.RequestHelper;
import eu.europa.ec.cipa.smp.server.util.XmlTestUtils;
import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ExtensionType;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ProcessListType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.*;
import org.junit.rules.TestRule;

import com.helger.commons.annotations.DevelopersNote;
import com.helger.commons.scopes.mock.ScopeTestRule;
import com.helger.web.http.basicauth.BasicAuthClientCredentials;
import com.sun.jersey.api.NotFoundException;

import eu.europa.ec.cipa.peppol.identifier.CIdentifier;
import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.identifier.doctype.SimpleDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.SimpleProcessIdentifier;
import eu.europa.ec.cipa.peppol.utils.ExtensionConverter;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;
import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;
import eu.europa.ec.cipa.smp.server.exception.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.exception.UnknownUserException;
import eu.europa.ec.cipa.smp.server.hook.DoNothingRegistrationHook;

import javax.persistence.EntityManager;

/**
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
// @Ignore
// ("Cannot be enabled by default, because it would fail without the correct configuration")
@DevelopersNote ("You need to adjust your local config.properties file to run this test")
public class DBMSDataManagerTest {
  private static final String PARTICIPANT_IDENTIFIER_SCHEME = CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME;
  private static final String DOCUMENT_SCHEME = CIdentifier.DEFAULT_DOCUMENT_TYPE_IDENTIFIER_SCHEME;
  private static final String PROCESS_SCHEME = CIdentifier.DEFAULT_PROCESS_IDENTIFIER_SCHEME;

  private static final String PARTICIPANT_IDENTIFIER1 = "0010:599900000000A";
  private static final String PARTICIPANT_IDENTIFIER2 = "0010:599900000000B";

  private static final String TEST_DOCTYPE_ID = "doc1";
  private static final String TEST_PROCESS_ID = "bis4";

  private static final String USERNAME = "peppol_user";
  private static final String PASSWORD = "Test1234";

  private static final String CERTIFICATE = "VGhpcyBpcyBzdXJlbHkgbm90IGEgdmFsaWQgY2VydGlmaWNhdGUsIGJ1dCBpdCBo\n"
                                            + "YXMgbW9yZSB0aGFuIDY0IGNoYXJhY3RlcnM=";
  private static final String ADDRESS = "http://test.eu/accesspoint.svc";
  private static final boolean REQUIRE_SIGNATURE = true;
  private static final String MINIMUM_AUTH_LEVEL = "1";
  private static final Date ACTIVIATION_DATE = new Date ();
  private static final String DESCRIPTION = "description123";
  private static final Date EXPIRATION_DATE = new Date ();
  private static final String TECH_CONTACT = "fake@peppol.eu";
  private static final String TECH_INFO = "http://fake.peppol.eu/";
  private static final String TRANSPORT_PROFILE = ESMPTransportProfile.TRANSPORT_PROFILE_START.getID ();

  private static final ParticipantIdentifierType PARTY_ID = SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_IDENTIFIER1);
  private static final ParticipantIdentifierType SERVICEGROUP_ID = PARTY_ID;
  private static final DocumentIdentifierType DOCTYPE_ID = new SimpleDocumentTypeIdentifier (DOCUMENT_SCHEME,
                                                                                             TEST_DOCTYPE_ID);
  private static final BasicAuthClientCredentials CREDENTIALS = new BasicAuthClientCredentials (USERNAME, PASSWORD);

  private static DBMSDataManager s_aDataMgr;

  private static final class SMPTestRule extends ScopeTestRule {
    @Override
    public void before () {
      super.before ();
      if (s_aDataMgr == null) {
        // Do it only once :)
        // SMPEntityManagerFactory.getInstance ();
        s_aDataMgr = new DBMSDataManager (new DoNothingRegistrationHook ());
      }
    }
  }

  @ClassRule
  public static TestRule s_aTestRule = new SMPTestRule ();

  private ServiceGroupType m_aServiceGroup;
  private ServiceMetadataType m_aServiceMetadata;
  private String m_sServiceMetadata;
  private boolean isServiceGroupToDelete = false;
  private boolean isServiceMetadataToDelete = false;

  @Before
  public void beforeTest () throws Throwable {
    createUser();

    final ExtensionType aExtension = ExtensionConverter.convert ("<root><any>value</any></root>");
    assertNotNull (aExtension);
    assertNotNull (aExtension.getAny ());

    final ObjectFactory aObjFactory = new ObjectFactory ();
    m_aServiceGroup = aObjFactory.createServiceGroupType ();
    m_aServiceGroup.setParticipantIdentifier (PARTY_ID);

    // Be sure to delete if it exists.
    try {
      s_aDataMgr.deleteServiceGroup (SERVICEGROUP_ID, CREDENTIALS);
    }
    catch (final Exception ex) {}

    // Create a new one
    s_aDataMgr.saveServiceGroup (m_aServiceGroup, CREDENTIALS);

    m_aServiceMetadata = aObjFactory.createServiceMetadataType ();
    final ServiceInformationType aServiceInformation = aObjFactory.createServiceInformationType ();
    aServiceInformation.setDocumentIdentifier (DOCTYPE_ID);
    aServiceInformation.setParticipantIdentifier (PARTY_ID);
    aServiceInformation.setExtension (aExtension);
    {
      final ProcessListType processList = aObjFactory.createProcessListType ();
      {
        final ProcessType process = aObjFactory.createProcessType ();
        process.setProcessIdentifier (new SimpleProcessIdentifier (PROCESS_SCHEME, TEST_PROCESS_ID));
        process.setExtension (aExtension);
        {
          final ServiceEndpointList serviceEndpointList = aObjFactory.createServiceEndpointList ();
          {
            final EndpointType endpoint = aObjFactory.createEndpointType ();
            endpoint.setCertificate (CERTIFICATE);
            endpoint.setEndpointReference (W3CEndpointReferenceUtils.createEndpointReference (ADDRESS));
            endpoint.setMinimumAuthenticationLevel (MINIMUM_AUTH_LEVEL);
            endpoint.setRequireBusinessLevelSignature (REQUIRE_SIGNATURE);
            endpoint.setServiceActivationDate (ACTIVIATION_DATE);
            endpoint.setServiceDescription (DESCRIPTION);
            endpoint.setServiceExpirationDate (EXPIRATION_DATE);
            endpoint.setExtension (aExtension);
            endpoint.setTechnicalContactUrl (TECH_CONTACT);
            endpoint.setTechnicalInformationUrl (TECH_INFO);
            endpoint.setTransportProfile (TRANSPORT_PROFILE);
            serviceEndpointList.getEndpoint ().add (endpoint);
          }
          process.setServiceEndpointList (serviceEndpointList);
        }
        processList.getProcess ().add (process);
      }
      aServiceInformation.setProcessList (processList);
    }
    m_aServiceMetadata.setServiceInformation (aServiceInformation);
    m_sServiceMetadata = XmlTestUtils.marshall(m_aServiceMetadata);
  }

  private void createUser() throws Throwable {
      String username = "CN=SMP_1000000181,O=DIGIT,C=DK:123456789";
      String password = "123456789";
      DBUser aDBUser = s_aDataMgr.getCurrentEntityManager().find(DBUser.class, username);
      init();
      if(aDBUser == null){
          aDBUser = new DBUser();
          aDBUser.setUsername(username);
          aDBUser.setPassword(password);
          s_aDataMgr.getCurrentEntityManager().persist(aDBUser);
          s_aDataMgr.getCurrentEntityManager().getTransaction().commit();
      }else{
          if(!aDBUser.getPassword().equals(password)){
              aDBUser.setPassword(password);
              s_aDataMgr.getCurrentEntityManager().merge(aDBUser);
              s_aDataMgr.getCurrentEntityManager().getTransaction().commit();
          }
      }
  }

  @Test
  public void testCreateServiceGroup () throws Throwable {
    m_aServiceGroup.getParticipantIdentifier ().setValue (PARTICIPANT_IDENTIFIER2);
    init();
    s_aDataMgr.saveServiceGroup (m_aServiceGroup, CREDENTIALS);

    final ParticipantIdentifierType aParticipantIdentifier2 = SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_IDENTIFIER2);
    final ServiceGroupType result = s_aDataMgr.getServiceGroup (aParticipantIdentifier2);
    assertNotNull (result);

    assertNull (result.getServiceMetadataReferenceCollection ());
    assertEquals (PARTICIPANT_IDENTIFIER_SCHEME, result.getParticipantIdentifier ().getScheme ());
    assertTrue (IdentifierUtils.areParticipantIdentifierValuesEqual (PARTICIPANT_IDENTIFIER2,
                                                                     result.getParticipantIdentifier ().getValue ()));
  }

  @Test
  public void testCreateServiceGroupInvalidPassword () throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials (USERNAME, "WRONG_PASSWORD");

    m_aServiceGroup.getParticipantIdentifier ().setValue (PARTICIPANT_IDENTIFIER2);
    try {
        init();
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
        init();
        s_aDataMgr.saveServiceGroup (m_aServiceGroup, aCredentials);
        fail ();
    }
    catch (final UnknownUserException ex) {}
  }

  @Test
  public void testDeleteServiceGroup () throws Throwable {
      init();
      s_aDataMgr.deleteServiceGroup (SERVICEGROUP_ID, CREDENTIALS);
      assertNull (s_aDataMgr.getServiceGroup (SERVICEGROUP_ID));
  }

  @Test
  public void testDeleteServiceGroupUnknownID () throws Throwable {
    final ParticipantIdentifierType aServiceGroupID2 = SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_IDENTIFIER2);
    try {
        init();
        s_aDataMgr.deleteServiceGroup (aServiceGroupID2, CREDENTIALS);
    }
    catch (final NotFoundException ex) {}
    assertNull (s_aDataMgr.getServiceGroup (aServiceGroupID2));
  }

  @Test
  public void testDeleteServiceGroupUnknownUser () throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials ("Unknown_User", PASSWORD);
    try {
        init();
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
        init();
        s_aDataMgr.deleteServiceGroup (SERVICEGROUP_ID, aCredentials);
        fail ();
    }
    catch (final UnauthorizedException ex) {}
  }

  @Test
  public void testCreateServiceMetadata () throws Throwable {
    // Save to DB
    s_aDataMgr.saveService (m_aServiceMetadata, m_sServiceMetadata, CREDENTIALS);

    // Retrieve from DB
    final String docDBServiceMetadata = s_aDataMgr.getService (SERVICEGROUP_ID, DOCTYPE_ID);
    final ServiceMetadataType aDBServiceMetadata = ServiceMetadataConverter.unmarshal(docDBServiceMetadata);
    assertNotNull (aDBServiceMetadata);

    final ProcessListType aOrigProcessList = m_aServiceMetadata.getServiceInformation ().getProcessList ();
    assertEquals (1, aOrigProcessList.getProcess ().size ());
    final ProcessType aOrigProcess = aOrigProcessList.getProcess ().get (0);
    assertEquals (1, aOrigProcess.getServiceEndpointList ().getEndpoint ().size ());
    final EndpointType aOrigEndpoint = aOrigProcess.getServiceEndpointList ().getEndpoint ().get (0);

    final ProcessType aDBProcess = aDBServiceMetadata.getServiceInformation ().getProcessList ().getProcess ().get (0);
    final EndpointType aDBEndpoint = aDBProcess.getServiceEndpointList ().getEndpoint ().get (0);

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
    assertEquals (aOrigEndpoint.getCertificate (), aDBEndpoint.getCertificate ());
    assertEquals (aOrigEndpoint.getMinimumAuthenticationLevel (), aDBEndpoint.getMinimumAuthenticationLevel ());
    assertEquals (aOrigEndpoint.getServiceDescription (), aDBEndpoint.getServiceDescription ());
    assertEquals (aOrigEndpoint.getTechnicalContactUrl (), aDBEndpoint.getTechnicalContactUrl ());
    assertEquals (aOrigEndpoint.getTechnicalInformationUrl (), aDBEndpoint.getTechnicalInformationUrl ());
    assertEquals (aOrigEndpoint.getTransportProfile (), aDBEndpoint.getTransportProfile ());
    assertEquals (W3CEndpointReferenceUtils.getAddress (aOrigEndpoint.getEndpointReference ()),
                  W3CEndpointReferenceUtils.getAddress (aDBEndpoint.getEndpointReference ()));

    isServiceMetadataToDelete = true;
  }

  @Test
  public void testCreateServiceMetadataUnknownUser () throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials ("Unknown_User", PASSWORD);
    try {
        init();
        s_aDataMgr.saveService (m_aServiceMetadata, m_sServiceMetadata, aCredentials);
        fail ();
    }
    catch (final UnknownUserException ex) {}
  }

  @Test
  public void testCreateServiceMetadataWrongPass () throws Throwable {
    final BasicAuthClientCredentials aCredentials = new BasicAuthClientCredentials (USERNAME, "WrongPassword");
    try {
        init();
        s_aDataMgr.saveService (m_aServiceMetadata, m_sServiceMetadata, aCredentials);
        s_aDataMgr.getCurrentEntityManager().getTransaction().commit();
        fail ();
    }
    catch (final UnauthorizedException ex) {}
  }

  @Test
  public void testPrintServiceMetadata () throws Throwable {
    // Ensure something is present :)
      init();
      isServiceMetadataToDelete = true;
      s_aDataMgr.saveService (m_aServiceMetadata, m_sServiceMetadata, CREDENTIALS);
      System.out.println (s_aDataMgr.getService (SERVICEGROUP_ID, DOCTYPE_ID));
  }

  @Test
  public void testDeleteServiceMetadata () throws Throwable {
    // Ensure something is present :)
    s_aDataMgr.saveService (m_aServiceMetadata, m_sServiceMetadata, CREDENTIALS);

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

  @Test
  public void testSaveServiceGroupByCertificate() throws Throwable {
      // # Authentication #
      String username = "CN=SMP_1000000181,O=DIGIT,C=DK:123456789";
      String password = "123456789";
      BasicAuthClientCredentials auth = new BasicAuthClientCredentials(username,password);

      // # Delete after leaving test #
      isServiceGroupToDelete = true;

      // # Save ServiceGroup #
      init();
      String participantId = PARTICIPANT_IDENTIFIER2 + "654987";
      m_aServiceGroup = createServiceGroup(participantId);
      s_aDataMgr.saveServiceGroup(m_aServiceGroup, auth);

      // # Check ServiceGroup after save #
      ServiceGroupType result = s_aDataMgr.getServiceGroup(m_aServiceGroup.getParticipantIdentifier());
      assertNotNull(result);
      assertNull(result.getServiceMetadataReferenceCollection());
      assertEquals(PARTICIPANT_IDENTIFIER_SCHEME, result.getParticipantIdentifier().getScheme());
      assertTrue(IdentifierUtils.areParticipantIdentifierValuesEqual(participantId,
              result.getParticipantIdentifier().getValue()));

      // # Check Ownership #
      assertNotNull( s_aDataMgr.getCurrentEntityManager().find(DBOwnership.class, new DBOwnershipID(auth.getUserName(), m_aServiceGroup.getParticipantIdentifier())));
  }

  @Test(expected = UnauthorizedException.class)
  public void testSaveServiceGroupByCertificateNotOwner() throws Throwable {
    String participantId = PARTICIPANT_IDENTIFIER2 + "951842";
    String certificateIdentifierHeader = "CN=SMP_1000000181,O=DIGIT,C=DK:123456789";
    ServiceGroupType serviceGroup = createServiceGroup(participantId);

    s_aDataMgr.saveServiceGroup(serviceGroup, CREDENTIALS);

    BasicAuthClientCredentials auth = new BasicAuthClientCredentials(certificateIdentifierHeader, "100password");
    s_aDataMgr.saveServiceGroup(serviceGroup, auth);
  }

  @Test(expected = UnknownUserException.class)
  public void testSaveServiceGroupByCertificateNotFound() throws Throwable {
    String certificateIdentifierHeader = "CN=SMP_123456789,O=DIGIT,C=PT:123456789";

    ServiceGroupType serviceGroup = createServiceGroup(certificateIdentifierHeader);
    BasicAuthClientCredentials auth = new BasicAuthClientCredentials(certificateIdentifierHeader, "100password");
    s_aDataMgr.saveServiceGroup(serviceGroup, auth);
  }

  @Test(expected = UnknownUserException.class)
  public void testSaveServiceGroupByUserNotFound() throws Throwable {
    String participantId = PARTICIPANT_IDENTIFIER2 + "123456789";
    ServiceGroupType serviceGroup = createServiceGroup(participantId);

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
  }

  @Test(expected = NullPointerException.class)
  public void verifyNullUser() throws Throwable {
        BasicAuthClientCredentials auth = new BasicAuthClientCredentials(null, "WrongPass");
        DBUser user = s_aDataMgr._verifyUser(auth);
  }

  private void init() throws Throwable {
    if (!s_aDataMgr.getCurrentEntityManager().getTransaction().isActive()) {
      s_aDataMgr.getCurrentEntityManager().getTransaction().begin();
    }
  }

  private ServiceGroupType createServiceGroup(String participantId) {
    final ObjectFactory aObjFactory = new ObjectFactory();
    ServiceGroupType m_aServiceGroup = aObjFactory.createServiceGroupType();
    m_aServiceGroup.setParticipantIdentifier(PARTY_ID);
    m_aServiceGroup.getParticipantIdentifier().setValue(participantId);

    return m_aServiceGroup;
  }

  @After
  public void deleteServiceGroup() throws Throwable {
      if (isServiceGroupToDelete) {
          init();
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
            init();
            DBServiceMetadata serviceMetadata = s_aDataMgr.getCurrentEntityManager().find(DBServiceMetadata.class, new DBServiceMetadataID(SERVICEGROUP_ID, DOCTYPE_ID));
            if (serviceMetadata != null) {
                s_aDataMgr.getCurrentEntityManager().remove(serviceMetadata);
                isServiceMetadataToDelete = false;
            }
        }
    }

  @AfterClass
  public static void deleteUser() throws Throwable {
        String username = "CN=SMP_1000000181,O=DIGIT,C=DK:123456789";
        DBUser aDBUser = s_aDataMgr.getCurrentEntityManager().find(DBUser.class, username);
        if(aDBUser != null){
            if (!s_aDataMgr.getCurrentEntityManager().getTransaction().isActive()) {
              s_aDataMgr.getCurrentEntityManager().getTransaction().begin();
            }
            s_aDataMgr.getCurrentEntityManager().remove(aDBUser);
        }
    }
}