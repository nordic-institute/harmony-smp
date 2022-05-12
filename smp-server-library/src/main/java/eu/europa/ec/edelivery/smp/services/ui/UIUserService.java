package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.security.utils.X509CertificateUtils;
import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.model.DBUserDeleteValidation;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.utils.BCryptPasswordHash;
import eu.europa.ec.edelivery.smp.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Service
public class UIUserService extends UIServiceBase<DBUser, UserRO> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIUserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private ConversionService conversionService;

    @Autowired
    private UITruststoreService truststoreService;


    @Override
    protected BaseDao<DBUser> getDatabaseDao() {
        return userDao;
    }

    /**
     * Method returns user resource object list for  UI list page.
     *
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filter
     * @return ServiceResult with list
     */
    @Transactional
    public ServiceResult<UserRO> getTableList(int page, int pageSize, String sortField, String sortOrder, Object filter) {
        ServiceResult<UserRO> resUsers = super.getTableList(page, pageSize, sortField, sortOrder, filter);
        resUsers.getServiceEntities().forEach(this::updateUserStatus);
        return resUsers;
    }

    protected void updateUserStatus(UserRO user) {
        // never return password even if is hashed...
        user.setPassword(null);
        if (user.getCertificate() != null && !StringUtils.isBlank(user.getCertificate().getCertificateId())) {
            // validate certificate
            try {
                truststoreService.checkFullCertificateValidity(user.getCertificate());
            } catch (CertificateException e) {
                LOG.warn("Set invalid cert status: " + user.getCertificate().getCertificateId() + " reason: " + e.getMessage());
                user.getCertificate().setInvalid(true);
                user.getCertificate().setInvalidReason(e.getMessage());
            }
        }

    }

    /**
     * Method regenerate access token for user and returns access token
     * In the database the access token value is saved in format BCryptPasswordHash
     *
     * @param userId
     * @return generated AccessToken.
     */
    @Transactional
    public AccessTokenRO generateAccessTokenForUser(Long userId, String currentPassword) {

        DBUser dbUser = userDao.find(userId);
        if (dbUser == null) {
            LOG.error("Can not update user password because user for id [{}] does not exist!", userId);
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "UserId", "Can not find user id!");
        }
        if (!BCrypt.checkpw(currentPassword, dbUser.getPassword())) {
            throw new BadCredentialsException("Password change failed; Invalid current password!");
        }
        Boolean testMode = configurationService.isSMPStartupInDevMode();
        AccessTokenRO token = SecurityUtils.generateAccessToken(testMode);
        OffsetDateTime generatedTime = token.getGeneratedOn();
        token.setExpireOn(generatedTime.plusDays(configurationService.getAccessTokenPolicyValidDays()));
        dbUser.setAccessTokenIdentifier(token.getIdentifier());
        dbUser.setAccessToken(BCryptPasswordHash.hashPassword(token.getValue()));
        dbUser.setAccessTokenGeneratedOn(generatedTime);
        dbUser.setAccessTokenExpireOn(token.getExpireOn());

        return token;
    }

    /**
     * Method regenerate access token for user and returns access token
     * In the database the access token value is saved in format BCryptPasswordHash
     *
     * @param userId
     * @return generated AccessToken.
     */
    @Transactional
    public boolean updateUserPassword(Long userId, String currentPassword, String newPassword) {

        Pattern pattern = configurationService.getPasswordPolicyRexExp();
        if (!pattern.matcher(newPassword).matches()) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "PasswordChange", configurationService.getPasswordPolicyValidationMessage());
        }
        DBUser dbUser = userDao.find(userId);
        if (dbUser == null) {
            LOG.error("Can not update user password because user for id [{}] does not exist!", userId);
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "UserId", "Can not find user id!");
        }

        if (!BCrypt.checkpw(currentPassword, dbUser.getPassword())) {
            throw new BadCredentialsException("Password change failed; Invalid current password!");
        }
        dbUser.setPassword(BCryptPasswordHash.hashPassword(newPassword));
        OffsetDateTime currentTime = OffsetDateTime.now();
        dbUser.setPasswordChanged(currentTime);
        dbUser.setPasswordExpireOn(currentTime.plusDays(configurationService.getPasswordPolicyValidDays()));
        return true;
    }

    @Transactional
    public void updateUserList(List<UserRO> lst, OffsetDateTime passwordChange) {
        for (UserRO userRO : lst) {
            createOrUpdateUser(userRO, passwordChange);
        }
    }

    @Transactional
    public void updateUserdata(Long userId, UserRO user) {
        DBUser dbUser = userDao.find(userId);
        if (dbUser == null) {
            LOG.error("Can not update user because user for id [{}] does not exist!", userId);
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "UserId", "Can not find user id!");
        }

        dbUser.setEmailAddress(user.getEmailAddress());
        if (user.getCertificate() != null && (dbUser.getCertificate() == null
                || !StringUtils.equals(dbUser.getCertificate().getCertificateId(), user.getCertificate().getCertificateId()))) {
            CertificateRO certRo = user.getCertificate();
            LOG.info(certRo.getEncodedValue());
            if (user.getCertificate().getEncodedValue() != null) {

                String certificateAlias;
                try {
                    X509Certificate x509Certificate = X509CertificateUtils.getX509Certificate(Base64.getMimeDecoder().decode(certRo.getEncodedValue()));
                    certificateAlias = truststoreService.addCertificate(certRo.getAlias(), x509Certificate);
                } catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException e) {
                    LOG.error("Error occurred while adding certificate to truststore.", e);
                    throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "AddUserCertificate", ExceptionUtils.getRootCauseMessage(e));
                }
                certRo.setAlias(certificateAlias);
            }
            // first
            DBCertificate certificate = conversionService.convert(user.getCertificate(), DBCertificate.class);
            dbUser.setCertificate(certificate);
        }
    }

    protected void createOrUpdateUser(UserRO userRO, OffsetDateTime passwordChange) {
        if (userRO.getStatus() == EntityROStatus.NEW.getStatusNumber()) {
            DBUser dbUser = convertFromRo(userRO);
            if (!StringUtils.isBlank(userRO.getPassword())) {
                dbUser.setPassword(BCryptPasswordHash.hashPassword(userRO.getPassword()));
            }
            userDao.persistFlushDetach(dbUser);
            return;
        }
        Optional<DBUser> optionalDBUser = userDao.findUserByUsername(userRO.getUsername());
        if (!optionalDBUser.isPresent()) {
            return;
        }
        DBUser dbUser = optionalDBUser.get();


        if (userRO.getStatus() == EntityROStatus.UPDATED.getStatusNumber()) {

            dbUser.setEmailAddress(userRO.getEmailAddress());
            dbUser.setRole(userRO.getRole());
            dbUser.setActive(userRO.isActive());
            dbUser.setUsername(userRO.getUsername());
            if (StringUtils.isBlank(userRO.getUsername())) {
                // if username is empty than clear the password
                dbUser.setPassword("");
            } else if (!StringUtils.isBlank(userRO.getPassword())) {
                // check for new password
                dbUser.setPassword(BCryptPasswordHash.hashPassword(userRO.getPassword()));
                dbUser.setPasswordChanged(passwordChange);
            }
            // update certificate data
            if (userRO.getCertificate() == null || StringUtils.isBlank(userRO.getCertificate().getCertificateId())) {
                dbUser.setCertificate(null);
            } else {
                CertificateRO certificateRO = userRO.getCertificate();
                DBCertificate dbCertificate = dbUser.getCertificate() != null ? dbUser.getCertificate() : new DBCertificate();
                dbUser.setCertificate(dbCertificate);
                if (certificateRO.getValidFrom() != null) {
                    dbCertificate.setValidFrom(OffsetDateTime.ofInstant(certificateRO.getValidFrom().toInstant(), ZoneId.systemDefault()));
                }
                if (certificateRO.getValidTo() != null) {
                    dbCertificate.setValidTo(OffsetDateTime.ofInstant(certificateRO.getValidTo().toInstant(), ZoneId.systemDefault()));
                }
                dbCertificate.setCertificateId(certificateRO.getCertificateId());
                dbCertificate.setSerialNumber(certificateRO.getSerialNumber());
                dbCertificate.setSubject(certificateRO.getSubject());
                dbCertificate.setIssuer(certificateRO.getIssuer());
            }
            userDao.update(dbUser);
        } else if (userRO.getStatus() == EntityROStatus.REMOVE.getStatusNumber()) {
            userDao.removeById(dbUser.getId());
        }

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
    public DBUser findUserByUsername(String userName) {
        return userDao.findUserByUsername(userName).orElseThrow(() -> new SMPRuntimeException(ErrorCode.USER_NOT_EXISTS));
    }

    @Override
    public UserRO convertToRo(DBUser d) {
        return conversionService.convert(d, UserRO.class);
    }


    public DeleteEntityValidation validateDeleteRequest(DeleteEntityValidation dev) {
        List<Long> idList = dev.getListIds().stream().map(encId -> SessionSecurityUtils.decryptEntityId(encId)).collect(Collectors.toList());
        List<DBUserDeleteValidation> lstMessages = userDao.validateUsersForDelete(idList);
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
