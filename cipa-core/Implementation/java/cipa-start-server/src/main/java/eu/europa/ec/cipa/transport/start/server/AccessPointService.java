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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.cipa.transport.start.server;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.Action;
import javax.xml.ws.BindingType;
import javax.xml.ws.FaultAction;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.Addressing;

import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.transport.start.cert.ServerConfigFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.CreateResponse;
import org.w3._2009._02.ws_tra.Delete;
import org.w3._2009._02.ws_tra.DeleteResponse;
import org.w3._2009._02.ws_tra.FaultMessage;
import org.w3._2009._02.ws_tra.Get;
import org.w3._2009._02.ws_tra.GetResponse;
import org.w3._2009._02.ws_tra.Put;
import org.w3._2009._02.ws_tra.PutResponse;

import com.phloc.commons.CGlobal;
import com.phloc.commons.GlobalDebug;
import com.phloc.commons.annotations.UnsupportedOperation;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.exceptions.InitializationException;
import com.phloc.commons.io.misc.SizeHelper;
import com.phloc.commons.lang.ClassHelper;
import com.phloc.commons.lang.ServiceLoaderBackport;
import com.phloc.commons.log.LogMessage;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.state.impl.SuccessWithValue;
import com.phloc.commons.stats.IStatisticsHandlerCounter;
import com.phloc.commons.stats.IStatisticsHandlerTimer;
import com.phloc.commons.stats.StatisticsManager;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.timing.StopWatch;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;

import eu.europa.ec.cipa.busdox.CBusDox;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.security.KeyStoreUtils;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.peppol.utils.CertificateUtils;
import eu.europa.ec.cipa.peppol.utils.ExceptionUtils;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;
import eu.europa.ec.cipa.transport.IMessageMetadata;
import eu.europa.ec.cipa.transport.MessageMetadataHelper;
import eu.europa.ec.cipa.transport.PingMessageHelper;
import eu.europa.ec.cipa.transport.start.util.EAPServerMode;

/**
 * WebService implementation.
 * 
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)<br>
 *         Dante Malaga(dante@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@WebService (serviceName = "accessPointService",
             portName = "ResourceBindingPort",
             endpointInterface = "org.w3._2009._02.ws_tra.Resource",
             targetNamespace = "http://www.w3.org/2009/02/ws-tra",
             wsdlLocation = CBusDox.START_WSDL_PATH)
@BindingType (value = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
@Addressing
public class AccessPointService {
  private static final Logger s_aLogger = LoggerFactory.getLogger (AccessPointService.class);
  private static final IStatisticsHandlerCounter s_aStatsCounterSuccess = StatisticsManager.getCounterHandler (AccessPointService.class +
                                                                                                               "$success");
  private static final IStatisticsHandlerCounter s_aStatsCounterFailure = StatisticsManager.getCounterHandler (AccessPointService.class +
                                                                                                               "$failure");
  private static final IStatisticsHandlerTimer s_aStatsTimerSuccess = StatisticsManager.getTimerHandler (AccessPointService.class +
                                                                                                         "$success");
  private static final IStatisticsHandlerTimer s_aStatsTimerFailure = StatisticsManager.getTimerHandler (AccessPointService.class +
                                                                                                         "$failure");
  private static final ISMLInfo SML_INFO = ESML.PRODUCTION;
  private static final List <IAccessPointServiceReceiverSPI> s_aReceivers;
  private static final X509Certificate s_aConfiguredCert;
  private static EAPServerMode s_aServerMode = EAPServerMode.PRODUCTION;

  static {
    final String sReceiverClassPath = ServerConfigFile.getReceiverClassPath ();
    if (sReceiverClassPath != null) {
      final String [] aPathEntries = StringHelper.getExplodedArray (',', sReceiverClassPath);
      final URL [] aPathEntriesURLS = new URL [aPathEntries.length];
      for (int i = 0; i < aPathEntries.length; i++) {
        final String sPathEntry = aPathEntries[i];
        if (!sPathEntry.endsWith ("/") && !sPathEntry.endsWith (".jar"))
          throw new InitializationException ("Invalid class path: '" +
                                             sPathEntry +
                                             "'; must end with either '/' or '.jar'");
        try {
          aPathEntriesURLS[i] = new URL (sPathEntry);
        }
        catch (final MalformedURLException e) {
          throw new InitializationException ("Invalid class path: '" +
                                             sPathEntry +
                                             "' must be a valid URL: " +
                                             e.getMessage ());
        }
      }
      // Load all SPI implementations with a custom class loader
      final URLClassLoader aCustomCL = new URLClassLoader (aPathEntriesURLS, ClassHelper.getDefaultClassLoader ());
      s_aReceivers = ContainerHelper.newUnmodifiableList (ServiceLoaderBackport.load (IAccessPointServiceReceiverSPI.class,
                                                                                      aCustomCL));

    }
    else {
      // Load all SPI implementations
      s_aReceivers = ContainerHelper.newUnmodifiableList (ServiceLoaderBackport.load (IAccessPointServiceReceiverSPI.class));

    }

    if (s_aReceivers.isEmpty ())
      s_aLogger.error ("No implementation of the SPI interface " +
                       IAccessPointServiceReceiverSPI.class +
                       " found! Incoming documents will be discarded!");

    final String sServerMode = ServerConfigFile.getServerMode ();
    if (sServerMode != null) {
      s_aServerMode = EAPServerMode.getFromIDOrNull (sServerMode);
      if (s_aServerMode == null)
        throw new InitializationException ("You configured your server to start in an unsupported mode '" +
                                           sServerMode +
                                           "'. Please check you serverConfig file");
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Starting access point in  " + s_aServerMode + " Mode");
      if (EAPServerMode.DEVELOPMENT_DIRECT_SMP.equals (s_aServerMode)) {
        final String sSMPUrl = ServerConfigFile.getServerSMPUrl ();
        if (StringHelper.hasNoText (sSMPUrl))
          throw new InitializationException ("You configured your server to start in direct smp mode. Please specify the SMP URL in the serverConfig file as well!");
      }
    }
    // Read certificate from configuration only once, so it is cached for
    // reuse
    try {
      final String sKeyStorePath = ServerConfigFile.getKeyStorePath ();
      final String sKeyStorePassword = ServerConfigFile.getKeyStorePassword ();
      final String sKeyStoreAlias = ServerConfigFile.getKeyStoreAlias ();

      final KeyStore aKeyStore = KeyStoreUtils.loadKeyStore (sKeyStorePath, sKeyStorePassword);
      s_aConfiguredCert = (X509Certificate) aKeyStore.getCertificate (sKeyStoreAlias);
      s_aLogger.info ("Our Certificate - Serial Number: " + s_aConfiguredCert.getSerialNumber ());
    }
    catch (final Exception ex) {
      throw new InitializationException ("Failed to read the configured certificate", ex);
    }
  }

  @Resource
  private WebServiceContext webServiceContext;

  /**
   * @param aMetadata
   * @return The access point URL
   * @throws FaultMessage
   *         In case the endpoint address could not be resolved.
   */
  @Nullable
  private static EndpointType _getRecipientEndpoint (@Nonnull final IMessageMetadata aMetadata,
                                                     @Nonnull final String sMessageID) throws FaultMessage {
    final SimpleParticipantIdentifier aRecipientID = aMetadata.getRecipientID ();
    try {
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug (sMessageID + " Looking up the endpoint of recipient " + aRecipientID.getURIEncoded ());

      // Query the SMP
      SMPServiceCaller aSMPClient;
      if (EAPServerMode.DEVELOPMENT_DIRECT_SMP.equals (s_aServerMode)) {
        aSMPClient = new SMPServiceCaller (new URI (ServerConfigFile.getServerSMPUrl ()));
      }
      else {
        aSMPClient = new SMPServiceCaller (aRecipientID, SML_INFO);
      }

      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug (sMessageID + " Performing SMP lookup at " + aSMPClient.getSMPHost ());

      return aSMPClient.getEndpoint (aRecipientID, aMetadata.getDocumentTypeID (), aMetadata.getProcessID ());
    }
    catch (final Throwable t) {
      throw ExceptionUtils.createFaultMessage (t, sMessageID +
                                                  " Failed to retrieve endpoint of recipient " +
                                                  aRecipientID.getURIEncoded ());
    }
  }

  private static void _checkIfRecipientEndpointURLMatches (@Nonnull final EndpointType aRecipientEndpoint,
                                                           @Nonnull final String sMessageID) throws FaultMessage {
    // Get our public endpoint address from the config file
    final String sOwnAPUrl = ServerConfigFile.getOwnAPURL ();
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug (sMessageID + " Our AP URL is " + sOwnAPUrl);

    // In debug mode, use our recipient URL, so that the URL check will work
    final String sRecipientAPUrl = GlobalDebug.isDebugMode () ? sOwnAPUrl
                                                             : SMPServiceCaller.getEndpointAddress (aRecipientEndpoint);
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug (sMessageID + " Recipient AP URL is " + sRecipientAPUrl);

    // Is it for us?
    if (!sRecipientAPUrl.contains (sOwnAPUrl)) {
      s_aLogger.error (sMessageID + " The received document is not for us!");
      s_aLogger.error (sMessageID + " Request is for: " + sRecipientAPUrl);
      s_aLogger.error (sMessageID + "    Our URL is: " + sOwnAPUrl);

      // Avoid endless loop
      throw ExceptionUtils.createFaultMessage (new IllegalStateException (sMessageID +
                                                                          " Receiver(" +
                                                                          sRecipientAPUrl +
                                                                          ") invalid for us (" +
                                                                          sOwnAPUrl +
                                                                          ")"), "The received document is not for us!");
    }
  }

  /**
   * Check if the certificate of the receiver is identical to the configured
   * one. This is done by checking the certificate serial numbers.
   * 
   * @param aReceiverCert
   *        The certificate of the receiver
   * @return <code>true</code> if equal
   */
  private static boolean _isTheSameCert (@Nullable final X509Certificate aReceiverCert, @Nonnull final String sMessageID) {
    if (GlobalDebug.isDebugMode ()) {
      s_aLogger.info (sMessageID + " In debug mode the certificate is always approved");
      return true;
    }

    if (aReceiverCert == null) {
      // Log message was already emitted
      return false;
    }

    // Compare serial numbers
    final BigInteger aMySerial = s_aConfiguredCert.getSerialNumber ();
    final BigInteger aReceiverSerial = aReceiverCert.getSerialNumber ();
    if (!aMySerial.equals (aReceiverSerial)) {
      s_aLogger.error (sMessageID + " Certificate serial number mismatch!");
      s_aLogger.info (sMessageID + " Our certificate serial number: " + aMySerial.toString ());
      s_aLogger.info (sMessageID + "        Receiver serial number: " + aReceiverSerial.toString ());
      return false;
    }

    // Serial numbers match
    return true;
  }

  private static void _checkIfEndpointCertificateMatches (@Nullable final EndpointType aRecipientEndpoint,
                                                          @Nonnull final String sMessageID) throws FaultMessage {
    final String sCertString = SMPServiceCaller.getEndpointCertificateString (aRecipientEndpoint);
    X509Certificate aRecipientSMPCert = null;
    try {
      aRecipientSMPCert = CertificateUtils.convertStringToCertficate (sCertString);
    }
    catch (final CertificateException t) {
      // In development mode it is okay, if this AccessPoint is not
      // registered
      // in an SMP
      if (!GlobalDebug.isDebugMode ())
        throw ExceptionUtils.createFaultMessage (t, sMessageID +
                                                    " Failed to convert endpoint certificate string '" +
                                                    sCertString +
                                                    "'");
    }

    if (aRecipientSMPCert == null)
      s_aLogger.error (sMessageID + " No Metadata certificate found! Is this AP maybe not contained in an SMP?");
    else {
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug (sMessageID + " Recipient certificate present: " + aRecipientSMPCert.toString ());
    }

    if (!_isTheSameCert (aRecipientSMPCert, sMessageID)) {
      s_aLogger.error (sMessageID +
                       " Metadata Certificate (" +
                       aRecipientSMPCert +
                       ") does not match Access Point Certificate (" +
                       s_aConfiguredCert +
                       ") - ignoring document");
      throw ExceptionUtils.createFaultMessage (new IllegalStateException (sMessageID +
                                                                          " Metadata Certificate does not match AP Certificate - ignoring document"),
                                               "Internal error: certificate mismatch!");
    }

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug (sMessageID + " The certificate of the recipient matches our certificate");
  }

  /**
   * @param body
   */
  @UnsupportedOperation
  public GetResponse get (final Get body) {
    throw new UnsupportedOperationException ("Not supported by the current implementation according to the specifications");
  }

  /**
   * @param body
   */
  @UnsupportedOperation
  public PutResponse put (final Put body) {
    throw new UnsupportedOperationException ("Not supported by the current implementation according to the specifications");
  }

  /**
   * @param body
   */
  @UnsupportedOperation
  public DeleteResponse delete (final Delete body) {
    throw new UnsupportedOperationException ("Not supported by the current implementation according to the specifications");
  }

  /**
   * Main action for receiving.
   * 
   * @param aBody
   * @return Never <code>null</code>
   * @throws FaultMessage
   *         In case of an error
   */
  @Action (input = "http://www.w3.org/2009/02/ws-tra/Create",
           output = "http://www.w3.org/2009/02/ws-tra/CreateResponse",
           fault = { @FaultAction (className = FaultMessage.class, value = "http://busdox.org/2010/02/channel/fault") })
  public CreateResponse create (final Create aBody) throws FaultMessage {
    final StopWatch aStopWatch = new StopWatch (true);
    boolean bFailure = false;
    try {
      if (GlobalDebug.isDebugMode ())
        s_aLogger.warn ("Receiving PEPPOL document in debug mode!");

      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("AccesspointService.create called");

      // Grabs the list of headers from the SOAP message
      final HeaderList aHeaderList = (HeaderList) webServiceContext.getMessageContext ()
                                                                   .get (JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
      final IMessageMetadata aMetadata = MessageMetadataHelper.createMetadataFromHeaders (aHeaderList);
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Extracted the following metadata from the headers\n" +
                         MessageMetadataHelper.getDebugInfo (aMetadata));
      final String sMessageID = "[" +
                                StringHelper.getNotNull (aMetadata.getMessageID (), "<no-message-id-provided>") +
                                "]";

      // TODO do we need a check, whether the message ID was already received
      if (PingMessageHelper.isPingMessage (aMetadata)) {
        // It's a PING message - no actions to be taken!
        s_aLogger.info (sMessageID +
                        " got a ping message from " +
                        aMetadata.getSenderID ().getURIEncoded () +
                        " - discarding it!");
      }
      else {
        // Not a ping message
        if (!EAPServerMode.DEVELOPMENT_STANDALONE.equals (s_aServerMode)) {
          // Get the endpoint information required from the recipient
          final EndpointType aRecipientEndpoint = _getRecipientEndpoint (aMetadata, sMessageID);

          // Check if the message is for us
          _checkIfRecipientEndpointURLMatches (aRecipientEndpoint, sMessageID);

          // Get the recipient certificate from the SMP
          _checkIfEndpointCertificateMatches (aRecipientEndpoint, sMessageID);
        }

        s_aLogger.info (sMessageID + " This is a handled request for " + aMetadata.getRecipientID ().getValue ());

        // Invoke all available SPI implementations
        ESuccess eOverallSuccess = ESuccess.SUCCESS;
        final List <LogMessage> aProcessingMessages = new ArrayList <LogMessage> ();
        try {
          // Invoke all available SPI implementations
          if (s_aLogger.isDebugEnabled ())
            s_aLogger.debug (sMessageID + " Now invoking " + s_aReceivers.size () + " SPI implementations");

          // For all receivers
          for (final IAccessPointServiceReceiverSPI aReceiver : s_aReceivers) {
            if (s_aLogger.isDebugEnabled ())
              s_aLogger.debug (sMessageID + " Now invoking " + aReceiver.toString ());

            // Main callback
            final SuccessWithValue <AccessPointReceiveError> aSV = aReceiver.receiveDocument (webServiceContext,
                                                                                              aMetadata,
                                                                                              aBody);
            if (s_aLogger.isDebugEnabled ())
              s_aLogger.debug (sMessageID + " Result of invoking " + aReceiver.toString () + ": " + aSV.toString ());

            // Calculate overall success
            eOverallSuccess = eOverallSuccess.and (aSV);

            // Was there a receiver specific callback message?
            final AccessPointReceiveError aError = aSV.get ();
            if (aError != null) {
              // Remember all messages
              aProcessingMessages.addAll (aError.getAllMessages ());
            }
          }
        }
        catch (final Exception ex) {
          // Exception (in callback)
          aProcessingMessages.add (new LogMessage (EErrorLevel.ERROR,
                                                   sMessageID + " Internal error in processing incoming message",
                                                   ex));

          // Overall failure
          eOverallSuccess = ESuccess.FAILURE;
        }

        if (!aProcessingMessages.isEmpty ()) {
          // Log all messages from processing in a structured and aggregated way
          s_aLogger.info (sMessageID +
                          " Messages from " +
                          (eOverallSuccess.isSuccess () ? "successfuly" : "failed") +
                          " processing of document " +
                          aMetadata.getMessageID () +
                          ":");
          for (final LogMessage aLogMsg : aProcessingMessages)
            s_aLogger.info (sMessageID + "  [" + aLogMsg.getErrorLevel ().getID () + "] " + aLogMsg.getMessage (),
                            aLogMsg.getThrowable ());
        }

        if (eOverallSuccess.isFailure ()) {
          bFailure = true;
          s_aLogger.error (sMessageID + " Failed to handle incoming document from PEPPOL");
          throw ExceptionUtils.createFaultMessage (new IllegalStateException (sMessageID +
                                                                              " Failure in processing document from PEPPOL"),
                                                   "Internal error in processing the incoming PEPPOL document");
        }

        // Log success
        s_aLogger.info (sMessageID + " Done handling incoming document via START");
      }

      if (GlobalDebug.isDebugMode ())
        _checkMemoryUsage ();

      // Create an empty response
      final CreateResponse aResponse = new CreateResponse ();
      return aResponse;
    }
    catch (final FaultMessage ex) {
      // Just recognize the overall failure
      bFailure = true;

      // And re-throw as-is
      throw ex;
    }
    finally {
      // Update invocation counter
      (bFailure ? s_aStatsCounterFailure : s_aStatsCounterSuccess).increment ();
      // Update time counter
      (bFailure ? s_aStatsTimerFailure : s_aStatsTimerSuccess).addTime (aStopWatch.stopAndGetMillis ());
    }
  }

  private static final long MEMORY_THRESHOLD_BYTES = 10 * CGlobal.BYTES_PER_MEGABYTE;
  private static long s_nLastUsageInBytes = 0;

  private static void _checkMemoryUsage () {
    System.gc ();
    final Runtime aRuntime = Runtime.getRuntime ();
    final long nFreeMemory = aRuntime.freeMemory ();
    final long nTotalMemory = aRuntime.totalMemory ();
    final long nUsedMemory = nTotalMemory - nFreeMemory;
    final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale (Locale.US);
    final String sMemoryStatus = aSH.getAsMatching (nUsedMemory, 1) +
                                 " / " +
                                 aSH.getAsMatching (nTotalMemory, 1) +
                                 " / " +
                                 aSH.getAsMatching (aRuntime.maxMemory (), 1);

    if (nUsedMemory <= (s_nLastUsageInBytes - MEMORY_THRESHOLD_BYTES) ||
        nUsedMemory >= (s_nLastUsageInBytes + MEMORY_THRESHOLD_BYTES)) {
      final String sThreadName = Thread.currentThread ().getName ();
      s_aLogger.info ("%%% [" + sThreadName + "] Memory usage: " + sMemoryStatus);
      s_nLastUsageInBytes = nUsedMemory;
    }
  }
}
