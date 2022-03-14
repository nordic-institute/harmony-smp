package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.exceptions.CertificateNotTrustedException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.utils.X509CertificateUtils;
import eu.europa.ec.edelivery.text.DistinguishedNamesCodingUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Collections.list;
import static java.util.Locale.US;

@Service
public class UITruststoreService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UITruststoreService.class);

    private static final ThreadLocal<DateFormat> dateFormatLocal = ThreadLocal.withInitial(() ->
            new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", US)
    );

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    CRLVerifierService crlVerifierService;

    @Autowired
    private ConversionService conversionService;

    private List<String> normalizedTrustedList = new ArrayList<>();
    private Map<String, X509Certificate> truststoreCertificates = new HashMap();
    private List<CertificateRO> certificateROList = new ArrayList<>();
    KeyStore trustStore = null;


    private long lastUpdateTrustoreFileTime = 0;
    private File lastUpdateTrustStoreFile = null;

    TrustManager[] trustManagers;


    @PostConstruct
    public void init() {
        setupJCEProvider();
        refreshData();
    }

    private void setupJCEProvider() {
        Provider[] providerList = Security.getProviders();
        if (providerList == null || providerList.length <= 0 || !(providerList[0] instanceof BouncyCastleProvider)) {
            Security.insertProviderAt(new BouncyCastleProvider(), 1);
        }
    }

    public boolean useTrustStore() {
        File truststoreFile = configurationService.getTruststoreFile();
        return truststoreFile != null;
    }


    /**
     * Method  validates the configuration properties and refresh the
     * cached data
     */
    public void refreshData() {
        if (!useTrustStore()) {
            LOG.warn("Truststore filename is not set! Certificates will not be validated by trusted issuers!");
            return;
        }

        // load keystore
        File truststoreFile = getTruststoreFile();
        trustStore = loadTruststore(truststoreFile);
        if (trustStore == null) {
            LOG.error("Keystore: '" + truststoreFile.getAbsolutePath() + "' is not loaded! Check the truststore filename" +
                    " and the configuration!");
            return;
        }

        // init key managers for TLS
        TrustManager[] trustManagersTemp;
        try {
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            trustManagersTemp = tmf.getTrustManagers();
        } catch (KeyStoreException | NoSuchAlgorithmException exception) {
            LOG.error("Error occurred while initialize trustManagers : "
                    + truststoreFile.getAbsolutePath() + " Error: " + ExceptionUtils.getRootCauseMessage(exception), exception);
            return;
        }


        // load keys for signature
        List<String> tmpList = new ArrayList<>();
        Map<String, X509Certificate> hmCertificates = new HashMap<>();

        try {
            List<String> aliases = list(trustStore.aliases());
            for (String alias : aliases) {
                Certificate cert = trustStore.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Certificate = (X509Certificate) cert;
                    String subject = x509Certificate.getSubjectX500Principal().getName();

                    subject = DistinguishedNamesCodingUtil.normalizeDN(subject,
                            DistinguishedNamesCodingUtil.getCommonAttributesDN());
                    tmpList.add(subject);
                    hmCertificates.put(alias, x509Certificate);
                    try {
                        x509Certificate.checkValidity();
                    } catch (CertificateExpiredException | CertificateNotYetValidException ex) {
                        LOG.warn("Certificate: '{}' from truststore is not valid anymore!");
                    }
                }

            }
        } catch (Exception exception) {
            LOG.error("Could not load truststore certificates Error: " + ExceptionUtils.getRootCauseMessage(exception), exception);
            return;
        }
        truststoreCertificates.clear();
        normalizedTrustedList.clear();

        trustManagers = trustManagersTemp;
        normalizedTrustedList.addAll(tmpList);
        truststoreCertificates.putAll(hmCertificates);

        lastUpdateTrustoreFileTime = truststoreFile.lastModified();
        lastUpdateTrustStoreFile = truststoreFile;
        // clear list to reload RO when required
        certificateROList.clear();
    }

    public CertificateRO getCertificateData(byte[] buff) throws CertificateException, IOException {
        return getCertificateData(buff, false);
    }

    public CertificateRO getCertificateData(byte[] buff, boolean validate) throws CertificateException, IOException {
        X509Certificate cert = X509CertificateUtils.getX509Certificate(buff);
        CertificateRO cro = convertToRo(cert);
        if (validate) {
            // first expect the worst
            cro.setInvalid(true);
            cro.setInvalidReason("Certificate is not validated!");
            try {
                checkFullCertificateValidity(cert);
                cro.setInvalid(false);
                cro.setInvalidReason(null);
            } catch (CertificateExpiredException ex) {
                cro.setInvalidReason("Certificate is expired!");
            } catch (CertificateNotYetValidException ex) {
                cro.setInvalidReason("Certificate is not yet valid!");
            } catch (CertificateRevokedException ex) {
                cro.setInvalidReason("Certificate is revoked!");
            } catch (CertificateNotTrustedException ex) {
                cro.setInvalidReason("Certificate is not trusted!");
            }
        }
        return cro;
    }

    public void checkFullCertificateValidity(X509Certificate cert) throws CertificateException {
        // test if certificate is valid
        cert.checkValidity();
        // check if certificate or its issuer is on trusted list
        // check only issuer because using Client-cert header we do not have whole chain.
        // if the truststore is empty then truststore validation is ignored
        // backward compatibility
        if (!normalizedTrustedList.isEmpty() && !(isSubjectOnTrustedList(cert.getSubjectX500Principal().getName())
                || isSubjectOnTrustedList(cert.getIssuerDN().getName()))) {

            throw new CertificateNotTrustedException("Certificate is not trusted!");
        }
        // check CRL - it is using only HTTP or https
        crlVerifierService.verifyCertificateCRLs(cert);
    }

    public void checkFullCertificateValidity(CertificateRO cert) throws CertificateException {
        // trust data in database
        Date currentDate = Calendar.getInstance().getTime();
        if (cert.getValidFrom() != null && currentDate.before(cert.getValidFrom())) {
            throw new CertificateNotYetValidException("Certificate: " + cert.getCertificateId() + " is valid from: "
                    + dateFormatLocal.get().format(cert.getValidFrom()) + ".");

        }
        if (cert.getValidTo() != null && currentDate.after(cert.getValidTo())) {
            throw new CertificateExpiredException("Certificate: " + cert.getCertificateId() + " was valid to: "
                    + dateFormatLocal.get().format(cert.getValidTo()) + ".");
        }
        // if trusted list is not empty and exists issuer or subject then validate
        if (!normalizedTrustedList.isEmpty() && (
                !StringUtils.isBlank(cert.getIssuer()) || !StringUtils.isBlank(cert.getSubject()))) {

            if (!isSubjectOnTrustedList(cert.getIssuer()) && !isSubjectOnTrustedList(cert.getSubject())) {
                throw new CertificateNotTrustedException("Certificate is not trusted!");
            }

        }

        // Check crl list
        String url = cert.getCrlUrl();
        if (!StringUtils.isBlank(url) && !StringUtils.isBlank(cert.getSerialNumber())) {
            try {
                crlVerifierService.verifyCertificateCRLs(cert.getSerialNumber(), url);
            } catch (CertificateRevokedException ex) {
                String msg = "Certificate: '" + cert.getCertificateId() + "'" +
                        " is revoked!";
                LOG.securityWarn(SMPMessageCode.SEC_USER_CERT_INVALID, cert.getCertificateId(), msg, ex);
                throw new CertificateException(msg);
            } catch (Throwable th) {
                String msg = "Error occurred while validating CRL for certificate!";
                LOG.error(SMPLogger.SECURITY_MARKER, msg + "Err: " + ExceptionUtils.getRootCauseMessage(th), th);
                throw new CertificateException(msg);
            }
        }
    }

    boolean isTruststoreChanged() {
        File file = getTruststoreFile();
        return !Objects.equals(lastUpdateTrustStoreFile, file) ||
                file != null && file.lastModified() != lastUpdateTrustoreFileTime;
    }

    public File getTruststoreFile() {
        return configurationService.getTruststoreFile();
    }


    public TrustManager[] getTrustManagers() {
        // check if keystore is changes
        if (isTruststoreChanged()) {
            refreshData();
        }
        return trustManagers;
    }


    private KeyStore loadTruststore(File truststoreFile) {

        if (truststoreFile == null) {
            LOG.error("Truststore file is not configured! Update SMP configuration!");
            return null;
        }
        // Load the KeyStore.
        if (!truststoreFile.exists()) {
            LOG.error("Truststore file '{}' does not exists!", truststoreFile.getAbsolutePath());
            return null;
        }
        String token = configurationService.getTruststoreCredentialToken();
        if (StringUtils.isEmpty(token)) {
            LOG.error("Truststore credentials are missing in configuration table for truststore: '{}' !", truststoreFile.getName());
            return null;
        }

        KeyStore truststore = null;
        try (InputStream truststoreInputStream = new FileInputStream(truststoreFile)) {
            truststore = KeyStore.getInstance("JKS");
            truststore.load(truststoreInputStream, token.toCharArray());
        } catch (Exception exception) {
            LOG.error("Could not load truststore:"
                    + truststoreFile + " Error: " + ExceptionUtils.getRootCauseMessage(exception), exception);
        }
        return truststore;
    }


    public boolean isSubjectOnTrustedList(String subject) {

        // do not validate if list is empty
        if (!useTrustStore() || normalizedTrustedList.isEmpty()) {
            return true;
        }

        if (StringUtils.isBlank(subject)) {
            LOG.warn("Null or empty subject!");
            return false;
        }
        String normSubj = DistinguishedNamesCodingUtil.normalizeDN(subject,
                DistinguishedNamesCodingUtil.getCommonAttributesDN());
        if (isTruststoreChanged()) {
            refreshData();
        }
        return normalizedTrustedList.contains(normSubj);
    }

    public List<String> getNormalizedTrustedList() {
        return normalizedTrustedList;
    }

    /**
     * Delete keys smp keystore
     *
     * @param alias
     */
    public void deleteCertificate(String alias) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {

        KeyStore keyStore = loadTruststore(getTruststoreFile());
        if (keyStore != null) {
            keyStore.deleteEntry(alias);
            // store keystore
            storeTruststore(keyStore);
            refreshData();
        }
    }

    public String addCertificate(String alias, X509Certificate certificate) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {

        KeyStore truststore = loadTruststore(getTruststoreFile());

        if (truststore != null) {
            String aliasPrivate = StringUtils.isBlank(alias) ? createAliasFromCert(certificate, truststore) : alias.trim();

            if (truststore.containsAlias(aliasPrivate)) {
                int i = 1;
                while (truststore.containsAlias(aliasPrivate + "_" + i)) {
                    i++;
                }
                aliasPrivate = aliasPrivate + "_" + i;
            }

            truststore.setCertificateEntry(aliasPrivate, certificate);
            // store truststore
            storeTruststore(truststore);
            refreshData();
            return aliasPrivate;
        }
        return null;
    }

    public KeyStore getTrustStore() {
        return trustStore;
    }

    public String createAliasFromCert(X509Certificate x509cert, KeyStore truststore) {


        String dn = x509cert.getSubjectX500Principal().getName();
        String alias = null;
        try {

            LdapName ldapDN = new LdapName(dn);
            Rdn cn = null;
            for (Rdn rdn : ldapDN.getRdns()) {

                if (rdn.size() > 1) {
                    NamingEnumeration enr = rdn.toAttributes().getAll();
                    while (enr.hasMore()) {
                        Object mvRDn = enr.next();
                        if (mvRDn instanceof BasicAttribute) {
                            BasicAttribute ba = (BasicAttribute) mvRDn;
                            if (Objects.equals("CN", ba.getID())) {
                                cn = new Rdn(ba.getID(), ba.get());
                                break;
                            }
                        }
                    }

                } else if (Objects.equals("CN", rdn.getType())) {
                    alias = rdn.getValue().toString().trim();
                    break;
                }
                if (cn != null) {
                    alias = cn.getValue().toString().trim();
                    break;
                }
            }

        } catch (NamingException e) {
            LOG.error("Can not parse certificate subject: " + dn);
        }
        alias = StringUtils.isEmpty(alias) ? UUID.randomUUID().toString() : alias;

        try {
            if (truststore != null && truststore.containsAlias(alias)) {
                int iVal = 1;
                while (truststore.containsAlias(alias + "_" + iVal)) {
                    iVal++;
                }
                alias = alias + "_" + iVal;
            }
        } catch (KeyStoreException e) {
            LOG.error("Error occured while reading truststore for validating alias: " + alias, e);
        }

        return alias;

    }


    /**
     * Store keystore
     *
     * @param keyStore to store
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    private void storeTruststore(KeyStore keyStore) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        File keystoreFilePath = getTruststoreFile();
        String token = configurationService.getTruststoreCredentialToken();
        try (FileOutputStream fos = new FileOutputStream(keystoreFilePath)) {
            keyStore.store(fos, token.toCharArray());
        }
    }

    public List<CertificateRO> getCertificateROEntriesList() {

        if (isTruststoreChanged()) {
            refreshData();
            // refresh also the list
            certificateROList.clear();
        }
        if (certificateROList.isEmpty() && !truststoreCertificates.isEmpty()) {
            truststoreCertificates.forEach((alias, cert) -> {
                CertificateRO certificateRO = convertToRo(cert);
                certificateRO.setAlias(alias);
                certificateROList.add(certificateRO);
            });
        }

        return certificateROList;
    }

    public CertificateRO convertToRo(X509Certificate d) {
        return conversionService.convert(d, CertificateRO.class);
    }

}
