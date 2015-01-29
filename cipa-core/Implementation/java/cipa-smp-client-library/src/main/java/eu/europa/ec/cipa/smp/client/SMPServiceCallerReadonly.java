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

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.JAXBElement;

import org.busdox.servicemetadata.publishing._1.CompleteServiceGroupType;
import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.RedirectType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupReferenceListType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.SignedServiceMetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2000._09.xmldsig.X509DataType;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.collections.ContainerHelper;
import com.helger.web.http.basicauth.BasicAuthClientCredentials;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import eu.europa.ec.cipa.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import eu.europa.ec.cipa.busdox.identifier.IReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.busdox.identifier.IReadonlyProcessIdentifier;
import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.peppol.uri.BusdoxURLUtils;
import eu.europa.ec.cipa.peppol.utils.CertificateUtils;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;
import eu.europa.ec.cipa.smp.client.exception.BadRequestException;
import eu.europa.ec.cipa.smp.client.exception.NotFoundException;
import eu.europa.ec.cipa.smp.client.exception.UnauthorizedException;
import eu.europa.ec.cipa.smp.client.exception.UnknownException;

/**
 * This class is used for calling the SMP REST interface. This particular class
 * only contains the read-only methods!
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@SuppressWarnings ("deprecation")
public class SMPServiceCallerReadonly {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServiceCallerReadonly.class);

  // Don't think of changing anything here! This is very sensitive. Don't
  // extract a base class or some else. DON'T TOUCH!
  // See http://sites.google.com/site/richchihlee/portal/j2ee/rs/rs-client
  private static final GenericType <JAXBElement <ServiceGroupType>> TYPE_SERVICEGROUP = new GenericType <JAXBElement <ServiceGroupType>> () {};
  private static final GenericType <JAXBElement <CompleteServiceGroupType>> TYPE_COMPLETESERVICEGROUP = new GenericType <JAXBElement <CompleteServiceGroupType>> () {};
  private static final GenericType <JAXBElement <ServiceGroupReferenceListType>> TYPE_SERVICEGROUPREFERENCELIST = new GenericType <JAXBElement <ServiceGroupReferenceListType>> () {};
  private static final GenericType <JAXBElement <SignedServiceMetadataType>> TYPE_SIGNEDSERVICEMETADATA = new GenericType <JAXBElement <SignedServiceMetadataType>> () {};

  // Members - free to change from here on

  private final URI m_aSMPHost;
  private final WebResource m_aWebResource;
  private final WebResource m_aWebResourceWithSignatureCheck;

  @Nonnull
  public static WebResource getResourceWithSignatureCheck (@Nonnull final URI aURI) {
    ValueEnforcer.notNull (aURI, "URI");

    final Client aClient = Client.create ();
    aClient.addFilter (new CheckSignatureFilter ());
    aClient.setFollowRedirects (Boolean.FALSE);
    return aClient.resource (aURI);
  }

  @Nonnull
  public static WebResource getResourceWithoutSignatureCheck (@Nonnull final URI aURI) {
    ValueEnforcer.notNull (aURI, "URI");

    final Client aClient = Client.create ();
    aClient.setFollowRedirects (Boolean.FALSE);
    return aClient.resource (aURI);
  }

  /**
   * Constructor with SML lookup
   *
   * @param aParticipantIdentifier
   *        The participant identifier to be used. Required to build the SMP
   *        access URI.
   * @param aSMLInfo
   *        The SML to be used. Required to build the SMP access URI.
   * @see BusdoxURLUtils#getSMPURIOfParticipant(IReadonlyParticipantIdentifier,
   *      ISMLInfo)
   */
  public SMPServiceCallerReadonly (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                                   @Nonnull final ISMLInfo aSMLInfo) {
    this (BusdoxURLUtils.getSMPURIOfParticipant (aParticipantIdentifier, aSMLInfo));
  }

  /**
   * Constructor with SML lookup
   *
   * @param aParticipantIdentifier
   *        The participant identifier to be used. Required to build the SMP
   *        access URI.
   * @param sSMLZoneName
   *        The SML DNS zone name to be used. Required to build the SMP access
   *        URI. Must end with a trailing dot (".") and may neither be
   *        <code>null</code> nor empty to build a correct URL. May not start
   *        with "http://". Example: <code>sml.peppolcentral.org.</code>
   * @see BusdoxURLUtils#getSMPURIOfParticipant(IReadonlyParticipantIdentifier,
   *      String)
   */
  public SMPServiceCallerReadonly (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                                   @Nonnull @Nonempty final String sSMLZoneName) {
    this (BusdoxURLUtils.getSMPURIOfParticipant (aParticipantIdentifier, sSMLZoneName));
  }

  /**
   * Constructor with a direct SMP URL.<br>
   * Remember: must be HTTP and using port 80 only!
   *
   * @param aSMPHost
   *        The address of the SMP service. Must be port 80 and basic http only
   *        (no https!). Example: http://smpcompany.company.org
   */
  public SMPServiceCallerReadonly (@Nonnull final URI aSMPHost) {
    ValueEnforcer.notNull (aSMPHost, "SMPHost");

    if (!"http".equals (aSMPHost.getScheme ()))
      s_aLogger.warn ("SMP URI " + aSMPHost + " does not use the expected http scheme!");
    // getPort () returns -1 if none was explicitly specified
    if (aSMPHost.getPort () != 80 && aSMPHost.getPort () != -1)
      s_aLogger.warn ("SMP URI " + aSMPHost + " is not running on port 80!");

    m_aSMPHost = aSMPHost;
    m_aWebResource = getResourceWithoutSignatureCheck (m_aSMPHost);
    m_aWebResourceWithSignatureCheck = getResourceWithSignatureCheck (m_aSMPHost);
  }

  /**
   * @return The SMP host URI we're operating on. Never <code>null</code>.
   */
  @Nonnull
  public URI getSMPHost () {
    return m_aSMPHost;
  }

  /**
   * @return The Jersey WebResource without signature check. Never
   *         <code>null</code>.
   */
  @Nonnull
  public WebResource getWebResource () {
    return m_aWebResource;
  }

  /**
   * @return The Jersey WebResource with signature check. Never
   *         <code>null</code>.
   */
  @Nonnull
  public WebResource getWebResourceWithSignatureCheck () {
    return m_aWebResourceWithSignatureCheck;
  }

  /**
   * Convert the passed generic HTTP exception into a more specific exception.
   *
   * @param ex
   *        The generic exception. May not be <code>null</code>.
   * @return A new SMP specific exception, using the passed exception as the
   *         cause.
   */
  @Nonnull
  public static Exception getConvertedException (@Nonnull final UniformInterfaceException ex) {
    final Status eHttpStatus = ex.getResponse ().getClientResponseStatus ();
    switch (eHttpStatus) {
      case FORBIDDEN:
        return new UnauthorizedException (ex);
      case NOT_FOUND:
        return new NotFoundException (ex);
      case BAD_REQUEST:
        return new BadRequestException (ex);
      default:
        return new UnknownException ("Error thrown with status code: '" +
                                     eHttpStatus +
                                     "' (" +
                                     eHttpStatus.getStatusCode () +
                                     "), and message: " +
                                     ex.getResponse ().getEntity (String.class));
    }
  }

  @Nonnull
  public static ServiceGroupReferenceListType getServiceGroupReferenceList (@Nonnull final WebResource aFullResource,
                                                                            @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    ValueEnforcer.notNull (aFullResource, "FullResource");
    ValueEnforcer.notNull (aCredentials, "Credentials");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("getServiceGroupReferenceList from " + aFullResource.getURI ());

    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION, aCredentials.getRequestValue ());
      return aBuilderWithAuth.get (TYPE_SERVICEGROUPREFERENCELIST).getValue ();
    }
    catch (final UniformInterfaceException e) {
      throw getConvertedException (e);
    }
  }

  @Nonnull
  public WebResource getServiceGroupReferenceListResource (@Nonnull final String sUserID) {
    ValueEnforcer.notEmpty (sUserID, "UserID");

    return m_aWebResource.path ("/list/" + BusdoxURLUtils.createPercentEncodedURL (sUserID));
  }

  /**
   * Gets a list of references to the CompleteServiceGroup's owned by the
   * specified userId.
   *
   * @param sUserID
   *        The username for which to retrieve service groups.
   * @param aCredentials
   *        The username and password to use as aCredentials.
   * @return A list of references to complete service groups.
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         The username or password was not correct.
   * @throws NotFoundException
   *         The userId did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  @Nonnull
  public ServiceGroupReferenceListType getServiceGroupReferenceList (@Nonnull final String sUserID,
                                                                     @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    ValueEnforcer.notNull (aCredentials, "Credentials");

    final WebResource aFullResource = getServiceGroupReferenceListResource (sUserID);
    return getServiceGroupReferenceList (aFullResource, aCredentials);
  }

  @Nullable
  public ServiceGroupReferenceListType getServiceGroupReferenceListOrNull (@Nonnull final String sUserID,
                                                                           @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    try {
      return getServiceGroupReferenceList (sUserID, aCredentials);
    }
    catch (final NotFoundException ex) {
      return null;
    }
  }

  @Nonnull
  public static CompleteServiceGroupType getCompleteServiceGroup (@Nonnull final WebResource aFullResource) throws Exception {
    ValueEnforcer.notNull (aFullResource, "FullResource");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("getCompleteServiceGroup from " + aFullResource.getURI ());

    try {
      return aFullResource.get (TYPE_COMPLETESERVICEGROUP).getValue ();
    }
    catch (final UniformInterfaceException e) {
      throw getConvertedException (e);
    }
  }

  @Nonnull
  public WebResource getCompleteServiceGroupResource (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) {
    ValueEnforcer.notNull (aServiceGroupID, "ServiceGroupID");

    return m_aWebResource.path ("/complete/" + IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID));
  }

  /**
   * Returns a complete service group. A complete service group contains both
   * the service group and the service metadata.
   *
   * @param aServiceGroupID
   *        The service group id corresponding to the service group which one
   *        wants to get.
   * @return The complete service group containing service group and service
   *         metadata. Never <code>null</code>.
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  @Nonnull
  public CompleteServiceGroupType getCompleteServiceGroup (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    final WebResource aFullResource = getCompleteServiceGroupResource (aServiceGroupID);
    return getCompleteServiceGroup (aFullResource);
  }

  /**
   * Returns a complete service group. A complete service group contains both
   * the service group and the service metadata.
   *
   * @param aServiceGroupID
   *        The service group id corresponding to the service group which one
   *        wants to get.
   * @return The complete service group containing service group and service
   *         metadata or <code>null</code> if no such service group exists.
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  @Nullable
  public CompleteServiceGroupType getCompleteServiceGroupOrNull (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    try {
      return getCompleteServiceGroup (aServiceGroupID);
    }
    catch (final NotFoundException ex) {
      return null;
    }
  }

  /**
   * Returns a complete service group. A complete service group contains both
   * the service group and the service metadata. This method is handy when using
   * the results from
   * {@link #getServiceGroupReferenceList(String, BasicAuthClientCredentials)}
   *
   * @param aURI
   *        The URI containing the complete service group
   * @return The complete service group containing service group and service
   *         metadata. Never <code>null</code>.
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  @Nonnull
  public static CompleteServiceGroupType getCompleteServiceGroup (@Nonnull final URI aURI) throws Exception {
    return getCompleteServiceGroup (getResourceWithoutSignatureCheck (aURI));
  }

  /**
   * Returns a complete service group. A complete service group contains both
   * the service group and the service metadata. This method is handy when using
   * the results from
   * {@link #getServiceGroupReferenceList(String, BasicAuthClientCredentials)}
   *
   * @param aURI
   *        The URI containing the complete service group
   * @return The complete service group containing service group and service
   *         metadata or <code>null</code> if no such service group exists.
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  @Nullable
  public static CompleteServiceGroupType getCompleteServiceGroupOrNull (@Nonnull final URI aURI) throws Exception {
    try {
      return getCompleteServiceGroup (aURI);
    }
    catch (final NotFoundException ex) {
      return null;
    }
  }

  /**
   * Returns a complete service group. A complete service group contains both
   * the service group and the service metadata.
   *
   * @param aSMLInfo
   *        The SML object to be used
   * @param aServiceGroupID
   *        The service group id corresponding to the service group which one
   *        wants to get.
   * @return The complete service group containing service group and service
   *         metadata
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  @Nonnull
  public static CompleteServiceGroupType getCompleteServiceGroupByDNS (@Nonnull final ISMLInfo aSMLInfo,
                                                                       @Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    return new SMPServiceCallerReadonly (aServiceGroupID, aSMLInfo).getCompleteServiceGroup (aServiceGroupID);
  }

  @Nonnull
  public static ServiceGroupType getServiceGroup (@Nonnull final WebResource aFullResource) throws Exception {
    ValueEnforcer.notNull (aFullResource, "FullResource");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("getServiceGroup from " + aFullResource.getURI ());

    try {
      return aFullResource.get (TYPE_SERVICEGROUP).getValue ();
    }
    catch (final UniformInterfaceException e) {
      throw getConvertedException (e);
    }
  }

  @Nonnull
  public WebResource getServiceGroupResource (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) {
    ValueEnforcer.notNull (aServiceGroupID, "ServiceGroupID");

    return m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID));
  }

  /**
   * Returns a service group. A service group references to the service
   * metadata.
   *
   * @param aServiceGroupID
   *        The service group id corresponding to the service group which one
   *        wants to get.
   * @return The service group
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  @Nonnull
  public ServiceGroupType getServiceGroup (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    final WebResource aFullResource = getServiceGroupResource (aServiceGroupID);
    return getServiceGroup (aFullResource);
  }

  @Nullable
  public ServiceGroupType getServiceGroupOrNull (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    try {
      return getServiceGroup (aServiceGroupID);
    }
    catch (final NotFoundException ex) {
      return null;
    }
  }

  /**
   * Returns a service group. A service group references to the service
   * metadata.
   *
   * @param aSMLInfo
   *        The SML object to be used
   * @param aServiceGroupID
   *        The service group id corresponding to the service group which one
   *        wants to get.
   * @return The service group
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  @Nonnull
  public static ServiceGroupType getServiceGroupByDNS (@Nonnull final ISMLInfo aSMLInfo,
                                                       @Nonnull final IReadonlyParticipantIdentifier aServiceGroupID) throws Exception {
    return new SMPServiceCallerReadonly (aServiceGroupID, aSMLInfo).getServiceGroup (aServiceGroupID);
  }

  @Nonnull
  public static SignedServiceMetadataType getServiceRegistration (@Nonnull final WebResource aFullResource) throws Exception {
    ValueEnforcer.notNull (aFullResource, "FullResource");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("getServiceRegistration from " + aFullResource.getURI ());

    try {
      SignedServiceMetadataType aMetadata = aFullResource.get (TYPE_SIGNEDSERVICEMETADATA).getValue ();

      // If the Redirect element is present, then follow 1 redirect.
      if (aMetadata.getServiceMetadata () != null && aMetadata.getServiceMetadata ().getRedirect () != null) {
        final RedirectType aRedirect = aMetadata.getServiceMetadata ().getRedirect ();

        // Follow the redirect
        final WebResource aRedirectFullResource = getResourceWithSignatureCheck (URI.create (aRedirect.getHref ()));
        s_aLogger.info ("Following a redirect to " + aRedirect.getHref ());
        aMetadata = aRedirectFullResource.get (TYPE_SIGNEDSERVICEMETADATA).getValue ();

        // Check that the certificateUID is correct.
        boolean bCertificateSubjectFound = false;
        outer: for (final Object aObj : aMetadata.getSignature ().getKeyInfo ().getContent ()) {
          final Object aInfoValue = ((JAXBElement <?>) aObj).getValue ();
          if (aInfoValue instanceof X509DataType) {
            final X509DataType aX509Data = (X509DataType) aInfoValue;
            for (final Object aX509Obj : aX509Data.getX509IssuerSerialOrX509SKIOrX509SubjectName ()) {
              final JAXBElement <?> aX509element = (JAXBElement <?>) aX509Obj;
              // Find the first subject (of type string)
              if (aX509element.getValue () instanceof String) {
                final String sSubject = (String) aX509element.getValue ();

                if (!aRedirect.getCertificateUID ().equals (sSubject)) {
                  throw new UnknownException ("The certificate UID of the redirect did not match the certificate subject. Subject: " +
                                              sSubject +
                                              ". CertificateUID: " +
                                              aRedirect.getCertificateUID ());
                }
                bCertificateSubjectFound = true;
                break outer;
              }
            }
          }
        }

        if (!bCertificateSubjectFound)
          throw new UnknownException ("The X509 certificate did not contain a certificate subject.");
      }

      return aMetadata;
    }
    catch (final UniformInterfaceException e) {
      throw getConvertedException (e);
    }
  }

  @Nonnull
  public WebResource getServiceRegistrationResource (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                     @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID) {
    ValueEnforcer.notNull (aServiceGroupID, "ServiceGroupID");
    ValueEnforcer.notNull (aDocumentTypeID, "DocumentTypeID");

    final String sPath = IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID) +
                         "/services/" +
                         IdentifierUtils.getIdentifierURIPercentEncoded (aDocumentTypeID);
    return m_aWebResourceWithSignatureCheck.path (sPath);
  }

  /**
   * Gets a signed service metadata object given by its service group id and its
   * document type.
   *
   * @param aServiceGroupID
   *        The service group id of the service metadata to get.
   * @param aDocumentTypeID
   *        The document type of the service metadata to get.
   * @return A signed service metadata object. Never <code>null</code>.
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id or document type did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  @Nonnull
  public SignedServiceMetadataType getServiceRegistration (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                           @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID) throws Exception {
    final WebResource aFullResource = getServiceRegistrationResource (aServiceGroupID, aDocumentTypeID);
    return getServiceRegistration (aFullResource);
  }

  /**
   * Gets a signed service metadata object given by its service group id and its
   * document type.
   *
   * @param aServiceGroupID
   *        The service group id of the service metadata to get.
   * @param aDocumentTypeID
   *        The document type of the service metadata to get.
   * @return A signed service metadata object or <code>null</code> if any error
   *         occurs.
   * @throws Exception
   *         in case something goes wrong
   */
  @Nullable
  public SignedServiceMetadataType getServiceRegistrationOrNull (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                                 @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID) throws Exception {
    try {
      return getServiceRegistration (aServiceGroupID, aDocumentTypeID);
    }
    catch (final NotFoundException ex) {
      return null;
    }
  }

  /**
   * Gets a signed service metadata object given by its service group id and its
   * document type.
   *
   * @param aSMLInfo
   *        The SML object to be used
   * @param aServiceGroupID
   *        The service group id of the service metadata to get.
   * @param aDocumentTypeID
   *        The document type of the service metadata to get.
   * @return A signed service metadata object.
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         A HTTP Forbidden was received, should not happen.
   * @throws NotFoundException
   *         The service group id or document type did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  @Nonnull
  public static SignedServiceMetadataType getServiceRegistrationByDNS (@Nonnull final ISMLInfo aSMLInfo,
                                                                       @Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                                       @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID) throws Exception {
    return new SMPServiceCallerReadonly (aServiceGroupID, aSMLInfo).getServiceRegistration (aServiceGroupID,
                                                                                            aDocumentTypeID);
  }

  @Nullable
  @Deprecated
  public EndpointType getEndpoint (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                   @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                   @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {
    // Backwards compatibility
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.TRANSPORT_PROFILE_START;
    return getEndpoint (aServiceGroupID, aDocumentTypeID, aProcessID, eTransportProfile);
  }

  @Nullable
  public EndpointType getEndpoint (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                   @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                   @Nonnull final IReadonlyProcessIdentifier aProcessID,
                                   @Nonnull final ESMPTransportProfile eTransportProfile) throws Exception {
    ValueEnforcer.notNull (aServiceGroupID, "serviceGroupID");
    ValueEnforcer.notNull (aDocumentTypeID, "DocumentTypeID");
    ValueEnforcer.notNull (aProcessID, "ProcessID");

    // Get meta data for participant/documentType
    final SignedServiceMetadataType aSignedServiceMetadata = getServiceRegistrationOrNull (aServiceGroupID,
                                                                                           aDocumentTypeID);
    return aSignedServiceMetadata == null ? null : getEndpoint (aSignedServiceMetadata, aProcessID, eTransportProfile);
  }

  @Nullable
  @Deprecated
  public EndpointType getEndpoint (@Nonnull final SignedServiceMetadataType aSignedServiceMetadata,
                                   @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {
    // Backwards compatibility
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.TRANSPORT_PROFILE_START;
    return getEndpoint (aSignedServiceMetadata, aProcessID, eTransportProfile);
  }

  /**
   * Extract the Endpoint from the signedServiceMetadata that matches the passed
   * process ID and the optional required transport profile.
   *
   * @param aSignedServiceMetadata
   *        The signed service meta data object (e.g. from a call to
   *        {@link #getServiceRegistrationOrNull(IReadonlyParticipantIdentifier, IReadonlyDocumentTypeIdentifier)}
   *        . May not be <code>null</code>.
   * @param aProcessID
   *        The process identifier to be looked up. May not be <code>null</code>
   *        .
   * @param eTransportProfile
   *        The required transport profile to be used. May not be
   *        <code>null</code>.
   * @return <code>null</code> if no matching endpoint was found
   */
  @Nullable
  public EndpointType getEndpoint (@Nonnull final SignedServiceMetadataType aSignedServiceMetadata,
                                   @Nonnull final IReadonlyProcessIdentifier aProcessID,
                                   @Nonnull final ESMPTransportProfile eTransportProfile) {
    ValueEnforcer.notNull (aSignedServiceMetadata, "SignedServiceMetadata");
    ValueEnforcer.notNull (aSignedServiceMetadata.getServiceMetadata (), "SignedServiceMetadata.ServiceMetadata");
    ValueEnforcer.notNull (aSignedServiceMetadata.getServiceMetadata ().getServiceInformation (),
                           "SignedServiceMetadata.ServiceMetadata.ServiceInformation");
    ValueEnforcer.notNull (aSignedServiceMetadata.getServiceMetadata ().getServiceInformation ().getProcessList (),
                           "SignedServiceMetadata.ServiceMetadata.ServiceInformation.ProcessList");
    ValueEnforcer.notNull (aProcessID, "ProcessID");
    ValueEnforcer.notNull (eTransportProfile, "TransportProfile");

    // Iterate all processes
    final List <ProcessType> aAllProcesses = aSignedServiceMetadata.getServiceMetadata ()
                                                                   .getServiceInformation ()
                                                                   .getProcessList ()
                                                                   .getProcess ();
    for (final ProcessType aProcessType : aAllProcesses) {
      // Matches the requested one?
      if (IdentifierUtils.areIdentifiersEqual (aProcessType.getProcessIdentifier (), aProcessID)) {
        // Get all endpoints
        final List <EndpointType> aEndpoints = aProcessType.getServiceEndpointList ().getEndpoint ();
        // Filter by required transport profile
        final List <EndpointType> aRelevantEndpoints = new ArrayList <EndpointType> ();
        for (final EndpointType aEndpoint : aEndpoints)
          if (eTransportProfile.getID ().equals (aEndpoint.getTransportProfile ()))
            aRelevantEndpoints.add (aEndpoint);

        if (aRelevantEndpoints.size () != 1) {
          s_aLogger.warn ("Found " +
                          aRelevantEndpoints.size () +
                          " endpoints for process " +
                          aProcessID +
                          " and transport profile " +
                          eTransportProfile.getID () +
                          (aRelevantEndpoints.isEmpty () ? "" : ": " +
                                                                aRelevantEndpoints.toString () +
                                                                " - using the first one"));
        }

        // Use the first endpoint or null
        return ContainerHelper.getFirstElement (aRelevantEndpoints);
      }
    }
    return null;
  }

  @Nullable
  @Deprecated
  public String getEndpointAddress (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                    @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                    @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {
    // Backwards compatibility
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.TRANSPORT_PROFILE_START;
    return getEndpointAddress (aServiceGroupID, aDocumentTypeID, aProcessID, eTransportProfile);
  }

  @Nullable
  public String getEndpointAddress (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                    @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                    @Nonnull final IReadonlyProcessIdentifier aProcessID,
                                    @Nonnull final ESMPTransportProfile eTransportProfile) throws Exception {
    final EndpointType aEndpoint = getEndpoint (aServiceGroupID, aDocumentTypeID, aProcessID, eTransportProfile);
    return getEndpointAddress (aEndpoint);
  }

  @Nullable
  public static String getEndpointAddress (@Nullable final EndpointType aEndpoint) {
    return aEndpoint == null ? null : W3CEndpointReferenceUtils.getAddress (aEndpoint.getEndpointReference ());
  }

  @Nullable
  @Deprecated
  public String getEndpointCertificateString (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                              @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                              @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {
    // Backwards compatibility
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.TRANSPORT_PROFILE_START;
    return getEndpointCertificateString (aServiceGroupID, aDocumentTypeID, aProcessID, eTransportProfile);
  }

  @Nullable
  public String getEndpointCertificateString (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                              @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                              @Nonnull final IReadonlyProcessIdentifier aProcessID,
                                              @Nonnull final ESMPTransportProfile eTransportProfile) throws Exception {
    final EndpointType aEndpoint = getEndpoint (aServiceGroupID, aDocumentTypeID, aProcessID, eTransportProfile);
    return aEndpoint == null ? null : aEndpoint.getCertificate ();
  }

  @Nullable
  public static String getEndpointCertificateString (@Nullable final EndpointType aEndpoint) {
    return aEndpoint == null ? null : aEndpoint.getCertificate ();
  }

  @Nullable
  @Deprecated
  public X509Certificate getEndpointCertificate (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                 @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                                 @Nonnull final IReadonlyProcessIdentifier aProcessID) throws Exception {
    // Backwards compatibility
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.TRANSPORT_PROFILE_START;
    return getEndpointCertificate (aServiceGroupID, aDocumentTypeID, aProcessID, eTransportProfile);
  }

  @Nullable
  public X509Certificate getEndpointCertificate (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                                 @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                                 @Nonnull final IReadonlyProcessIdentifier aProcessID,
                                                 @Nonnull final ESMPTransportProfile eTransportProfile) throws Exception {
    final String sCertString = getEndpointCertificateString (aServiceGroupID,
                                                             aDocumentTypeID,
                                                             aProcessID,
                                                             eTransportProfile);
    return CertificateUtils.convertStringToCertficate (sCertString);
  }

  @Nullable
  public static X509Certificate getEndpointCertificate (@Nullable final EndpointType aEndpoint) throws CertificateException {
    final String sCertString = getEndpointCertificateString (aEndpoint);
    return CertificateUtils.convertStringToCertficate (sCertString);
  }
}
