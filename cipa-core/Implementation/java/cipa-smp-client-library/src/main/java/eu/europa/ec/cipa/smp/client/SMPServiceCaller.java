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

import javax.annotation.Nonnull;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.web.http.basicauth.BasicAuthClientCredentials;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import eu.europa.ec.cipa.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import eu.europa.ec.cipa.busdox.identifier.IReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.peppol.uri.BusdoxURLUtils;
import eu.europa.ec.cipa.smp.client.exception.BadRequestException;
import eu.europa.ec.cipa.smp.client.exception.NotFoundException;
import eu.europa.ec.cipa.smp.client.exception.UnauthorizedException;
import eu.europa.ec.cipa.smp.client.exception.UnknownException;

/**
 * This class is used for calling the SMP REST interface. This particular class
 * contains the writing methods.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SMPServiceCaller extends SMPServiceCallerReadonly {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SMPServiceCaller.class);

  // Members - free to change from here on
  private static final ObjectFactory s_aObjFactory = new ObjectFactory ();

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
  public SMPServiceCaller (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                           @Nonnull final ISMLInfo aSMLInfo) {
    super (aParticipantIdentifier, aSMLInfo);
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
  public SMPServiceCaller (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                           @Nonnull @Nonempty final String sSMLZoneName) {
    super (aParticipantIdentifier, sSMLZoneName);
  }

  /**
   * Constructor with a direct SMP URL.<br>
   * Remember: must be HTTP and using port 80 only!
   *
   * @param aSMPHost
   *        The address of the SMP service. Must be port 80 and basic http only
   *        (no https!). Example: http://smpcompany.company.org
   */
  public SMPServiceCaller (@Nonnull final URI aSMPHost) {
    super (aSMPHost);
  }

  public static void saveServiceGroup (@Nonnull final WebResource aFullResource,
                                       @Nonnull final ServiceGroupType aServiceGroup,
                                       @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    ValueEnforcer.notNull (aFullResource, "FullResource");
    ValueEnforcer.notNull (aServiceGroup, "ServiceGroup");
    ValueEnforcer.notNull (aCredentials, "Credentials");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_saveServiceGroup from " + aFullResource.getURI ());

    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION, aCredentials.getRequestValue ());

      // Important to build a JAXBElement around the service group
      aBuilderWithAuth.type (MediaType.TEXT_XML).put (s_aObjFactory.createServiceGroup (aServiceGroup));
    }
    catch (final UniformInterfaceException e) {
      throw getConvertedException (e);
    }
  }

  /**
   * Saves a service group. The meta data references should not be set and are
   * not used.
   *
   * @param aServiceGroup
   *        The service group to save.
   * @param aCredentials
   *        The user name and password to use as aCredentials.
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         The user name or password was not correct.
   * @throws NotFoundException
   *         A HTTP Not Found was received. This can happen if the service was
   *         not found.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public void saveServiceGroup (@Nonnull final ServiceGroupType aServiceGroup,
                                @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    ValueEnforcer.notNull (aServiceGroup, "ServiceGroup");
    ValueEnforcer.notNull (aCredentials, "Credentials");

    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroup.getParticipantIdentifier ()));
    saveServiceGroup (aFullResource, aServiceGroup, aCredentials);
  }

  /**
   * Saves a service group. The meta data references should not be set and are
   * not used.
   *
   * @param aParticipantID
   *        The participant identifier for which the service group is to save.
   * @param aCredentials
   *        The user name and password to use as aCredentials.
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         The user name or password was not correct.
   * @throws NotFoundException
   *         A HTTP Not Found was received. This can happen if the service was
   *         not found.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public void saveServiceGroup (@Nonnull final IReadonlyParticipantIdentifier aParticipantID,
                                @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    ValueEnforcer.notNull (aParticipantID, "ParticipantID");
    ValueEnforcer.notNull (aCredentials, "Credentials");

    final ServiceGroupType aServiceGroup = s_aObjFactory.createServiceGroupType ();
    aServiceGroup.setParticipantIdentifier (new SimpleParticipantIdentifier (aParticipantID));
    saveServiceGroup (aServiceGroup, aCredentials);
  }

  public static void deleteServiceGroup (@Nonnull final WebResource aFullResource,
                                         @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    ValueEnforcer.notNull (aFullResource, "FullResource");
    ValueEnforcer.notNull (aCredentials, "Credentials");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_deleteServiceGroup from " + aFullResource.getURI ());

    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION, aCredentials.getRequestValue ());
      aBuilderWithAuth.delete ();
    }
    catch (final UniformInterfaceException ex) {
      throw getConvertedException (ex);
    }
  }

  /**
   * Deletes a service group given by its service group id.
   *
   * @param aServiceGroupID
   *        The service group id of the service group to delete.
   * @param aCredentials
   *        The user name and password to use as aCredentials.
   * @throws Exception
   *         in case something goes wrong
   * @throws NotFoundException
   *         The service group id did not exist.
   * @throws UnauthorizedException
   *         The user name or password was not correct.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public void deleteServiceGroup (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                  @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    ValueEnforcer.notNull (aServiceGroupID, "ServiceGroupID");
    ValueEnforcer.notNull (aCredentials, "Credentials");

    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID));
    deleteServiceGroup (aFullResource, aCredentials);
  }

  public static void saveServiceRegistration (@Nonnull final WebResource aFullResource,
                                              @Nonnull final ServiceMetadataType aServiceMetadata,
                                              @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    ValueEnforcer.notNull (aFullResource, "FullResource");
    ValueEnforcer.notNull (aServiceMetadata, "ServiceMetadata");
    ValueEnforcer.notNull (aCredentials, "Credentials");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_saveServiceRegistration from " + aFullResource.getURI ());

    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION, aCredentials.getRequestValue ());

      // Create JAXBElement!
      aBuilderWithAuth.type (MediaType.TEXT_XML).put (s_aObjFactory.createServiceMetadata (aServiceMetadata));
    }
    catch (final UniformInterfaceException e) {
      throw getConvertedException (e);
    }
  }

  /**
   * Saves a service meta data object. The ServiceGroupReference value is
   * ignored.
   *
   * @param aServiceMetadata
   *        The service meta data object to save.
   * @param aCredentials
   *        The user name and password to use as aCredentials.
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         The user name or password was not correct.
   * @throws NotFoundException
   *         A HTTP Not Found was received. This can happen if the service was
   *         not found.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public void saveServiceRegistration (@Nonnull final ServiceMetadataType aServiceMetadata,
                                       @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    ValueEnforcer.notNull (aServiceMetadata, "ServiceMetadata");
    ValueEnforcer.notNull (aCredentials, "Credentials");

    final ServiceInformationType aServiceInformation = aServiceMetadata.getServiceInformation ();
    if (aServiceInformation == null)
      throw new IllegalArgumentException ("ServiceMetadata does not contain serviceInformation");
    final IReadonlyParticipantIdentifier aServiceGroupID = aServiceInformation.getParticipantIdentifier ();
    if (aServiceGroupID == null)
      throw new IllegalArgumentException ("ServiceInformation does not contain serviceGroupID");
    final IReadonlyDocumentTypeIdentifier aDocumentTypeID = aServiceInformation.getDocumentIdentifier ();
    if (aDocumentTypeID == null)
      throw new IllegalArgumentException ("ServiceInformation does not contain documentTypeID");

    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID) +
                                                           "/services/" +
                                                           IdentifierUtils.getIdentifierURIPercentEncoded (aDocumentTypeID));
    saveServiceRegistration (aFullResource, aServiceMetadata, aCredentials);
  }

  public static void deleteServiceRegistration (@Nonnull final WebResource aFullResource,
                                                @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    ValueEnforcer.notNull (aFullResource, "FullResource");
    ValueEnforcer.notNull (aCredentials, "Credentials");

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("_deleteServiceRegistration from " + aFullResource.getURI ());

    try {
      final Builder aBuilderWithAuth = aFullResource.header (HttpHeaders.AUTHORIZATION, aCredentials.getRequestValue ());
      aBuilderWithAuth.delete ();
    }
    catch (final UniformInterfaceException e) {
      throw getConvertedException (e);
    }
  }

  /**
   * Deletes a service meta data object given by its service group id and its
   * document type.
   *
   * @param aServiceGroupID
   *        The service group id of the service meta data to delete.
   * @param aDocumentTypeID
   *        The document type of the service meta data to delete.
   * @param aCredentials
   *        The user name and password to use as aCredentials.
   * @throws Exception
   *         in case something goes wrong
   * @throws UnauthorizedException
   *         The user name or password was not correct.
   * @throws NotFoundException
   *         The service meta data object did not exist.
   * @throws UnknownException
   *         An unknown HTTP exception was received.
   * @throws BadRequestException
   *         The request was not well formed.
   */
  public void deleteServiceRegistration (@Nonnull final IReadonlyParticipantIdentifier aServiceGroupID,
                                         @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                         @Nonnull final BasicAuthClientCredentials aCredentials) throws Exception {
    ValueEnforcer.notNull (aServiceGroupID, "ServiceGroupID");
    ValueEnforcer.notNull (aDocumentTypeID, "DocumentTypeID");
    ValueEnforcer.notNull (aCredentials, "Credentials");

    final WebResource aFullResource = m_aWebResource.path (IdentifierUtils.getIdentifierURIPercentEncoded (aServiceGroupID) +
                                                           "/services/" +
                                                           IdentifierUtils.getIdentifierURIPercentEncoded (aDocumentTypeID));
    deleteServiceRegistration (aFullResource, aCredentials);
  }
}
