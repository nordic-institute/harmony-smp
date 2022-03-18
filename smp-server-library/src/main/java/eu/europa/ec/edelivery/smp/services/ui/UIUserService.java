package eu.europa.ec.edelivery.smp.services.ui;

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
import eu.europa.ec.edelivery.smp.utils.BCryptPasswordHash;
import eu.europa.ec.edelivery.smp.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class UIUserService extends UIServiceBase<DBUser, UserRO> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIUserService.class);

    @Autowired
    private UserDao userDao;

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
     *  Method regenerate access token for user and returns access token
     *  In the database the access token value is saved in format BCryptPasswordHash
     *
     * @param userRO
     * @return generated AccessToken.
     */
    @Transactional
    public AccessTokenRO generateAccessTokenForUser(Long userId) {
        DBUser dbUser = userDao.find(userId);
        AccessTokenRO token = SecurityUtils.generateAccessToken();
        dbUser.setAccessTokenIdentifier(token.getIdentifier());
        dbUser.setAccessToken(BCryptPasswordHash.hashPassword(token.getValue()));
        dbUser.setAccessTokenGeneratedOn(token.getGeneratedOn());
        userDao.update(dbUser);
        return token;
    }

    @Transactional
    public void updateUserList(List<UserRO> lst, LocalDateTime passwordChange) {
        for (UserRO userRO : lst) {
            if (userRO.getStatus() == EntityROStatus.NEW.getStatusNumber()) {
                DBUser dbUser = convertFromRo(userRO);
                if (!StringUtils.isBlank(userRO.getPassword())) {
                    dbUser.setPassword(BCryptPasswordHash.hashPassword(userRO.getPassword()));
                }
                userDao.persistFlushDetach(dbUser);
            } else if (userRO.getStatus() == EntityROStatus.UPDATED.getStatusNumber()) {
                DBUser dbUser = userDao.find(userRO.getId());
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
                        dbCertificate.setValidFrom(LocalDateTime.ofInstant(certificateRO.getValidFrom().toInstant(), ZoneId.systemDefault()));
                    }
                    if (certificateRO.getValidTo() != null) {
                        dbCertificate.setValidTo(LocalDateTime.ofInstant(certificateRO.getValidTo().toInstant(), ZoneId.systemDefault()));
                    }
                    dbCertificate.setCertificateId(certificateRO.getCertificateId());
                    dbCertificate.setSerialNumber(certificateRO.getSerialNumber());
                    dbCertificate.setSubject(certificateRO.getSubject());
                    dbCertificate.setIssuer(certificateRO.getIssuer());
                }
                dbUser.setLastUpdatedOn(LocalDateTime.now());
                userDao.update(dbUser);
            } else if (userRO.getStatus() == EntityROStatus.REMOVE.getStatusNumber()) {
                userDao.removeById(userRO.getId());
            }
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
        List<DBUserDeleteValidation> lstMessages = userDao.validateUsersForDelete(dev.getListIds());
        dev.setValidOperation(lstMessages.isEmpty());
        StringWriter sw = new StringWriter();
        sw.write("Could not delete user with ownerships! ");
        lstMessages.forEach(msg -> {
            dev.getListDeleteNotPermitedIds().add(msg.getId());
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
