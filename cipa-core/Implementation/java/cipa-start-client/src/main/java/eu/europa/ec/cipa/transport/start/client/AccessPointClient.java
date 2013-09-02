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

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.busdox.transport.start.saml.SAMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.AccessPointService;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.CreateResponse;
import org.w3._2009._02.ws_tra.FaultMessage;
import org.w3._2009._02.ws_tra.Resource;
import org.w3c.dom.Document;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.exceptions.InitializationException;
import com.phloc.commons.lang.GenericReflection;
import com.phloc.commons.random.VerySecureRandom;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.string.StringHelper;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.developer.WSBindingProvider;

import eu.europa.ec.cipa.peppol.security.HostnameVerifierAlwaysTrue;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;
import eu.europa.ec.cipa.transport.IMessageMetadata;
import eu.europa.ec.cipa.transport.MessageMetadataHelper;
import eu.europa.ec.cipa.transport.cert.AccessPointX509TrustManager;

/**
 * The accesspointClient class aims to hold all the processes required for
 * consuming an AccessPoint.
 * 
 * @author Dante Malaga(dante@alfa1lab.com) Jose Gorvenia<br>
 *         Narvaez(jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class AccessPointClient
{
  /** String that represents the SSL security provided. */
  public static final String SSL_PROTOCOL = "SSL";

  /** Logger to follow this class behavior. */
  private static final Logger s_aLogger = LoggerFactory.getLogger (AccessPointClient.class);

  static
  {
    if (GenericReflection.getClassFromNameSafe ("com.sun.xml.ws.Closeable") == null)
      throw new InitializationException ("Seems like Metro is not in the classpath. If you are using Maven please ensure the dependency 'org.glassfish.metro:webservices-rt' is added in scope 'provided' and that Metro is in your application server 'endorsed' directory.");
  }

  private AccessPointClient ()
  {}

  /**
   * Sets up the certificate TrustManager.
   * 
   * @throws NoSuchAlgorithmException
   */
  @Nonnull
  private static SSLSocketFactory _createSSLSocketFactory () throws Exception
  {
    final KeyManager [] aKeyManagers = null;
    final TrustManager [] aTrustManagers = new TrustManager [] { new AccessPointX509TrustManager (null, null) };
    final SSLContext aSSLContext = SSLContext.getInstance (SSL_PROTOCOL);
    aSSLContext.init (aKeyManagers, aTrustManagers, VerySecureRandom.getInstance ());
    return aSSLContext.getSocketFactory ();
  }

  /**
   * Sets up the certificate TrustManager.
   */
  @Nonnull
  private static ESuccess _setupCertificateTrustManager ()
  {
    try
    {
      HttpsURLConnection.setDefaultSSLSocketFactory (_createSSLSocketFactory ());
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug (">> Set Certificate Trust Manager");
      return ESuccess.SUCCESS;
    }
    catch (final Exception e)
    {
      s_aLogger.error ("Error setting the Certificate Trust Manager.", e);
      return ESuccess.FAILURE;
    }
  }

  /**
   * Configures and returns a port that points to the a specific endpoint
   * address.
   * 
   * @param sAddress
   *        the address of the receiving side.
   * @return the port.
   */
  @Nullable
  public static Resource createPort (@Nonnull @Nonempty final String sAddress)
  {
    if (StringHelper.hasNoText (sAddress))
      throw new IllegalArgumentException ("Address may not be empty!");

    try
    {
      // Host name verifier
      HttpsURLConnection.setDefaultHostnameVerifier (new HostnameVerifierAlwaysTrue ());
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug (">> Set HostVerifier");

      // SSL socket factory
      _setupCertificateTrustManager ();

      final AccessPointService aService = new AccessPointService ();
      final Resource aPort = aService.getResourceBindingPort ();
      final Map <String, Object> aRequestContext = ((BindingProvider) aPort).getRequestContext ();
      aRequestContext.put (BindingProvider.ENDPOINT_ADDRESS_PROPERTY, sAddress);
      if (false)
      {
        // According to the JAX-WS specs, this should work, but because of RM it
        // does not!
        // See Metro bug WSIT-1632
        aRequestContext.put (JAXWSProperties.HOSTNAME_VERIFIER, new HostnameVerifierAlwaysTrue ());
        aRequestContext.put (JAXWSProperties.SSL_SOCKET_FACTORY, _createSSLSocketFactory ());
      }
      return aPort;
    }
    catch (final Exception e)
    {
      s_aLogger.error ("Error creating the START WS Port for URL '" + sAddress + "'", e);
      return null;
    }
  }

  /**
   * Sends a Create object using a given port and attaching the given
   * SOAPHeaderObject data to the SOAP-envelope.
   * 
   * @param aPort
   *        the port which will be used to send the message.
   * @param aMetadata
   *        the SOAPHeaderObject holding the BUSDOX headers information that
   *        will be attached into the SOAP-envelope.
   * @param aBody
   *        Create object holding the SOAP-envelope payload.
   * @return {@link ESuccess}.
   */
  @Nonnull
  public static ESuccess send (@Nonnull final Resource aPort,
                               @Nonnull final IMessageMetadata aMetadata,
                               @Nonnull final Create aBody)
  {
    if (aPort == null)
      throw new NullPointerException ("port");
    if (aMetadata == null)
      throw new NullPointerException ("metadata");
    if (aBody == null)
      throw new NullPointerException ("body");
    if (!SAMLConfiguration.getInstance ().isConfigurationOK ())
      throw new IllegalStateException ("SAML configuration is invalid - see log file for details!");

    s_aLogger.info ("Ready for sending message\n" +
                    MessageMetadataHelper.getDebugInfo (aMetadata) +
                    "\n\tReceiver AP:\t" +
                    ((BindingProvider) aPort).getRequestContext ().get (BindingProvider.ENDPOINT_ADDRESS_PROPERTY));
    try
    {
      // Assign the headers
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Adding BUSDOX headers to SOAP-envelope");
      final List <Header> aHeaders = MessageMetadataHelper.createHeadersFromMetadata (aMetadata);
      ((WSBindingProvider) aPort).setOutboundHeaders (aHeaders);

      // Main client call
      final CreateResponse aResponse = aPort.create (aBody);

      // Build response log message
      final StringBuilder aResponseMsg = new StringBuilder ();
      aResponseMsg.append ("Message ").append (aMetadata.getMessageID ()).append (" has been successfully delivered!");
      if (aResponse != null)
      {
        if (aResponse.getResourceCreated () != null &&
            !aResponse.getResourceCreated ().getEndpointReference ().isEmpty ())
        {
          aResponseMsg.append ("\n  EndpointReferences: ");
          int nIndex = 0;
          for (final W3CEndpointReference aEPRef : aResponse.getResourceCreated ().getEndpointReference ())
          {
            if (nIndex++ > 0)
              aResponseMsg.append (", ");
            aResponseMsg.append (W3CEndpointReferenceUtils.getAddress (aEPRef));
          }
        }
        if (aResponse.getAny () != null)
          aResponseMsg.append ("\n  Response content: ").append (aResponse.getAny ());
        if (!aResponse.getOtherAttributes ().isEmpty ())
          aResponseMsg.append ("\n  Response attributes: ").append (aResponse.getOtherAttributes ());
      }
      s_aLogger.info (aResponseMsg.toString ());

      // Done
      return ESuccess.SUCCESS;
    }
    catch (final JAXBException ex)
    {
      // Usually a JAXB marshalling error
      s_aLogger.error ("An error occurred while marshalling headers.", ex);
    }
    catch (final FaultMessage ex)
    {
      // A wrapped error from the START server
      s_aLogger.error ("Error while sending the message.", ex);
    }
    catch (final WebServiceException ex)
    {
      // An error from the Metro framework
      s_aLogger.error ("Internal error while sending the message", ex);
    }
    finally
    {
      // Close the port directly after sending.
      // This is important for WSRM!
      ((com.sun.xml.ws.Closeable) aPort).close ();
    }
    return ESuccess.FAILURE;
  }

  @Nonnull
  public static ESuccess send (@Nonnull final String sAddressURL,
                               @Nonnull final IMessageMetadata aMetadata,
                               @Nonnull final Create aBody)
  {
    final Resource aPort = createPort (sAddressURL);
    if (aPort == null)
    {
      // Warning was already emitted
      return ESuccess.FAILURE;
    }
    return send (aPort, aMetadata, aBody);
  }

  /**
   * Send an XML document via START to the destination AP.
   * 
   * @param sAddressURL
   *        The absolute URL of the receiving AP. Must include the service name!
   * @param aMetadata
   *        The metadata of the document to send. May not be <code>null</code>.
   * @param aXMLDoc
   *        The XML document to be transmitted as the payload. May not be
   *        <code>null</code> and must contain a document element!
   * @return {@link ESuccess#SUCCESS} if sending succeeded,
   *         {@link ESuccess#FAILURE} otherwise. In case of a failure check the
   *         log-file.
   */
  @Nonnull
  public static ESuccess send (@Nonnull final String sAddressURL,
                               @Nonnull final IMessageMetadata aMetadata,
                               @Nonnull final Document aXMLDoc)
  {
    if (aXMLDoc == null)
      throw new NullPointerException ("XMLDocument");

    // Check if a document-element is present
    if (aXMLDoc.getDocumentElement () == null)
      throw new IllegalArgumentException ("Passed XML node does not belong to a Document that has a DocumentElement!");

    final Create aCreateBody = new Create ();
    aCreateBody.getAny ().add (aXMLDoc.getDocumentElement ());
    return send (sAddressURL, aMetadata, aCreateBody);
  }
}
