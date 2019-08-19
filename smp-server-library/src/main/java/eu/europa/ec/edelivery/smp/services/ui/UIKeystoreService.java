package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import static java.util.Collections.list;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class UIKeystoreService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIKeystoreService.class);

    @Autowired
    SecurityUtilsServices securityUtilsServices;

    @Autowired
    private ConversionService conversionService;


    @Value("${smp.keystore.password}")
    private String smpKeyStorePasswordEncrypted;

    @Value("${smp.keystore.filename}")
    private String smpKeyStoreFilename;

    @Value("${configuration.dir}")
    private String configurationDir;

    @Value("${encryption.key.filename}")
    private String encryptionFilename;

    private String smpKeyStorePasswordDecrypted;

    private Map<String, Key> keystoreKeys;
    private Map<String, X509Certificate> keystoreCertificates;
    List<CertificateRO> certificateROList = new ArrayList<>();

    private KeyManager[] keyManagers;

    private long lastUpdateKeystoreFileTime = 0;
    private File lastUpdateKeystoreFile = null;

    @PostConstruct
    public void init() {
        keystoreKeys = new HashMap();
        keystoreCertificates = new HashMap();
        setupJCEProvider();
        refreshData();
    }

    private void setupJCEProvider() {
        Provider[] providerList = Security.getProviders();
        if (providerList == null || providerList.length <= 0 || !(providerList[0] instanceof BouncyCastleProvider)) {
            Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
        }
    }

    public void validateConfigurationData() {

        // validate configuration data
        if (StringUtils.isBlank(configurationDir)) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "SMP keystore folder is not set or is empty value! Set the following property in database: '" + SMPPropertyEnum.CONFIGURATION_DIR.getProperty() + "'.");
        }

        if (StringUtils.isBlank(encryptionFilename)) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Encryption filename is not set or is empty value! Set the following property in database: '" + SMPPropertyEnum.ENCRYPTION_FILENAME.getProperty() + "'.");
        }

        if (StringUtils.isBlank(smpKeyStoreFilename)) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "SMP keystore filename is not set or is empty value! Set the following property in database: '" + SMPPropertyEnum.KEYSTORE_FILENAME.getProperty() + "'.");
        }

        if (StringUtils.isBlank(smpKeyStorePasswordEncrypted)) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Encrypted keystore password not exists in database : '" + SMPPropertyEnum.KEYSTORE_PASSWORD.getProperty() + "'.");
        }

        File file = new File(configurationDir + File.separator + encryptionFilename);
        if (!file.exists()) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Encryption key file: '" + file.getAbsolutePath() + "' does not exists!");
        }

        File keystoreFile = getKeyStoreFile();
        if (!keystoreFile.exists()) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Keystore file: '" + keystoreFile.getAbsolutePath() + "' does not exists!");
        }
        // decrypt password
        smpKeyStorePasswordDecrypted = securityUtilsServices.decrypt(file, smpKeyStorePasswordEncrypted);

    }

    /**
     * Method  validates the configuration properties and refresh the
     * cached data
     */
    public void refreshData() {
        try {
            validateConfigurationData();
        } catch (SMPRuntimeException ex) {
            LOG.error("Keystore was not (re)loaded. Invalid configuration: " + ex.getMessage());
            return;
        }

        // load keystore
        File keystoreFile  = getKeyStoreFile();
        KeyStore keyStore = loadKeystore(keystoreFile);
        if (keyStore == null) {
            LOG.error("Keystore: '"+keystoreFile.getAbsolutePath()+"' is not loaded! Check the keystore and the configuration!");
            return;
        }
        // init key managers for TLS
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, smpKeyStorePasswordDecrypted.toCharArray());
            keyManagers = kmf.getKeyManagers();
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException exception) {
            LOG.error("Error occurred while initialize  keyManagers : " + keystoreFile.getAbsolutePath() + " Error: " + ExceptionUtils.getRootCauseMessage(exception), exception);
            return;
        }

        // load keys for signature
        try {

            Map<String, Key> hmKeys = new HashMap<>();
            Map<String, X509Certificate> hmCertificates = new HashMap<>();

            List<String> aliases= list(keyStore.aliases());
            for (String alias : aliases) {
                loadKeyAndCert(keyStore, alias, hmKeys,hmCertificates );
            }
            // setup new values
            keystoreKeys.clear();
            keystoreCertificates.clear();


            keystoreKeys.putAll(hmKeys);
            keystoreCertificates.putAll(hmCertificates);

        } catch (Exception exception) {
            LOG.error("Could not load signing certificate amd private keys Error: " + ExceptionUtils.getRootCauseMessage(exception), exception);
            return;
        }
        lastUpdateKeystoreFileTime = keystoreFile.lastModified();
        lastUpdateKeystoreFile = keystoreFile;
        certificateROList.clear();
    }


    boolean isKeyStoreChanged() {
        File  file = getKeyStoreFile();
        return  !Objects.equals(lastUpdateKeystoreFile, file ) ||  file.lastModified() != lastUpdateKeystoreFileTime;
    }


    public File getKeyStoreFile() {
        return new File(configurationDir + File.separator + smpKeyStoreFilename);
    }

    public KeyManager[] getKeyManagers() {
        // check if keystore is changes
        if (isKeyStoreChanged()) {
            refreshData();
        }
        return keyManagers;
    }


    private KeyStore loadKeystore(File keyStoreFile) {
        // Load the KeyStore.
        if (!keyStoreFile.exists()) {
            LOG.error("Keystore file '{}' does not exists!", keyStoreFile.getAbsolutePath());
            return null;
        }

        KeyStore keyStore = null;
        try (InputStream keystoreInputStream = new FileInputStream(keyStoreFile)) {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keystoreInputStream, smpKeyStorePasswordDecrypted.toCharArray());
        } catch (Exception exception) {
            LOG.error("Could not load signing certificate with private key from keystore file:"
                    + keyStoreFile + " Error: " + ExceptionUtils.getRootCauseMessage(exception), exception);
        }
        return keyStore;
    }

    private void loadKeyAndCert(KeyStore keyStore, String alias, Map<String, Key> hmKeys, Map<String, X509Certificate> hmCertificates) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        Key key = keyStore.getKey(alias, smpKeyStorePasswordDecrypted.toCharArray());
        Certificate certificate = keyStore.getCertificate(alias);
        if (key == null || certificate == null || !(certificate instanceof X509Certificate)) {
            LOG.warn("Wrong entry type found in keystore, only certificates with keypair are accepted, entry alias: "
                    + alias + ". Entry is ignored");
            return;
        }
        // add to cache
        hmKeys.put(alias, key);
        hmCertificates.put(alias, (X509Certificate) certificate);;
    }


    public List<CertificateRO> getKeystoreEntriesList() {

        if (isKeyStoreChanged()) {
            refreshData();
            // refresh also the list
            certificateROList.clear();
        }
        if (certificateROList.isEmpty() && !keystoreCertificates.isEmpty()){
            keystoreCertificates.forEach( (alias, cert)-> {
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

    public Key getKey(String keyAlias) {
        if (keystoreKeys.size() == 1) {
            // for backward compatibility...
            // don't care about configured alias in single-domain setup
            // and return the only key
            LOG.warn("Returning the only key in keystore regardless the configuration");
            return keystoreKeys.values().iterator().next();
        }

        if (isBlank(keyAlias) || !keystoreKeys.containsKey(keyAlias)) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Wrong configuration, missing key pair from keystore or wrong alias: " + keyAlias);
        }

        return keystoreKeys.get(keyAlias);
    }

    public X509Certificate getCert(String certAlias) {

        if (isKeyStoreChanged()) {
            refreshData();
        }

        if (keystoreCertificates.size() == 1) {
            // for backward compatibility...
            // don't care about configured alias in single-domain setup
            // and return the only key
            LOG.warn("Returning the only certificate in keystore regardless the configuration");
            return keystoreCertificates.values().iterator().next();
        }
        if (isBlank(certAlias) || !keystoreCertificates.containsKey(certAlias)) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Wrong configuration, missing key pair from keystore or wrong alias: " + certAlias);
        }
        return keystoreCertificates.get(certAlias);
    }

    /**
     * Import keys smp keystore
     *
     * @param newKeystore
     * @param password
     */
    public void importKeys(KeyStore newKeystore, String password) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {


        KeyStore keyStore = loadKeystore(getKeyStoreFile());
        if (keyStore != null) {
            securityUtilsServices.mergeKeystore(keyStore, smpKeyStorePasswordDecrypted, newKeystore, password);
            // store keystore
            storeKeystore(keyStore);
            // refresh
            refreshData();
        }
    }

    /**
     * Delete keys smp keystore
     *
     * @param alias
     */
    public void deleteKey(String alias) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {

        KeyStore keyStore = loadKeystore(getKeyStoreFile());
        if (keyStore != null) {
            keyStore.deleteEntry(alias);
            // store keystore
            storeKeystore(keyStore);
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
    private void storeKeystore(KeyStore keyStore) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        File keystoreFilePath = new File(configurationDir + File.separator + smpKeyStoreFilename);
        try (FileOutputStream fos = new FileOutputStream(keystoreFilePath)) {
            keyStore.store(fos, smpKeyStorePasswordDecrypted.toCharArray());
        }
    }
}
