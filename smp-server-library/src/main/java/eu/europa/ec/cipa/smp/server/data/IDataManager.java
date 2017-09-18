/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.data;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;

import eu.europa.ec.cipa.smp.server.data.dbms.model.DBServiceMetadata;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBServiceMetadataID;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBUser;

import eu.europa.ec.cipa.smp.server.errors.exceptions.UnknownUserException;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadata;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.web.http.basicauth.BasicAuthClientCredentials;

/**
 * This interface is used by the REST interface for accessing the underlying SMP
 * data. One should implement this interface if a new data source is needed.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public interface IDataManager {
  /**
   * Gets the service group ids owned by the given credentials.
   * 
   * @param username
   *        The credentials to get service groups id for.
   * @return A collection of service group id's.
   * @throws Throwable
   */
  @Nonnull
  @ReturnsMutableCopy
  Collection <ParticipantIdentifierType> getServiceGroupList (@Nonnull String username) throws Throwable;

  @Nonnull
  DBUser _verifyUser(@Nonnull String sUsername) throws UnknownUserException;

  /**
   * This method returns a ServiceGroup given its id.
   * 
   * @param aServiceGroupID
   *        The service group id.
   * @return The service group corresponding to the id.
   * @throws Throwable
   */
  @Nullable
  ServiceGroup getServiceGroup (@Nonnull ParticipantIdentifierType aServiceGroupID);

  /**
   * Persists the service group in the underlying data layer. This operation
   * requires credentials.
   * 
   * @param aServiceGroup
   *        The service group to save.
   * @param newOwnerName
   *        The credentials to use.
   * @return true, if ServiceGroup was added; false, if ServiceGroup was updated
   * @throws Throwable
   */
  boolean saveServiceGroup (@Nonnull ServiceGroup aServiceGroup, @Nonnull String newOwnerName);

  /**
   * Deletes the service group having the specified id.
   * 
   * @param aServiceGroupID
   *        The ID of the service group to delete.
   * @throws Throwable
   */
  void deleteServiceGroup(@Nonnull final ParticipantIdentifierType aServiceGroupID);

  /**
   * Gets a list of the document id's of the given service group.
   * 
   * @param aServiceGroupID
   *        The id of the service group.
   * @return The corresponding document id's.
   * @throws Throwable
   */
  @Nonnull
  @ReturnsMutableCopy
  List <DBServiceMetadataID> getDocumentTypes (@Nonnull ParticipantIdentifierType aServiceGroupID) throws Throwable;

  /**
   * Gets the list of service metadata objects corresponding to a given service
   * group id.
   * 
   * @param aServiceGroupID
   *        The service group id.
   * @return A list of service metadata objects.
   * @throws Throwable
   */
  @Nonnull
  @ReturnsMutableCopy
  Collection <ServiceMetadata> getServices (@Nonnull ParticipantIdentifierType aServiceGroupID) throws Throwable;

  /**
   * Gets the service metadata corresponding to the service group id and
   * document id.
   * 
   * @param aServiceGroupID
   *        The service group id of the service metadata.
   * @param aDocType
   *        The document id of the service metadata.
   * @return The corresponding service metadata.
   * @throws Throwable
   */
  @Nullable
  String getService (@Nonnull ParticipantIdentifierType aServiceGroupID,
                       @Nonnull DocumentIdentifier aDocType);

  /**
   * Saves the given service metadata in the underlying data layer.
   * 
   * @param aServiceGroupID
   *        The service group id to save.
   * @param aDocTypeID
   *        The document id to save.
   * @param sXmlContent
   *        The service metadata XML content to save.
   * @return true, if ServiceMetadata was added; false, if ServiceMetadata was updated
   * @throws Throwable
   *        A throwable is thrown if an error occurs.
   */
  boolean saveService(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                             @Nonnull final DocumentIdentifier aDocTypeID,
                             @Nonnull final String sXmlContent /*,
                             @Nonnull final String username*/);

  /**
   * Deletes a service metadata object given by its service group id and
   * document id.
   * 
   * @param aServiceGroupID
   *        The service group id of the service metadata.
   * @param aDocType
   *        The document id of the service metadata.
   * @throws Throwable
   */
  void deleteService (@Nonnull ParticipantIdentifierType aServiceGroupID,
                      @Nonnull DocumentIdentifier aDocType /*,
                      @Nonnull String username*/);

  /**
   * Checks whether the ServiceMetadata should be found elsewhere.
   * 
   * @param aServiceGroupID
   *        The service group id of the service metadata. May not be
   *        <code>null</code>.
   * @param aDocTypeID
   *        The document id of the service metadata. May not be
   *        <code>null</code>.
   * @return The URI to be redirected to. null if no redirection should take
   *         place.
   * @throws Throwable
   */
  @Nullable
  ServiceMetadata getRedirection (@Nonnull ParticipantIdentifierType aServiceGroupID,
                                      @Nonnull DocumentIdentifier aDocTypeID) throws Throwable;

  /**
   * Creates Entity Manager
   **/
  EntityManager getCurrentEntityManager();


  /**
   * Checks if user exists in the database and if password matches
   **/
  DBUser _verifyUser(@Nonnull final BasicAuthClientCredentials aCredentials) throws UnknownUserException;
}
