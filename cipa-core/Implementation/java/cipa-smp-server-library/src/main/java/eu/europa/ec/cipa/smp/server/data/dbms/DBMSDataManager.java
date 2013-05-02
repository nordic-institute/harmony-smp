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
package eu.europa.ec.cipa.smp.server.data.dbms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ExtensionType;
import org.busdox.servicemetadata.publishing._1.ObjectFactory;
import org.busdox.servicemetadata.publishing._1.ProcessListType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.RedirectType;
import org.busdox.servicemetadata.publishing._1.ServiceEndpointList;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceInformationType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.GlobalDebug;
import com.phloc.web.http.basicauth.BasicAuthClientCredentials;
import com.sun.jersey.api.NotFoundException;

import eu.europa.ec.cipa.peppol.utils.ExtensionConverter;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBEndpoint;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBEndpointID;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBOwnership;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBOwnershipID;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBProcess;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBProcessID;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBServiceGroup;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBServiceGroupID;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBServiceMetadata;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBServiceMetadataID;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBServiceMetadataRedirection;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBServiceMetadataRedirectionID;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBUser;
import eu.europa.ec.cipa.smp.server.exception.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.exception.UnknownUserException;
import eu.europa.ec.cipa.smp.server.hook.IRegistrationHook;
import eu.europa.ec.cipa.smp.server.hook.RegistrationHookFactory;

/**
 * A Hibernate implementation of the DataManager interface.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class DBMSDataManager implements IDataManager {
  private static final Logger s_aLogger = LoggerFactory.getLogger (DBMSDataManager.class);

  private final IRegistrationHook m_aHook;
  private final ObjectFactory m_aObjFactory = new ObjectFactory ();

  public DBMSDataManager () {
    this (RegistrationHookFactory.getInstance ());
  }

  public DBMSDataManager (@Nonnull final IRegistrationHook aHook) {
    if (aHook == null)
      throw new NullPointerException ("hook");
    m_aHook = aHook;
  }

  @Nonnull
  private EntityManager getEntityManager () {
    return SMPEntityManagerWrapper.getInstance ().getEntityManager ();
  }

  @Nonnull
  private DBUser _verifyUser (@Nonnull final BasicAuthClientCredentials aCredentials) {
    final String sUsername = aCredentials.getUserName ();
    final DBUser aDBUser = getEntityManager ().find (DBUser.class, sUsername);

    // Check that the user exists
    if (aDBUser == null) {
      s_aLogger.warn ("No such user '" + sUsername + "'");
      throw new UnknownUserException (sUsername);
    }

    // Check that the password is correct
    if (!aDBUser.getPassword ().equals (aCredentials.getPassword ())) {
      s_aLogger.warn ("Illegal password for user '" + aDBUser.getUsername () + "'");
      throw new UnauthorizedException ("Illegal password for user '" + aDBUser.getUsername () + "'");
    }
    return aDBUser;
  }

  public Collection <ParticipantIdentifierType> getServiceGroupList (final BasicAuthClientCredentials aCredentials) {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      final DBUser aDBUser = _verifyUser (aCredentials);

      final List <DBOwnership> aDBOwnerships = getEntityManager ().createQuery ("SELECT p FROM DBOwnership p WHERE p.user = :user",
                                                                                DBOwnership.class)
                                                                  .setParameter ("user", aDBUser)
                                                                  .getResultList ();

      final Collection <ParticipantIdentifierType> ret = new ArrayList <ParticipantIdentifierType> ();
      for (final DBOwnership aDBOwnership : aDBOwnerships) {
        final DBServiceGroupID aDBServiceGroupID = aDBOwnership.getServiceGroup ().getId ();
        ret.add (aDBServiceGroupID.asBusinessIdentifier ());
      }

      aTransaction.commit ();
      return ret;
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in getServiceGroupList!");
      }
    }
  }

  @Nullable
  public ServiceGroupType getServiceGroup (final ParticipantIdentifierType aServiceGroupID) {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID (aServiceGroupID);
      final DBServiceGroup aDBServiceGroup = getEntityManager ().find (DBServiceGroup.class, aDBServiceGroupID);
      if (aDBServiceGroup == null) {
        s_aLogger.warn ("No such service group to retrieve: " + aDBServiceGroupID.toString ());
        aTransaction.rollback ();
        return null;
      }

      // Convert service group DB to service group service
      final ServiceGroupType ret = m_aObjFactory.createServiceGroupType ();
      ret.setParticipantIdentifier (aServiceGroupID);
      ret.setExtension (ExtensionConverter.convert (aDBServiceGroup.getExtension ()));
      // This is set by the REST interface:
      // ret.setServiceMetadataReferenceCollection(value)

      aTransaction.commit ();
      return ret;
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in getServiceGroup!");
      }
    }
  }

  public void saveServiceGroup (final ServiceGroupType aServiceGroup, final BasicAuthClientCredentials aCredentials) {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      final DBUser aDBUser = _verifyUser (aCredentials);

      final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID (aServiceGroup.getParticipantIdentifier ());
      DBServiceGroup aDBServiceGroup = getEntityManager ().find (DBServiceGroup.class, aDBServiceGroupID);
      final DBOwnershipID aDBOwnershipID = new DBOwnershipID (aCredentials.getUserName (),
                                                              aServiceGroup.getParticipantIdentifier ());

      // Check whether the business already exists
      if (aDBServiceGroup != null) {
        // The business did exist. So it must be owned by user.
        if (getEntityManager ().find (DBOwnership.class, aDBOwnershipID) == null) {
          s_aLogger.warn ("No such ownership: " + aDBOwnershipID.asBusinessIdentifier ().toString ());
          throw new UnauthorizedException ();
        }

        // Simply update the extension
        final String sXMLString = ExtensionConverter.convert (aServiceGroup.getExtension ());
        aDBServiceGroup.setExtension (sXMLString);
      }
      else {
        m_aHook.create (aServiceGroup.getParticipantIdentifier ());

        // Did not exist. Create it.
        aDBServiceGroup = new DBServiceGroup (aDBServiceGroupID);

        final String xmlString = ExtensionConverter.convert (aServiceGroup.getExtension ());
        aDBServiceGroup.setExtension (xmlString);

        final DBOwnership aDBOwnership = new DBOwnership (aDBOwnershipID, aDBUser, aDBServiceGroup);
        getEntityManager ().persist (aDBServiceGroup);
        getEntityManager ().persist (aDBOwnership);
      }

      aTransaction.commit ();
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in saveServiceGroup!");
      }
    }
  }

  public void deleteServiceGroup (final ParticipantIdentifierType aPI, final BasicAuthClientCredentials aCredentials) {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      _verifyUser (aCredentials);

      m_aHook.delete (aPI);

      // Check if the service group is existing
      final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID (aPI);
      final DBServiceGroup aDBServiceGroup = getEntityManager ().find (DBServiceGroup.class, aDBServiceGroupID);
      if (aDBServiceGroup == null) {
        s_aLogger.warn ("No such service group to delete: " + aPI.toString ());
        return;
      }

      // Check the owner ship of the service group
      final DBOwnershipID aDBOwnershipID = new DBOwnershipID (aCredentials.getUserName (), aPI);
      final DBOwnership aDBOwnership = getEntityManager ().find (DBOwnership.class, aDBOwnershipID);
      if (aDBOwnership == null) {
        s_aLogger.warn ("User: " + aCredentials.getUserName () + " does not own " + aPI);
        throw new UnauthorizedException ("User: " +
                                         aCredentials.getUserName () +
                                         " does not own " +
                                         aPI.getScheme () +
                                         "::" +
                                         aPI.getValue ());
      }

      getEntityManager ().remove (aDBOwnership);
      getEntityManager ().remove (aDBServiceGroup);

      aTransaction.commit ();
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in deleteServiceGroup!");
      }
    }
  }

  public List <DocumentIdentifierType> getDocumentTypes (final ParticipantIdentifierType aServiceGroupID) {
    final List <DocumentIdentifierType> ret = new ArrayList <DocumentIdentifierType> ();

    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      final List <DBServiceMetadata> aServices = getEntityManager ().createQuery ("SELECT p FROM DBServiceMetadata p WHERE p.id.businessIdentifierScheme = :scheme AND p.id.businessIdentifier = :value",
                                                                                  DBServiceMetadata.class)
                                                                    .setParameter ("scheme",
                                                                                   aServiceGroupID.getScheme ())
                                                                    .setParameter ("value", aServiceGroupID.getValue ())
                                                                    .getResultList ();

      for (final DBServiceMetadata aService : aServices)
        ret.add (aService.getId ().asDocumentTypeIdentifier ());

      aTransaction.commit ();
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in getDocumentTypes!");
      }
    }

    return ret;
  }

  @Nullable
  public ServiceMetadataType getService (final ParticipantIdentifierType serviceGroupId,
                                         final DocumentIdentifierType docType) {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      final DBServiceMetadataID id = new DBServiceMetadataID (serviceGroupId, docType);
      final DBServiceMetadata aDBServiceMetadata = getEntityManager ().find (DBServiceMetadata.class, id);

      if (aDBServiceMetadata == null) {
        s_aLogger.info ("Service group ID " + id.toString () + " not found");
        aTransaction.rollback ();
        return null;
      }

      final ServiceMetadataType serviceMetadata = m_aObjFactory.createServiceMetadataType ();
      _convertFromDBToService (aDBServiceMetadata, serviceMetadata);
      aTransaction.commit ();

      return serviceMetadata;
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in getService!");
      }
    }
  }

  public Collection <ServiceMetadataType> getServices (final ParticipantIdentifierType aServiceGroupID) {
    final List <ServiceMetadataType> ret = new ArrayList <ServiceMetadataType> ();

    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      final List <DBServiceMetadata> aServices = getEntityManager ().createQuery ("SELECT p FROM DBServiceMetadata p WHERE p.id.businessIdentifierScheme = :scheme AND p.id.businessIdentifier = :value",
                                                                                  DBServiceMetadata.class)
                                                                    .setParameter ("scheme",
                                                                                   aServiceGroupID.getScheme ())
                                                                    .setParameter ("value", aServiceGroupID.getValue ())
                                                                    .getResultList ();

      for (final DBServiceMetadata aService : aServices) {
        final ServiceMetadataType aServiceMetadata = m_aObjFactory.createServiceMetadataType ();
        _convertFromDBToService (aService, aServiceMetadata);
        ret.add (aServiceMetadata);
      }

      aTransaction.commit ();
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in getServices!");
      }
    }

    return ret;
  }

  public void saveService (final ServiceMetadataType aServiceMetadata, final BasicAuthClientCredentials aCredentials) {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      _verifyUser (aCredentials);

      final ParticipantIdentifierType aBusinessID = aServiceMetadata.getServiceInformation ()
                                                                    .getParticipantIdentifier ();

      // Check that the business is owned by the user
      final DBOwnershipID aDBwnershipID = new DBOwnershipID (aCredentials.getUserName (), aBusinessID);
      if (getEntityManager ().find (DBOwnership.class, aDBwnershipID) == null)
        throw new UnauthorizedException ();

      // Check if an existing service is already contained
      final DBServiceMetadataID aDBServiceMetadataID = new DBServiceMetadataID (aBusinessID,
                                                                                aServiceMetadata.getServiceInformation ()
                                                                                                .getDocumentIdentifier ());
      DBServiceMetadata aDBServiceMetadata = getEntityManager ().find (DBServiceMetadata.class, aDBServiceMetadataID);
      // Check whether the service already exists
      if (aDBServiceMetadata != null) {
        // Remove all existing info
        for (final DBProcess aDBProcess : aDBServiceMetadata.getProcesses ()) {
          for (final DBEndpoint aDBEndpoint : aDBProcess.getEndpoints ())
            getEntityManager ().remove (aDBEndpoint);
          getEntityManager ().remove (aDBProcess);
        }
        getEntityManager ().remove (aDBServiceMetadata);

        // Commit this, in case the same information is written again
        aTransaction.commit ();
        aTransaction.begin ();
      }
      else {
        // Create a new entry
        aDBServiceMetadata = new DBServiceMetadata ();
        aDBServiceMetadata.setId (aDBServiceMetadataID);
      }

      _convertFromServiceToDB (aServiceMetadata, aDBServiceMetadata);
      getEntityManager ().persist (aDBServiceMetadata);

      // For all processes
      for (final DBProcess aDBProcess : aDBServiceMetadata.getProcesses ()) {
        getEntityManager ().persist (aDBProcess);

        // For all endpoints
        for (final DBEndpoint aDBEndpoint : aDBProcess.getEndpoints ())
          getEntityManager ().persist (aDBEndpoint);
      }

      aTransaction.commit ();
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in saveService!");
      }
    }
  }

  public void deleteService (final ParticipantIdentifierType aServiceGroupID,
                             final DocumentIdentifierType aDocTypeID,
                             final BasicAuthClientCredentials aCredentials) {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      _verifyUser (aCredentials);

      // Check that the business is owned by the user
      final DBOwnershipID aOwnershipID = new DBOwnershipID (aCredentials.getUserName (), aServiceGroupID);
      if (getEntityManager ().find (DBOwnership.class, aOwnershipID) == null)
        throw new UnauthorizedException ("User: " +
                                         aCredentials.getUserName () +
                                         " does not own " +
                                         aServiceGroupID.getScheme () +
                                         "::" +
                                         aServiceGroupID.getValue ());

      final DBServiceMetadataID aDBServiceMetadataID = new DBServiceMetadataID (aServiceGroupID, aDocTypeID);
      final DBServiceMetadata aDBServiceMetadata = getEntityManager ().find (DBServiceMetadata.class,
                                                                             aDBServiceMetadataID);
      if (aDBServiceMetadata == null) {
        // There were no service to delete.
        s_aLogger.warn ("No such service to delete: " + aServiceGroupID.toString ());
        throw new NotFoundException ("");
      }

      // Remove all attached processes incl. their endpoints
      for (final DBProcess aDBProcess : aDBServiceMetadata.getProcesses ()) {
        // First endpoints
        for (final DBEndpoint aDBEndpoint : aDBProcess.getEndpoints ())
          getEntityManager ().remove (aDBEndpoint);

        // Than process
        getEntityManager ().remove (aDBProcess);
      }

      // Remove main service data
      getEntityManager ().remove (aDBServiceMetadata);

      aTransaction.commit ();
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in deleteService!");
      }
    }
  }

  public ServiceMetadataType getRedirection (final ParticipantIdentifierType aServiceGroupId,
                                             final DocumentIdentifierType docType) {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      final DBServiceMetadataRedirectionID id = new DBServiceMetadataRedirectionID (aServiceGroupId, docType);
      final DBServiceMetadataRedirection aDBServiceMetadataRedirection = getEntityManager ().find (DBServiceMetadataRedirection.class,
                                                                                                   id);

      if (aDBServiceMetadataRedirection == null) {
        if (GlobalDebug.isDebugMode ())
          s_aLogger.info ("No redirection service group id: " + aServiceGroupId.toString ());
        aTransaction.rollback ();
        return null;
      }

      // First check whether an redirect exists.
      final ServiceMetadataType serviceMetadata = m_aObjFactory.createServiceMetadataType ();

      // Then return a redirect instead.
      final RedirectType redirectType = m_aObjFactory.createRedirectType ();
      redirectType.setCertificateUID (aDBServiceMetadataRedirection.getCertificateUid ());
      redirectType.setHref (aDBServiceMetadataRedirection.getRedirectionUrl ());
      redirectType.setExtension (ExtensionConverter.convert (aDBServiceMetadataRedirection.getExtension ()));
      serviceMetadata.setRedirect (redirectType);

      aTransaction.commit ();

      return serviceMetadata;
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in getRedirection!");
      }
    }
  }

  private void _convertFromDBToService (final DBServiceMetadata serviceMetadataDB,
                                        final ServiceMetadataType serviceMetadata) {
    final ParticipantIdentifierType businessIdType = serviceMetadataDB.getId ().asBusinessIdentifier ();
    final ExtensionType extension = ExtensionConverter.convert (serviceMetadataDB.getExtension ());

    final DocumentIdentifierType documentIdentifier = serviceMetadataDB.getId ().asDocumentTypeIdentifier ();

    final ServiceInformationType serviceInformationType = m_aObjFactory.createServiceInformationType ();
    serviceInformationType.setParticipantIdentifier (businessIdType);
    // serviceInformationType.setCertificateUID(serviceMetadataDB.g));
    serviceInformationType.setExtension (extension);
    serviceInformationType.setDocumentIdentifier (documentIdentifier);

    serviceMetadata.setServiceInformation (serviceInformationType);

    final ProcessListType processListType = m_aObjFactory.createProcessListType ();
    for (final DBProcess aDBProcess : serviceMetadataDB.getProcesses ()) {
      final ProcessType aProcessType = m_aObjFactory.createProcessType ();

      final ServiceEndpointList endpoints = m_aObjFactory.createServiceEndpointList ();
      for (final DBEndpoint aDBEndpoint : aDBProcess.getEndpoints ()) {
        final EndpointType aEndpointType = m_aObjFactory.createEndpointType ();

        aEndpointType.setTransportProfile (aDBEndpoint.getId ().getTransportProfile ());
        aEndpointType.setExtension (ExtensionConverter.convert (aDBEndpoint.getExtension ()));

        final W3CEndpointReference endpointRef = W3CEndpointReferenceUtils.createEndpointReference (aDBEndpoint.getId ()
                                                                                                               .getEndpointReference ());
        aEndpointType.setEndpointReference (endpointRef);

        aEndpointType.setServiceActivationDate (aDBEndpoint.getServiceActivationDate ());
        aEndpointType.setServiceDescription (aDBEndpoint.getServiceDescription ());
        aEndpointType.setServiceExpirationDate (aDBEndpoint.getServiceExpirationDate ());
        aEndpointType.setTechnicalContactUrl (aDBEndpoint.getTechnicalContactUrl ());
        aEndpointType.setTechnicalInformationUrl (aDBEndpoint.getTechnicalInformationUrl ());
        aEndpointType.setCertificate (aDBEndpoint.getCertificate ());
        aEndpointType.setMinimumAuthenticationLevel (aDBEndpoint.getMinimumAuthenticationLevel ());
        aEndpointType.setRequireBusinessLevelSignature (aDBEndpoint.isRequireBusinessLevelSignature ());

        endpoints.getEndpoint ().add (aEndpointType);
      }

      aProcessType.setServiceEndpointList (endpoints);
      aProcessType.setExtension (ExtensionConverter.convert (aDBProcess.getExtension ()));
      aProcessType.setProcessIdentifier (aDBProcess.getId ().asProcessIdentifier ());

      processListType.getProcess ().add (aProcessType);
    }

    serviceInformationType.setProcessList (processListType);
  }

  private static void _convertFromServiceToDB (@Nonnull final ServiceMetadataType aServiceMetadata,
                                               @Nonnull final DBServiceMetadata aDBServiceMetadata) {
    // Update it.
    final ServiceInformationType aServiceInformation = aServiceMetadata.getServiceInformation ();
    aDBServiceMetadata.setExtension (ExtensionConverter.convert (aServiceInformation.getExtension ()));

    final Set <DBProcess> aDBProcesses = new HashSet <DBProcess> ();
    for (final ProcessType aProcess : aServiceInformation.getProcessList ().getProcess ()) {
      final DBProcessID aDBProcessID = new DBProcessID (aDBServiceMetadata.getId (), aProcess.getProcessIdentifier ());
      final DBProcess aDBProcess = new DBProcess (aDBProcessID);

      final Set <DBEndpoint> aDBEndpoints = new HashSet <DBEndpoint> ();
      for (final EndpointType aEndpoint : aProcess.getServiceEndpointList ().getEndpoint ()) {
        final DBEndpointID aDBEndpointID = new DBEndpointID (aDBProcessID,
                                                             W3CEndpointReferenceUtils.getAddress (aEndpoint.getEndpointReference ()),
                                                             aEndpoint.getTransportProfile ());

        final DBEndpoint aDBEndpoint = new DBEndpoint ();
        aDBEndpoint.setExtension (ExtensionConverter.convert (aEndpoint.getExtension ()));
        aDBEndpoint.setId (aDBEndpointID);
        aDBEndpoint.setServiceActivationDate (aEndpoint.getServiceActivationDate ());
        aDBEndpoint.setServiceDescription (aEndpoint.getServiceDescription ());
        aDBEndpoint.setServiceExpirationDate (aEndpoint.getServiceExpirationDate ());
        aDBEndpoint.setTechnicalContactUrl (aEndpoint.getTechnicalContactUrl ());
        aDBEndpoint.setTechnicalInformationUrl (aEndpoint.getTechnicalInformationUrl ());
        aDBEndpoint.setCertificate (aEndpoint.getCertificate ());
        aDBEndpoint.setMinimumAuthenticationLevel (aEndpoint.getMinimumAuthenticationLevel ());
        aDBEndpoint.setRequireBusinessLevelSignature (aEndpoint.isRequireBusinessLevelSignature ());

        aDBEndpoints.add (aDBEndpoint);
      }

      aDBProcess.setEndpoints (aDBEndpoints);
      aDBProcess.setExtension (ExtensionConverter.convert (aProcess.getExtension ()));

      aDBProcesses.add (aDBProcess);
    }

    aDBServiceMetadata.setProcesses (aDBProcesses);
  }
}
