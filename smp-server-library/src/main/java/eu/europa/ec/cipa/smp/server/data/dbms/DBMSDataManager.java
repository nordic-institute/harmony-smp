/**
 * Version: MPL 1.1/EUPL 1.1
 * <p>
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 * <p>
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * <p>
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 * <p>
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * <p>
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.smp.server.data.dbms;

import com.helger.commons.GlobalDebug;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.callback.LoggingExceptionHandler;
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
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnknownUserException;
import eu.europa.ec.cipa.smp.server.hook.IRegistrationHook;
import eu.europa.ec.cipa.smp.server.hook.RegistrationHookFactory;
import eu.europa.ec.cipa.smp.server.util.ExtensionUtils;
import eu.europa.ec.cipa.smp.server.util.IdentifierUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * A Hibernate implementation of the DataManager interface.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class DBMSDataManager extends JPAEnabledManager implements IDataManager {
    private static final Logger s_aLogger = LoggerFactory.getLogger(DBMSDataManager.class);

    private final IRegistrationHook m_aHook;
    private final ObjectFactory m_aObjFactory = new ObjectFactory();

    public DBMSDataManager() {
        this(RegistrationHookFactory.createInstance());
    }

    public DBMSDataManager(@Nonnull final IRegistrationHook aHook) {
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
    }

    /**
     * Check if an SMP user matching the user name of the BasicAuth credentials
     * exists, and that the passwords match. So this method verifies that the
     * BasicAuth credentials are valid.
     *
     * @param aCredentials The credentials to be validated. May not be <code>null</code>.
     * @return The matching non-<code>null</code> {@link DBUser}.
     * @throws UnknownUserException  If no user matching the passed user name is present
     * @throws UnauthorizedException If the password in the credentials does not match the stored
     *                               password
     */
    @Nonnull
    @Override
    public DBUser _verifyUser(@Nonnull final BasicAuthClientCredentials aCredentials) throws UnknownUserException,
            UnauthorizedException {
        final String sUsername = aCredentials.getUserName();
        final DBUser aDBUser = getEntityManager().find(DBUser.class, sUsername);

        // Check that the user exists
        if (aDBUser == null) {
            throw new UnknownUserException(sUsername);
        }

        // Check that the password is correct
        if (!isNullPasswordAllowed(aDBUser.getPassword(),aCredentials.getPassword())){
            if(aCredentials.getPassword()== null || isBlank(aDBUser.getPassword()) ||
                    !aDBUser.getPassword().equals(aCredentials.getPassword())) {
                throw new UnauthorizedException("Illegal password for user '" + sUsername + "'");
            }
        }

        if (s_aLogger.isDebugEnabled()) {
            s_aLogger.debug("Verified credentials of user '" + sUsername + "' successfully");
        }

        return aDBUser;
    }

    private boolean isNullPasswordAllowed(String requestPassword, String databasePassword){
       return (isBlank(requestPassword) && isBlank(databasePassword));
    }

    /**
     * Verify that the passed service group is owned by the user specified in the
     * credentials.
     *
     * @param aServiceGroupID The service group to be verified
     * @param aCredentials    The credentials to be checked
     * @throws UnauthorizedException If the participant identifier is not owned by the user specified in
     *                               the credentials
     */
    @Nonnull
    private void _verifyOwnership(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                                         @Nonnull final BasicAuthClientCredentials aCredentials) throws UnauthorizedException {

        if (_isAdmin(aCredentials.getUserName())){
            return;
        }

        final DBOwnershipID aOwnershipID = new DBOwnershipID(aCredentials.getUserName(), aServiceGroupID);
        final DBOwnership aOwnership = getEntityManager().find(DBOwnership.class, aOwnershipID);
        if (aOwnership == null) {
            throw new UnauthorizedException("User '" +
                    aCredentials.getUserName() +
                    "' does not own " +
                    IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID));
        }

        if (s_aLogger.isDebugEnabled())
            s_aLogger.debug("Verified service group ID " +
                    IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID) +
                    " is owned by user '" +
                    aCredentials.getUserName() +
                    "'");
    }

    private boolean _isAdmin(@Nonnull String username) {
        final DBUser aDBUser = getEntityManager().find(DBUser.class, username);
        return aDBUser.isAdmin();
    }

    /**
     * Checks if exists a ServiceGroup with that ServiceGroupId
     * @param aServiceGroupID Service Group Id
     * @throws NotFoundException NotFoundException is thrown if Service Group does not exist
     */
    private void _verifyServiceGroup(ParticipantIdentifierType aServiceGroupID) throws NotFoundException {
        final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID(aServiceGroupID);
        DBServiceGroup aDBServiceGroup = getEntityManager().find(DBServiceGroup.class, aDBServiceGroupID);
        if(aDBServiceGroup == null) {
            throw new NotFoundException(String.format("ServiceGroup '%s::%s' was not found", aServiceGroupID.getScheme(), aServiceGroupID.getValue()));
        }
    }


    @Nonnull
    @ReturnsMutableCopy
    public Collection<ParticipantIdentifierType> getServiceGroupList(@Nonnull final BasicAuthClientCredentials aCredentials) throws Throwable {
        JPAExecutionResult<Collection<ParticipantIdentifierType>> ret;
        ret = doSelect(new Callable<Collection<ParticipantIdentifierType>>() {
            @Nonnull
            @ReturnsMutableCopy
            public Collection<ParticipantIdentifierType> call() throws Exception {
                final DBUser aDBUser = _verifyUser(aCredentials);

                final List<DBOwnership> aDBOwnerships = getEntityManager().createQuery("SELECT p FROM DBOwnership p WHERE p.user = :user",
                        DBOwnership.class)
                        .setParameter("user", aDBUser)
                        .getResultList();

                final Collection<ParticipantIdentifierType> aList = new ArrayList<ParticipantIdentifierType>();
                for (final DBOwnership aDBOwnership : aDBOwnerships) {
                    final DBServiceGroupID aDBServiceGroupID = aDBOwnership.getServiceGroup().getId();
                    aList.add(aDBServiceGroupID.asBusinessIdentifier());
                }
                return aList;
            }
        });
        return ret.getOrThrow();
    }

    //TODO: Remove once migrated to Spring...
    private static CaseSensitivityNormalizer getCaseSensitivityNormalizer() {
        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        return ctx.getBean(CaseSensitivityNormalizer.class);
    }

    @Nullable
    public ServiceGroup getServiceGroup(@Nonnull final ParticipantIdentifierType aServiceGroupID) throws Throwable {
        final ParticipantIdentifierType normalizedServiceGroupID = getCaseSensitivityNormalizer().normalize(aServiceGroupID);
        JPAExecutionResult<ServiceGroup> ret;
        ret = doInTransaction(new Callable<ServiceGroup>() {
            @Nullable
            public ServiceGroup call() throws Exception {
                final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID(normalizedServiceGroupID);
                final DBServiceGroup aDBServiceGroup = getEntityManager().find(DBServiceGroup.class, aDBServiceGroupID);
                if (aDBServiceGroup == null) {
                    s_aLogger.warn("No such service group to retrieve: " +
                            IdentifierUtils.getIdentifierURIEncoded(normalizedServiceGroupID));
                    return null;
                }

                // Convert service group DB to service group service
                final ServiceGroup aServiceGroup = m_aObjFactory.createServiceGroup();
                aServiceGroup.setParticipantIdentifier(normalizedServiceGroupID);
                aServiceGroup.getExtensions().addAll(ExtensionUtils.unmarshalExtensions(aDBServiceGroup.getExtension()));
                // This is set by the REST interface:
                // ret.setServiceMetadataReferenceCollection(value)
                return aServiceGroup;
            }
        });
        return ret.getOrThrow();
    }

    public boolean saveServiceGroup(@Nonnull final ServiceGroup aServiceGroup,
                                    @Nonnull final BasicAuthClientCredentials aCredentials) throws Throwable {
        JPAExecutionResult<Boolean> ret;
        final EntityManager aEM = getEntityManager();
        ret = doInTransaction(aEM, false, new Callable<Boolean>() {
            public Boolean call() throws JAXBException, XMLStreamException {
                final DBUser aDBUser = _verifyUser(aCredentials);
                final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID(aServiceGroup.getParticipantIdentifier());

                // Check if the passed service group ID is already in use
                DBServiceGroup aDBServiceGroup = aEM.find(DBServiceGroup.class, aDBServiceGroupID);

                if (aDBServiceGroup != null) {
                    // The business did exist. So it must be owned by the passed user.

                    _verifyOwnership(aServiceGroup.getParticipantIdentifier(), aCredentials);

                    // Simply update the extension
                    aDBServiceGroup.setExtension(ExtensionUtils.marshalExtensions(aServiceGroup.getExtensions()));
                    aEM.merge(aDBServiceGroup);
                    return false;
                } else {
                    // It's a new service group
                    m_aHook.create(aServiceGroup.getParticipantIdentifier());

                    // Did not exist. Create it.
                    aDBServiceGroup = new DBServiceGroup(aDBServiceGroupID);
                    aDBServiceGroup.setExtension(ExtensionUtils.marshalExtensions(aServiceGroup.getExtensions()));
                    aEM.persist(aDBServiceGroup);

                    // Save the ownership information
                    final DBOwnershipID aDBOwnershipID = new DBOwnershipID(aCredentials.getUserName(), aServiceGroup.getParticipantIdentifier());
                    final DBOwnership aDBOwnership = new DBOwnership(aDBOwnershipID, aDBUser, aDBServiceGroup);
                    aEM.persist(aDBOwnership);
                    return true;
                }
            }
        });
        return ret.getOrThrow();
    }

    public void deleteServiceGroup(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                                   @Nonnull final BasicAuthClientCredentials aCredentials) throws Throwable {
        JPAExecutionResult<EChange> ret;
        ret = doInTransaction(new Callable<EChange>() {
            @Nonnull
            public EChange call() {
                _verifyUser(aCredentials);

                // Check if the service group is existing
                final EntityManager aEM = getEntityManager();
                final DBServiceGroupID aDBServiceGroupID = new DBServiceGroupID(aServiceGroupID);
                final DBServiceGroup aDBServiceGroup = aEM.find(DBServiceGroup.class, aDBServiceGroupID);
                if (aDBServiceGroup == null) {
                    s_aLogger.warn("No such service group to delete: " +
                            IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID));
                    return EChange.UNCHANGED;
                }

                // Check the ownership afterwards, so that only existing serviceGroups are checked
                _verifyOwnership(aServiceGroupID, aCredentials);

                _removeServiceGroup(aDBServiceGroup);

                m_aHook.delete(aServiceGroupID);

                return EChange.CHANGED;
            }
        });
        if (ret.hasThrowable())
            throw ret.getThrowable();
        if (ret.get().isUnchanged())
            throw new NotFoundException(IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID));
    }

    private void _removeServiceGroup(DBServiceGroup dbServiceGroup){
        getEntityManager().createQuery("DELETE FROM DBOwnership o WHERE o.id.businessIdentifierScheme = :scheme and o.id.businessIdentifier = :id")
                .setParameter("scheme", dbServiceGroup.getId().getBusinessIdentifierScheme())
                .setParameter("id", dbServiceGroup.getId().getBusinessIdentifier())
                .executeUpdate();

        getEntityManager().remove(dbServiceGroup);
    }

    @Nonnull
    @ReturnsMutableCopy
    public List<DocumentIdentifier> getDocumentTypes(@Nonnull final ParticipantIdentifierType aServiceGroupID) throws Throwable {
        JPAExecutionResult<List<DocumentIdentifier>> ret;
        ret = doSelect(new Callable<List<DocumentIdentifier>>() {
            @Nonnull
            @ReturnsMutableCopy
            public List<DocumentIdentifier> call() throws Exception {
                final List<DBServiceMetadata> aServices = getEntityManager().createQuery("SELECT p FROM DBServiceMetadata p WHERE p.id.businessIdentifierScheme = :scheme AND p.id.businessIdentifier = :value",
                        DBServiceMetadata.class)
                        .setParameter("scheme",
                                aServiceGroupID.getScheme())
                        .setParameter("value",
                                aServiceGroupID.getValue())
                        .getResultList();

                final List<DocumentIdentifier> aList = new ArrayList<DocumentIdentifier>();
                for (final DBServiceMetadata aService : aServices)
                    aList.add(aService.getId().asDocumentTypeIdentifier());
                return aList;
            }
        });
        return ret.getOrThrow();
    }

    @Nonnull
    @ReturnsMutableCopy
    public Collection<ServiceMetadata> getServices(@Nonnull final ParticipantIdentifierType aServiceGroupID) throws Throwable {
        JPAExecutionResult<Collection<ServiceMetadata>> ret;
        ret = doSelect(new Callable<Collection<ServiceMetadata>>() {
            @Nonnull
            @ReturnsMutableCopy
            public Collection<ServiceMetadata> call() throws Exception {
                final List<DBServiceMetadata> aServices = getEntityManager().createQuery("SELECT p FROM DBServiceMetadata p WHERE p.id.businessIdentifierScheme = :scheme AND p.id.businessIdentifier = :value",
                        DBServiceMetadata.class)
                        .setParameter("scheme",
                                aServiceGroupID.getScheme())
                        .setParameter("value",
                                aServiceGroupID.getValue())
                        .getResultList();

                final List<ServiceMetadata> aList = new ArrayList<ServiceMetadata>();
                for (final DBServiceMetadata aService : aServices) {
                    ServiceMetadata aServiceMetadata = ServiceMetadataConverter.unmarshal(aService.getXmlContent());
                    aList.add(aServiceMetadata);
                }
                return aList;
            }
        });
        return ret.getOrThrow();
    }

    @Nullable
    public String getService(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                             @Nonnull final DocumentIdentifier aDocTypeID) throws Throwable {
        JPAExecutionResult<String> ret;
        ret = doSelect(new Callable<String>() {
            public String call() throws Exception {
                final DBServiceMetadataID aDBServiceMetadataID = new DBServiceMetadataID(aServiceGroupID, aDocTypeID);
                final DBServiceMetadata aDBServiceMetadata = getEntityManager().find(DBServiceMetadata.class,
                        aDBServiceMetadataID);

                if (aDBServiceMetadata == null) {
                    s_aLogger.info("Service metadata with ID " +
                            IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID) +
                            " / " +
                            IdentifierUtils.getIdentifierURIEncoded(aDocTypeID) +
                            " not found");
                    return null;
                }

                return aDBServiceMetadata.getXmlContent();
            }
        });
        return ret.getOrThrow();
    }

    public boolean saveService(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                               @Nonnull final DocumentIdentifier aDocTypeID,
                               @Nonnull final String sXmlContent,
                               @Nonnull final BasicAuthClientCredentials aCredentials) throws Throwable{
        boolean newServiceCreated = true;
        _verifyUser(aCredentials);
        _verifyServiceGroup(aServiceGroupID);
        _verifyOwnership(aServiceGroupID, aCredentials);

        // Delete an eventually contained previous service in a separate transaction
        if (_deleteService(aServiceGroupID, aDocTypeID) == EChange.CHANGED) {
            newServiceCreated = false;
        }

        // Create a new entry
        JPAExecutionResult<?> ret = doInTransaction(new Runnable() {
                public void run() {
                    final EntityManager aEM = getEntityManager();

                    // Check if an existing service is already contained
                    // This should have been deleted previously!
                    final DBServiceMetadataID aDBServiceMetadataID = new DBServiceMetadataID(aServiceGroupID, aDocTypeID);
                    DBServiceMetadata aDBServiceMetadata = aEM.find(DBServiceMetadata.class, aDBServiceMetadataID);
                    if (aDBServiceMetadata != null) {
                        throw new IllegalStateException("No DB ServiceMeta data with ID " +
                                IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID) +
                                " should be present!");
                    }

                    // Create a new entry
                    aDBServiceMetadata = new DBServiceMetadata();
                    aDBServiceMetadata.setId(aDBServiceMetadataID);
                    try {
                        _convertFromServiceToDB(aServiceGroupID, aDocTypeID, sXmlContent, aDBServiceMetadata);
                    } catch (JAXBException | XMLStreamException e) {
                        throw new IllegalStateException("Problems converting from Service to DB", e);
                    }
                    aEM.persist(aDBServiceMetadata);
                }
        });
        if (ret.hasThrowable()) {
            throw ret.getThrowable();
        }
        return newServiceCreated;
    }

    @Nonnull
    private EChange _deleteService(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                                   @Nonnull final DocumentIdentifier aDocTypeID) throws Throwable {
        JPAExecutionResult<EChange> ret;
        ret = doInTransaction(new Callable<EChange>() {
            public EChange call() {
                final EntityManager aEM = getEntityManager();

                final DBServiceMetadataID aDBServiceMetadataID = new DBServiceMetadataID(aServiceGroupID, aDocTypeID);
                final DBServiceMetadata aDBServiceMetadata = aEM.find(DBServiceMetadata.class, aDBServiceMetadataID);
                if (aDBServiceMetadata == null) {
                    // There were no service to delete.
                    s_aLogger.warn("No such service to delete: " +
                            IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID) +
                            " / " +
                            IdentifierUtils.getIdentifierURIEncoded(aDocTypeID));
                    return EChange.UNCHANGED;
                }

                // Remove main service data
                aEM.remove(aDBServiceMetadata);
                return EChange.CHANGED;
            }
        });
        return ret.getOrThrow();
    }

    public void deleteService(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                              @Nonnull final DocumentIdentifier aDocTypeID,
                              @Nonnull final BasicAuthClientCredentials aCredentials) throws Throwable {
        _verifyUser(aCredentials);
        _verifyServiceGroup(aServiceGroupID);
        _verifyOwnership(aServiceGroupID, aCredentials);

        final EChange eChange = _deleteService(aServiceGroupID, aDocTypeID);
        if (eChange.isUnchanged())
            throw new NotFoundException(IdentifierUtils.getIdentifierURIEncoded(aServiceGroupID) +
                    " / " +
                    IdentifierUtils.getIdentifierURIEncoded(aDocTypeID));
    }

    @Nullable
    public ServiceMetadata getRedirection(@Nonnull final ParticipantIdentifierType aServiceGroupID,
                                              @Nonnull final DocumentIdentifier aDocTypeID) throws Throwable {
        JPAExecutionResult<ServiceMetadata> ret;
        ret = doSelect(new Callable<ServiceMetadata>() {
            @Nullable
            public ServiceMetadata call() throws Exception {
                final DBServiceMetadataRedirectionID aDBRedirectID = new DBServiceMetadataRedirectionID(aServiceGroupID,
                        aDocTypeID);
                final DBServiceMetadataRedirection aDBServiceMetadataRedirection = getEntityManager().find(DBServiceMetadataRedirection.class,
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
            }
        });
        return ret.getOrThrow();
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

    public EntityManager getCurrentEntityManager() {
        return getEntityManager();
    }
}
