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
package eu.europa.ec.cipa.sml.server.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.busdox.servicemetadata.locator._1.MigrationRecordType;
import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.busdox.servicemetadata.locator._1.PageRequestType;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

import com.phloc.commons.annotations.DevelopersNote;

import eu.europa.ec.cipa.peppol.identifier.CIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.sml.server.IParticipantDataHandler;
import eu.europa.ec.cipa.sml.server.ISMPDataHandler;
import eu.europa.ec.cipa.sml.server.exceptions.BadRequestException;
import eu.europa.ec.cipa.sml.server.exceptions.NotFoundException;
import eu.europa.ec.cipa.sml.server.exceptions.UnauthorizedException;

/**
 * Test class for class {@link JPAParticipantDataHandler} and
 * {@link JPASMPDataHandler}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@DevelopersNote ("You need to adjust your local META-INF/persistence.xml file to run this test")
public final class JPADataHandlerTest {
  private static final String PARTICIPANT_IDENTIFIER_SCHEME = CIdentifier.DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME;
  private static final String PARTICIPANT_IDENTIFIER_DEFAULT = "0010:5999000000001";

  private static final String CLIENT_UNIQUE_ID = "IntegrationTestUser1";

  private static final String SMP_ID = "TEST-SMP-ID1";
  private static final String SMP_LOGICAL_ADDRESS = "http://test.com";
  private static final String SMP_PHYSICAL_ADDRESS = "198.0.0.1";

  private static final String CLIENT_UNIQUE_ID2 = "IntegrationTestUser2";

  private static final String SMP_ID2 = "TEST-SMP-ID2";
  private static final String SMP_LOGICAL_ADDRESS2 = "http://test2.com";
  private static final String SMP_PHYSICAL_ADDRESS2 = "198.0.0.2";

  // Static for every test.
  private static final ObjectFactory s_aObjFactory = new ObjectFactory ();
  private static IParticipantDataHandler s_aParticipantHandler;
  private static ISMPDataHandler s_aSMPHandler;

  // These variables are reset on every test.
  private ServiceMetadataPublisherServiceType m_aSMPService;
  private ParticipantIdentifierPageType m_aParticipantIdentifierPage;

  @BeforeClass
  public static void initDataHandler () {
    try {
      SMLJPAWrapper.getInstance ();
    }
    catch (final ExceptionInInitializerError ex) {
      // No database present
      throw new AssumptionViolatedException ("No database connection could be established");
    }
    s_aParticipantHandler = new JPAParticipantDataHandler ();
    s_aSMPHandler = new JPASMPDataHandler ();
  }

  @Before
  public void initTest () throws Exception {
    m_aSMPService = s_aObjFactory.createServiceMetadataPublisherServiceType ();
    m_aSMPService.setServiceMetadataPublisherID (SMP_ID);
    final PublisherEndpointType endpoint = s_aObjFactory.createPublisherEndpointType ();
    endpoint.setLogicalAddress (SMP_LOGICAL_ADDRESS);
    endpoint.setPhysicalAddress (SMP_PHYSICAL_ADDRESS);
    m_aSMPService.setPublisherEndpoint (endpoint);

    try {
      s_aSMPHandler.deleteSMPData (SMP_ID, CLIENT_UNIQUE_ID);
    }
    catch (final NotFoundException e) {
      // This is ok, since we just want to make sure it isn't there.
    }
    try {
      s_aSMPHandler.deleteSMPData (SMP_ID2, CLIENT_UNIQUE_ID2);
    }
    catch (final NotFoundException e) {
      // This is ok, since we just want to make sure it isn't there.
    }

    s_aSMPHandler.createSMPData (m_aSMPService, CLIENT_UNIQUE_ID);

    // Create default participant identifier
    m_aParticipantIdentifierPage = s_aObjFactory.createParticipantIdentifierPageType ();
    m_aParticipantIdentifierPage.setNextPageIdentifier ("");
    m_aParticipantIdentifierPage.setServiceMetadataPublisherID (SMP_ID);
    m_aParticipantIdentifierPage.getParticipantIdentifier ()
                                .add (SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_IDENTIFIER_DEFAULT));
  }

  /*
   * Test service metadata methods
   */
  @Test
  public void testCreateMetadataNotAllowed () throws Exception {
    try {
      s_aSMPHandler.deleteSMPData (SMP_ID, "WRONG_USER_ID");
      fail ("The registry was created successfully.");
    }
    catch (final UnauthorizedException e) {
      // This must happen.
    }

    // First make sure it doesn't exist.
    try {
      s_aSMPHandler.deleteSMPData (SMP_ID, CLIENT_UNIQUE_ID);
    }
    catch (final NotFoundException e) {
      // This is ok, since we just want to make sure it isn't there.
    }
  }

  @Test (expected = BadRequestException.class)
  public void testCreateMetadataAlreadyExist () throws Exception {
    s_aSMPHandler.createSMPData (m_aSMPService, CLIENT_UNIQUE_ID);
    fail ("The registry was created successfully.");
  }

  @Test (expected = BadRequestException.class)
  public void testCreateMetadataNotOwnedID () throws Exception {
    s_aSMPHandler.createSMPData (m_aSMPService, CLIENT_UNIQUE_ID2);
    fail ("The registry was created successfully.");
  }

  @Test (expected = UnauthorizedException.class)
  public void testDeleteMetadataNotOwnedID () throws Exception {
    s_aSMPHandler.deleteSMPData (SMP_ID, CLIENT_UNIQUE_ID2);
    fail ("The registry was deleted successfully.");
  }

  @Test
  public void testDeleteMetadata () throws Exception {
    s_aSMPHandler.deleteSMPData (SMP_ID, CLIENT_UNIQUE_ID);

    try {
      s_aSMPHandler.getSMPData (SMP_ID, CLIENT_UNIQUE_ID);
      fail ("The metadata could be read.");
    }
    catch (final NotFoundException e) {
      // This must happen.
    }
  }

  @Test
  public void testUpdateMetadata () throws Exception {
    final PublisherEndpointType endpoint = m_aSMPService.getPublisherEndpoint ();
    endpoint.setLogicalAddress (SMP_LOGICAL_ADDRESS2);
    endpoint.setPhysicalAddress (SMP_PHYSICAL_ADDRESS2);

    s_aSMPHandler.updateSMPData (m_aSMPService, CLIENT_UNIQUE_ID);

    final ServiceMetadataPublisherServiceType result = s_aSMPHandler.getSMPData (SMP_ID, CLIENT_UNIQUE_ID);

    assertEquals (SMP_LOGICAL_ADDRESS2, result.getPublisherEndpoint ().getLogicalAddress ());
    assertEquals (SMP_PHYSICAL_ADDRESS2, result.getPublisherEndpoint ().getPhysicalAddress ());
  }

  @Test (expected = UnauthorizedException.class)
  public void testUpdateMetadataNotOwned () throws Exception {
    final PublisherEndpointType endpoint = m_aSMPService.getPublisherEndpoint ();
    endpoint.setLogicalAddress (SMP_LOGICAL_ADDRESS2);
    endpoint.setPhysicalAddress (SMP_PHYSICAL_ADDRESS2);

    s_aSMPHandler.updateSMPData (m_aSMPService, CLIENT_UNIQUE_ID2);
  }

  @Test (expected = NotFoundException.class)
  public void testUpdateMetadataNotExist () throws Exception {
    m_aSMPService.setServiceMetadataPublisherID (SMP_ID2);
    s_aSMPHandler.updateSMPData (m_aSMPService, CLIENT_UNIQUE_ID2);
  }

  /*
   * Test participant identifier methods
   */
  @Test
  public void testCreateParticipantIdentifier () throws Exception {
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);

    final PageRequestType request = s_aObjFactory.createPageRequestType ();
    request.setServiceMetadataPublisherID (SMP_ID);
    final ParticipantIdentifierPageType result = s_aParticipantHandler.listParticipantIdentifiers (request,
                                                                                                   CLIENT_UNIQUE_ID);

    assertEquals (1, result.getParticipantIdentifier ().size ());
    assertEquals (PARTICIPANT_IDENTIFIER_DEFAULT, result.getParticipantIdentifier ().get (0).getValue ());
    assertEquals (PARTICIPANT_IDENTIFIER_SCHEME, result.getParticipantIdentifier ().get (0).getScheme ());
  }

  @Test (expected = BadRequestException.class)
  public void testCreateParticipantIdentifierNotExistID () throws Exception {
    m_aParticipantIdentifierPage.setServiceMetadataPublisherID (SMP_ID2);
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);
  }

  @Test (expected = UnauthorizedException.class)
  public void testCreateParticipantIdentifierNotOwnedID () throws Exception {
    try {
      m_aSMPService.setServiceMetadataPublisherID (SMP_ID2);
      s_aSMPHandler.createSMPData (m_aSMPService, CLIENT_UNIQUE_ID2);
    }
    catch (final UnauthorizedException e) {
      fail ("The create of second metadata shouldn't fail.");
    }

    m_aParticipantIdentifierPage.setServiceMetadataPublisherID (SMP_ID2);
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);
  }

  @Test (expected = BadRequestException.class)
  public void testCreateParticipantIdentifierAlreadySelfOwned () throws Exception {
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);
  }

  @Test (expected = BadRequestException.class)
  public void testCreateParticipantIdentifierAlreadyOwnedByOther () throws Exception {
    try {
      m_aSMPService.setServiceMetadataPublisherID (SMP_ID2);
      s_aSMPHandler.createSMPData (m_aSMPService, CLIENT_UNIQUE_ID2);
    }
    catch (final BadRequestException e) {
      fail ("The create of second metadata shouldn't fail.");
    }

    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);
    m_aParticipantIdentifierPage.setServiceMetadataPublisherID (SMP_ID2);
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID2);
  }

  @Test
  public void testDeleteParticipantIdentifier () throws Exception {
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);

    s_aParticipantHandler.deleteParticipantIdentifiers (m_aParticipantIdentifierPage.getParticipantIdentifier (),
                                                        CLIENT_UNIQUE_ID);

    final PageRequestType request = s_aObjFactory.createPageRequestType ();
    request.setServiceMetadataPublisherID (SMP_ID);

    final ParticipantIdentifierPageType result = s_aParticipantHandler.listParticipantIdentifiers (request,
                                                                                                   CLIENT_UNIQUE_ID);

    assertEquals (0, result.getParticipantIdentifier ().size ());
  }

  @Test (expected = UnauthorizedException.class)
  public void testDeleteParticipantIdentifierNotExistID () throws Exception {
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);

    m_aParticipantIdentifierPage.setServiceMetadataPublisherID (SMP_ID2);
    s_aParticipantHandler.deleteParticipantIdentifiers (m_aParticipantIdentifierPage.getParticipantIdentifier (),
                                                        CLIENT_UNIQUE_ID2);
  }

  @Test (expected = UnauthorizedException.class)
  public void testDeleteParticipantIdentifierNotOwnedID () throws Exception {
    try {
      m_aSMPService.setServiceMetadataPublisherID (SMP_ID2);
      s_aSMPHandler.createSMPData (m_aSMPService, CLIENT_UNIQUE_ID2);
    }
    catch (final UnauthorizedException e) {
      fail ("The create of second metadata shouldn't fail.");
    }

    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);

    m_aParticipantIdentifierPage.setServiceMetadataPublisherID (SMP_ID2);
    s_aParticipantHandler.deleteParticipantIdentifiers (m_aParticipantIdentifierPage.getParticipantIdentifier (),
                                                        CLIENT_UNIQUE_ID2);
  }

  @Test
  public void testListParticipantIdentifiers () throws Exception {
    final ParticipantIdentifierType secondIdentifier = SimpleParticipantIdentifier.createWithDefaultScheme ("0010:599900000002");

    m_aParticipantIdentifierPage.getParticipantIdentifier ().add (secondIdentifier);
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);

    final PageRequestType request = s_aObjFactory.createPageRequestType ();
    request.setServiceMetadataPublisherID (SMP_ID);
    final ParticipantIdentifierPageType result = s_aParticipantHandler.listParticipantIdentifiers (request,
                                                                                                   CLIENT_UNIQUE_ID);

    assertEquals (2, result.getParticipantIdentifier ().size ());
  }

  @Test
  public void testMigration () throws Exception {
    // Make sure that ID1 owns identifier
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);

    // Make sure that ID2 exist.
    m_aSMPService.setServiceMetadataPublisherID (SMP_ID2);
    s_aSMPHandler.createSMPData (m_aSMPService, CLIENT_UNIQUE_ID2);

    final MigrationRecordType migrationRecord = s_aObjFactory.createMigrationRecordType ();
    migrationRecord.setMigrationKey ("EXISTING_KEY");
    migrationRecord.setParticipantIdentifier (SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_IDENTIFIER_DEFAULT));
    migrationRecord.setServiceMetadataPublisherID (SMP_ID);

    s_aParticipantHandler.prepareToMigrate (migrationRecord, CLIENT_UNIQUE_ID);

    migrationRecord.setServiceMetadataPublisherID (SMP_ID2);
    s_aParticipantHandler.migrate (migrationRecord, CLIENT_UNIQUE_ID2);

    final PageRequestType request = s_aObjFactory.createPageRequestType ();
    request.setServiceMetadataPublisherID (SMP_ID2);
    final ParticipantIdentifierPageType resultID2 = s_aParticipantHandler.listParticipantIdentifiers (request,
                                                                                                      CLIENT_UNIQUE_ID2);

    assertEquals (1, resultID2.getParticipantIdentifier ().size ());
    assertEquals (PARTICIPANT_IDENTIFIER_DEFAULT, resultID2.getParticipantIdentifier ().get (0).getValue ());
    assertEquals (PARTICIPANT_IDENTIFIER_SCHEME, resultID2.getParticipantIdentifier ().get (0).getScheme ());

    request.setServiceMetadataPublisherID (SMP_ID);
    final ParticipantIdentifierPageType resultID1 = s_aParticipantHandler.listParticipantIdentifiers (request,
                                                                                                      CLIENT_UNIQUE_ID);
    assertEquals (0, resultID1.getParticipantIdentifier ().size ());
  }

  @Test (expected = NotFoundException.class)
  public void testMigrateNotExistingKeyAndIdentifier () throws Exception {
    final MigrationRecordType migrationRecord = s_aObjFactory.createMigrationRecordType ();
    migrationRecord.setMigrationKey ("NOT_EXISTING_KEY");
    migrationRecord.setParticipantIdentifier (SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_IDENTIFIER_DEFAULT));
    migrationRecord.setServiceMetadataPublisherID (SMP_ID);

    s_aParticipantHandler.migrate (migrationRecord, CLIENT_UNIQUE_ID);
  }

  @Test (expected = UnauthorizedException.class)
  public void testPrepareToMigrateNotOwnedIdentifier () throws Exception {
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);

    final MigrationRecordType migrationRecord = s_aObjFactory.createMigrationRecordType ();
    migrationRecord.setMigrationKey ("EXISTING_KEY");
    migrationRecord.setParticipantIdentifier (SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_IDENTIFIER_DEFAULT));

    s_aParticipantHandler.prepareToMigrate (migrationRecord, CLIENT_UNIQUE_ID2);
  }

  @Test
  public void testMigrationWrongKey () throws Exception {

    // Make sure that ID1 owns identifier
    s_aParticipantHandler.createParticipantIdentifiers (m_aParticipantIdentifierPage, CLIENT_UNIQUE_ID);

    // Make sure that ID2 exist.
    m_aSMPService.setServiceMetadataPublisherID (SMP_ID2);
    s_aSMPHandler.createSMPData (m_aSMPService, CLIENT_UNIQUE_ID2);

    final MigrationRecordType migrationRecord = s_aObjFactory.createMigrationRecordType ();
    migrationRecord.setMigrationKey ("EXISTING_KEY");
    migrationRecord.setParticipantIdentifier (SimpleParticipantIdentifier.createWithDefaultScheme (PARTICIPANT_IDENTIFIER_DEFAULT));

    s_aParticipantHandler.prepareToMigrate (migrationRecord, CLIENT_UNIQUE_ID);

    try {
      migrationRecord.setServiceMetadataPublisherID (SMP_ID2);
      migrationRecord.setMigrationKey ("WRONG_KEY");

      try {
        s_aParticipantHandler.migrate (migrationRecord, CLIENT_UNIQUE_ID2);
        fail ("Migrate must fail when key isn't found.");
      }
      catch (final NotFoundException e) {
        // This must happen
      }

      final PageRequestType request = s_aObjFactory.createPageRequestType ();
      request.setServiceMetadataPublisherID (SMP_ID);
      final ParticipantIdentifierPageType resultID2 = s_aParticipantHandler.listParticipantIdentifiers (request,
                                                                                                        CLIENT_UNIQUE_ID);

      assertEquals (1, resultID2.getParticipantIdentifier ().size ());
      assertEquals (PARTICIPANT_IDENTIFIER_DEFAULT, resultID2.getParticipantIdentifier ().get (0).getValue ());
      assertEquals (PARTICIPANT_IDENTIFIER_SCHEME, resultID2.getParticipantIdentifier ().get (0).getScheme ());

      request.setServiceMetadataPublisherID (SMP_ID2);
      final ParticipantIdentifierPageType resultID1 = s_aParticipantHandler.listParticipantIdentifiers (request,
                                                                                                        CLIENT_UNIQUE_ID2);

      assertEquals (0, resultID1.getParticipantIdentifier ().size ());
    }
    finally {
      // To avoid having the EXISTING_KEY in the DB!
      migrationRecord.setServiceMetadataPublisherID (SMP_ID2);
      migrationRecord.setMigrationKey ("EXISTING_KEY");
      s_aParticipantHandler.migrate (migrationRecord, CLIENT_UNIQUE_ID2);
    }
  }
}
