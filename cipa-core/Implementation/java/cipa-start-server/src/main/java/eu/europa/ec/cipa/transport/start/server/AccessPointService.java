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
import eu.europa.ec.cipa.transport.start.util.CIPAServerMode;

/**
 * WebService implementation.
 * 
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com) Dante<br>
 *         Malaga(dante@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@WebService(serviceName = "accessPointService", portName = "ResourceBindingPort", endpointInterface = "org.w3._2009._02.ws_tra.Resource", targetNamespace = "http://www.w3.org/2009/02/ws-tra", wsdlLocation = CBusDox.START_WSDL_PATH)
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_BINDING)
@Addressing
public class AccessPointService {
	private static final Logger s_aLogger = LoggerFactory
			.getLogger(AccessPointService.class);
	private static final ISMLInfo SML_INFO = ESML.PRODUCTION;
	private static final List<IAccessPointServiceReceiverSPI> s_aReceivers;
	private static final X509Certificate s_aConfiguredCert;
	private static CIPAServerMode SERVER_MODE = CIPAServerMode.PRODUCTION;

	static {
		String receiverClassPath = ServerConfigFile.getReceiverClassPath();
		if (receiverClassPath != null) {
			String[] pathEntries = receiverClassPath.split(",");
			URL[]  pathEntriesURLS = new URL[pathEntries.length];
			for (int i = 0; i < pathEntries.length; i++) {
				String pathEntry = pathEntries[i];
				if (!pathEntry.endsWith("/")
						&& !pathEntry.endsWith(".jar")) throw new InitializationException("Invalid class path: "
							+ pathEntry
							+ " ; must end with either / or .jar");
				try {
					pathEntriesURLS[i] = new URL(pathEntry);	
				} catch (MalformedURLException e) {
					throw new InitializationException("Invalid class path: "+ pathEntry +"must Be a valid URL ");
				}
			}
			URLClassLoader customCL;
			customCL = new URLClassLoader(pathEntriesURLS,ClassHelper.getDefaultClassLoader ());
			s_aReceivers = ContainerHelper
					.newUnmodifiableList(ServiceLoaderBackport.load(
							IAccessPointServiceReceiverSPI.class, customCL));

		} else {
			// Load all SPI implementations
			s_aReceivers = ContainerHelper
			.newUnmodifiableList(ServiceLoaderBackport
					.load(IAccessPointServiceReceiverSPI.class));


		}

		if (s_aReceivers.isEmpty())
			s_aLogger.error("No implementation of the SPI interface "
					+ IAccessPointServiceReceiverSPI.class
					+ " found! Incoming documents will be discarded!");
		final String sServerMode = ServerConfigFile.getServerMode();
		if (sServerMode != null) {
			try {
				SERVER_MODE = CIPAServerMode.valueOf(sServerMode);
				if (s_aLogger.isDebugEnabled())
					s_aLogger.debug("Starting access point in  " + SERVER_MODE
							+ " Mode");
				if (CIPAServerMode.DEVELOPMENT_DIRECT_SMP.equals(SERVER_MODE)) {
					final String sSMPUrl = ServerConfigFile.getServerSMPUrl();
					if (sSMPUrl == null) {
						throw new InitializationException(
								"You configured your server to start in direct smp mod please specify the smp url in the serverConfig file");
					}
				}

			} catch (IllegalArgumentException iae) {
				throw new InitializationException(
						"You configured your server to start in an unssuported mode please check you serverConfig file",
						iae);
			}
		}
		// Read certificate from configuration only once, so it is cached for
		// reuse
		try {
			final String sKeyStorePath = ServerConfigFile.getKeyStorePath();
			final String sKeyStorePassword = ServerConfigFile
					.getKeyStorePassword();
			final String sKeyStoreAlias = ServerConfigFile.getKeyStoreAlias();

			final KeyStore aKeyStore = KeyStoreUtils.loadKeyStore(
					sKeyStorePath, sKeyStorePassword);
			s_aConfiguredCert = (X509Certificate) aKeyStore
					.getCertificate(sKeyStoreAlias);
			s_aLogger.info("Our Certificate - Serial Number: "
					+ s_aConfiguredCert.getSerialNumber());
		} catch (final Exception ex) {
			throw new InitializationException(
					"Failed to read the configured certificate", ex);
		}
	}

	@Resource
	private WebServiceContext webServiceContext;

	/**
	 * @param aMetadata
	 * @return The access point URL
	 * @throws FaultMessage
	 *             In case the endpoint address could not be resolved.
	 */
	@Nullable
	private static EndpointType _getRecipientEndpoint(
			@Nonnull final IMessageMetadata aMetadata) throws FaultMessage {
		final SimpleParticipantIdentifier aRecipientID = aMetadata
				.getRecipientID();
		try {
			if (s_aLogger.isDebugEnabled())
				s_aLogger.debug("Looking up the endpoint of recipient "
						+ aRecipientID.getURIEncoded());

			// Query the SMP
			SMPServiceCaller aSMPClient;
			if (CIPAServerMode.DEVELOPMENT_DIRECT_SMP.equals(SERVER_MODE)) {
				aSMPClient = new SMPServiceCaller(new URI(
						ServerConfigFile.getServerSMPUrl()));
			} else {
				aSMPClient = new SMPServiceCaller(aRecipientID, SML_INFO);
			}

			if (s_aLogger.isDebugEnabled())
				s_aLogger.debug("Performing SMP lookup at "
						+ aSMPClient.getSMPHost());

			return aSMPClient.getEndpoint(aRecipientID,
					aMetadata.getDocumentTypeID(), aMetadata.getProcessID());
		} catch (final Throwable t) {
			throw ExceptionUtils.createFaultMessage(
					t,
					"Failed to retrieve endpoint of recipient "
							+ aRecipientID.getURIEncoded());
		}
	}

	private static void _checkIfRecipientEndpointURLMatches(
			final EndpointType aRecipientEndpoint) throws FaultMessage {
		// Get our public endpoint address from the config file
		final String sOwnAPUrl = ServerConfigFile.getOwnAPURL();
		if (s_aLogger.isDebugEnabled())
			s_aLogger.debug("Our AP URL is " + sOwnAPUrl);

		// In debug mode, use our recipient URL, so that the URL check will work
		final String sRecipientAPUrl = GlobalDebug.isDebugMode() ? sOwnAPUrl
				: SMPServiceCaller.getEndpointAddress(aRecipientEndpoint);
		if (s_aLogger.isDebugEnabled())
			s_aLogger.debug("Recipient AP URL is " + sRecipientAPUrl);

		// Is it for us?
		if (!sRecipientAPUrl.contains(sOwnAPUrl)) {
			s_aLogger.error("The received document is not for us!");
			s_aLogger.error("Request is for: " + sRecipientAPUrl);
			s_aLogger.error("    Our URL is: " + sOwnAPUrl);

			// Avoid endless loop
			throw ExceptionUtils.createFaultMessage(new IllegalStateException(
					"Receiver(" + sRecipientAPUrl + ") invalid for us ("
							+ sOwnAPUrl + ")"),
					"The received document is not for us!");
		}
	}

	/**
	 * Check if the certificate of the receiver is identical to the configured
	 * one. This is done by checking the certificate serial numbers.
	 * 
	 * @param aReceiverCert
	 *            The certificate of the receiver
	 * @return <code>true</code> if equal
	 */
	private static boolean _isTheSameCert(
			@Nullable final X509Certificate aReceiverCert) {
		if (GlobalDebug.isDebugMode()) {
			s_aLogger.info("In debug mode the certificate is always approved");
			return true;
		}

		if (aReceiverCert == null) {
			// Log message was already emitted
			return false;
		}

		// Compare serial numbers
		final BigInteger aMySerial = s_aConfiguredCert.getSerialNumber();
		final BigInteger aReceiverSerial = aReceiverCert.getSerialNumber();
		if (!aMySerial.equals(aReceiverSerial)) {
			s_aLogger.error("Certificate serial number mismatch!");
			s_aLogger.info("Our certificate serial number: "
					+ aMySerial.toString());
			s_aLogger.info("       Receiver serial number: "
					+ aReceiverSerial.toString());
			return false;
		}

		// Serial numbers match
		return true;
	}

	private static void _checkIfEndpointCertificateMatches(
			final EndpointType aRecipientEndpoint) throws FaultMessage {
		final String sCertString = SMPServiceCaller
				.getEndpointCertificateString(aRecipientEndpoint);
		X509Certificate aRecipientSMPCert = null;
		try {
			aRecipientSMPCert = CertificateUtils
					.convertStringToCertficate(sCertString);
		} catch (final CertificateException t) {
			// In development mode it is okay, if this AccessPoint is not
			// registered
			// in an SMP
			if (!GlobalDebug.isDebugMode())
				throw ExceptionUtils.createFaultMessage(t,
						"Failed to convert endpoint certificate string '"
								+ sCertString + "'");
		}

		if (aRecipientSMPCert == null)
			s_aLogger
					.error("No Metadata Certificate found! Is this AP maybe not contained in an SMP?");
		else {
			if (s_aLogger.isDebugEnabled())
				s_aLogger.debug("Recipient certificate present: "
						+ aRecipientSMPCert.toString());
		}

		if (!_isTheSameCert(aRecipientSMPCert)) {
			s_aLogger.error("Metadata Certificate (" + aRecipientSMPCert
					+ ") does not match Access Point Certificate ("
					+ s_aConfiguredCert + ") - ignoring document");
			throw ExceptionUtils
					.createFaultMessage(
							new IllegalStateException(
									"Metadata Certificate does not match AP Certificate - ignoring document"),
							"Internal error: certificate mismatch!");
		}

		if (s_aLogger.isDebugEnabled())
			s_aLogger
					.debug("The certificate of the recipient matches our certificate");
	}

	/**
	 * @param body
	 */
	@UnsupportedOperation
	public GetResponse get(final Get body) {
		throw new UnsupportedOperationException(
				"Not supported by the current implementation according to the specifications");
	}

	/**
	 * @param body
	 */
	@UnsupportedOperation
	public PutResponse put(final Put body) {
		throw new UnsupportedOperationException(
				"Not supported by the current implementation according to the specifications");
	}

	/**
	 * @param body
	 */
	@UnsupportedOperation
	public DeleteResponse delete(final Delete body) {
		throw new UnsupportedOperationException(
				"Not supported by the current implementation according to the specifications");
	}

	/**
	 * Main action for receiving.
	 * 
	 * @param aBody
	 * @return Never <code>null</code>
	 * @throws FaultMessage
	 *             In case of an error
	 */
	@Action(input = "http://www.w3.org/2009/02/ws-tra/Create", output = "http://www.w3.org/2009/02/ws-tra/CreateResponse", fault = { @FaultAction(className = FaultMessage.class, value = "http://busdox.org/2010/02/channel/fault") })
	public CreateResponse create(final Create aBody) throws FaultMessage {
		if (GlobalDebug.isDebugMode())
			s_aLogger.warn("Receiving PEPPOL document in debug mode!");

		if (s_aLogger.isDebugEnabled())
			s_aLogger.debug("AccesspointService.create called");

		// Grabs the list of headers from the SOAP message
		final HeaderList aHeaderList = (HeaderList) webServiceContext
				.getMessageContext().get(
						JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
		final IMessageMetadata aMetadata = MessageMetadataHelper
				.createMetadataFromHeaders(aHeaderList);
		if (s_aLogger.isDebugEnabled())
			s_aLogger
					.debug("Extracted the following metadata from the headers\n"
							+ MessageMetadataHelper.getDebugInfo(aMetadata));

		// TODO do we need a check, whether the message ID was already received
		if (PingMessageHelper.isPingMessage(aMetadata)) {
			// It's a PING message - no actions to be taken!
			s_aLogger.info("Got a ping message from "
					+ aMetadata.getSenderID().getURIEncoded()
					+ " - discarding it!");
		} else {
			// Not a ping message

			if (!CIPAServerMode.DEVELOPMENT_STANDALONE.equals(SERVER_MODE)) {
				// Get the endpoint information required from the recipient
				final EndpointType aRecipientEndpoint = _getRecipientEndpoint(aMetadata);

				// Check if the message is for us
				_checkIfRecipientEndpointURLMatches(aRecipientEndpoint);

				// Get the recipient certificate from the SMP
				_checkIfEndpointCertificateMatches(aRecipientEndpoint);

			}

			s_aLogger.info("This is a handled request for "
					+ aMetadata.getRecipientID().getValue());

			// Invoke all available SPI implementations
			ESuccess eOverallSuccess = ESuccess.SUCCESS;
			final List<LogMessage> aProcessingMessages = new ArrayList<LogMessage>();
			try {
				// Invoke all available SPI implementations
				if (s_aLogger.isDebugEnabled())
					s_aLogger.debug("Now invoking " + s_aReceivers.size()
							+ " SPI implementations");

				for (final IAccessPointServiceReceiverSPI aReceiver : s_aReceivers) {
					if (s_aLogger.isDebugEnabled())
						s_aLogger.debug("Now invoking " + aReceiver.toString());

					final SuccessWithValue<AccessPointReceiveError> aSV = aReceiver
							.receiveDocument(webServiceContext, aMetadata,
									aBody);
					if (s_aLogger.isDebugEnabled())
						s_aLogger.debug("Result of invoking "
								+ aReceiver.toString() + ": " + aSV.toString());

					eOverallSuccess = eOverallSuccess.and(aSV);
					final AccessPointReceiveError aError = aSV.get();
					if (aError != null) {
						// Remember all messages
						aProcessingMessages.addAll(aError.getAllMessages());
					}
				}
			} catch (final Exception ex) {
				aProcessingMessages.add(new LogMessage(EErrorLevel.ERROR,
						"Internal error in processing incoming message", ex));
				eOverallSuccess = ESuccess.FAILURE;
			}

			if (!aProcessingMessages.isEmpty()) {
				// Log all messages from processing
				s_aLogger.info("Messages from "
						+ (eOverallSuccess.isSuccess() ? "successfuly"
								: "failed") + " processing of document "
						+ aMetadata.getMessageID() + ":");
				for (final LogMessage aLogMsg : aProcessingMessages)
					s_aLogger.info("  [" + aLogMsg.getErrorLevel().getID()
							+ "] " + aLogMsg.getMessage(),
							aLogMsg.getThrowable());
			}

			if (eOverallSuccess.isFailure()) {
				s_aLogger
						.error("Failed to handle incoming document from PEPPOL");
				throw ExceptionUtils
						.createFaultMessage(new IllegalStateException(
								"Failure in processing document from PEPPOL"),
								"Internal error in processing the incoming PEPPOL document");
			}

			// Log success
			s_aLogger.info("Done handling incoming document via START");
		}

		if (GlobalDebug.isDebugMode())
			_checkMemoryUsage();

		// Create an empty response
		final CreateResponse aResponse = new CreateResponse();
		return aResponse;
	}

	private static final long MEMORY_THRESHOLD_BYTES = 10 * CGlobal.BYTES_PER_MEGABYTE;
	private static long s_nLastUsageInBytes = 0;

	private static void _checkMemoryUsage() {
		System.gc();
		final Runtime aRuntime = Runtime.getRuntime();
		final long nFreeMemory = aRuntime.freeMemory();
		final long nTotalMemory = aRuntime.totalMemory();
		final long nUsedMemory = nTotalMemory - nFreeMemory;
		final SizeHelper aSH = SizeHelper.getSizeHelperOfLocale(Locale.US);
		final String sMemoryStatus = aSH.getAsMatching(nUsedMemory, 1) + " / "
				+ aSH.getAsMatching(nTotalMemory, 1) + " / "
				+ aSH.getAsMatching(aRuntime.maxMemory(), 1);

		if (nUsedMemory <= (s_nLastUsageInBytes - MEMORY_THRESHOLD_BYTES)
				|| nUsedMemory >= (s_nLastUsageInBytes + MEMORY_THRESHOLD_BYTES)) {
			final String sThreadName = Thread.currentThread().getName();
			s_aLogger.info("%%% [" + sThreadName + "] Memory usage: "
					+ sMemoryStatus);
			s_nLastUsageInBytes = nUsedMemory;
		}
	}
}
