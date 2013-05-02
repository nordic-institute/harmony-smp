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
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
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
import com.phloc.db.jpa.IEntityManagerProvider;
import com.phloc.db.jpa.JPAEnabledManager;
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
public final class DBMSDataManager extends JPAEnabledManager implements IDataManager {
  private static final Logger s_aLogger = LoggerFactory.getLogger (DBMSDataManager.class);

  private final IRegistrationHook m_aHook;
  private final ObjectFactory m_aObjFactory = new ObjectFactory ();

  public DBMSDataManager () {
    this (RegistrationHookFactory.getInstance ());
  }

  public DBMSDataManager (@Nonnull final IRegistrationHook aHook) {
    super (new IEntityManagerProvider () {
      // This additional indirection level is required!!!
      // So that for every request the correct getInstance is invoked!
      @Nonnull
      public EntityManager getEntityManager () {
        return SMPEntityManagerWrapper.getInstance ().getEntityManager ();
      }
    });
    if (aHook == null)
      throw new NullPointerException ("hook");
    m_aHook = aHook;
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

  @Nonnull
  private DBOwnership _verifyOwnership (@Nonnull final BasicAuthClientCredentials aCredentials,
                                        @Nonnull final ParticipantIdentifierType aBusinessID) {
    final DBOwnershipID aOwnershipID = new DBOwnershipID (aCredentials.getUserName (), aBusinessID);
    final DBOwnership aOwnership = getEntityManager ().find (DBOwnership.class, aOwnershipID);
    if (aOwnership == null) {
      final String sErrorMsg = "User: " +
                               aCredentials.getUserName () +
                               " does not own " +
                               aBusinessID.getScheme () +
                               "::" +
                               aBusinessID.getValue ();
      s_aLogger.warn (sErrorMsg);
      throw new UnauthorizedException (sErrorMsg);
    }
    return aOwnership;
  }

  public Collection <ParticipantIdentifierType> getServiceGroupList (final BasicAuthClientCredentials aCredentials) {
    return doSelect (new Callable <Collection <ParticipantIdentifierType>> () {
      public Collection <ParticipantIdentifierType> call () throws Exception {
        final EntityManager aEM = getEntityManager ();
        final DBUser aDBUser = _verifyUser (aCredentials);
        final List <DBOwnership> aDBOwnerships = aEM.createQuery ("SELECT p FROM DBOwnership p WHERE p.user = :user",
                                                                  DBOwnership.class)
                                                    .setParameter ("user", aDBUser)
                                                    .getResultList ();
        final Collection <ParticipantIdentifierType> ret = new ArrayList <ParticipantIdentifierType> ();
        for (final DBOwnership aDBOwnership : aDBOwnerships) {
          final DBServiceGroupID aDBServiceGroupID = aDBOwnership.getServiceGroup ().getId ();
          ret.add (aDBServiceGroupID.asBusinessIdentifier ());
        }
        return ret;
      }
    }).get ();
  }

  @Nullable
  public ServiceGroupType getServiceGroup (final ParticipantIdentifierType aServiceGroupID) {
    return doInTransaction (new Callable <ServiceGroupType> () {
      public ServiceGroupType call () throws Exception {
        final EntityManager aEM = getEntityManager ();
        final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID (aServiceGroupID);
        final DBServiceGroup aDBServiceGroup = aEM.find (DBServiceGroup.class, aDBServiceGroupID);
        if (aDBServiceGroup == null) {
          s_aLogger.warn ("No such service group to retrieve: " + aDBServiceGroupID.toString ());
          return null;
        }

        // Convert service group DB to service group service
        final ServiceGroupType ret = m_aObjFactory.createServiceGroupType ();
        ret.setParticipantIdentifier (aServiceGroupID);
        ret.setExtension (ExtensionConverter.convert (aDBServiceGroup.getExtension ()));
        // This is set by the REST interface:
        // ret.setServiceMetadataReferenceCollection(value)
        return ret;
      }
    }).get ();
  }

  public void saveServiceGroup (final ServiceGroupType aServiceGroup, final BasicAuthClientCredentials aCredentials) {
    doInTransaction (new Runnable () {
      public void run () {
        final EntityManager aEM = getEntityManager ();
        final DBUser aDBUser = _verifyUser (aCredentials);

        final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID (aServiceGroup.getParticipantIdentifier ());
        DBServiceGroup aDBServiceGroup = aEM.find (DBServiceGroup.class, aDBServiceGroupID);
        final DBOwnershipID aDBOwnershipID = new DBOwnershipID (aCredentials.getUserName (),
                                                                aServiceGroup.getParticipantIdentifier ());

        // Check whether the business already exists
        if (aDBServiceGroup != null) {
          // The business did exist. So it must be owned by user.
          if (aEM.find (DBOwnership.class, aDBOwnershipID) == null) {
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

          final String sXmlString = ExtensionConverter.convert (aServiceGroup.getExtension ());
          aDBServiceGroup.setExtension (sXmlString);

          final DBOwnership aDBOwnership = new DBOwnership (aDBOwnershipID, aDBUser, aDBServiceGroup);
          aEM.persist (aDBServiceGroup);
          aEM.persist (aDBOwnership);
        }
      }
    });
  }

  public void deleteServiceGroup (final ParticipantIdentifierType aBusinessID,
                                  final BasicAuthClientCredentials aCredentials) {
    doInTransaction (new Runnable () {
      public void run () {
        final EntityManager aEM = getEntityManager ();
        _verifyUser (aCredentials);
        final DBOwnership aDBOwnership = _verifyOwnership (aCredentials, aBusinessID);

        m_aHook.delete (aBusinessID);

        // Check if the service group is existing
        final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID (aBusinessID);
        final DBServiceGroup aDBServiceGroup = aEM.find (DBServiceGroup.class, aDBServiceGroupID);
        if (aDBServiceGroup == null) {
          s_aLogger.warn ("No such service group to delete: " + aBusinessID.toString ());
          return;
        }

        aEM.remove (aDBOwnership);
        aEM.remove (aDBServiceGroup);
      }
    });
  }

  public List <DocumentIdentifierType> getDocumentTypes (final ParticipantIdentifierType aServiceGroupID) {
    return doSelect (new Callable <List <DocumentIdentifierType>> () {
      public List <DocumentIdentifierType> call () throws Exception {
        final List <DBServiceMetadata> aServices = getEntityManager ().createQuery ("SELECT p FROM DBServiceMetadata p WHERE p.id.businessIdentifierScheme = :scheme AND p.id.businessIdentifier = :value",
                                                                                    DBServiceMetadata.class)
                                                                      .setParameter ("scheme",
                                                                                     aServiceGroupID.getScheme ())
                                                                      .setParameter ("value",
                                                                                     aServiceGroupID.getValue ())
                                                                      .getResultList ();

        final List <DocumentIdentifierType> ret = new ArrayList <DocumentIdentifierType> ();
        for (final DBServiceMetadata aService : aServices)
          ret.add (aService.getId ().asDocumentTypeIdentifier ());
        return ret;
      }
    }).get ();
  }

  @Nullable
  public ServiceMetadataType getService (final ParticipantIdentifierType serviceGroupId,
                                         final DocumentIdentifierType docType) {
    return doSelect (new Callable <ServiceMetadataType> () {

      public ServiceMetadataType call () throws Exception {
        final DBServiceMetadataID id = new DBServiceMetadataID (serviceGroupId, docType);
        final DBServiceMetadata aDBServiceMetadata = getEntityManager ().find (DBServiceMetadata.class, id);

        if (aDBServiceMetadata == null) {
          s_aLogger.info ("Service group ID " + id.toString () + " not found");
          return null;
        }

        final ServiceMetadataType serviceMetadata = m_aObjFactory.createServiceMetadataType ();
        _convertFromDBToService (aDBServiceMetadata, serviceMetadata);
        return serviceMetadata;
      }
    }).get ();
  }

  public Collection <ServiceMetadataType> getServices (final ParticipantIdentifierType aServiceGroupID) {
    return doSelect (new Callable <Collection <ServiceMetadataType>> () {
      public Collection <ServiceMetadataType> call () throws Exception {
        final List <DBServiceMetadata> aServices = getEntityManager ().createQuery ("SELECT p FROM DBServiceMetadata p WHERE p.id.businessIdentifierScheme = :scheme AND p.id.businessIdentifier = :value",
                                                                                    DBServiceMetadata.class)
                                                                      .setParameter ("scheme",
                                                                                     aServiceGroupID.getScheme ())
                                                                      .setParameter ("value",
                                                                                     aServiceGroupID.getValue ())
                                                                      .getResultList ();

        final List <ServiceMetadataType> ret = new ArrayList <ServiceMetadataType> ();
        for (final DBServiceMetadata aService : aServices) {
          final ServiceMetadataType aServiceMetadata = m_aObjFactory.createServiceMetadataType ();
          _convertFromDBToService (aService, aServiceMetadata);
          ret.add (aServiceMetadata);
        }
        return ret;
      }
    }).get ();
  }

  public void saveService (@Nonnull final ServiceMetadataType aServiceMetadata,
                           @Nonnull final BasicAuthClientCredentials aCredentials) {
    final ParticipantIdentifierType aBusinessID = aServiceMetadata.getServiceInformation ().getParticipantIdentifier ();
    final DocumentIdentifierType aDocTypeID = aServiceMetadata.getServiceInformation ().getDocumentIdentifier ();
    deleteService (aBusinessID, aDocTypeID, aCredentials);
    final DBServiceMetadataID aDBServiceMetadataID = new DBServiceMetadataID (aBusinessID, aDocTypeID);

    // Delete any existing service in a separate transaction
    deleteService (aBusinessID, aDocTypeID, aCredentials);

    // Create a new entry
    doInTransaction (new Runnable () {
      public void run () {
        final EntityManager aEM = getEntityManager ();
        _verifyUser (aCredentials);
        _verifyOwnership (aCredentials, aBusinessID);

        // Check if an existing service is already contained
        // This should have been deleted previously!
        DBServiceMetadata aDBServiceMetadata = aEM.find (DBServiceMetadata.class, aDBServiceMetadataID);
        if (aDBServiceMetadata != null)
          throw new IllegalStateException ("No DB ServiceMeta data with ID " +
                                           aDBServiceMetadataID.toString () +
                                           " should be present!");

        // Create a new entry
        aDBServiceMetadata = new DBServiceMetadata ();
        aDBServiceMetadata.setId (aDBServiceMetadataID);

        _convertFromServiceToDB (aServiceMetadata, aDBServiceMetadata);
        aEM.persist (aDBServiceMetadata);

        // For all processes
        for (final DBProcess aDBProcess : aDBServiceMetadata.getProcesses ()) {
          aEM.persist (aDBProcess);

          // For all endpoints
          for (final DBEndpoint aDBEndpoint : aDBProcess.getEndpoints ())
            aEM.persist (aDBEndpoint);
        }
      }
    });
  }

  public void deleteService (@Nonnull final ParticipantIdentifierType aBusinessID,
                             @Nonnull final DocumentIdentifierType aDocTypeID,
                             @Nonnull final BasicAuthClientCredentials aCredentials) {
    doInTransaction (new Runnable () {
      public void run () {
        final EntityManager aEM = getEntityManager ();
        _verifyUser (aCredentials);
        _verifyOwnership (aCredentials, aBusinessID);

        final DBServiceMetadataID aDBServiceMetadataID = new DBServiceMetadataID (aBusinessID, aDocTypeID);
        final DBServiceMetadata aDBServiceMetadata = aEM.find (DBServiceMetadata.class, aDBServiceMetadataID);
        if (aDBServiceMetadata == null) {
          // There were no service to delete.
          s_aLogger.warn ("No such service to delete: " + aBusinessID.toString ());
          throw new NotFoundException ("");
        }

        // Remove all attached processes incl. their endpoints
        for (final DBProcess aDBProcess : aDBServiceMetadata.getProcesses ()) {
          // First endpoints
          for (final DBEndpoint aDBEndpoint : aDBProcess.getEndpoints ())
            aEM.remove (aDBEndpoint);

          // Than process
          aEM.remove (aDBProcess);
        }

        // Remove main service data
        aEM.remove (aDBServiceMetadata);
      }
    });
  }

  public ServiceMetadataType getRedirection (final ParticipantIdentifierType aServiceGroupId,
                                             final DocumentIdentifierType docType) {
    return doSelect (new Callable <ServiceMetadataType> () {
      public ServiceMetadataType call () throws Exception {
        final DBServiceMetadataRedirectionID id = new DBServiceMetadataRedirectionID (aServiceGroupId, docType);
        final DBServiceMetadataRedirection aDBServiceMetadataRedirection = getEntityManager ().find (DBServiceMetadataRedirection.class,
                                                                                                     id);

        if (aDBServiceMetadataRedirection == null) {
          if (GlobalDebug.isDebugMode ())
            s_aLogger.info ("No redirection service group id: " + aServiceGroupId.toString ());
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

        return serviceMetadata;
      }
    }).get ();
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
