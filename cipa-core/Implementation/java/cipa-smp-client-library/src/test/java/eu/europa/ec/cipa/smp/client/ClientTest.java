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
package eu.europa.ec.cipa.smp.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.busdox.servicemetadata.publishing._1.CompleteServiceGroupType;
import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ProcessListType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceGroupReferenceListType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupReferenceType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.busdox.servicemetadata.publishing._1.SignedServiceMetadataType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.junit.Ignore;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.commons.url.URLUtils;

import eu.europa.ec.cipa.peppol.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.doctype.SimpleDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.issuingagency.EPredefinedIdentifierIssuingAgency;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.SimpleProcessIdentifier;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.peppol.utils.ExtensionConverter;
import eu.europa.ec.cipa.peppol.utils.IReadonlyUsernamePWCredentials;
import eu.europa.ec.cipa.peppol.utils.ReadonlyUsernamePWCredentials;
import eu.europa.ec.cipa.smp.client.exception.NotFoundException;
import eu.europa.ec.cipa.smp.client.exception.UnauthorizedException;

/**
 * Expects a local SMP up and running at port 80 at the ROOT context. DNS is not
 * needed. See {@link #SMP_URI} constant.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Ignore
public final class ClientTest {
  private static final ISMLInfo SML_INFO = ESML.DEVELOPMENT_LOCAL;

  private static final String TEST_BUSINESS_IDENTIFIER = "0088:5798000999988";
  private static final String TEST_DOCUMENT = "urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##OIOUBL-2.02";
  private static final String TEST_PROCESS = "BII03";

  private static final String SMP_USERNAME = "peppol_user";
  private static final String SMP_PASSWORD = "Test1234";
  private static final IReadonlyUsernamePWCredentials SMP_CREDENTIALS = new ReadonlyUsernamePWCredentials (SMP_USERNAME,
                                                                                                           SMP_PASSWORD);
  public static final URI SMP_URI = URLUtils.getAsURI ("http://localhost/");

  @BeforeClass
  public static void init () throws Exception {
    final SMPServiceCaller aClient = new SMPServiceCaller (SMP_URI);

    // Ensure to delete TEST_BUSINESS_IDENTIFIER
    try {
      final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER);
      aClient.deleteServiceGroup (aServiceGroupID, SMP_CREDENTIALS);
    }
    catch (final Exception e) {
      // This is ok
    }
  }

  @Test (expectedExceptions = NotFoundException.class)
  public void testGetServiceMetadataNotExistsOnExistingSMP () throws Exception {
    final String sParticipantID = "0088:5798000099988";

    final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createWithDefaultScheme (sParticipantID);
    final DocumentIdentifierType aDocumentTypeID = SimpleDocumentTypeIdentifier.createWithDefaultScheme (TEST_DOCUMENT);

    final SMPServiceCaller aClient = new SMPServiceCaller (SMP_URI);
    aClient.getServiceRegistration (aServiceGroupID, aDocumentTypeID);
  }

  @Test
  public void testGetServiceGroupReferenceList () throws Exception {
    final SMPServiceCaller aClient = new SMPServiceCaller (SMP_URI);
    final ServiceGroupReferenceListType aServiceGroupReferenceList = aClient.getServiceGroupReferenceList (SMP_USERNAME,
                                                                                                           SMP_CREDENTIALS);
    assertNotNull (aServiceGroupReferenceList);
    for (final ServiceGroupReferenceType aServiceGroupReference : aServiceGroupReferenceList.getServiceGroupReference ()) {
      final CompleteServiceGroupType aCSG = SMPServiceCaller.getCompleteServiceGroup (new URI (aServiceGroupReference.getHref ()));
      assertNotNull (aCSG);
    }
  }

  @Nonnull
  private static ServiceGroupType _createSaveServiceGroup (final SMPServiceCaller aClient,
                                                           final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    final ParticipantIdentifierType aPI = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER);
    final ServiceGroupType aServiceGroup = new ObjectFactory ().createServiceGroupType ();
    aServiceGroup.setParticipantIdentifier (aPI);

    aClient.saveServiceGroup (aServiceGroup, aCredentials);
    return aServiceGroup;
  }

  @Nonnull
  private static void _deleteServiceGroup (final SMPServiceCaller aClient,
                                           final IReadonlyUsernamePWCredentials aCredentials) throws Exception {
    final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER);
    aClient.deleteServiceGroup (aServiceGroupID, aCredentials);
  }

  @Test
  public void testCRUDServiceGroup () throws Exception {
    final String sContent = "Test";
    final String sElement = "TestElement";
    final String sTestXML = "<" + sElement + ">" + sContent + "</" + sElement + ">";

    final SMPServiceCaller aClient = new SMPServiceCaller (SMP_URI);
    final ServiceGroupType serviceGroupCreate = _createSaveServiceGroup (aClient, SMP_CREDENTIALS);
    serviceGroupCreate.setExtension (ExtensionConverter.convert (sTestXML));
    aClient.saveServiceGroup (serviceGroupCreate, SMP_CREDENTIALS);

    final ParticipantIdentifierType aServiceGroupID = serviceGroupCreate.getParticipantIdentifier ();
    final ServiceGroupType serviceGroupGet2 = aClient.getServiceGroup (aServiceGroupID);
    assertNotNull (serviceGroupGet2);
    assertNotNull (serviceGroupGet2.getExtension ());
    assertNotNull (serviceGroupGet2.getExtension ().getAny ());
    assertEquals (sContent, ((Element) serviceGroupGet2.getExtension ().getAny ()).getTextContent ());
    assertEquals (sElement, ((Element) serviceGroupGet2.getExtension ().getAny ()).getLocalName ());

    _deleteServiceGroup (aClient, SMP_CREDENTIALS);
  }

  @Test (expectedExceptions = UnauthorizedException.class)
  public void testUnauthorizedUser () throws Exception {
    final SMPServiceCaller aClient = new SMPServiceCaller (SMP_URI);
    final ReadonlyUsernamePWCredentials aCredentials = new ReadonlyUsernamePWCredentials (SMP_USERNAME + "wronguser",
                                                                                          SMP_PASSWORD);
    _createSaveServiceGroup (aClient, aCredentials);
  }

  @Test (expectedExceptions = UnauthorizedException.class)
  public void testUnauthorizedPassword () throws Exception {
    final SMPServiceCaller aClient = new SMPServiceCaller (SMP_URI);
    final ReadonlyUsernamePWCredentials aCredentials = new ReadonlyUsernamePWCredentials (SMP_USERNAME, SMP_PASSWORD +
                                                                                                        "wrongpass");
    _createSaveServiceGroup (aClient, aCredentials);
  }

  @Test
  @DevelopersNote ("May fails to validate the signed response because of test keystore")
  public void testCRUDServiceRegistration () throws Exception {
    final SMPServiceCaller aClient = new SMPServiceCaller (SMP_URI);
    _createSaveServiceGroup (aClient, SMP_CREDENTIALS);

    final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createWithDefaultScheme (TEST_BUSINESS_IDENTIFIER);
    final DocumentIdentifierType aDocumentID = SimpleDocumentTypeIdentifier.createWithDefaultScheme (TEST_DOCUMENT);
    final ProcessIdentifierType aProcessID = SimpleProcessIdentifier.createWithDefaultScheme (TEST_PROCESS);

    final ObjectFactory aObjFactory = new ObjectFactory ();
    final ServiceMetadataType aServiceMetadata = aObjFactory.createServiceMetadataType ();
    {
      final ServiceInformationType aServiceInformation = aObjFactory.createServiceInformationType ();
      {
        final ProcessListType aProcessList = aObjFactory.createProcessListType ();
        {
          final ProcessType aProcess = aObjFactory.createProcessType ();
          {
            final ServiceEndpointList aServiceEndpointList = aObjFactory.createServiceEndpointList ();
            {
              final EndpointType aEndpoint = aObjFactory.createEndpointType ();
              final W3CEndpointReference aEndpointReferenceType = new W3CEndpointReferenceBuilder ().address ("http://peppol.eu/sampleService/")
                                                                                                    .build ();
              aEndpoint.setEndpointReference (aEndpointReferenceType);
              aEndpoint.setTransportProfile (CSMPIdentifier.TRANSPORT_PROFILE_START);
              // Certificate: Base64.encodeBytes (certificate.getEncoded ());
              aEndpoint.setCertificate ("1234567890");
              aEndpoint.setServiceActivationDate (new Date (System.currentTimeMillis ()));
              aEndpoint.setServiceDescription ("TEST DESCRIPTION");
              aEndpoint.setServiceExpirationDate (new Date (System.currentTimeMillis () + 1000));
              aEndpoint.setTechnicalContactUrl ("mailto:test@test.eu");
              aEndpoint.setMinimumAuthenticationLevel ("2");
              aEndpoint.setRequireBusinessLevelSignature (false);

              aServiceEndpointList.getEndpoint ().add (aEndpoint);
            }

            aProcess.setProcessIdentifier (aProcessID);
            aProcess.setServiceEndpointList (aServiceEndpointList);
          }

          aProcessList.getProcess ().add (aProcess);
        }

        aServiceInformation.setDocumentIdentifier (aDocumentID);
        aServiceInformation.setParticipantIdentifier (aServiceGroupID);
        aServiceInformation.setProcessList (aProcessList);
      }

      aServiceMetadata.setServiceInformation (aServiceInformation);
    }
    aClient.saveServiceRegistration (aServiceMetadata, SMP_CREDENTIALS);

    final SignedServiceMetadataType signedServiceMetadata = aClient.getServiceRegistration (aServiceGroupID,
                                                                                            aDocumentID);
    System.out.println ("Service aMetadata ID:" +
                        signedServiceMetadata.getServiceMetadata ()
                                             .getServiceInformation ()
                                             .getParticipantIdentifier ()
                                             .getValue ());

    aClient.deleteServiceRegistration (aServiceGroupID, aDocumentID, SMP_CREDENTIALS);
    _deleteServiceGroup (aClient, SMP_CREDENTIALS);
  }

  @Ignore
  @Test
  @DevelopersNote ("Requires DNS enabled")
  public void getByDNSTest () throws Exception {
    // Make sure that the dns exists.
    final String sParticipantID = "0088:5798000000001";
    final String sDocumentID = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::InvoiceDisputeDisputeInvoice##UBL-2.0";

    final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createWithDefaultScheme (sParticipantID);
    final DocumentIdentifierType aDocumentTypeID = SimpleDocumentTypeIdentifier.createWithDefaultScheme (sDocumentID);

    final ServiceGroupType aGroup = SMPServiceCaller.getServiceGroupByDNS (SML_INFO, aServiceGroupID);
    assertNotNull (aGroup);

    final SignedServiceMetadataType aMetadata = SMPServiceCaller.getServiceRegistrationByDNS (SML_INFO,
                                                                                              aServiceGroupID,
                                                                                              aDocumentTypeID);
    assertNotNull (aMetadata);
  }

  @Ignore
  @Test
  @DevelopersNote ("Requires DNS enabled; Used as the example in the SMP guideline")
  public void getByDNSTestForDocs () throws Exception {
    // ServiceGroup = participant identifier; GLN = 0088
    final ParticipantIdentifierType aServiceGroupID = EPredefinedIdentifierIssuingAgency.GLN.createParticipantIdentifier ("5798000000001");
    // Document type identifier from enumeration
    final DocumentIdentifierType aDocumentTypeID = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getAsDocumentTypeIdentifier ();
    // Main call to the SMP client with the correct SML to use
    final SignedServiceMetadataType aMetadata = SMPServiceCaller.getServiceRegistrationByDNS (ESML.DEVELOPMENT_LOCAL,
                                                                                              aServiceGroupID,
                                                                                              aDocumentTypeID);
    assertNotNull (aMetadata);
  }

  @Ignore
  @Test
  @DevelopersNote ("Requires DNS enabled")
  public void redirectTest () throws Exception {
    final String sParticipantID = "0088:5798000009997";
    final String sDocumentID = "urn:oasis:names:specification:ubl:schema:xsd:SubmitCatalogue-2::SubmitCatalogue##UBL-2.0";

    final ParticipantIdentifierType aServiceGroupID = SimpleParticipantIdentifier.createWithDefaultScheme (sParticipantID);
    final DocumentIdentifierType aDocumentTypeID = SimpleDocumentTypeIdentifier.createWithDefaultScheme (sDocumentID);

    final SignedServiceMetadataType aMetadata = SMPServiceCaller.getServiceRegistrationByDNS (SML_INFO,
                                                                                              aServiceGroupID,
                                                                                              aDocumentTypeID);
    assertNotNull (aMetadata);
  }
}
