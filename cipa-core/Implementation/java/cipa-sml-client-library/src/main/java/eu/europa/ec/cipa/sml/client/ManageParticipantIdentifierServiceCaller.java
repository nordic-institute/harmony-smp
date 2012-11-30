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
package eu.europa.ec.cipa.sml.client;

import java.net.URL;
import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.xml.ws.BindingProvider;

import org.busdox.servicemetadata.locator._1.MigrationRecordType;
import org.busdox.servicemetadata.locator._1.PageRequestType;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceForParticipantType;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.BadRequestFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.InternalErrorFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.ManageBusinessIdentifierService;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.ManageBusinessIdentifierServiceSoap;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.NotFoundFault;
import org.busdox.servicemetadata.managebusinessidentifierservice._1.UnauthorizedFault;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;

/**
 * This class is used for calling the Manage Participant Identifier interface on
 * the SML.
 * 
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class ManageParticipantIdentifierServiceCaller {
  private static final Logger s_aLogger = LoggerFactory.getLogger (ManageParticipantIdentifierServiceCaller.class);
  private static final String NO_SMP_ID_REQUIRED = "";

  private final URL m_aEndpointAddress;

  /**
   * Constructs a service caller for the manage business identifier interface.<br>
   * Example of a host:<br>
   * https://sml.peppolcentral.org/managebusinessidentifier
   * 
   * @param aSMLInfo
   *        The SML info object
   */
  public ManageParticipantIdentifierServiceCaller (@Nonnull final ISMLInfo aSMLInfo) {
    this (aSMLInfo.getManageParticipantIdentifierEndpointAddress ());
  }

  /**
   * Constructs a service caller for the manage business identifier interface.<br>
   * Example of a host:<br>
   * https://sml.peppolcentral.org/managebusinessidentifier
   * 
   * @param aEndpointAddress
   *        The URL of the manage business identifier interface.
   */
  public ManageParticipantIdentifierServiceCaller (@Nonnull final URL aEndpointAddress) {
    if (aEndpointAddress == null)
      throw new NullPointerException ("endpointAddress");
    m_aEndpointAddress = aEndpointAddress;
  }

  /**
   * Create the main WebService client for the specified endpoint address.
   * 
   * @return The WebService port to be used.
   */
  @Nonnull
  private ManageBusinessIdentifierServiceSoap _createPort () {
    final ManageBusinessIdentifierService aService = new ManageBusinessIdentifierService ();
    final ManageBusinessIdentifierServiceSoap aPort = aService.getManageBusinessIdentifierServicePort ();
    final BindingProvider aPortBP = (BindingProvider) aPort;
    aPortBP.getRequestContext ().put (BindingProvider.ENDPOINT_ADDRESS_PROPERTY, m_aEndpointAddress.toString ());
    return aPort;
  }

  /**
   * Creates a new participant identifier for the SMP given by the ID.
   * 
   * @param sSMPID
   *        Identifies the SMP to create the identifier for. Example:
   *        test-smp-0000000001
   * @param aIdentifier
   *        The identifier to be created.
   * @throws BadRequestFault
   *         Is thrown if the request sent to the service was not well-formed.
   * @throws InternalErrorFault
   *         Is thrown if an internal error happened on the service.
   * @throws UnauthorizedFault
   *         Is thrown if the user was not authorized.
   * @throws NotFoundFault
   *         Is thrown if the service metadata publisher was not found.
   */
  public void create (final String sSMPID, final ParticipantIdentifierType aIdentifier) throws BadRequestFault,
                                                                                       InternalErrorFault,
                                                                                       UnauthorizedFault,
                                                                                       NotFoundFault {
    final ServiceMetadataPublisherServiceForParticipantType aSMPParticpantService = new ServiceMetadataPublisherServiceForParticipantType ();
    aSMPParticpantService.setServiceMetadataPublisherID (sSMPID);
    aSMPParticpantService.setParticipantIdentifier (aIdentifier);
    create (aSMPParticpantService);
  }

  /**
   * Creates a new business identifier for the SMP given by the publisher id in
   * ServiceMetadataPublisherServiceForParticipantType.
   * 
   * @param aSMPParticpantService
   *        Specifies the identifier to create.
   * @throws BadRequestFault
   *         Is thrown if the request sent to the service was not well-formed.
   * @throws InternalErrorFault
   *         Is thrown if an internal error happened on the service.
   * @throws UnauthorizedFault
   *         Is thrown if the user was not authorized.
   * @throws NotFoundFault
   *         Is thrown if the service metadata publisher was not found.
   */
  public void create (final ServiceMetadataPublisherServiceForParticipantType aSMPParticpantService) throws BadRequestFault,
                                                                                                    InternalErrorFault,
                                                                                                    UnauthorizedFault,
                                                                                                    NotFoundFault {
    s_aLogger.info ("Trying to create new participant " +
                    IdentifierUtils.getIdentifierURIEncoded (aSMPParticpantService.getParticipantIdentifier ()) +
                    " in SMP '" +
                    aSMPParticpantService.getServiceMetadataPublisherID () +
                    "'");
    _createPort ().create (aSMPParticpantService);
  }

  @Nonnull
  private static String _toString (@Nonnull final Collection <? extends ParticipantIdentifierType> aParticipantIdentifiers) {
    final StringBuilder aSB = new StringBuilder ();
    for (final ParticipantIdentifierType aPI : aParticipantIdentifiers) {
      if (aSB.length () > 0)
        aSB.append (", ");
      aSB.append (IdentifierUtils.getIdentifierURIEncoded (aPI));
    }
    return aSB.toString ();
  }

  /**
   * Creates a list of participant identifiers.
   * 
   * @param aParticipantIdentifiers
   *        The collection of identifiers to create
   * @param sSMPID
   *        The id of the service metadata.
   * @throws BadRequestFault
   *         Is thrown if the request sent to the service was not well-formed.
   * @throws InternalErrorFault
   *         Is thrown if an internal error happened on the service.
   * @throws NotFoundFault
   * @throws UnauthorizedFault
   *         Is thrown if the user was not authorized.
   */
  public void createList (final Collection <? extends ParticipantIdentifierType> aParticipantIdentifiers,
                          final String sSMPID) throws BadRequestFault,
                                              InternalErrorFault,
                                              NotFoundFault,
                                              UnauthorizedFault {
    s_aLogger.info ("Trying to create multiple new participants " +
                    _toString (aParticipantIdentifiers) +
                    " in SMP '" +
                    sSMPID +
                    "'");
    final ParticipantIdentifierPageType aParticipantList = new ParticipantIdentifierPageType ();
    aParticipantList.getParticipantIdentifier ().addAll (aParticipantIdentifiers);
    aParticipantList.setServiceMetadataPublisherID (sSMPID);
    _createPort ().createList (aParticipantList);
  }

  /**
   * Deletes a given participant identifier
   * 
   * @param aIdentifier
   *        The business identifier to delete
   * @throws BadRequestFault
   *         Is thrown if the request sent to the service was not well-formed.
   * @throws InternalErrorFault
   *         Is thrown if an internal error happened on the service.
   * @throws NotFoundFault
   *         Is thrown if the business identifier could not be found and
   *         therefore deleted.
   * @throws UnauthorizedFault
   *         Is thrown if the user was not authorized.
   */
  public void delete (final ParticipantIdentifierType aIdentifier) throws BadRequestFault,
                                                                  InternalErrorFault,
                                                                  NotFoundFault,
                                                                  UnauthorizedFault {
    final ServiceMetadataPublisherServiceForParticipantType aSMPParticpantService = new ServiceMetadataPublisherServiceForParticipantType ();
    // No SMP ID required here, since identifier scheme+value must be unique!
    aSMPParticpantService.setServiceMetadataPublisherID (NO_SMP_ID_REQUIRED);
    aSMPParticpantService.setParticipantIdentifier (aIdentifier);
    delete (aSMPParticpantService);
  }

  /**
   * Deletes a given participant identifier given by the
   * ServiceMetadataPublisherServiceForBusinessType parameter.
   * 
   * @param aSMPParticpantService
   *        The participant identifier to delete.
   * @throws BadRequestFault
   *         Is thrown if the request sent to the service was not well-formed.
   * @throws InternalErrorFault
   *         Is thrown if an internal error happened on the service.
   * @throws NotFoundFault
   *         Is thrown if the business identifier could not be found and
   *         therefore deleted.
   * @throws UnauthorizedFault
   *         Is thrown if the user was not authorized.
   */
  public void delete (final ServiceMetadataPublisherServiceForParticipantType aSMPParticpantService) throws BadRequestFault,
                                                                                                    InternalErrorFault,
                                                                                                    NotFoundFault,
                                                                                                    UnauthorizedFault {
    s_aLogger.info ("Trying to delete participant " +
                    IdentifierUtils.getIdentifierURIEncoded (aSMPParticpantService.getParticipantIdentifier ()));
    _createPort ().delete (aSMPParticpantService);
  }

  /**
   * Deletes a list of participant identifiers
   * 
   * @param aParticipantIdentifiers
   *        The list of participant identifiers
   * @throws BadRequestFault
   *         Is thrown if the request sent to the service was not well-formed.
   * @throws InternalErrorFault
   *         Is thrown if an internal error happened on the service.
   * @throws NotFoundFault
   *         Is thrown if a business identifier could not be found and therefore
   *         deleted.
   * @throws UnauthorizedFault
   *         Is thrown if the user was not authorized.
   */
  public void deleteList (final Collection <ParticipantIdentifierType> aParticipantIdentifiers) throws BadRequestFault,
                                                                                               InternalErrorFault,
                                                                                               NotFoundFault,
                                                                                               UnauthorizedFault {
    s_aLogger.info ("Trying to delete multiple participants " + _toString (aParticipantIdentifiers));
    final ParticipantIdentifierPageType deleteListIn = new ParticipantIdentifierPageType ();
    deleteListIn.getParticipantIdentifier ().addAll (aParticipantIdentifiers);
    _createPort ().deleteList (deleteListIn);
  }

  /**
   * Lists the participant identifiers registered for the SMP associated with
   * the publisher id. The method is paged, so the page id can be used to get
   * the next page.
   * 
   * @param sPageId
   *        The id of the next page. Empty string if it is the first page.
   * @param sSMPID
   *        The publisher id corresponding to the SMP.
   * @return A page of participant identifiers.
   * @throws BadRequestFault
   *         Is thrown if the request sent to the service was not well-formed.
   * @throws InternalErrorFault
   *         Is thrown if an internal error happened on the service.
   * @throws NotFoundFault
   *         Is thrown if certificateUid was not found.
   * @throws UnauthorizedFault
   *         Is thrown if the user was not authorized.
   */
  public ParticipantIdentifierPageType list (final String sPageId, final String sSMPID) throws BadRequestFault,
                                                                                       InternalErrorFault,
                                                                                       NotFoundFault,
                                                                                       UnauthorizedFault {
    final PageRequestType aPageRequest = new PageRequestType ();
    aPageRequest.setServiceMetadataPublisherID (sSMPID);
    aPageRequest.setNextPageIdentifier (sPageId);
    return list (aPageRequest);
  }

  /**
   * Lists the participant identifiers registered for the SMP associated with
   * the publisher id. The method is paged, so the page id can be used to get
   * the next page.
   * 
   * @param aPageRequest
   *        The page request
   * @return A page of business identifiers.
   * @throws BadRequestFault
   *         Is thrown if the request sent to the service was not well-formed.
   * @throws InternalErrorFault
   *         Is thrown if an internal error happened on the service.
   * @throws NotFoundFault
   *         Is thrown if certificateUid was not found.
   * @throws UnauthorizedFault
   *         Is thrown if the user was not authorized.
   */
  public ParticipantIdentifierPageType list (final PageRequestType aPageRequest) throws BadRequestFault,
                                                                                InternalErrorFault,
                                                                                NotFoundFault,
                                                                                UnauthorizedFault {
    s_aLogger.info ("Trying to list participants in SMP '" + aPageRequest.getServiceMetadataPublisherID () + "'");
    return _createPort ().list (aPageRequest);
  }

  /**
   * Prepares a migrate of the given participant identifier.
   * 
   * @param aIdentifier
   *        The participant identifier.
   * @return The UUID to transfer out-of-band to the other SMP.
   * @throws BadRequestFault
   *         Is thrown if the request sent to the service was not well-formed.
   * @throws InternalErrorFault
   *         Is thrown if an internal error happened on the service.
   * @throws NotFoundFault
   *         If the business identifier was not found.
   * @throws UnauthorizedFault
   *         Is thrown if the user was not authorized.
   */
  @Nonnull
  public UUID prepareToMigrate (@Nonnull final ParticipantIdentifierType aIdentifier, final String sSMPID) throws BadRequestFault,
                                                                                                          InternalErrorFault,
                                                                                                          NotFoundFault,
                                                                                                          UnauthorizedFault {
    s_aLogger.info ("Preparing to migrate participant " +
                    IdentifierUtils.getIdentifierURIEncoded (aIdentifier) +
                    " from SMP '" +
                    sSMPID +
                    "'");
    final UUID aUUID = UUID.randomUUID ();
    final MigrationRecordType aMigrationRecord = new MigrationRecordType ();
    aMigrationRecord.setParticipantIdentifier (aIdentifier);
    aMigrationRecord.setMigrationKey (aUUID.toString ());
    aMigrationRecord.setServiceMetadataPublisherID (sSMPID);

    _createPort ().prepareToMigrate (aMigrationRecord);
    return aUUID;
  }

  /**
   * Migrates a given participant identifier to an SMP given by the publisher
   * id.
   * 
   * @param aIdentifier
   *        The participant identifier to migrate.
   * @param aMigrationKey
   *        The migration key received by the previous owner.
   * @param sSMPID
   *        The publisher id corresponding to the new owner SMP.
   * @throws BadRequestFault
   *         Is thrown if the request sent to the service was not well-formed.
   * @throws InternalErrorFault
   *         Is thrown if an internal error happened on the service.
   * @throws NotFoundFault
   *         If the business identifier was not found.
   * @throws UnauthorizedFault
   *         Is thrown if the user was not authorized.
   */
  public void migrate (@Nonnull final ParticipantIdentifierType aIdentifier,
                       @Nonnull final UUID aMigrationKey,
                       final String sSMPID) throws BadRequestFault,
                                           InternalErrorFault,
                                           NotFoundFault,
                                           UnauthorizedFault {
    s_aLogger.info ("Finishing migration of participant " +
                    IdentifierUtils.getIdentifierURIEncoded (aIdentifier) +
                    " to SMP '" +
                    sSMPID +
                    "' using migration key '" +
                    aMigrationKey.toString () +
                    "'");
    final MigrationRecordType aMigrationRecord = new MigrationRecordType ();
    aMigrationRecord.setParticipantIdentifier (aIdentifier);
    aMigrationRecord.setMigrationKey (aMigrationKey.toString ());
    aMigrationRecord.setServiceMetadataPublisherID (sSMPID);

    _createPort ().migrate (aMigrationRecord);
  }
}
