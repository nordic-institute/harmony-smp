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
package eu.europa.ec.cipa.sml.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.servicemetadata.manageservicemetadataservice._1.NotFoundFault;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.helger.commons.annotations.Nonempty;

import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.sml.AbstractSMLClientTest;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@Ignore
public final class LibraryTest extends AbstractSMLClientTest {
  private static final String SMP_ID = "SMP-TEST-ID";
  private static final String SMP_ID2 = "SMP-TEST-ID2";

  private static final String L_ENDPOINTADDRESS = "http://test.com";
  private static final String P_ENDPOINTADDRESS = "127.0.0.1";

  private static final String TEST_BUSINESS_IDENTIFIER1 = "0088:5798000999999";
  private static final String TEST_BUSINESS_IDENTIFIER2 = "0088:5798009999999";

  private ManageServiceMetadataServiceCaller m_aClient;
  private ServiceMetadataPublisherServiceType m_aServiceMetadataCreate;

  @Nonnull
  private static ServiceMetadataPublisherServiceType _createSmp (@Nonnull final ManageServiceMetadataServiceCaller aSMPClient,
                                                                 @Nonnull @Nonempty final String sSMPID) throws Exception {
    final ServiceMetadataPublisherServiceType aServiceMetadataCreate = new ServiceMetadataPublisherServiceType ();
    aServiceMetadataCreate.setServiceMetadataPublisherID (sSMPID);
    final PublisherEndpointType aEndpoint = new PublisherEndpointType ();
    aEndpoint.setLogicalAddress (L_ENDPOINTADDRESS);
    aEndpoint.setPhysicalAddress (P_ENDPOINTADDRESS);
    aServiceMetadataCreate.setPublisherEndpoint (aEndpoint);

    aSMPClient.create (aServiceMetadataCreate);
    return aServiceMetadataCreate;
  }

  @Before
  public void cleanup () throws Exception {
    m_aClient = new ManageServiceMetadataServiceCaller (SML_INFO);
    try {
      m_aClient.delete (SMP_ID);
    }
    catch (final NotFoundFault e) {
      // This is fine, since we are just cleaning
    }

    m_aServiceMetadataCreate = _createSmp (m_aClient, SMP_ID);
  }

  @Ignore
  @Test
  public void dnsCreationTest () throws Exception {
    final ManageParticipantIdentifierServiceCaller biClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);
    final ParticipantIdentifierType aPI = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1);
    try {
      biClient.delete (aPI);
    }
    catch (final org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault e) {
      // Do nothing since we just want to make sure it doesn't exists.
    }

    final String sDNSString = "http://" + TEST_BUSINESS_IDENTIFIER1 + ".0010.ubis.sml.smloc.org/";

    InetAddress aAddress;
    try {
      aAddress = InetAddress.getByName (sDNSString);
      fail ("The hostname shouldn't exist");
    }
    catch (final UnknownHostException e) {
      // This should happen!
    }

    biClient.create (m_aServiceMetadataCreate.getServiceMetadataPublisherID (), aPI);

    aAddress = InetAddress.getByName (sDNSString);
    assertEquals (P_ENDPOINTADDRESS, aAddress.getHostAddress ());
  }

  @Test
  public void testManageServiceMetadata () throws Exception {
    final ServiceMetadataPublisherServiceType aServiceMetadataRead = m_aClient.read (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());

    m_aClient.delete (aServiceMetadataRead.getServiceMetadataPublisherID ());

    assertEquals (m_aServiceMetadataCreate.getServiceMetadataPublisherID (),
                  aServiceMetadataRead.getServiceMetadataPublisherID ());
    assertEquals (m_aServiceMetadataCreate.getPublisherEndpoint ().getLogicalAddress (),
                  aServiceMetadataRead.getPublisherEndpoint ().getLogicalAddress ());
    assertEquals (m_aServiceMetadataCreate.getPublisherEndpoint ().getPhysicalAddress (),
                  aServiceMetadataRead.getPublisherEndpoint ().getPhysicalAddress ());
  }

  @Test
  public void testManageServiceMetadataWithManyIdentifier () throws Exception {
    final ManageParticipantIdentifierServiceCaller aPIClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final long nStartIdentifier = 5798000999999l;
    final int nLastIdentifier = 150;
    for (int i = 0; i <= nLastIdentifier; i++) {
      System.out.println ("Creating number: " + i);
      final long nIdentifier = nStartIdentifier + i;
      aPIClient.create (m_aServiceMetadataCreate.getServiceMetadataPublisherID (),
                        SimpleParticipantIdentifier.createWithDefaultScheme ("0088:" + nIdentifier));
    }

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());

    final ServiceMetadataPublisherServiceType aServiceMetadataCreateNew = _createSmp (m_aClient, SMP_ID);

    // Delete one that was on a second page
    final long nIdentifier = nStartIdentifier + nLastIdentifier;
    aPIClient.create (aServiceMetadataCreateNew.getServiceMetadataPublisherID (),
                      SimpleParticipantIdentifier.createWithDefaultScheme ("0088:" + nIdentifier));
  }

  @Test (expected = NotFoundFault.class)
  public void testManageServiceMetadataDoubleDelete () throws Exception {
    final ServiceMetadataPublisherServiceType aServiceMetadataRead = m_aClient.read (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    assertNotNull (aServiceMetadataRead);
    m_aClient.delete (aServiceMetadataRead.getServiceMetadataPublisherID ());
    // Delete again
    m_aClient.delete (aServiceMetadataRead.getServiceMetadataPublisherID ());
  }

  @Test
  public void manageServiceMetadataUpdateTest () throws Exception {
    final ServiceMetadataPublisherServiceType aServiceMetadataRead = m_aClient.read (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    assertNotNull (aServiceMetadataRead);
    aServiceMetadataRead.getPublisherEndpoint ().setPhysicalAddress ("173.156.1.1");
    m_aClient.update (aServiceMetadataRead);
    final ServiceMetadataPublisherServiceType aAfterSignedServiceMetadataRead = m_aClient.read (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    m_aClient.delete (aAfterSignedServiceMetadataRead.getServiceMetadataPublisherID ());
    assertEquals ("173.156.1.1", aAfterSignedServiceMetadataRead.getPublisherEndpoint ().getPhysicalAddress ());
  }

  @Test
  public void testManageBusinessIdentifier () throws Exception {
    final ManageParticipantIdentifierServiceCaller aPIClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final ParticipantIdentifierType aBusinessIdentifierCreate = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1);

    final ServiceMetadataPublisherServiceForParticipantType saSrviceMetadataPublisherServiceForBusiness = new ServiceMetadataPublisherServiceForParticipantType ();
    saSrviceMetadataPublisherServiceForBusiness.setParticipantIdentifier (aBusinessIdentifierCreate);
    saSrviceMetadataPublisherServiceForBusiness.setServiceMetadataPublisherID (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());

    aPIClient.create (saSrviceMetadataPublisherServiceForBusiness);

    final ParticipantIdentifierPageType aResult = aPIClient.list ("",
                                                                  m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    assertNotNull (aResult);
    final List <ParticipantIdentifierType> aBusinessIdentifiers = aResult.getParticipantIdentifier ();

    assertEquals (1, aBusinessIdentifiers.size ());

    final ParticipantIdentifierType aBusinessIdentifierRead = aBusinessIdentifiers.get (0);

    assertEquals (aBusinessIdentifierCreate.getScheme (), aBusinessIdentifierRead.getScheme ());
    assertEquals (aBusinessIdentifierCreate.getValue (), aBusinessIdentifierRead.getValue ());

    aPIClient.deleteList (aBusinessIdentifiers);

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
  }

  @Test (expected = org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault.class)
  public void testManageBusinessIdentifierDoubleDelete () throws Exception {
    final ManageParticipantIdentifierServiceCaller aPIClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final ParticipantIdentifierType aBusinessIdentifierCreate = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1);

    final ServiceMetadataPublisherServiceForParticipantType aServiceMetadataPublisherServiceForBusiness = new ServiceMetadataPublisherServiceForParticipantType ();
    aServiceMetadataPublisherServiceForBusiness.setParticipantIdentifier (aBusinessIdentifierCreate);
    aServiceMetadataPublisherServiceForBusiness.setServiceMetadataPublisherID (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());

    aPIClient.create (aServiceMetadataPublisherServiceForBusiness);

    final ParticipantIdentifierPageType aResult = aPIClient.list ("",
                                                                  m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    assertNotNull (aResult);
    final List <ParticipantIdentifierType> aBusinessIdentifiers = aResult.getParticipantIdentifier ();

    assertEquals (1, aBusinessIdentifiers.size ());

    final ParticipantIdentifierType aBusinessIdentifierRead = aBusinessIdentifiers.get (0);

    assertEquals (aBusinessIdentifierCreate.getScheme (), aBusinessIdentifierRead.getScheme ());
    assertEquals (aBusinessIdentifierCreate.getValue (), aBusinessIdentifierRead.getValue ());

    aPIClient.deleteList (aBusinessIdentifiers);
    aPIClient.deleteList (aBusinessIdentifiers);

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
  }

  @Test
  public void testManageBusinessIdentifierListWithZeroElements () throws Exception {
    final ManageParticipantIdentifierServiceCaller aPIClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final Collection <ParticipantIdentifierType> aRecipientBusinessIdentifiers = new ArrayList <ParticipantIdentifierType> ();

    aPIClient.createList (aRecipientBusinessIdentifiers, SMP_ID);

    final ParticipantIdentifierPageType aResult = aPIClient.list ("",
                                                                  m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    assertNotNull (aResult);

    final List <ParticipantIdentifierType> aBusinessIdentifiers = aResult.getParticipantIdentifier ();
    assertEquals (0, aBusinessIdentifiers.size ());

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
  }

  @Test
  public void testManageBusinessIdentifierListWithOneElement () throws Exception {
    final ManageParticipantIdentifierServiceCaller aPIClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final Collection <ParticipantIdentifierType> aRecipientBusinessIdentifiers = new ArrayList <ParticipantIdentifierType> ();

    final ParticipantIdentifierType aBusinessIdentifierCreate1 = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1);

    aRecipientBusinessIdentifiers.add (aBusinessIdentifierCreate1);
    aPIClient.createList (aRecipientBusinessIdentifiers, SMP_ID);

    final ParticipantIdentifierPageType aResult = aPIClient.list ("",
                                                                  m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    assertNotNull (aResult);

    final List <ParticipantIdentifierType> aBusinessIdentifiers = aResult.getParticipantIdentifier ();

    assertEquals (1, aBusinessIdentifiers.size ());

    final ParticipantIdentifierType aBusinessIdentifierRead = aBusinessIdentifiers.get (0);

    assertEquals (aBusinessIdentifierCreate1.getScheme (), aBusinessIdentifierRead.getScheme ());
    assertEquals (aBusinessIdentifierCreate1.getValue (), aBusinessIdentifierRead.getValue ());

    aPIClient.deleteList (aBusinessIdentifiers);

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
  }

  @Test
  public void testManageBusinessIdentifierListWithTwoElement () throws Exception {
    final ManageParticipantIdentifierServiceCaller aPIClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final Map <String, ParticipantIdentifierType> aBusinessIdentifiersCreate = new HashMap <String, ParticipantIdentifierType> ();

    ParticipantIdentifierType aBusinessIdentifierCreate1 = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1);
    ParticipantIdentifierType aBusinessIdentifierCreate2 = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER2);

    aBusinessIdentifiersCreate.put (aBusinessIdentifierCreate1.getValue (), aBusinessIdentifierCreate1);
    aBusinessIdentifiersCreate.put (aBusinessIdentifierCreate2.getValue (), aBusinessIdentifierCreate2);

    aPIClient.createList (aBusinessIdentifiersCreate.values (), SMP_ID);

    final ParticipantIdentifierPageType aResult = aPIClient.list ("",
                                                                  m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    assertNotNull (aResult);

    final List <ParticipantIdentifierType> aBusinessIdentifiers = aResult.getParticipantIdentifier ();

    assertEquals (2, aBusinessIdentifiers.size ());

    final ParticipantIdentifierType aBusinessIdentifierRead1 = aBusinessIdentifiers.get (0);
    aBusinessIdentifierCreate1 = aBusinessIdentifiersCreate.get (aBusinessIdentifierRead1.getValue ());

    final ParticipantIdentifierType aBusinessIdentifierRead2 = aBusinessIdentifiers.get (1);
    aBusinessIdentifierCreate2 = aBusinessIdentifiersCreate.get (aBusinessIdentifierRead2.getValue ());

    assertEquals (aBusinessIdentifierCreate1.getScheme (), aBusinessIdentifierRead1.getScheme ());
    assertEquals (aBusinessIdentifierCreate1.getValue (), aBusinessIdentifierRead1.getValue ());

    assertEquals (aBusinessIdentifierCreate2.getScheme (), aBusinessIdentifierRead2.getScheme ());
    assertEquals (aBusinessIdentifierCreate2.getValue (), aBusinessIdentifierRead2.getValue ());

    aPIClient.deleteList (aBusinessIdentifiers);

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
  }

  @Test
  public void migrateTest () throws Exception {
    final ManageParticipantIdentifierServiceCaller aPIClientOld = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final ManageServiceMetadataServiceCaller aClient2 = new ManageServiceMetadataServiceCaller (SML_INFO);
    try {
      aClient2.delete (SMP_ID2);
    }
    catch (final NotFoundFault e) {
      // This is fine, since we are just cleaning
    }

    _createSmp (aClient2, SMP_ID2);

    final ManageParticipantIdentifierServiceCaller aPIClientNew = new ManageParticipantIdentifierServiceCaller (SML_INFO);
    final ParticipantIdentifierType aPI = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1);
    aPIClientOld.create (SMP_ID, aPI);
    final UUID aMigrationKey = aPIClientOld.prepareToMigrate (aPI, SMP_ID);
    assertNotNull (aMigrationKey);
    aPIClientNew.migrate (aPI, aMigrationKey, SMP_ID2);

    try {
      aPIClientOld.delete (aPI);
      fail ();
    }
    catch (final org.busdox.servicemetadata.managebusinessidentifierservice._1.UnauthorizedFault e) {
      // This must happen.
    }

    // Should be able to delete, since New is now the owner
    aPIClientNew.delete (aPI);
  }

  @Test (expected = org.busdox.servicemetadata.managebusinessidentifierservice._1.BadRequestFault.class)
  public void createExistingBusinessIdentifierUnauthorized () throws Exception {
    final ManageParticipantIdentifierServiceCaller aPIClientOld = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final ManageServiceMetadataServiceCaller aClient2 = new ManageServiceMetadataServiceCaller (SML_INFO);
    try {
      aClient2.delete (SMP_ID2);
    }
    catch (final NotFoundFault e) {
      // This is fine, since we are just cleaning
    }

    _createSmp (aClient2, SMP_ID2);

    final ManageParticipantIdentifierServiceCaller aPIClientNew = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    aPIClientOld.create (SMP_ID, SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1));

    aPIClientNew.create (SMP_ID, SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1));
  }
}
