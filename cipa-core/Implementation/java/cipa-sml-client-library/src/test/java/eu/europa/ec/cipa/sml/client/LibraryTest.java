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
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.servicemetadata.manageservicemetadataservice._1.NotFoundFault;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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

  @Before
  public void cleanup () throws Exception {
    m_aClient = new ManageServiceMetadataServiceCaller (SML_INFO);
    try {
      m_aClient.delete (SMP_ID);
    }
    catch (final NotFoundFault e) {
      // This is fine, since we are just cleaning
    }

    m_aServiceMetadataCreate = createSmp (m_aClient, SMP_ID);
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

    final String dnsString = "http://" + TEST_BUSINESS_IDENTIFIER1 + ".0010.ubis.sml.smloc.org/";

    InetAddress address;
    try {
      address = InetAddress.getByName (dnsString);
      fail ("The hostname shouldn't exist");
    }
    catch (final UnknownHostException e) {
      // This should happen!
    }

    biClient.create (m_aServiceMetadataCreate.getServiceMetadataPublisherID (), aPI);

    address = InetAddress.getByName (dnsString);
    assertEquals (P_ENDPOINTADDRESS, address.getHostAddress ());
  }

  @Test
  public void testManageServiceMetadata () throws Exception {
    final ServiceMetadataPublisherServiceType serviceMetadataRead = m_aClient.read (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());

    m_aClient.delete (serviceMetadataRead.getServiceMetadataPublisherID ());

    Assert.assertEquals (m_aServiceMetadataCreate.getServiceMetadataPublisherID (),
                         serviceMetadataRead.getServiceMetadataPublisherID ());
    Assert.assertEquals (m_aServiceMetadataCreate.getPublisherEndpoint ().getLogicalAddress (),
                         serviceMetadataRead.getPublisherEndpoint ().getLogicalAddress ());
    Assert.assertEquals (m_aServiceMetadataCreate.getPublisherEndpoint ().getPhysicalAddress (),
                         serviceMetadataRead.getPublisherEndpoint ().getPhysicalAddress ());
  }

  @Test
  public void testManageServiceMetadataWithManyIdentifier () throws Exception {
    final ManageParticipantIdentifierServiceCaller biClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final long startIdentifier = 5798000999999l;
    final int lastIdentifier = 150;
    for (int i = 0; i <= lastIdentifier; i++) {
      System.out.println ("Creating number: " + i);
      final long identifier = startIdentifier + i;
      biClient.create (m_aServiceMetadataCreate.getServiceMetadataPublisherID (),
                       SimpleParticipantIdentifier.createWithDefaultScheme ("0088:" + identifier));
    }

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());

    final ServiceMetadataPublisherServiceType serviceMetadataCreateNew = createSmp (m_aClient, SMP_ID);

    // Delete one that was on a second page
    final long identifier = startIdentifier + lastIdentifier;
    biClient.create (serviceMetadataCreateNew.getServiceMetadataPublisherID (),
                     SimpleParticipantIdentifier.createWithDefaultScheme ("0088:" + identifier));
  }

  @Test (expected = NotFoundFault.class)
  public void testManageServiceMetadataDoubleDelete () throws Exception {
    final ServiceMetadataPublisherServiceType serviceMetadataRead = m_aClient.read (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    m_aClient.delete (serviceMetadataRead.getServiceMetadataPublisherID ());
    m_aClient.delete (serviceMetadataRead.getServiceMetadataPublisherID ());
  }

  @Test
  public void manageServiceMetadataUpdateTest () throws Exception {
    final ServiceMetadataPublisherServiceType serviceMetadataRead = m_aClient.read (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    serviceMetadataRead.getPublisherEndpoint ().setPhysicalAddress ("173.156.1.1");
    m_aClient.update (serviceMetadataRead);
    final ServiceMetadataPublisherServiceType afterSignedServiceMetadataRead = m_aClient.read (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    m_aClient.delete (afterSignedServiceMetadataRead.getServiceMetadataPublisherID ());
    Assert.assertEquals ("173.156.1.1", afterSignedServiceMetadataRead.getPublisherEndpoint ().getPhysicalAddress ());
  }

  @Test
  public void testManageBusinessIdentifier () throws Exception {
    final ManageParticipantIdentifierServiceCaller biClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final ParticipantIdentifierType businessIdentifierCreate = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1);

    final ServiceMetadataPublisherServiceForParticipantType serviceMetadataPublisherServiceForBusiness = new ServiceMetadataPublisherServiceForParticipantType ();
    serviceMetadataPublisherServiceForBusiness.setParticipantIdentifier (businessIdentifierCreate);
    serviceMetadataPublisherServiceForBusiness.setServiceMetadataPublisherID (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());

    biClient.create (serviceMetadataPublisherServiceForBusiness);

    final ParticipantIdentifierPageType res = biClient.list ("",
                                                             m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    final List <ParticipantIdentifierType> businessIdentifiers = res.getParticipantIdentifier ();

    Assert.assertEquals (1, businessIdentifiers.size ());

    final ParticipantIdentifierType businessIdentifierRead = businessIdentifiers.get (0);

    Assert.assertEquals (businessIdentifierCreate.getScheme (), businessIdentifierRead.getScheme ());
    Assert.assertEquals (businessIdentifierCreate.getValue (), businessIdentifierRead.getValue ());

    biClient.deleteList (businessIdentifiers);

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
  }

  @Test (expected = org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault.class)
  public void testManageBusinessIdentifierDoubleDelete () throws Exception {
    final ManageParticipantIdentifierServiceCaller biClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final ParticipantIdentifierType businessIdentifierCreate = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1);

    final ServiceMetadataPublisherServiceForParticipantType serviceMetadataPublisherServiceForBusiness = new ServiceMetadataPublisherServiceForParticipantType ();
    serviceMetadataPublisherServiceForBusiness.setParticipantIdentifier (businessIdentifierCreate);
    serviceMetadataPublisherServiceForBusiness.setServiceMetadataPublisherID (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());

    biClient.create (serviceMetadataPublisherServiceForBusiness);

    final ParticipantIdentifierPageType res = biClient.list ("",
                                                             m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    final List <ParticipantIdentifierType> businessIdentifiers = res.getParticipantIdentifier ();

    Assert.assertEquals (1, businessIdentifiers.size ());

    final ParticipantIdentifierType businessIdentifierRead = businessIdentifiers.get (0);

    Assert.assertEquals (businessIdentifierCreate.getScheme (), businessIdentifierRead.getScheme ());
    Assert.assertEquals (businessIdentifierCreate.getValue (), businessIdentifierRead.getValue ());

    biClient.deleteList (businessIdentifiers);
    biClient.deleteList (businessIdentifiers);

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
  }

  @Test
  public void testManageBusinessIdentifierListWithZeroElements () throws Exception {
    final ManageParticipantIdentifierServiceCaller biClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final Collection <ParticipantIdentifierType> recipientBusinessIdentifiers = new ArrayList <ParticipantIdentifierType> ();

    biClient.createList (recipientBusinessIdentifiers, SMP_ID);

    final ParticipantIdentifierPageType res = biClient.list ("",
                                                             m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    final List <ParticipantIdentifierType> businessIdentifiers = res.getParticipantIdentifier ();

    Assert.assertEquals (0, businessIdentifiers.size ());

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
  }

  @Test
  public void testManageBusinessIdentifierListWithOneElement () throws Exception {
    final ManageParticipantIdentifierServiceCaller biClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final Collection <ParticipantIdentifierType> recipientBusinessIdentifiers = new ArrayList <ParticipantIdentifierType> ();

    final ParticipantIdentifierType businessIdentifierCreate1 = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1);

    recipientBusinessIdentifiers.add (businessIdentifierCreate1);
    biClient.createList (recipientBusinessIdentifiers, SMP_ID);

    final ParticipantIdentifierPageType res = biClient.list ("",
                                                             m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    final List <ParticipantIdentifierType> businessIdentifiers = res.getParticipantIdentifier ();

    Assert.assertEquals (1, businessIdentifiers.size ());

    final ParticipantIdentifierType businessIdentifierRead = businessIdentifiers.get (0);

    Assert.assertEquals (businessIdentifierCreate1.getScheme (), businessIdentifierRead.getScheme ());
    Assert.assertEquals (businessIdentifierCreate1.getValue (), businessIdentifierRead.getValue ());

    biClient.deleteList (businessIdentifiers);

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
  }

  @Test
  public void testManageBusinessIdentifierListWithTwoElement () throws Exception {
    final ManageParticipantIdentifierServiceCaller biClient = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final Map <String, ParticipantIdentifierType> businessIdentifiersCreate = new HashMap <String, ParticipantIdentifierType> ();

    ParticipantIdentifierType businessIdentifierCreate1 = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1);
    ParticipantIdentifierType businessIdentifierCreate2 = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER2);

    businessIdentifiersCreate.put (businessIdentifierCreate1.getValue (), businessIdentifierCreate1);
    businessIdentifiersCreate.put (businessIdentifierCreate2.getValue (), businessIdentifierCreate2);

    biClient.createList (businessIdentifiersCreate.values (), SMP_ID);

    final ParticipantIdentifierPageType res = biClient.list ("",
                                                             m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
    final List <ParticipantIdentifierType> businessIdentifiers = res.getParticipantIdentifier ();

    Assert.assertEquals (2, businessIdentifiers.size ());

    final ParticipantIdentifierType businessIdentifierRead1 = businessIdentifiers.get (0);
    businessIdentifierCreate1 = businessIdentifiersCreate.get (businessIdentifierRead1.getValue ());

    final ParticipantIdentifierType businessIdentifierRead2 = businessIdentifiers.get (1);
    businessIdentifierCreate2 = businessIdentifiersCreate.get (businessIdentifierRead2.getValue ());

    Assert.assertEquals (businessIdentifierCreate1.getScheme (), businessIdentifierRead1.getScheme ());
    Assert.assertEquals (businessIdentifierCreate1.getValue (), businessIdentifierRead1.getValue ());

    Assert.assertEquals (businessIdentifierCreate2.getScheme (), businessIdentifierRead2.getScheme ());
    Assert.assertEquals (businessIdentifierCreate2.getValue (), businessIdentifierRead2.getValue ());

    biClient.deleteList (businessIdentifiers);

    m_aClient.delete (m_aServiceMetadataCreate.getServiceMetadataPublisherID ());
  }

  @Test
  public void migrateTest () throws Exception {
    final ManageParticipantIdentifierServiceCaller biClientOld = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final ManageServiceMetadataServiceCaller client2 = new ManageServiceMetadataServiceCaller (SML_INFO);
    try {
      client2.delete (SMP_ID2);
    }
    catch (final NotFoundFault e) {
      // This is fine, since we are just cleaning
    }

    createSmp (client2, SMP_ID2);

    final ManageParticipantIdentifierServiceCaller biClientNew = new ManageParticipantIdentifierServiceCaller (SML_INFO);
    final ParticipantIdentifierType aPI = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1);
    biClientOld.create (SMP_ID, aPI);
    final UUID code = biClientOld.prepareToMigrate (aPI, SMP_ID);
    biClientNew.migrate (aPI, code, SMP_ID2);

    try {
      biClientOld.delete (aPI);
      fail ();
    }
    catch (final org.busdox.servicemetadata.managebusinessidentifierservice._1.UnauthorizedFault e) {
      // This must happen.
    }

    // Should be able to delete, since New is now the owner
    biClientNew.delete (aPI);
  }

  @Test (expected = org.busdox.servicemetadata.managebusinessidentifierservice._1.BadRequestFault.class)
  public void createExistingBusinessIdentifierUnauthorized () throws Exception {
    final ManageParticipantIdentifierServiceCaller biClientOld = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    final ManageServiceMetadataServiceCaller client2 = new ManageServiceMetadataServiceCaller (SML_INFO);
    try {
      client2.delete (SMP_ID2);
    }
    catch (final NotFoundFault e) {
      // This is fine, since we are just cleaning
    }

    createSmp (client2, SMP_ID2);

    final ManageParticipantIdentifierServiceCaller biClientNew = new ManageParticipantIdentifierServiceCaller (SML_INFO);

    biClientOld.create (SMP_ID, SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1));

    biClientNew.create (SMP_ID, SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER1));
  }

  private static ServiceMetadataPublisherServiceType createSmp (final ManageServiceMetadataServiceCaller client,
                                                                final String ID) throws Exception {
    final ServiceMetadataPublisherServiceType serviceMetadataCreate = new ServiceMetadataPublisherServiceType ();
    serviceMetadataCreate.setServiceMetadataPublisherID (ID);
    final PublisherEndpointType endpoint = new PublisherEndpointType ();
    endpoint.setLogicalAddress (L_ENDPOINTADDRESS);
    endpoint.setPhysicalAddress (P_ENDPOINTADDRESS);
    serviceMetadataCreate.setPublisherEndpoint (endpoint);

    client.create (serviceMetadataCreate);
    return serviceMetadataCreate;
  }
}
