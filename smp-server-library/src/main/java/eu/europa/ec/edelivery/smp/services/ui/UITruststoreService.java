package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import eu.europa.ec.edelivery.text.DistinguishedNamesCodingUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.list;

@Service
public class UITruststoreService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UITruststoreService.class);

    @Autowired
    private SecurityUtilsServices securityUtilsServices;

    @Autowired
    private ConfigurationService configurationService;


    private List<String> normalizedTrustedList = new ArrayList<>();


    private long lastUpdateTrustoreFileTime = 0;
    private File lastUpdateTrustStoreFile = null;

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

    private boolean useTrustStore() {
        String truststoreFilename = configurationService.getTruststoreFilename();
        return !StringUtils.isBlank(truststoreFilename);
    }


    /**
     * Method  validates the configuration properties and refresh the
     * cached data
     */
    public void refreshData() {
        String truststoreFilename = configurationService.getTruststoreFilename();
        if (!useTrustStore()) {
            LOG.warn("Truststore filename is not set! Cerificates will not be validated by trusted issuers!");
            return;
        }


        // load keystore
        File truststoreFile = getTruststoreFile();
        KeyStore trustStore = loadTruststore(truststoreFile);
        if (trustStore == null) {
            LOG.error("Keystore: '" + truststoreFile.getAbsolutePath() + "' is not loaded! Check the truststore filename" +
                    " and the configuration!");
            return;
        }
        // init key managers for TLS


        // load keys for signature
        List<String> tmpList = new ArrayList<>();

        try {
            List<String> aliases = list(trustStore.aliases());
            for (String alias : aliases) {
                Certificate cert = trustStore.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    String subject = ((X509Certificate) cert).getSubjectX500Principal().getName();
                    subject = DistinguishedNamesCodingUtil.normalizeDN(subject,
                            DistinguishedNamesCodingUtil.getCommonAttributesDN());
                    tmpList.add(subject);
                }

            }
        } catch (Exception exception) {
            LOG.error("Could not load truststore certificates Error: " + ExceptionUtils.getRootCauseMessage(exception), exception);
            return;
        }

        normalizedTrustedList.clear();
        normalizedTrustedList.addAll(tmpList);
    }

    boolean isKeyStoreChanged() {
        File file = getTruststoreFile();
        return !Objects.equals(lastUpdateTrustStoreFile, file) || file.lastModified() != lastUpdateTrustoreFileTime;
    }

    public File getTruststoreFile() {
        return new File(configurationService.getConfigurationFolder() + configurationService.getTruststoreFilename());
    }


    private KeyStore loadTruststore(File truststoreFile) {
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

    public boolean isOnTrustedSubject(String subject) {

        if (!useTrustStore()) {
            return true;
        }

        if (StringUtils.isBlank(subject)) {
            LOG.warn("Null or empty subject!");
            return false;
        }
        String normSubj = DistinguishedNamesCodingUtil.normalizeDN(subject,
                DistinguishedNamesCodingUtil.getCommonAttributesDN());
        if (isKeyStoreChanged()) {
            refreshData();
        }
        return normalizedTrustedList.contains(normSubj);
    }


    /**
     * Delete keys smp keystore
     *
     * @param alias
     */
    public void deleteKey(String alias) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {

        KeyStore keyStore = loadTruststore(getTruststoreFile());
        if (keyStore != null) {
            keyStore.deleteEntry(alias);
            // store keystore
            storeTruststore(keyStore);
            refreshData();
        }
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
}
