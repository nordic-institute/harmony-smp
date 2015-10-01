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
package eu.europa.ec.cipa.transport.start.client.console;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.PosixParser;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.busdox.transport.start._1.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.commons.SystemProperties;
import com.helger.commons.charset.CCharset;
import com.helger.commons.io.streams.NonBlockingStringWriter;
import com.helger.commons.io.streams.StringInputStream;
import com.helger.commons.lang.CGStringHelper;
import com.helger.commons.xml.XMLFactory;
import com.helger.commons.xml.serialize.DOMReader;

import eu.europa.ec.cipa.busdox.CBusDox;
import eu.europa.ec.cipa.peppol.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.EPredefinedProcessIdentifier;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.peppol.utils.ConfigFile;
import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;
import eu.europa.ec.cipa.transport.IMessageMetadata;
import eu.europa.ec.cipa.transport.MessageMetadata;
import eu.europa.ec.cipa.transport.PingMessageHelper;
import eu.europa.ec.cipa.transport.start.client.AccessPointClient;
import eu.europa.ec.cipa.transport.start.client.AccessPointClientSendResult;

public class StartClientConsole {
  private static final Logger s_aLogger = LoggerFactory.getLogger (StartClientConsole.class);

  private static enum EClientMode {
    DIRECT_AP,
    DIRECT_SMP,
    FULL;
  }

  private static void _enableProxy () {
    final ConfigFile aProxyConfig = new ConfigFile ("configProxy.properties");
    if (!aProxyConfig.isRead ()) {
      s_aLogger.error ("No configProxy.properties file provided - proxy will not be configured ");
    }
    else {
      System.setProperty ("http.proxyHost", aProxyConfig.getString ("http.proxyHost"));
      System.setProperty ("http.proxyPort", aProxyConfig.getString ("http.proxyPort"));
      System.setProperty ("https.proxyHost", aProxyConfig.getString ("https.proxyHost"));
      System.setProperty ("https.proxyPort", aProxyConfig.getString ("https.proxyPort"));
    }
  }

  @Nullable
  private static String _getAccessPointUrl (@Nonnull final URI aSMPAddress, @Nonnull final IMessageMetadata aMetadata) throws Exception {
    // SMP client
    final SMPServiceCaller aServiceCaller = new SMPServiceCaller (aSMPAddress);
    // get service info
    return aServiceCaller.getEndpointAddress (aMetadata.getRecipientID (),
                                              aMetadata.getDocumentTypeID (),
                                              aMetadata.getProcessID (),
                                              ESMPTransportProfile.TRANSPORT_PROFILE_START);

  }

  @Nullable
  private static String _getAccessPointUrl (@Nonnull final IMessageMetadata aMetadata, @Nonnull final ISMLInfo aSMLInfo) throws Exception {
    // SMP client
    final SMPServiceCaller aServiceCaller = new SMPServiceCaller (aMetadata.getRecipientID (), aSMLInfo);
    // get service info
    return aServiceCaller.getEndpointAddress (aMetadata.getRecipientID (),
                                              aMetadata.getDocumentTypeID (),
                                              aMetadata.getProcessID (),
                                              ESMPTransportProfile.TRANSPORT_PROFILE_START);

  }

  @Nonnull
  private static IMessageMetadata _createPingMetadata () {
    final ParticipantIdentifierType aSender = PingMessageHelper.PING_SENDER;
    final ParticipantIdentifierType aRecipient = PingMessageHelper.PING_RECIPIENT;
    final DocumentIdentifierType aDocumentType = PingMessageHelper.PING_DOCUMENT_TYPE;
    final ProcessIdentifierType aProcessIdentifier = PingMessageHelper.PING_PROCESS;
    final String sMessageID = "uuid:" + UUID.randomUUID ().toString ();
    return new MessageMetadata (sMessageID, "ping-channel", aSender, aRecipient, aDocumentType, aProcessIdentifier);
  }

  private static void enableDebug () throws Exception {
    CBusDox.setMetroDebugSystemProperties (true);
    // FileOutputStream fos = new FileOutputStream();

    // Debug logging
    SystemProperties.setPropertyValue ("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
                                       Boolean.toString (false));
    SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.rm.runtime.ClientTube.dump", "true");
    // Metro uses java.util.logging
    java.util.logging.LogManager.getLogManager ()
                                .readConfiguration (new StringInputStream ("handlers=java.util.logging.ConsoleHandler\r\n"
                                                                               + "java.util.logging.ConsoleHandler.level=FINEST",
                                                                           CCharset.CHARSET_ISO_8859_1_OBJ));
    final FileHandler fh = new FileHandler ("metroOut.log");
    fh.setLevel (Level.FINEST);

    java.util.logging.Logger.getLogger ("com.sun.metro.rx").setLevel (java.util.logging.Level.FINER);
    java.util.logging.Logger.getLogger ("com.sun.metro.rx").addHandler (fh);
    java.util.logging.Logger.getLogger ("com.sun.xml.ws").addHandler (fh);
    java.util.logging.Logger.getLogger ("com.sun.xml.wss").addHandler (fh);

    // Metro debugging
    SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.mc.runtime.McTubeFactory.dump.client.after", "true");
    SystemProperties.setPropertyValue ("com.sun.xml.ws.rx.mc.runtime.McTubeFactory.dump.endpoint.before", "true");
    SystemProperties.setPropertyValue ("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.client.after", "true");
    SystemProperties.setPropertyValue ("com.sun.xml.wss.provider.wsit.SecurityTubeFactory.dump.endpoint.before", "true");
  }

  /**
   * @param args
   *        commandline arguments
   * @throws Exception
   *         in case of an error
   */
  public static void main (final String [] args) throws Exception {
    final StartClientOptions aOptions = new StartClientOptions ();
    final CommandLine cmd = new PosixParser ().parse (aOptions, args);
    EClientMode eMode = null;
    boolean bGoodCmd = true;
    System.setProperty ("java.net.useSystemProxies", "true");

    if (cmd.hasOption ("debug") && Boolean.parseBoolean (cmd.getOptionValue ("debug")))
      enableDebug ();

    if (cmd.hasOption ("proxy") && Boolean.parseBoolean (cmd.getOptionValue ("proxy")))
      _enableProxy ();

    if (!cmd.hasOption ("s") ||
        !cmd.hasOption ("r") ||
        !cmd.hasOption ("p") ||
        !cmd.hasOption ("d") ||
        !cmd.hasOption ("dpath")) {
      System.out.println ("You did not specify all the mandatory parameters please check the help");
      bGoodCmd = false;
    }

    if (!cmd.hasOption ("m")) {
      bGoodCmd = false;
    }
    else {
      eMode = EClientMode.valueOf (cmd.getOptionValue ("m"));
      switch (eMode) {
        case DIRECT_AP:
          if (!cmd.hasOption ("ap")) {
            System.out.println ("AP url required in DIRECT_AP mode ");
            bGoodCmd = false;
          }
          break;
        case DIRECT_SMP:
          if (!cmd.hasOption ("smp")) {
            System.out.println ("SMP url required in DIRECT_AP mode ");
            bGoodCmd = false;
          }
          break;
        case FULL:
          break;
        default:
          break;
      }
    }
    if (!bGoodCmd) {
      final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
      new HelpFormatter ().printHelp (new PrintWriter (aSW),
                                      HelpFormatter.DEFAULT_WIDTH,
                                      CGStringHelper.getClassLocalName (StartClientConsole.class),
                                      null,
                                      aOptions,
                                      HelpFormatter.DEFAULT_LEFT_PAD,
                                      HelpFormatter.DEFAULT_DESC_PAD,
                                      null);
      System.out.println (aSW);
      System.exit (-3);
    }

    IMessageMetadata aMetadata = null;
    Document aDoc = null;
    final ISMLInfo aSMLInfo = ESML.PRODUCTION;

    if (cmd.hasOption ("ping") && Boolean.parseBoolean (cmd.getOptionValue ("ping"))) {
      System.out.println ("Sending Ping Messsage");
      aMetadata = _createPingMetadata ();

      // Create a ping document
      aDoc = XMLFactory.newDocument ();
      // See START-Types-1.0.xsd for details
      aDoc.appendChild (aDoc.createElementNS (ObjectFactory._Ping_QNAME.getNamespaceURI (),
                                              ObjectFactory._Ping_QNAME.getLocalPart ()));
    }
    else {
      final ParticipantIdentifierType aSender = SimpleParticipantIdentifier.createWithDefaultScheme (cmd.getOptionValue ('s'));
      final ParticipantIdentifierType aRecipient = SimpleParticipantIdentifier.createWithDefaultScheme (cmd.getOptionValue ('r'));
      final DocumentIdentifierType aDocumentType = EPredefinedDocumentTypeIdentifier.valueOf (cmd.getOptionValue ('d'))
                                                                                    .getAsDocumentTypeIdentifier ();
      final ProcessIdentifierType aProcessIdentifier = EPredefinedProcessIdentifier.valueOf (cmd.getOptionValue ('p'))
                                                                                   .getAsProcessIdentifier ();
      String sMessageID = null;
      if (cmd.hasOption ("muid")) {
        sMessageID = cmd.getOptionValue ("muid");
      }
      else {
        sMessageID = "uuid:" + UUID.randomUUID ().toString ();
      }

      aMetadata = new MessageMetadata (sMessageID,
                                       "test-channel",
                                       aSender,
                                       aRecipient,
                                       aDocumentType,
                                       aProcessIdentifier);
      final File aFile = new File (cmd.getOptionValue ("dpath"));
      aDoc = DOMReader.readXMLDOM (aFile);
      if (aDoc == null)
        throw new IllegalArgumentException ("Failed to read XML document from " + aFile);
    }

    String sAPURL;
    AccessPointClientSendResult aResult = null;
    switch (eMode) {
      case DIRECT_AP:
        sAPURL = cmd.getOptionValue ("ap");
        aResult = AccessPointClient.send (sAPURL, aMetadata, aDoc);
        break;
      case DIRECT_SMP:
        sAPURL = _getAccessPointUrl (new URI (cmd.getOptionValue ("smp")), aMetadata);
        aResult = AccessPointClient.send (sAPURL, aMetadata, aDoc);
        break;
      case FULL:
        sAPURL = _getAccessPointUrl (aMetadata, aSMLInfo);
        aResult = AccessPointClient.send (sAPURL, aMetadata, aDoc);
        break;
    }
    System.out.println ("Send result: " + (aResult == null ? "null" : aResult.isSuccess () ? "success" : "failure"));
  }
}