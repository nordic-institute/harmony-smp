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
package eu.europa.ec.cipa.transport.start.client;

import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.phloc.commons.SystemProperties;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.io.streams.StringInputStream;
import com.phloc.commons.xml.serialize.XMLReader;

import eu.europa.ec.cipa.busdox.CBusDox;
import eu.europa.ec.cipa.peppol.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.EPredefinedProcessIdentifier;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;
import eu.europa.ec.cipa.transport.IMessageMetadata;
import eu.europa.ec.cipa.transport.MessageMetadata;

/**
 * Test sending a document to a receiver
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class MainSendDocument {
  public static final boolean USE_PROXY = false;
  public static final String PROXY_HOST = "172.30.9.12";
  public static final String PROXY_PORT = "8080";
  public static final boolean USE_LOCAL_AP = false;
  public static final boolean DEBUG = false;
  public static final String RECEIVER = "9915:B";

  private static final Logger s_aLogger = LoggerFactory.getLogger (MainSendDocument.class);

  @Nonnull
  private static IMessageMetadata _createMetadata () {
    final ParticipantIdentifierType aSender = SimpleParticipantIdentifier.createWithDefaultScheme ("9914:ATU00000000");
    final ParticipantIdentifierType aRecipient = SimpleParticipantIdentifier.createWithDefaultScheme (RECEIVER);
    final DocumentIdentifierType aDocumentType = EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A.getAsDocumentTypeIdentifier ();
    final ProcessIdentifierType aProcessIdentifier = EPredefinedProcessIdentifier.BIS4A.getAsProcessIdentifier ();
    final String sMessageID = "uuid:" + UUID.randomUUID ().toString ();
    return new MessageMetadata (sMessageID, "test-channel", aSender, aRecipient, aDocumentType, aProcessIdentifier);
  }

  @Nullable
  private static String _getAccessPointUrl (@Nonnull final IMessageMetadata aMetadata) throws Exception {
    // SMP client
    s_aLogger.info ("Querying endpoint address of " + aMetadata.getRecipientID ().getURIEncoded ());
    final SMPServiceCaller aServiceCaller = new SMPServiceCaller (aMetadata.getRecipientID (), ESML.PRODUCTION);
    // get service info
    return aServiceCaller.getEndpointAddress (aMetadata.getRecipientID (),
                                              aMetadata.getDocumentTypeID (),
                                              aMetadata.getProcessID ());
  }

  private static void _sendDocument (final IReadableResource aXmlRes) throws Exception {
    final IMessageMetadata aMetadata = _createMetadata ();
    final String sAccessPointURL = USE_LOCAL_AP ? "http://localhost:8090/accessPointService"
                                               : _getAccessPointUrl (aMetadata);
    s_aLogger.info ("Using AP URL " + sAccessPointURL);
    final Document aXMLDoc = XMLReader.readXMLDOM (aXmlRes);
    AccessPointClient.send (sAccessPointURL, aMetadata, aXMLDoc);
  }

  public static void main (final String [] args) throws Exception {
    Locale.setDefault (Locale.US);
    System.setProperty ("java.net.useSystemProxies", "true");

    if (USE_PROXY) {
      System.setProperty ("http.proxyHost", PROXY_HOST);
      System.setProperty ("http.proxyPort", PROXY_PORT);
      System.setProperty ("https.proxyHost", PROXY_HOST);
      System.setProperty ("https.proxyPort", PROXY_PORT);
    }

    // enable debugging info?
    CBusDox.setMetroDebugSystemProperties (DEBUG);
    CBusDox.enableSoapLogging (DEBUG);
    if (DEBUG) {
      // Metro uses java.util.logging
      java.util.logging.LogManager.getLogManager ()
                                  .readConfiguration (new StringInputStream ("handlers=java.util.logging.ConsoleHandler\r\n"
                                                                                 + ".level=FINEST\r\n"
                                                                                 + "java.util.logging.ConsoleHandler.level=FINEST\r\n"
                                                                                 + "java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter",
                                                                             CCharset.CHARSET_ISO_8859_1_OBJ));
      java.util.logging.Logger.getLogger ("com.sun.metro.rx").setLevel (java.util.logging.Level.FINER);
      java.util.logging.Logger.getLogger ("org.jcp.xml.dsig.internal.level").setLevel (java.util.logging.Level.FINER);
      java.util.logging.Logger.getLogger ("com.sun.org.apache.xml.internal.security.level")
                              .setLevel (java.util.logging.Level.FINER);
    }
    if (false) {
      // Metro debugging
      SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.mc.runtime.McTubeFactory.dump.client.after", "true");
      SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.mc.runtime.McTubeFactory.dump.endpoint.before", "true");
      SystemProperties.setPropertyValue ("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.client.after", "true");
      SystemProperties.setPropertyValue ("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.endpoint.before",
                                         "true");
    }
    _sendDocument (new ClassPathResource ("xml/test-at-gov.xml"));
  }
}
