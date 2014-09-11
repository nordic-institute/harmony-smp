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
package eu.europa.ec.cipa.transport.start.example;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.helger.commons.xml.XMLFactory;

import eu.europa.ec.cipa.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import eu.europa.ec.cipa.busdox.identifier.IReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.busdox.identifier.IReadonlyProcessIdentifier;
import eu.europa.ec.cipa.peppol.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.EPredefinedProcessIdentifier;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;
import eu.europa.ec.cipa.transport.IMessageMetadata;
import eu.europa.ec.cipa.transport.MessageMetadata;
import eu.europa.ec.cipa.transport.PingMessageHelper;
import eu.europa.ec.cipa.transport.start.client.AccessPointClient;

/**
 * Example for pinging a single Access Point
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class ExamplePingAccessPoint {
  private static final ISMLInfo SML_INFO = ESML.PRODUCTION;
  private static IReadonlyDocumentTypeIdentifier DOCUMENT_INVOICE = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A;
  private static IReadonlyProcessIdentifier PROCESS_BII04 = EPredefinedProcessIdentifier.BIS4A;
  private static IReadonlyParticipantIdentifier PI_alfa1lab = SimpleParticipantIdentifier.createWithDefaultScheme ("9902:DK28158815");

  @Nonnull
  private static IMessageMetadata _createPingMetadata () {
    final ParticipantIdentifierType aSenderID = PingMessageHelper.PING_SENDER;
    final ParticipantIdentifierType aRecipientID = PingMessageHelper.PING_RECIPIENT;
    final DocumentIdentifierType aDocumentTypeID = PingMessageHelper.PING_DOCUMENT_TYPE;
    final ProcessIdentifierType aProcessIdentifierType = PingMessageHelper.PING_PROCESS;
    final String sMessageID = "uuid:" + UUID.randomUUID ().toString ();
    return new MessageMetadata (sMessageID, null, aSenderID, aRecipientID, aDocumentTypeID, aProcessIdentifierType);
  }

  @Nullable
  private static String _getAccessPointUrl () throws Exception {
    // SMP client
    final SMPServiceCaller aServiceCaller = new SMPServiceCaller (PI_alfa1lab, SML_INFO);
    // get service info
    return aServiceCaller.getEndpointAddress (PI_alfa1lab, DOCUMENT_INVOICE, PROCESS_BII04);
  }

  @Nonnull
  private static Document _createDummyPayload () {
    // Create a new XML document with some crappy content
    final Document aXMLDoc = XMLFactory.newDocument ();
    final Node aRoot = aXMLDoc.appendChild (aXMLDoc.createElement ("test"));
    aRoot.appendChild (aXMLDoc.createTextNode ("Text content"));
    return aXMLDoc;
  }

  public static void main (final String [] args) throws Exception {
    System.setProperty ("java.net.useSystemProxies", "true");
    final IMessageMetadata aMetadata = _createPingMetadata ();
    final String sAccessPointURLstr = _getAccessPointUrl ();
    final Document aXMLDoc = _createDummyPayload ();
    AccessPointClient.send (sAccessPointURLstr, aMetadata, aXMLDoc);
  }
}
