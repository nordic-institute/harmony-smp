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
package eu.europa.ec.cipa.smp.server.data.dbms;

import com.helger.commons.GlobalDebug;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.state.EChange;
import com.helger.db.jpa.IEntityManagerProvider;
import com.helger.db.jpa.JPAEnabledManager;
import com.helger.db.jpa.JPAExecutionResult;
import com.helger.web.http.basicauth.BasicAuthClientCredentials;
import eu.europa.ec.cipa.smp.server.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.cipa.smp.server.conversion.ServiceMetadataConverter;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.data.dbms.model.*;
import eu.europa.ec.cipa.smp.server.errors.exceptions.NotFoundException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnknownUserException;
import eu.europa.ec.cipa.smp.server.hook.IRegistrationHook;
import eu.europa.ec.cipa.smp.server.util.ExtensionUtils;
import eu.europa.ec.cipa.smp.server.util.IdentifierUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * A Hibernate implementation of the DataManager interface.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Service
@Transactional
public /*final*/ class DBMSDataManager /*extends JPAEnabledManager*/ /*implements IDataManager*/ {
    private static final Logger s_aLogger = LoggerFactory.getLogger(DBMSDataManager.class);

    //@Autowired
    //EntityManager em;

    @Autowired
    private IRegistrationHook m_aHook;

    @Autowired
    private CaseSensitivityNormalizer caseSensitivityNormalizer;

    @PersistenceContext
    EntityManager entityManager;

    private final ObjectFactory m_aObjFactory = new ObjectFactory();

    public DBMSDataManager() {
        /*
        super(new IEntityManagerProvider() {
            // This additional indirection level is required!!!
            // So that for every request the correct getInstance is invoked!
            @Nonnull
            public EntityManager getEntityManager() {
                //return SMPEntityManagerWrapper.getInstance().getEntityManager();
                EntityManagerFactory emf = ContextLoader.getCurrentWebApplicationContext().getBean(EntityManagerFactory.class);
                return EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
            }
        });
        */
    }

    /*
    public DBMSDataManager() {
        this(RegistrationHookFactory.createInstance(), new CaseSensitivityNormalizer());
    }

    public DBMSDataManager(@Nonnull final IRegistrationHook aHook, CaseSensitivityNormalizer caseSensitivityNormalizer) {
        super(new IEntityManagerProvider() {
            // This additional indirection level is required!!!
            // So that for every request the correct getInstance is invoked!
            @Nonnull
            public EntityManager getEntityManager() {
                return SMPEntityManagerWrapper.getInstance().getEntityManager();
            }
        });

        ValueEnforcer.notNull(aHook, "Hook");

        // Exceptions are handled by logging them
        setCustomExceptionHandler(new LoggingExceptionHandler());

        // To avoid some EclipseLink logging issues
        setUseTransactionsForSelect(true);

        m_aHook = aHook;
        this.caseSensitivityNormalizer = caseSensitivityNormalizer;
    }
    */

    public void setCaseSensitivityNormalizer(CaseSensitivityNormalizer caseSensitivityNormalizer) {
        this.caseSensitivityNormalizer = caseSensitivityNormalizer;
    }

    /**
     * Check if an SMP user matching the user name of the BasicAuth credentials
     * exists, and that the passwords match. So this method verifies that the
     * BasicAuth credentials are valid.
     *
     * @param sUsername The credentials to be validated. May not be <code>null</code>.
     * @return The matching non-<code>null</code> {@link DBUser}.
     * @throws UnknownUserException  If no user matching the passed user name is present
     * @throws UnauthorizedException If the password in the credentials does not match the stored
     *                               password
     */
    @Nonnull
    ////@Override
    public DBUser _verifyUser(@Nonnull final String sUsername) throws UnknownUserException{

        //final String sUsername = aCredentials.getUserName();

        final DBUser aDBUser = entityManager.find(DBUser.class, sUsername);

        // Check that the user exists
        if (aDBUser == null) {
            throw new UnknownUserException(sUsername);
        }
/*
        // Check that the password is correct
        if (!isNullPasswordAllowed(aDBUser.getPassword(),aCredentials.getPassword())){
            if(aCredentials.getPassword()== null || isBlank(aDBUser.getPassword()) ||
                    ! BCrypt.checkpw(aCredentials.getPassword(), aDBUser.getPassword())) {
                throw new UnauthorizedException("Illegal password for user '" + sUsername + "'");
            }
        }

        if (s_aLogger.isDebugEnabled()) {
            s_aLogger.debug("Verified credentials of user '" + sUsername + "' successfully");
        }
*/
        return aDBUser;
    }
/*

    private boolean isNullPasswordAllowed(String requestPassword, String databasePassword){
       return (isBlank(requestPassword) && isBlank(databasePassword));
    }
*/

    /**
     * Verify that the passed service group is owned by the user specified in the
     * credentials.
     *
     * @param aServiceGroupID The service group to be verified
     * @param username    The credentials to be checked
     * @throws UnauthorizedException If the participant identifier is not owned by the user specified in
     *                               the credentials
     */
    @Nonnull
    private void _verifyOwnership(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                                         @Nonnull final String username)  {
/*
        if (_isAdmin(username)){
            return;
        }

        final DBOwnershipID aOwnershipID = new DBOwnershipID(username, aServiceGroupID);
        final DBOwnership aOwnership = getEntityManager().find(DBOwnership.class, aOwnershipID);
        if (aOwnership == null) {
            throw new UnauthorizedException("User '" +
                    username +
                    "' does not own " +
                    IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID));
        }

        if (s_aLogger.isDebugEnabled())
            s_aLogger.debug("Verified service group ID " +
                    IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID) +
                    " is owned by user '" +
                    username +
                    "'");
        */
    }

    private boolean _isAdmin(@Nonnull String username) {
        final DBUser aDBUser = entityManager.find(DBUser.class, username);
        return aDBUser.isAdmin();
    }

    /**
     * Checks if exists a ServiceGroup with that ServiceGroupId
     * @param aServiceGroupID Service Group Id
     * @throws NotFoundException NotFoundException is thrown if Service Group does not exist
     */
    private void _verifyServiceGroup(ParticipantIdentifierType aServiceGroupID) throws NotFoundException {
        final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID(aServiceGroupID);
        DBServiceGroup aDBServiceGroup = entityManager.find(DBServiceGroup.class, aDBServiceGroupID);
        if(aDBServiceGroup == null) {
            throw new NotFoundException(String.format("ServiceGroup '%s::%s' was not found", aServiceGroupID.getScheme(), aServiceGroupID.getValue()));
        }
    }


    @Nonnull
    @ReturnsMutableCopy
    public Collection<ParticipantIdentifierType> getServiceGroupList(@Nonnull final String username) throws Throwable {
        /*
        JPAExecutionResult<Collection<ParticipantIdentifierType>> ret;
        ret = doSelect(new Callable<Collection<ParticipantIdentifierType>>() {
            @Nonnull
            @ReturnsMutableCopy
            public Collection<ParticipantIdentifierType> call() throws Exception {
            */
                final DBUser aDBUser = _verifyUser(username);

                final List<DBOwnership> aDBOwnerships = entityManager.createQuery("SELECT p FROM DBOwnership p WHERE p.user = :user",
                        DBOwnership.class)
                        .setParameter("user", aDBUser)
                        .getResultList();

                final Collection<ParticipantIdentifierType> aList = new ArrayList<ParticipantIdentifierType>();
                for (final DBOwnership aDBOwnership : aDBOwnerships) {
                    final DBServiceGroupID aDBServiceGroupID = aDBOwnership.getServiceGroup().getId();
                    aList.add(aDBServiceGroupID.asBusinessIdentifier());
                }
                return aList;
                /*
            }
        });
        return ret.getOrThrow();
        */
    }


    @Nullable
    public ServiceGroup getServiceGroup(@Nonnull final ParticipantIdentifierType aServiceGroupID) {
        final ParticipantIdentifierType normalizedServiceGroupID = caseSensitivityNormalizer.normalize(aServiceGroupID);
        /*
        JPAExecutionResult<ServiceGroup> ret;
        ret = doInTransaction(getEntityManager(), true, new Callable<ServiceGroup>() {
            @Nullable
            public ServiceGroup call() throws Exception {
            */
                final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID(normalizedServiceGroupID);
                final DBServiceGroup aDBServiceGroup = entityManager.find(DBServiceGroup.class, aDBServiceGroupID);
                if (aDBServiceGroup == null) {
                    s_aLogger.warn("No such service group to retrieve: " +
                            IdentifierUtils.getIdentifierURIEncoded(normalizedServiceGroupID));
                    return null;
                }

                // Convert service group DB to service group service
                final ServiceGroup aServiceGroup = m_aObjFactory.createServiceGroup();
                aServiceGroup.setParticipantIdentifier(normalizedServiceGroupID);
                List<ExtensionType> extensions = null;
                try {
                    extensions = ExtensionUtils.unmarshalExtensions(aDBServiceGroup.getExtension());
                } catch (JAXBException e) {
                    throw new RuntimeException(e);
                }
                aServiceGroup.getExtensions().addAll(extensions);
                // This is set by the REST interface:
                // ret.setServiceMetadataReferenceCollection(value)
                return aServiceGroup;
                /*
            }
        });
        try {
            return ret.getOrThrow();
        } catch (Throwable throwable) {
            //TODO Don't bother about it, this class will be removed in next sprint.
            throw (RuntimeException)throwable;
        }
        */
    }

    public boolean saveServiceGroup(@Nonnull final ServiceGroup serviceGroup,
                                    @Nonnull final String newOwnerName) {

        final ServiceGroup normalizedServiceGroup = normalizeIdentifierCaseSensitivity(serviceGroup);
        /*
        JPAExecutionResult<Boolean> ret;
        final EntityManager aEM = getEntityManager();
        ret = doInTransaction(aEM, true, new Callable<Boolean>() {
            public Boolean call() throws JAXBException, XMLStreamException {
            */
                final DBUser aDBUser = _verifyUser(newOwnerName);
                final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID(normalizedServiceGroup.getParticipantIdentifier());

                // Check if the passed service group ID is already in use
                DBServiceGroup aDBServiceGroup = entityManager.find(DBServiceGroup.class, aDBServiceGroupID);

        String extensions = null;
        try {
            extensions = ExtensionUtils.marshalExtensions(normalizedServiceGroup.getExtensions());
        } catch (JAXBException|XMLStreamException e) {
            throw new RuntimeException(e);
        }
        if (aDBServiceGroup != null) {
                    // The business did exist. So it must be owned by the passed user.

                    //TODO: Probably verification is no longer needed as anly SMP_ADMIN can call this method
                    //_verifyOwnership(serviceGroup.getParticipantIdentifier(), newOwnerName);

                    // Simply update the extension
                    aDBServiceGroup.setExtension(extensions);
                    entityManager.merge(aDBServiceGroup);
                    return false;
                } else {

                    // It's a new service group
                    m_aHook.create(normalizedServiceGroup.getParticipantIdentifier());

                    // Did not exist. Create it.
                    aDBServiceGroup = new DBServiceGroup(aDBServiceGroupID);
                    aDBServiceGroup.setExtension(extensions);
                    entityManager.persist(aDBServiceGroup);

                    // Save the ownership information
                    final DBOwnershipID aDBOwnershipID = new DBOwnershipID(newOwnerName, normalizedServiceGroup.getParticipantIdentifier());
                    final DBOwnership aDBOwnership = new DBOwnership(aDBOwnershipID, aDBUser, aDBServiceGroup);
                    entityManager.persist(aDBOwnership);
                    return true;
                }
                /*
            }
        });
        try {
            return ret.getOrThrow();
        } catch (Throwable throwable) {
            //TODO Don't bother about it, this class will be removed in next sprint.
            throw (RuntimeException)throwable;
        }
        */
    }

    private ServiceGroup normalizeIdentifierCaseSensitivity(ServiceGroup serviceGroup) {
        final ServiceGroup sg = new ServiceGroup();
        sg.setParticipantIdentifier(caseSensitivityNormalizer.normalize(serviceGroup.getParticipantIdentifier()));
        sg.setServiceMetadataReferenceCollection(serviceGroup.getServiceMetadataReferenceCollection());
        sg.getExtensions().addAll(serviceGroup.getExtensions());
        return sg;
    }

/*

    private ServiceGroup normalizeIdentifierCaseSensitivity(@Nonnull ServiceGroup aServiceGroup) {
        final ServiceGroup sg = new ServiceGroup();
        sg.setParticipantIdentifier(caseSensitivityNormalizer.normalize(aServiceGroup.getParticipantIdentifier()));
        sg.setServiceMetadataReferenceCollection(aServiceGroup.getServiceMetadataReferenceCollection());
        sg.getExtensions().addAll(aServiceGroup.getExtensions());
        return sg;
    }
*/

    //@Override
    public void deleteServiceGroup(@Nonnull final ParticipantIdentifierType aServiceGroupID) {
        final ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(aServiceGroupID);
        /*
        JPAExecutionResult<EChange> ret;
        ret = doInTransaction(getEntityManager(), true, new Callable<EChange>() {
            @Nonnull
            public EChange call() {
            */
                //_verifyUser(username);

                // Check if the service group is existing
                //final EntityManager aEM = getEntityManager();
                final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID(normalizedServiceGroupId);
                final DBServiceGroup aDBServiceGroup = entityManager.find(DBServiceGroup.class, aDBServiceGroupID);
                if (aDBServiceGroup == null) {
                    s_aLogger.warn("No such service group to delete: " +
                            IdentifierUtils.getIdentifierURIEncoded(normalizedServiceGroupId));
                    throw new NotFoundException(IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID));
                }

                // Check the ownership afterwards, so that only existing serviceGroups are checked
                //_verifyOwnership(normalizedServiceGroupId, username);

                _removeServiceGroup(aDBServiceGroup);

                m_aHook.delete(normalizedServiceGroupId);

                //return EChange.CHANGED;
                /*
            }
        });
        if (ret.hasThrowable()) {
            //TODO Don't bother about it, this class will be removed in next sprint.
            throw (RuntimeException)ret.getThrowable();
        }else if (ret.get().isUnchanged())
            throw new NotFoundException(IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID));
            */
    }

    private void _removeServiceGroup(DBServiceGroup dbServiceGroup){
        entityManager.createQuery("DELETE FROM DBOwnership o WHERE o.id.businessIdentifierScheme = :scheme and o.id.businessIdentifier = :id")
                .setParameter("scheme", dbServiceGroup.getId().getBusinessIdentifierScheme())
                .setParameter("id", dbServiceGroup.getId().getBusinessIdentifier())
                .executeUpdate();

        entityManager.remove(dbServiceGroup);
    }

    @Nonnull
    @ReturnsMutableCopy
    public List<DBServiceMetadataID> getDocumentTypes(@Nonnull final ParticipantIdentifierType aServiceGroupID) throws Throwable {
        final ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(aServiceGroupID);
        /*
        JPAExecutionResult<List<DBServiceMetadataID>> ret;
        ret = doSelect(new Callable<List<DBServiceMetadataID>>() {
            @Nonnull
            @ReturnsMutableCopy
            public List<DBServiceMetadataID> call() throws Exception {
            */
                final List<DBServiceMetadata> aServices = entityManager.createQuery("SELECT p FROM DBServiceMetadata p WHERE p.id.businessIdentifierScheme = :scheme AND p.id.businessIdentifier = :value",
                        DBServiceMetadata.class)
                        .setParameter("scheme",
                                normalizedServiceGroupId.getScheme())
                        .setParameter("value",
                                normalizedServiceGroupId.getValue())
                        .getResultList();

                final List<DBServiceMetadataID> aList = new ArrayList<DBServiceMetadataID>();
                for (final DBServiceMetadata aService : aServices)
                    aList.add(aService.getId());
                return aList;
                /*
            }
        });
        return ret.getOrThrow();
        */
    }

    @Nonnull
    @ReturnsMutableCopy
    public Collection<ServiceMetadata> getServices(@Nonnull final ParticipantIdentifierType aServiceGroupID) throws Throwable {
        final ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(aServiceGroupID);
        /*
        JPAExecutionResult<Collection<ServiceMetadata>> ret;
        ret = doSelect(new Callable<Collection<ServiceMetadata>>() {
            @Nonnull
            @ReturnsMutableCopy
            public Collection<ServiceMetadata> call() throws Exception {
            */
                final List<DBServiceMetadata> aServices = entityManager.createQuery("SELECT p FROM DBServiceMetadata p WHERE p.id.businessIdentifierScheme = :scheme AND p.id.businessIdentifier = :value",
                        DBServiceMetadata.class)
                        .setParameter("scheme",
                                normalizedServiceGroupId.getScheme())
                        .setParameter("value",
                                normalizedServiceGroupId.getValue())
                        .getResultList();

                final List<ServiceMetadata> aList = new ArrayList<ServiceMetadata>();
                for (final DBServiceMetadata aService : aServices) {
                    ServiceMetadata aServiceMetadata = ServiceMetadataConverter.unmarshal(aService.getXmlContent());
                    aList.add(aServiceMetadata);
                }
                return aList;
                /*
            }
        });
        return ret.getOrThrow();
        */
    }

    @Nullable
    public String getService(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                             @Nonnull final DocumentIdentifier aDocTypeID){
        final ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(aServiceGroupID);
        final DocumentIdentifier normalizedDocId = caseSensitivityNormalizer.normalize(aDocTypeID);
        /*
        JPAExecutionResult<String> ret;
        ret = doSelect(new Callable<String>() {
            public String call() throws Exception {
            */
                final DBServiceMetadataID aDBServiceMetadataID = new DBServiceMetadataID(normalizedServiceGroupId, normalizedDocId);
                final DBServiceMetadata aDBServiceMetadata = entityManager.find(DBServiceMetadata.class,
                        aDBServiceMetadataID);

                if (aDBServiceMetadata == null) {
                    s_aLogger.info("Service metadata with ID " +
                            IdentifierUtils.getIdentifierURIEncoded(normalizedServiceGroupId) +
                            " / " +
                            IdentifierUtils.getIdentifierURIEncoded(normalizedDocId) +
                            " not found");
                    return null;
                }

                return aDBServiceMetadata.getXmlContent();
                /*
            }
        });
        try {
            return ret.getOrThrow();
        } catch (Throwable throwable) {
            //TODO Don't bother about it, this class will be removed in next sprint.
            throw new RuntimeException(throwable);
        }
        */
    }

    //@Override
    public boolean saveService(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                               @Nonnull final DocumentIdentifier aDocTypeID,
                               @Nonnull final String sXmlContent /*,
                               @Nonnull final String username*/){
        boolean newServiceCreated = true;

        final ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(aServiceGroupID);
        final DocumentIdentifier normalizedDocId = caseSensitivityNormalizer.normalize(aDocTypeID);

        //_verifyUser(username);
        _verifyServiceGroup(normalizedServiceGroupId);
        //_verifyOwnership(normalizedServiceGroupId, username);

        // Delete an eventually contained previous service in a separate transaction
        try {
            if (_deleteService(normalizedServiceGroupId, normalizedDocId) == EChange.CHANGED) {
                newServiceCreated = false;
            }
        } catch (Throwable throwable) {
            //TODO Don't bother about it, this class will be removed in next sprint.
            throw new RuntimeException(throwable);
        }

        // Create a new entry
        /*
        JPAExecutionResult<?> ret = doInTransaction(getEntityManager(), true, new Runnable() {
                public void run() {
                */
                    //final EntityManager aEM = getEntityManager();

                    // Check if an existing service is already contained
                    // This should have been deleted previously!
                    final DBServiceMetadataID aDBServiceMetadataID = new DBServiceMetadataID(normalizedServiceGroupId, normalizedDocId);
                    DBServiceMetadata aDBServiceMetadata = entityManager.find(DBServiceMetadata.class, aDBServiceMetadataID);
                    if (aDBServiceMetadata != null) {
                        throw new IllegalStateException("No DB ServiceMeta data with ID " +
                                IdentifierUtils.getIdentifierURIEncoded(normalizedServiceGroupId) +
                                " should be present!");
                    }

                    // Create a new entry
                    aDBServiceMetadata = new DBServiceMetadata();
                    aDBServiceMetadata.setId(aDBServiceMetadataID);
                    try {
                        _convertFromServiceToDB(normalizedServiceGroupId, normalizedDocId, sXmlContent, aDBServiceMetadata);
                    } catch (JAXBException | XMLStreamException e) {
                        throw new IllegalStateException("Problems converting from Service to DB", e);
                    }
                    entityManager.persist(aDBServiceMetadata);
                    /*
                }
        });
        if (ret.hasThrowable()) {
            //TODO Don't bother about it, this class will be removed in next sprint.
            throw new RuntimeException(ret.getThrowable());
        }
        */
        return newServiceCreated;
    }

    @Nonnull
    private EChange _deleteService(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                                   @Nonnull final DocumentIdentifier aDocTypeID){
                                   /*
        JPAExecutionResult<EChange> ret;
        ret = doInTransaction(getEntityManager(), true, new Callable<EChange>() {
            public EChange call() {
            */
                //final EntityManager aEM = getEntityManager();

                final DBServiceMetadataID aDBServiceMetadataID = new DBServiceMetadataID(aServiceGroupID, aDocTypeID);
                final DBServiceMetadata aDBServiceMetadata = entityManager.find(DBServiceMetadata.class, aDBServiceMetadataID);
                if (aDBServiceMetadata == null) {
                    // There were no service to delete.
                    s_aLogger.warn("No such service to delete: " +
                            IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID) +
                            " / " +
                            IdentifierUtils.getIdentifierURIEncoded(aDocTypeID));
                    return EChange.UNCHANGED;
                }

                // Remove main service data
                entityManager.remove(aDBServiceMetadata);
                return EChange.CHANGED;
                /*
            }
        });
        try {
            return ret.getOrThrow();
        } catch (Throwable throwable) {
            //TODO Don't bother about it, this class will be removed in next sprint.
            throw new RuntimeException(throwable);
        }
        */
    }

    //@Override
    public void deleteService(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                              @Nonnull final DocumentIdentifier aDocTypeID /*,
                              @Nonnull final String username*/){

        final ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(aServiceGroupID);
        final DocumentIdentifier normalizedDocId = caseSensitivityNormalizer.normalize(aDocTypeID);

        //_verifyUser(username);
        _verifyServiceGroup(normalizedServiceGroupId);
        //_verifyOwnership(normalizedServiceGroupId, username);

        final EChange eChange = _deleteService(normalizedServiceGroupId, normalizedDocId);
        if (eChange.isUnchanged())
            throw new NotFoundException(IdentifierUtils.getIdentifierURIEncoded(normalizedServiceGroupId) +
                    " / " +
                    IdentifierUtils.getIdentifierURIEncoded(normalizedDocId));
    }

    @Nullable
    public ServiceMetadata getRedirection(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                                              @Nonnull final DocumentIdentifier aDocTypeID) throws Throwable {

        /*JPAExecutionResult<ServiceMetadata> ret;
        ret = doSelect(new Callable<ServiceMetadata>() {
            @Nullable
            public ServiceMetadata call() throws Exception {
        */
                final DBServiceMetadataRedirectionID aDBRedirectID = new DBServiceMetadataRedirectionID(aServiceGroupID,
                        aDocTypeID);
                final DBServiceMetadataRedirection aDBServiceMetadataRedirection = entityManager.find(DBServiceMetadataRedirection.class,
                        aDBRedirectID);

                if (aDBServiceMetadataRedirection == null) {
                    if (GlobalDebug.isDebugMode())
                        s_aLogger.info("No redirection service group id: " +
                                IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID));
                    return null;
                }

                // First check whether an redirect exists.
                final ServiceMetadata aServiceMetadata = m_aObjFactory.createServiceMetadata();

                // Then return a redirect instead.
                final RedirectType aRedirect = m_aObjFactory.createRedirectType();
                aRedirect.setCertificateUID(aDBServiceMetadataRedirection.getCertificateUid());
                aRedirect.setHref(aDBServiceMetadataRedirection.getRedirectionUrl());
                aRedirect.getExtensions().addAll(ExtensionUtils.unmarshalExtensions(aDBServiceMetadataRedirection.getExtension()));
                aServiceMetadata.setRedirect(aRedirect);

                return aServiceMetadata;
                /*
            }
        });
        return ret.getOrThrow();
        */
    }

    private static void _convertFromServiceToDB(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                                                @Nonnull final DocumentIdentifier aDocTypeID,
                                                @Nonnull final String sXmlContent,
                                                @Nonnull final DBServiceMetadata aDBServiceMetadata) throws JAXBException, XMLStreamException {
        // Update it.
        ServiceMetadata aServiceMetadata = ServiceMetadataConverter.unmarshal(sXmlContent);
        final ServiceInformationType aServiceInformation = aServiceMetadata.getServiceInformation();
        if(aServiceInformation != null && aServiceInformation.getExtensions().size() > 0) {
            aDBServiceMetadata.setExtension(ExtensionUtils.marshalExtensions(aServiceInformation.getExtensions()));
        }
        final RedirectType aRedirect = aServiceMetadata.getRedirect();
        if(aRedirect != null && aRedirect.getExtensions().size() > 0) {
            aDBServiceMetadata.setExtension(ExtensionUtils.marshalExtensions(aRedirect.getExtensions()));
        }
        aDBServiceMetadata.setXmlContent(sXmlContent);
    }
/*

    public EntityManager getCurrentEntityManager() {
        //return getEntityManager();
        return entityManager;
    }

    private EntityManager getEntityManager() {
        //return getEntityManager();
        return entityManager;
    }
*/

    //@Override
    public DBUser _verifyUser(@Nonnull BasicAuthClientCredentials aCredentials) throws UnknownUserException {
        return null;
    }
}
