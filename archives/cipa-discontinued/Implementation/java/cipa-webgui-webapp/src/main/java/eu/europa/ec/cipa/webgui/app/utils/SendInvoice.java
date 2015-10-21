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
package eu.europa.ec.cipa.webgui.app.utils;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.w3c.dom.Document;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.xml.serialize.XMLReader;

import eu.europa.ec.cipa.peppol.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.EPredefinedProcessIdentifier;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;
import eu.europa.ec.cipa.transport.IMessageMetadata;
import eu.europa.ec.cipa.transport.MessageMetadata;
import eu.europa.ec.cipa.transport.start.client.AccessPointClient;

public class SendInvoice {
  // public static final String RECEIVER = "9914:ATU53309209"; //OK
  public static final String RECEIVER = "0088:el113766102"; // OK AP2
  // public static final String RECEIVER = "9912:el061828591"; //OK AP1
  // public static final String RECEIVER = "9914:ATU53309209"; //FOUND OK
  public static final boolean USE_PROXY = true;
  public static final String PROXY_HOST = "172.30.9.12";
  public static final String PROXY_PORT = "8080";

  @Nonnull
  private static IMessageMetadata _createMetadata () {
    final ParticipantIdentifierType aSender = SimpleParticipantIdentifier.createWithDefaultScheme ("9914:ATU00000003");
    // final ParticipantIdentifierType aSender =
    // SimpleParticipantIdentifier.createWithDefaultScheme ("0088:el113766102");
    final ParticipantIdentifierType aRecipient = SimpleParticipantIdentifier.createWithDefaultScheme (RECEIVER);
    final DocumentIdentifierType aDocumentType = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getAsDocumentTypeIdentifier ();
    final ProcessIdentifierType aProcessIdentifier = EPredefinedProcessIdentifier.BIS4A.getAsProcessIdentifier ();
    final String sMessageID = "uuid:" + UUID.randomUUID ().toString ();
    return new MessageMetadata (sMessageID, "test-channel", aSender, aRecipient, aDocumentType, aProcessIdentifier);
  }

  @Nullable
  private static String _getAccessPointUrl (@Nonnull final IMessageMetadata aMetadata) throws Exception {
    // SMP client
    final SMPServiceCaller aServiceCaller = new SMPServiceCaller (aMetadata.getRecipientID (), ESML.PRODUCTION);
    // get service info
    final String ret = aServiceCaller.getEndpointAddress (aMetadata.getRecipientID (),
                                                          aMetadata.getDocumentTypeID (),
                                                          aMetadata.getProcessID ());
    // System.out.println(ret);
    return ret;
  }

  public static void sendDocument (final IReadableResource aXmlRes) throws Exception {
    System.setProperty ("java.net.useSystemProxies", "true");
    if (USE_PROXY) {
      System.setProperty ("http.proxyHost", PROXY_HOST);
      System.setProperty ("http.proxyPort", PROXY_PORT);
      System.setProperty ("https.proxyHost", PROXY_HOST);
      System.setProperty ("https.proxyPort", PROXY_PORT);
    }

    final IMessageMetadata aMetadata = _createMetadata ();
    // final String sAccessPointURL = false ?
    // "http://localhost:8090/accessPointService" : _getAccessPointUrl
    // (aMetadata);
    // final String sAccessPointURL = false ?
    // "https://ap2.peppol.gr/ap/accessPointService" : _getAccessPointUrl
    // (aMetadata);
    final String sAccessPointURL = true ? "https://ap2.peppol.gr/ap/accessPointService"
                                       : _getAccessPointUrl (aMetadata);
    // final String sAccessPointURL = true ?
    // "https://localhost/peppol-transport-start-server-2.3.0-SNAPSHOT/accessPointService"
    // : _getAccessPointUrl (aMetadata);
    final Document aXMLDoc = XMLReader.readXMLDOM (aXmlRes);
    AccessPointClient.send (sAccessPointURL, aMetadata, aXMLDoc);
  }
}
