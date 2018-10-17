package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.smp.BCryptPasswordHash;
import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ServiceGroupService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ejb.Local;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class UIUserService extends UIServiceBase<DBUser, UserRO> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIUserService.class);

    @Autowired
    UserDao userDao;

    @Override
    protected BaseDao<DBUser> getDatabaseDao() {
        return userDao;
    }

    /**
     * Method returns user resource object list for page.
     *
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filter
     * @return
     */
    @Transactional
    public ServiceResult<UserRO> getTableList(int page, int pageSize,
                                                 String sortField,
                                                 String sortOrder, Object filter) {

        ServiceResult<UserRO> resUsers =  super.getTableList(page, pageSize, sortField, sortOrder, filter);
        resUsers.getServiceEntities().forEach(usr -> usr.setPassword(null));
        return resUsers;
    }

    @Transactional
    public void updateUserList(List<UserRO> lst) {
        boolean suc = false;
        for (UserRO userRO : lst) {

            if (userRO.getStatus() == EntityROStatus.NEW.getStatusNumber()) {
                DBUser dbUser = convertFromRo(userRO);
                userDao.persistFlushDetach(dbUser);
            } else if (userRO.getStatus() == EntityROStatus.UPDATED.getStatusNumber()) {
                DBUser dbUser = userDao.find(userRO.getId());
                dbUser.setEmail(userRO.getEmail());
                dbUser.setRole(userRO.getRole());
                dbUser.setActive(userRO.isActive());
                // check for new password
                if (!StringUtils.isBlank(userRO.getPassword())) {
                    if (!StringUtils.isBlank(dbUser.getPassword())) {
                        if (!BCrypt.checkpw(userRO.getPassword(), dbUser.getPassword())) {
                            LOG.debug("User with id {} changed password!", dbUser.getId());

                            dbUser.setPassword(BCryptPasswordHash.hashPassword(userRO.getPassword().trim()));
                            dbUser.setPasswordChanged(LocalDateTime.now());
                        }
                    } else {
                        dbUser.setPassword(BCryptPasswordHash.hashPassword(userRO.getPassword()));
                    }
                }
                // update certificate data
                if (userRO.getCertificateData() == null) {
                    dbUser.setCertificate(null);
                } else {
                    CertificateRO certificateRO = userRO.getCertificateData();
                    DBCertificate dbCertificate = dbUser.getCertificate() != null ? dbUser.getCertificate() : new DBCertificate();
                    dbUser.setCertificate(dbCertificate);
                    dbCertificate.setValidFrom(certificateRO.getValidFrom());
                    dbCertificate.setValidFrom(certificateRO.getValidTo());
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

    public CertificateRO getCertificateData(byte[] buff)  throws CertificateException{

        CertificateFactory fact = null;

            fact = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream is = new ByteArrayInputStream(buff);
            X509Certificate cert = (X509Certificate)  fact.generateCertificate(is);
            String subject = cert.getSubjectDN().getName();
            String issuer = cert.getIssuerDN().getName();
            String hash = cert.getIssuerDN().getName();
            BigInteger serial = cert.getSerialNumber();
            String certId = getCertificateIdFromCertificate(subject,issuer, serial );
            CertificateRO cro = new CertificateRO();
            cro.setCertificateId(certId);
            cro.setSubject(subject);
            cro.setIssuer(issuer);
            // set serial as HEX
            cro.setSerialNumber(serial.toString(16));
            cro.setValidFrom(LocalDateTime.ofInstant(cert.getNotBefore().toInstant(), ZoneId.systemDefault()));
            cro.setValidTo(LocalDateTime.ofInstant(cert.getNotAfter().toInstant(), ZoneId.systemDefault()));

            return cro;



    }

    public String getCertificateIdFromCertificate(String subject, String issuer, BigInteger serial ){
        return new PreAuthenticatedCertificatePrincipal(subject, issuer, serial).getName();
    }

    @Override
    public UserRO convertToRo(DBUser d) {
        try {
            UserRO dro = new UserRO();
            BeanUtils.copyProperties(dro, d);

            if (d.getCertificate()!=null) {
                CertificateRO certData = new CertificateRO();
                BeanUtils.copyProperties(certData, d.getCertificate());
                dro.setCertificateData(certData);
            }
            return dro;
        } catch ( InvocationTargetException | IllegalAccessException e) {
            String msg = "Error occurred while converting to RO Entity for " +UserRO.class.getName();
            LOG.error(msg, e );
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    public DBUser convertFromRo(UserRO d) {
        try {
            DBUser dro = new DBUser();
            BeanUtils.copyProperties(dro, d);
            DBCertificate cert = new DBCertificate();
            if (d.getCertificateData()!=null) {
                DBCertificate certData = new DBCertificate();
                BeanUtils.copyProperties(certData, d.getCertificateData());
                dro.setCertificate(cert);
            }

            return dro;
        } catch ( InvocationTargetException | IllegalAccessException e) {
            String msg = "Error occurred while converting to RO Entity for " +UserRO.class.getName();
            LOG.error(msg, e );
            throw new RuntimeException(msg, e);
        }
    }

}
