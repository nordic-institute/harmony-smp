package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.smp.BCryptPasswordHash;
import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.model.DBUserDeleteValidation;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.java2d.pipe.SpanShapeRenderer;

import java.io.*;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class UIUserService extends UIServiceBase<DBUser, UserRO> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIUserService.class);

    private static final byte[] S_PEM_START_TAG = "-----BEGIN CERTIFICATE-----\n".getBytes();
    private static final byte[] S_PEM_END_TAG = "\n-----END CERTIFICATE-----".getBytes();

    private static final String S_BLUECOAT_DATEFORMAT ="MMM dd HH:mm:ss yyyy";

    @Autowired
    UserDao userDao;

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
     * @return ServiceResult wiht list
     */
    @Transactional
    public ServiceResult<UserRO> getTableList(int page, int pageSize,
                                              String sortField,
                                              String sortOrder, Object filter) {

        ServiceResult<UserRO> resUsers = super.getTableList(page, pageSize, sortField, sortOrder, filter);
        resUsers.getServiceEntities().forEach(usr -> usr.setPassword(null));
        return resUsers;
    }

    @Transactional
    public void updateUserList(List<UserRO> lst) {
        boolean suc = false;
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
                // check for new password
                if (!StringUtils.isBlank(userRO.getPassword())) {
                    dbUser.setPassword(BCryptPasswordHash.hashPassword(userRO.getPassword()));
                }
                // update certificate data
                if (userRO.getCertificate() == null || StringUtils.isBlank(userRO.getCertificate().getCertificateId())) {
                    dbUser.setCertificate(null);
                } else {
                    CertificateRO certificateRO = userRO.getCertificate();
                    DBCertificate dbCertificate = dbUser.getCertificate() != null ? dbUser.getCertificate() : new DBCertificate();
                    dbUser.setCertificate(dbCertificate);
                    if (certificateRO.getValidFrom()!=null) {
                        dbCertificate.setValidFrom(LocalDateTime.ofInstant(certificateRO.getValidFrom().toInstant(), ZoneId.systemDefault()));
                    }
                    if (certificateRO.getValidTo()!=null) {
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

    public CertificateRO getCertificateData(byte[] buff) throws CertificateException, IOException {

        // get pem encoding -
        InputStream isCert = createPEMFormat(buff);

        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) fact.generateCertificate(isCert);
        String subject = cert.getSubjectDN().getName();
        String issuer = cert.getIssuerDN().getName();
        String hash = cert.getIssuerDN().getName();
        BigInteger serial = cert.getSerialNumber();
        String certId = getCertificateIdFromCertificate(subject, issuer, serial);
        CertificateRO cro = new CertificateRO();
        cro.setCertificateId(certId);
        cro.setSubject(subject);
        cro.setIssuer(issuer);
        // set serial as HEX
        cro.setSerialNumber(serial.toString(16));
        cro.setValidFrom(cert.getNotBefore());
        cro.setValidTo(cert.getNotAfter());
        cro.setEncodedValue(Base64.getMimeEncoder().encodeToString(cert.getEncoded()));
        // generate bluecoat header
        SimpleDateFormat sdf = new SimpleDateFormat(S_BLUECOAT_DATEFORMAT);
        StringWriter sw = new StringWriter();
        sw.write("sno=");
        sw.write(serial.toString(16));
        sw.write("&subject=");
        sw.write(urlEnodeString(subject));
        sw.write("&validfrom=");
        sw.write(urlEnodeString(sdf.format(cert.getNotBefore())+" GTM"));
        sw.write("&validto=");
        sw.write(urlEnodeString(sdf.format(cert.getNotAfter())+" GTM"));
        sw.write("&issuer=");
        sw.write(urlEnodeString(issuer));
        cro.setBlueCoatHeader(sw.toString());

        return cro;
    }

    private String urlEnodeString(String val){
        if (StringUtils.isBlank(val)){
            return "";
        } else {
            try {
                return URLEncoder.encode(val, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOG.error("Error occured while url encoding the certificate string:" + val, e );
            }
        }
        return "";
    }

    public boolean isCertificatePemEncoded(byte[] certData) {
        if (certData != null && certData.length > S_PEM_START_TAG.length) {
            for (int i = 0; i < certData.length; i++) {
                if (certData[i] != S_PEM_START_TAG[i]) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Method tests if certificate is in PEM  format. If not it creates pem format else returns original data.
     *
     * @param certData - certificate data
     * @return
     * @throws IOException
     */
    public ByteArrayInputStream createPEMFormat(byte[] certData) throws IOException {
        ByteArrayInputStream is;
        byte[] pemBuff = getPemEncodedString(certData);
        if (pemBuff!=null) {
            is = new ByteArrayInputStream(pemBuff);
        } else {
            // try to encode
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(S_PEM_START_TAG);
            bos.write(Base64.getMimeEncoder().encode(certData));
            bos.write(S_PEM_END_TAG);
            is = new ByteArrayInputStream(bos.toByteArray());
        }
        return is;
    }

    /**
     * pem encoded cartificate can have header_?? this code finds the certificate part and return the part
     * @param buff
     * @return
     */
    private byte[] getPemEncodedString(byte[] buff){
        int iStart = indexOf(buff, S_PEM_START_TAG, 0);
        if (iStart < 0){
            return null;
        }
        int iEnd = indexOf(buff, S_PEM_END_TAG, iStart);
        if (iEnd<=iStart){
            return null;
        }
        return Arrays.copyOfRange(buff, iStart, iEnd + S_PEM_END_TAG.length);



    }

    /**
     * Check if file contains bytearraz
     * @param outerArray
     * @param smallerArray
     * @return
     */
    public int indexOf(byte[] outerArray, byte[] smallerArray, int iStart) {
        for(int i = iStart; i < outerArray.length - smallerArray.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i+j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    public String getCertificateIdFromCertificate(String subject, String issuer, BigInteger serial) {
        return new PreAuthenticatedCertificatePrincipal(subject, issuer, serial).getName();
    }

    @Override
    public UserRO convertToRo(DBUser d) {

        UserRO dro = new UserRO();
        dro.setEmailAddress(d.getEmailAddress());
        dro.setUsername(d.getUsername());
        dro.setRole(d.getRole());
        dro.setPassword(d.getPassword());
        dro.setPasswordChanged(d.getPasswordChanged());
        dro.setActive(d.isActive());
        dro.setId(d.getId());

        if (d.getCertificate() != null) {
            CertificateRO certData = new CertificateRO();
            if (d.getCertificate().getValidTo() != null) {

                certData.setValidTo(Date.from(d.getCertificate().getValidTo().toInstant(ZoneOffset.UTC)));
            }
            if (d.getCertificate().getValidFrom() != null) {
                certData.setValidFrom(Date.from(d.getCertificate().getValidFrom().toInstant(ZoneOffset.UTC)));
            }
            certData.setCertificateId(d.getCertificate().getCertificateId());
            certData.setSerialNumber(d.getCertificate().getSerialNumber());
            certData.setIssuer(d.getCertificate().getIssuer());
            certData.setSubject(d.getCertificate().getSubject());
            dro.setCertificate(certData);
        }
        return dro;
    }

    public DeleteEntityValidation validateDeleteRequest(DeleteEntityValidation dev){
        List<DBUserDeleteValidation>  lstMessages = userDao.validateUsersForDelete(dev.getListIds());
        dev.setValidOperation(lstMessages.isEmpty());
        StringWriter sw = new StringWriter();
        sw.write("Could not delete user with ownerships! ");
        lstMessages.forEach(msg ->{
            dev.getListDeleteNotPermitedIds().add(msg.getId());
            sw.write("User: ");
            sw.write(StringUtils.isBlank(msg.getUsername())?msg.getCertificateId(): msg.getUsername());
            sw.write(" owns SG count: ");
            sw.write( msg.getCount().toString());
            sw.write( ". ");
        });
        dev.setStringMessage(sw.toString());
        return dev;
    }


    @Override
    public DBUser convertFromRo(UserRO d) {
        DBUser dro = new DBUser();
        dro.setEmailAddress(d.getEmailAddress());
        dro.setUsername(d.getUsername());
        dro.setRole(d.getRole());
        dro.setPassword(d.getPassword());
        dro.setActive(d.isActive());
        dro.setId(d.getId());
        dro.setPasswordChanged(d.getPasswordChanged());
        if (d.getCertificate() != null) {
            DBCertificate certData = new DBCertificate();
            if (d.getCertificate().getValidTo() != null) {
                certData.setValidTo(LocalDateTime.ofInstant(d.getCertificate().getValidTo().toInstant(), ZoneId.systemDefault()));
            }
            if (d.getCertificate().getValidFrom() != null) {
                certData.setValidFrom(LocalDateTime.ofInstant(d.getCertificate().getValidFrom().toInstant(), ZoneId.systemDefault()));
            }
            certData.setCertificateId(d.getCertificate().getCertificateId());
            certData.setSerialNumber(d.getCertificate().getSerialNumber());
            certData.setIssuer(d.getCertificate().getIssuer());
            certData.setSubject(d.getCertificate().getSubject());

            dro.setCertificate(certData);
        }
        return dro;

    }

}
