/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.config.SMPEnvironmentProperties;
import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.enums.CredentialTargetType;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.DBUserDeleteValidationMapping;
import eu.europa.ec.edelivery.smp.data.model.user.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.BadRequestException;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.CredentialService;
import eu.europa.ec.edelivery.smp.services.CredentialsAlertService;
import eu.europa.ec.edelivery.smp.utils.BCryptPasswordHash;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for user management in UI. It provides methods for user CRUD operations and user credential management.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Service
public class UIUserService extends UIServiceBase<DBUser, UserRO> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIUserService.class);
    private static final String USER_ID_REQUEST_TYPE = "UserId";

    private final UserDao userDao;
    private final CredentialService credentialService;
    CredentialDao credentialDao;
    private final ConfigurationService configurationService;
    private final ConversionService conversionService;
    private final CredentialsAlertService alertService;
    private final UITruststoreService truststoreService;

    public UIUserService(UserDao userDao,
                         CredentialDao credentialDao,
                         ConfigurationService configurationService,
                         ConversionService conversionService,
                         UITruststoreService truststoreService,
                         CredentialsAlertService alertService,
                         CredentialService credentialService) {
        this.userDao = userDao;
        this.credentialDao = credentialDao;
        this.configurationService = configurationService;
        this.conversionService = conversionService;
        this.truststoreService = truststoreService;
        this.alertService = alertService;
        this.credentialService = credentialService;
    }

    @Override
    protected BaseDao<DBUser> getDatabaseDao() {
        return userDao;
    }

    /**
     * Method returns user resource object list for  UI list page.
     *
     * @param page      - page number
     * @param pageSize  - page size
     * @param sortField - sort field
     * @param sortOrder - sort order
     * @param filter    - filter object
     * @return ServiceResult with list
     */
    @Transactional
    @Override
    public ServiceResult<UserRO> getTableList(int page, int pageSize, String sortField, String sortOrder, Object filter) {
        return super.getTableList(page, pageSize, sortField, sortOrder, filter);
    }

    public AccessTokenRO createAccessTokenForUser(Long userId, CredentialRO credInit) {

        DBUser dbUser = userDao.find(userId);
        if (dbUser == null) {
            LOG.error("Can not update user password because authorized user with id [{}] does not exist!", userId);
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, USER_ID_REQUEST_TYPE, "Can not find user id!");
        }

        boolean testMode = SMPEnvironmentProperties.getInstance().isSMPStartupInDevMode();
        AccessTokenRO token = generateAccessToken(testMode);
        OffsetDateTime generatedTime = token.getGeneratedOn();
        OffsetDateTime expireOnTime = generatedTime.plusDays(configurationService.getAccessTokenPolicyValidDays());

        DBCredential dbCredential = new DBCredential();
        dbCredential.setCredentialTarget(CredentialTargetType.REST_API);
        dbCredential.setCredentialType(CredentialType.ACCESS_TOKEN);
        dbCredential.setName(token.getIdentifier());
        dbCredential.setValue(BCryptPasswordHash.hashPassword(token.getValue()));
        dbCredential.setChangedOn(generatedTime);
        dbCredential.setDescription(credInit.getDescription());
        dbCredential.setActive(credInit.isActive());
        dbCredential.setActiveFrom(credInit.getActiveFrom() != null ? credInit.getActiveFrom() : generatedTime);
        dbCredential.setExpireOn(credInit.getExpireOn() != null ? credInit.getExpireOn() : expireOnTime);
        dbCredential.setUser(dbUser);
        credentialDao.persistFlushDetach(dbCredential);

        CredentialRO result = conversionService.convert(dbCredential, CredentialRO.class);
        if (result != null) {
            result.setStatus(EntityROStatus.NEW.getStatusNumber());
            token.setCredential(result);
        }
        return token;
    }

    public static AccessTokenRO generateAccessToken(boolean devMode) {
        AccessTokenRO accessToken = new AccessTokenRO();
        accessToken.setGeneratedOn(OffsetDateTime.now());
        accessToken.setIdentifier(SecurityUtils.generateAuthenticationTokenIdentifier(devMode));
        accessToken.setValue(SecurityUtils.generateAuthenticationToken(devMode));
        return accessToken;
    }


    public CredentialRO storeCertificateCredentialForUser(Long userId, CredentialRO credential) {

        DBUser dbUser = userDao.find(userId);
        if (dbUser == null) {
            LOG.error("Can not update user password because authorized user with id [{}] does not exist!", userId);
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, USER_ID_REQUEST_TYPE, "Can not find user id!");
        }
        CertificateRO certificate = credential.getCertificate();
        if (certificate == null || StringUtils.isBlank(certificate.getCertificateId())) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "CertificateCredentials", "Certificate is not given for certificate credential!");
        }


        DBCredential dbCredential = conversionService.convert(credential, DBCredential.class);
        dbCredential.setCredentialType(CredentialType.CERTIFICATE);
        dbCredential.setCredentialTarget(CredentialTargetType.REST_API);
        dbCredential.setName(certificate.getCertificateId());
        DBCertificate dbCertificate = conversionService.convert(certificate, DBCertificate.class);
        if (dbCredential.getExpireOn() == null || dbCredential.getExpireOn().isAfter(dbCertificate.getValidTo())) {
            dbCredential.setExpireOn(dbCertificate.getValidTo());
        }

        if (dbCredential.getActiveFrom() == null || dbCredential.getActiveFrom().isBefore(dbCertificate.getValidFrom())) {
            dbCredential.setActiveFrom(dbCertificate.getValidFrom());
        }
        dbCredential.setUser(dbUser);
        dbCredential.setCertificate(dbCertificate);
        credentialDao.persistFlushDetach(dbCredential);


        CredentialRO result = conversionService.convert(dbCredential, CredentialRO.class);
        CertificateRO resultCertificate = truststoreService.getCertificateData(dbCertificate.getPemEncoding(), true, false);
        result.setCertificate(resultCertificate);
        result.setStatus(EntityROStatus.NEW.getStatusNumber());
        return result;
    }

    /**
     * Method updates the user password
     *
     * @param authorizedUserId        - authorized user id
     * @param userToUpdateId          - user id to update password  user id
     * @param authorizationPassword   - authorization password
     * @param newPassword             - new password for the userToUpdateId
     * @param validateCurrentPassword - validate authorizationPassword - if CAS authenticated skip this part
     * @return generated DBUser.
     */
    @Transactional
    public DBUser updateUserPassword(Long authorizedUserId, Long userToUpdateId, String authorizationPassword, String newPassword, boolean validateCurrentPassword) {

        Pattern pattern = configurationService.getPasswordPolicyRexExp();
        if (pattern != null && !pattern.matcher(newPassword).matches()) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "PasswordChange", configurationService.getPasswordPolicyValidationMessage());
        }
        Optional<DBCredential> dbCredential = credentialDao.findUsernamePasswordCredentialForUserIdAndUI(authorizedUserId);
        DBCredential dbAuthorizedCredentials = dbCredential.orElseThrow(() ->
                new SMPRuntimeException(ErrorCode.INVALID_REQUEST, USER_ID_REQUEST_TYPE, "Can not find user id"));

        DBUser authorizedUser = dbAuthorizedCredentials.getUser();

        if (validateCurrentPassword && !BCrypt.checkpw(authorizationPassword, dbAuthorizedCredentials.getValue())) {
            LOG.info(SMPLogger.SECURITY_MARKER, "Change/set password failed because of invalid credentials of the user changing the credentials: [{}]", authorizedUser.getUsername());
            throw new BadCredentialsException("Password change failed; Invalid authorization password!");
        }

        boolean adminUpdate = userToUpdateId != null
                && !Objects.equals(authorizedUserId, userToUpdateId);

        // check if authorized user has the permission to change other user credentials
        if (adminUpdate && authorizedUser.getApplicationRole() != ApplicationRoleType.SYSTEM_ADMIN) {
            LOG.info(SMPLogger.SECURITY_MARKER, "Change/set password failed because user changing the credentials does not have required permissions: [{}]", authorizedUser.getUsername());
            throw new BadCredentialsException("Password change failed; Insufficient permissions!");
        }
        return updateUsernamePasswordForUser(userToUpdateId, newPassword, adminUpdate);
    }

    /**
     * Method creates/updates the Username/passwords credentials for the user with given userId. In case the
     * flag adminUpdate is set to true - the ExpireOn date is not set. The method must be called inside active
     * transactions.
     *
     * @param userID      to change/create username-password credentials
     * @param password    the new password
     * @param adminUpdate who is changing the password.
     */
    protected DBUser updateUsernamePasswordForUser(Long userID, String password, boolean adminUpdate) {
        Optional<DBCredential> optCredential = credentialDao.findUsernamePasswordCredentialForUserIdAndUI(userID);

        DBCredential dbCredential = optCredential.orElse(credentialService.createCredentialsForUser(userID,
                CredentialType.USERNAME_PASSWORD,
                CredentialTargetType.UI));

        // check if new password is the same as the old one
        // but allow admin to overwrite it
        if (!adminUpdate
                && StringUtils.isNotBlank(dbCredential.getValue())
                && BCrypt.checkpw(password, dbCredential.getValue())) {
            LOG.info(SMPLogger.SECURITY_MARKER, "Change/set password failed because 'new' password match the old password for user: [{}]", userID);
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "PasswordChange", configurationService.getPasswordPolicyValidationMessage());
        }

        dbCredential.setValue(BCryptPasswordHash.hashPassword(password));
        OffsetDateTime currentTime = OffsetDateTime.now();
        dbCredential.setChangedOn(currentTime);
        dbCredential.setActiveFrom(currentTime);
        dbCredential.setExpireOn(adminUpdate ? null :
                currentTime.plusDays(configurationService.getPasswordPolicyValidDays()));
        // clear reset token if exists
        dbCredential.setResetToken(null);
        dbCredential.setResetExpireOn(null);

        // clear failed attempts
        dbCredential.setLastFailedLoginAttempt(null);
        dbCredential.setSequentialLoginFailureCount(null);
        // if the credentials are not managed by the session , e.g. "new" then persist it
        if (dbCredential.getId() == null) {
            credentialDao.persist(dbCredential);
        }
        // submit mail with reset token
        alertService.alertCredentialChanged(dbCredential);
        return dbCredential.getUser();
    }

    /**
     * Method updates the user password
     *
     * @param authorizedUserId      - authorized user id
     * @param userToUpdateId        - user id to update password  user id
     * @param authorizationPassword - authorization password
     * @param newPassword           - new password for the userToUpdateId
     * @return DBUser the updated user
     */
    @Transactional
    public DBUser updateUserPassword(Long authorizedUserId, Long userToUpdateId, String authorizationPassword, String newPassword) {
        return updateUserPassword(authorizedUserId, userToUpdateId, authorizationPassword, newPassword, true);
    }

    /**
     * Method updates user profile data to database
     *
     * @param userId the User id for updating user profile
     * @param user   userRO data
     */
    @Transactional
    public void updateUserProfile(Long userId, UserRO user) {
        DBUser dbUser = userDao.find(userId);
        if (dbUser == null) {
            LOG.error("Can not update user because user for id [{}] does not exist!", userId);
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, USER_ID_REQUEST_TYPE, "Can not find user id!");
        }
        LOG.debug("Update user [{}]: email [{}], fullName [{}], smp theme [{}]", user.getUsername(), user.getEmailAddress(), user.getFullName(), user.getSmpTheme());
        // update user profile data on managed db entity. (For now Just email, name and theme)
        dbUser.setEmailAddress(user.getEmailAddress());
        dbUser.setFullName(user.getFullName());
        dbUser.setSmpTheme(user.getSmpTheme());
        dbUser.setSmpLocale(user.getSmpLocale());
    }

    @Transactional
    public void adminUpdateUserData(Long userId, UserRO user) {
        DBUser dbUser = userDao.find(userId);
        if (dbUser == null) {
            LOG.error("Can not update user because user for id [{}] does not exist!", userId);
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, USER_ID_REQUEST_TYPE, "Can not find user id!");
        }
        LOG.debug("Update user [{}]: email [{}], fullName [{}], smp theme [{}]", user.getUsername(), user.getEmailAddress(), user.getFullName(), user.getSmpTheme());
        // update user data by admin
        dbUser.setActive(user.isActive());
        dbUser.setApplicationRole(user.getRole());
        dbUser.setEmailAddress(user.getEmailAddress());
        dbUser.setFullName(user.getFullName());
        dbUser.setSmpTheme(user.getSmpTheme());
        dbUser.setSmpLocale(user.getSmpLocale());
        alertService.alertUserUpdated(dbUser);
    }

    @Transactional
    public UserRO adminCreateUserData(UserRO user) {

        Optional<DBUser> testUser = userDao.findUserByUsername(user.getUsername());
        if (testUser.isPresent()) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "CreateUser", "User with username [" + user.getUsername() + "] already exists!");
        }
        DBUser dbUser = new DBUser();
        // update user data by admin
        dbUser.setUsername(user.getUsername());
        dbUser.setApplicationRole(user.getRole());
        dbUser.setEmailAddress(user.getEmailAddress());
        dbUser.setFullName(user.getFullName());
        dbUser.setSmpTheme(user.getSmpTheme());
        dbUser.setSmpLocale(user.getSmpLocale());
        return createDBUser(dbUser);
    }

    @Transactional
    public UserRO createDBUser(DBUser dbUser) {
        userDao.persistFlushDetach(dbUser);
        UserRO userRO =  conversionService.convert(dbUser, UserRO.class);
        if (StringUtils.isNotBlank(userRO.getEmailAddress())){
            alertService.alertUserCreated(dbUser);
        }
        return userRO;
    }

    @Transactional
    public UserRO adminDeleteUserData(Long userId) {
        DBUser dbUser = userDao.find(userId);
        if (dbUser == null) {
            LOG.error("Can not delete user because user for id [{}] does not exist!", userId);
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, USER_ID_REQUEST_TYPE, "Can not find user id!");
        }
        userDao.remove(dbUser);
        return conversionService.convert(dbUser, UserRO.class);
    }

    /**
     * Returns the user entity by its primary key or throws a {@code SMPRuntimeException} if such entity does not exist.
     *
     * @param userId The primary key of the user entity
     * @return the user entity
     * @throws SMPRuntimeException if a user entity having the provided primary key does not exist.
     */
    @Transactional(readOnly = true)
    public DBUser findUser(Long userId) {
        return userDao.findUser(userId).orElseThrow(() -> new SMPRuntimeException(ErrorCode.USER_NOT_EXISTS));
    }

    @Transactional(readOnly = true)
    public UserRO getUserById(Long userId) {
        DBUser user = userDao.findUser(userId).orElseThrow(() -> new SMPRuntimeException(ErrorCode.USER_NOT_EXISTS));
        return convertToRo(user);
    }

    public List<CredentialRO> getUserCredentials(Long userId,
                                                 CredentialType credentialType,
                                                 CredentialTargetType credentialTargetType) {
        LOG.debug("get User credential status: [{}]", userId);
        // Update the user and mark the password as changed at this very instant of time
        List<DBCredential> credentialROs = credentialDao
                .findUserCredentialForByUserIdTypeAndTarget(userId, credentialType, credentialTargetType);

        return credentialROs.stream().map(this::convertAndValidateCertificateCredential)
                .collect(Collectors.toList());
    }

    public CredentialRO convertAndValidateCertificateCredential(DBCredential credential) {
        CredentialRO credentialRO = conversionService.convert(credential, CredentialRO.class);
        if (credential.getCertificate() != null) {
            DBCertificate dbCert = credential.getCertificate();

            CertificateRO certificateRO;
            if (StringUtils.isNotBlank(dbCert.getPemEncoding())) {
                certificateRO = truststoreService.getCertificateData(dbCert.getPemEncoding(), true, false);

            } else {
                certificateRO = conversionService.convert(credential.getCertificate(), CertificateRO.class);
            }
            credentialRO.setCertificate(certificateRO);
        }
        return credentialRO;
    }

    @Transactional
    public CredentialRO getUserCertificateCredential(Long userId, Long certificateCredentialId) {
        // Update the user and mark the password as changed at this very instant of time
        DBCredential credential = credentialDao.findCredential(certificateCredentialId).orElseThrow(
                () -> new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Credentials", "Credentials not exists!"));
        validateCredentials(credential, userId, CredentialType.CERTIFICATE, CredentialTargetType.REST_API);
        CredentialRO credentialRO = conversionService.convert(credential, CredentialRO.class);
        if (credential.getCertificate() != null) {
            CertificateRO certificateRO = conversionService.convert(credential.getCertificate(), CertificateRO.class);
            credentialRO.setCertificate(certificateRO);
        }
        return credentialRO;
    }

    @Transactional
    public CredentialRO deleteUserCredentials(Long userId,
                                              Long credentialId,
                                              CredentialType credentialType,
                                              CredentialTargetType credentialTargetType) {
        LOG.debug("Delete user credential status: [{}]", userId);
        DBCredential credential = credentialDao.find(credentialId);
        validateCredentials(credential, userId, credentialType, credentialTargetType);
        credentialDao.remove(credential);
        CredentialRO credentialRO = conversionService.convert(credential, CredentialRO.class);
        credentialRO.setStatus(EntityROStatus.REMOVED.getStatusNumber());

        return credentialRO;
    }

    protected void validateCredentials(DBCredential credential, Long userId, CredentialType credentialType, CredentialTargetType credentialTargetType) {
        if (credential == null) {
            LOG.warn("Can not delete credential for ID [{}], because it does not exists!", userId);
            throw new BadRequestException(ErrorBusinessCode.UNAUTHORIZED, "Credential does not exist!");
        }
        // validate data
        if (!Objects.equals(credential.getUser().getId(), userId)) {
            throw new BadRequestException(ErrorBusinessCode.UNAUTHORIZED, "User is not owner of the credential");
        }

        if (credential.getCredentialType() != credentialType) {
            throw new BadRequestException(ErrorBusinessCode.UNAUTHORIZED, "Credentials are not expected credential type!");
        }

        if (credential.getCredentialTarget() != credentialTargetType) {
            throw new BadRequestException(ErrorBusinessCode.UNAUTHORIZED, "Credentials are not expected target type!");
        }
    }

    @Transactional
    public CredentialRO updateUserCredentials(Long userId,
                                              Long credentialId,
                                              CredentialType credentialType,
                                              CredentialTargetType credentialTargetType,
                                              CredentialRO credentialDataRO
    ) {
        LOG.debug("update User credential status: [{}]", userId);


        DBCredential credential = credentialDao.find(credentialId);
        validateCredentials(credential, userId, credentialType, credentialTargetType);

        credential.setDescription(credentialDataRO.getDescription());
        credential.setActive(credentialDataRO.isActive());
        credential.setActiveFrom(credentialDataRO.getActiveFrom());
        credential.setExpireOn(credentialDataRO.getExpireOn());

        CredentialRO credentialResultRO;
        if (credentialType == CredentialType.CERTIFICATE) {
            credentialResultRO = convertAndValidateCertificateCredential(credential);
        } else {
            credentialResultRO = conversionService.convert(credential, CredentialRO.class);
        }
        credentialResultRO.setStatus(EntityROStatus.UPDATED.getStatusNumber());

        alertService.alertCredentialChanged(credential);

        return credentialResultRO;
    }

    @Transactional
    public ServiceResult<SearchUserRO> searchUsers(int page, int pageSize, String filter) {
        Long count = userDao.getFilteredUserListCount(filter);
        ServiceResult<SearchUserRO> result = new ServiceResult<>();
        result.setPage(page);
        result.setPageSize(pageSize);
        if (count < 1) {
            result.setCount(0L);
            return result;
        }
        result.setCount(count);
        List<DBUser> users = userDao.getFilteredUserList(page, pageSize, filter);
        List<SearchUserRO> userList = users.stream().map(usr -> conversionService.convert(usr, SearchUserRO.class)).collect(Collectors.toList());

        result.getServiceEntities().addAll(userList);
        return result;
    }


    @Transactional(readOnly = true)
    public DBUser findUserByUsername(String userName) {
        return userDao.findUserByUsername(userName).orElseThrow(() -> new SMPRuntimeException(ErrorCode.USER_NOT_EXISTS));
    }

    @Override
    public UserRO convertToRo(DBUser d) {
        return conversionService.convert(d, UserRO.class);
    }


    /**
     * User can be deleted only if it does not own any of the service groups.
     *
     * @param dev
     * @return
     */
    public DeleteEntityValidation validateDeleteRequest(DeleteEntityValidation dev) {
        List<Long> idList = dev.getListIds().stream().map(SessionSecurityUtils::decryptEntityId).collect(Collectors.toList());
        List<DBUserDeleteValidationMapping> lstMessages = userDao.validateUsersForDelete(idList);
        dev.setValidOperation(lstMessages.isEmpty());
        StringWriter sw = new StringWriter();
        sw.write("Could not delete user with ownerships! ");
        lstMessages.forEach(msg -> {
            dev.getListDeleteNotPermitedIds().add(SessionSecurityUtils.encryptedEntityId(msg.getId()));
            sw.write("User: ");
            sw.write(StringUtils.isBlank(msg.getUsername()) ? msg.getCertificateId() : msg.getUsername());
            sw.write(" owns SG count: ");
            sw.write(msg.getCount().toString());
            sw.write(". ");
        });
        dev.setStringMessage(sw.toString());
        return dev;
    }

    @Override
    public DBUser convertFromRo(UserRO d) {
        return conversionService.convert(d, DBUser.class);
    }
}
