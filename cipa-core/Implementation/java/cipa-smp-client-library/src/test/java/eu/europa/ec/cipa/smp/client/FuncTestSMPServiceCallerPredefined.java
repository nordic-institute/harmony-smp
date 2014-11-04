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
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;
import java.security.cert.X509Certificate;

import org.junit.Test;

import com.sun.jersey.api.client.ClientHandlerException;

import eu.europa.ec.cipa.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import eu.europa.ec.cipa.busdox.identifier.IReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.busdox.identifier.IReadonlyProcessIdentifier;
import eu.europa.ec.cipa.peppol.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.EPredefinedProcessIdentifier;
import eu.europa.ec.cipa.peppol.sml.ESML;

/**
 * Test class for class {@link SMPServiceCaller}.
 *
 * @author philip
 */
public final class FuncTestSMPServiceCallerPredefined {
  private static IReadonlyDocumentTypeIdentifier DOCUMENT_INVOICE = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A;
  private static IReadonlyDocumentTypeIdentifier DOCUMENT_INVOICE_V20 = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A_V20;
  private static IReadonlyProcessIdentifier PROCESS_BII04 = EPredefinedProcessIdentifier.BIS4A;
  private static IReadonlyProcessIdentifier PROCESS_BII04_V20 = EPredefinedProcessIdentifier.BIS4A_V20;

  private static IReadonlyParticipantIdentifier PI_AT_Test = SimpleParticipantIdentifier.createWithDefaultScheme ("9915:test");
  private static IReadonlyParticipantIdentifier PI_alfa1lab = SimpleParticipantIdentifier.createWithDefaultScheme ("9902:DK28158815");

  @Test
  public void testGetEndpointAddress () throws Throwable {
    String sEndpointAddress;

    try {
      sEndpointAddress = new SMPServiceCaller (PI_AT_Test, ESML.PRODUCTION).getEndpointAddress (PI_AT_Test,
                                                                                                DOCUMENT_INVOICE,
                                                                                                PROCESS_BII04,
                                                                                                ESMPTransportProfile.TRANSPORT_PROFILE_START);
      assertEquals ("https://test.erb.gv.at/accessPointService", sEndpointAddress);

      sEndpointAddress = new SMPServiceCaller (PI_AT_Test, ESML.PRODUCTION).getEndpointAddress (PI_AT_Test,
                                                                                                DOCUMENT_INVOICE_V20,
                                                                                                PROCESS_BII04_V20,
                                                                                                ESMPTransportProfile.TRANSPORT_PROFILE_AS2);
      assertEquals ("https://test.erb.gv.at/as2", sEndpointAddress);

      sEndpointAddress = new SMPServiceCaller (PI_alfa1lab, ESML.PRODUCTION).getEndpointAddress (PI_alfa1lab,
                                                                                                 DOCUMENT_INVOICE,
                                                                                                 PROCESS_BII04,
                                                                                                 ESMPTransportProfile.TRANSPORT_PROFILE_START);
      assertEquals ("https://start-ap.alfa1lab.com:443/accessPointService", sEndpointAddress);
    }
    catch (final ClientHandlerException ex) {
      // Happens when being offline!
      assertTrue (ex.getCause () instanceof UnknownHostException);
    }
  }

  @Test
  public void testGetEndpointCertificate () throws Throwable {
    X509Certificate aEndpointCertificate;

    try {
      aEndpointCertificate = new SMPServiceCaller (PI_AT_Test, ESML.PRODUCTION).getEndpointCertificate (PI_AT_Test,
                                                                                                        DOCUMENT_INVOICE,
                                                                                                        PROCESS_BII04,
                                                                                                        ESMPTransportProfile.TRANSPORT_PROFILE_START);
      assertNotNull (aEndpointCertificate);
      assertEquals ("78329019474768541616080482084586682241", aEndpointCertificate.getSerialNumber ().toString ());

      aEndpointCertificate = new SMPServiceCaller (PI_AT_Test, ESML.PRODUCTION).getEndpointCertificate (PI_AT_Test,
                                                                                                        DOCUMENT_INVOICE_V20,
                                                                                                        PROCESS_BII04_V20,
                                                                                                        ESMPTransportProfile.TRANSPORT_PROFILE_AS2);
      assertNotNull (aEndpointCertificate);
      assertEquals ("78329019474768541616080482084586682241", aEndpointCertificate.getSerialNumber ().toString ());

      aEndpointCertificate = new SMPServiceCaller (PI_alfa1lab, ESML.PRODUCTION).getEndpointCertificate (PI_alfa1lab,
                                                                                                         DOCUMENT_INVOICE,
                                                                                                         PROCESS_BII04,
                                                                                                         ESMPTransportProfile.TRANSPORT_PROFILE_START);
      assertNotNull (aEndpointCertificate);
      assertEquals ("56025519523792163866580293261663838570", aEndpointCertificate.getSerialNumber ().toString ());
    }
    catch (final ClientHandlerException ex) {
      // Happens when being offline!
      assertTrue (ex.getCause () instanceof UnknownHostException);
    }
  }
}
