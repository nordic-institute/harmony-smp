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
package eu.europa.ec.cipa.smp.server.data;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;

import eu.europa.ec.cipa.peppol.utils.IReadonlyUsernamePWCredentials;

/**
 * This interface is used by the REST interface for accessing the underlying SMP
 * data. One should implement this interface if a new data source is needed.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public interface IDataManager {
  /**
   * This method returns a ServiceGroup given its id.
   * 
   * @param id
   *        The service group id.
   * @return The service group corresponding to the id.
   */
  @Nullable
  ServiceGroupType getServiceGroup (ParticipantIdentifierType id);

  /**
   * Persists the service group in the underlying data layer. This operation
   * requires credentials.
   * 
   * @param serviceGroup
   *        The service group to save.
   * @param creds
   *        The credentials to use.
   */
  void saveServiceGroup (ServiceGroupType serviceGroup, IReadonlyUsernamePWCredentials creds);

  /**
   * Deletes the service group having the specified id.
   * 
   * @param serviceGroupId
   *        The ID of the service group to delete.
   * @param creds
   *        The credentials to use.
   */
  void deleteServiceGroup (ParticipantIdentifierType serviceGroupId, IReadonlyUsernamePWCredentials creds);

  /**
   * Gets a list of the document id's of the given service group.
   * 
   * @param serviceGroupId
   *        The id of the service group.
   * @return The corresponding document id's.
   */
  List <DocumentIdentifierType> getDocumentTypes (ParticipantIdentifierType serviceGroupId);

  /**
   * Gets the service metadata corresponding to the service group id and
   * document id.
   * 
   * @param serviceGroupId
   *        The service group id of the service metadata.
   * @param docType
   *        The document id of the service metadata.
   * @return The corresponding service metadata.
   */
  ServiceMetadataType getService (ParticipantIdentifierType serviceGroupId, DocumentIdentifierType docType);

  /**
   * Saves the given service metadata in the underlying data layer.
   * 
   * @param serviceMetadata
   *        The service metadata to save.
   * @param creds
   *        The credentials to use.
   */
  void saveService (ServiceMetadataType serviceMetadata, IReadonlyUsernamePWCredentials creds);

  /**
   * Deletes a service metadata object given by its service group id and
   * document id.
   * 
   * @param serviceGroupId
   *        The service group id of the service metadata.
   * @param docType
   *        The document id of the service metadata.
   * @param creds
   *        The credentials to use.
   */
  void deleteService (ParticipantIdentifierType serviceGroupId,
                      DocumentIdentifierType docType,
                      IReadonlyUsernamePWCredentials creds);

  /**
   * Checks whether the ServiceMetadata should be found elsewhere.
   * 
   * @param servGroupId
   *        The service group id of the service metadata.
   * @param docTypeId
   *        The document id of the service metadata.
   * @return The URI to be redirected to. null if no redirection should take
   *         place.
   */
  ServiceMetadataType getRedirection (ParticipantIdentifierType servGroupId, DocumentIdentifierType docTypeId);

  /**
   * Gets the service group ids owned by the given credentials.
   * 
   * @param creds
   *        The credentials to get service groups id for.
   * @return A collection of service group id's.
   */
  Collection <ParticipantIdentifierType> getServiceGroupList (IReadonlyUsernamePWCredentials creds);

  /**
   * Gets the list of service metadata objects corresponding to a given service
   * group id.
   * 
   * @param serviceGroupId
   *        The service group id.
   * @return A list of service metadata objects.
   */
  Collection <ServiceMetadataType> getServices (ParticipantIdentifierType serviceGroupId);
}
