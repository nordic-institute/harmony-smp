package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SecurityUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;

import static java.util.Collections.list;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class UIKeystoreService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIKeystoreService.class);

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


    @PostConstruct
    public void init() {
        keystoreKeys = new HashMap();
        keystoreCertificates = new HashMap();
        refreshData();
    }

    public void refreshData(){
        keystoreKeys.clear();
        keystoreCertificates.clear();

        LOG.info("initialize from configuration folder:"+configurationDir
                        +", enc file: "+encryptionFilename+", keystore " +  smpKeyStoreFilename);
        if (configurationDir==null || encryptionFilename==null){
            LOG.warn("Configuration folder and/or encryption filename are not set in database!");
            return;
        }

        String encFilePath = configurationDir + File.separator +  encryptionFilename;
         File file = new File(configurationDir + File.separator +  encryptionFilename);
         File keystoreFilePath = new File(configurationDir + File.separator + smpKeyStoreFilename );
         if (!file.exists()){
             LOG.error("Encryption key file '{}' does not exists!", file.getAbsolutePath());
            return;
        }
        if (!keystoreFilePath.exists()){
            LOG.error("Keystore file '{}' does not exists!", keystoreFilePath.getAbsolutePath());
            return;
        }
        
        try {
            smpKeyStorePasswordDecrypted = SecurityUtils.decrypt(file, smpKeyStorePasswordEncrypted);
        } catch (SMPRuntimeException exception) {
            LOG.error("Error occurred while using encryption key: " + file.getAbsolutePath() + " Error: " + ExceptionUtils.getRootCauseMessage(exception), exception);
            return;
        }

        // Load the KeyStore and get the signing key and certificate.
        try (InputStream keystoreInputStream = new FileInputStream(keystoreFilePath)) {

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keystoreInputStream, smpKeyStorePasswordDecrypted.toCharArray());


            for (String alias : list(keyStore.aliases())) {
                loadKeyAndCert(keyStore, alias);
            }
        } catch (Exception exception) {
            LOG.error("Could not load signing certificate with private key from keystore file:"
                    + keystoreFilePath + " Error: "+ ExceptionUtils.getRootCauseMessage(exception), exception);
        }
    }

    private void loadKeyAndCert(KeyStore keyStore, String alias) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        Key key = keyStore.getKey(alias, smpKeyStorePasswordDecrypted.toCharArray());
        Certificate certificate = keyStore.getCertificate(alias);
        if (key == null || certificate == null || !(certificate instanceof X509Certificate)) {
            throw new IllegalStateException("Wrong entry type found in keystore, only certificates with keypair are accepted, entry alias: " + alias);
        }
        keystoreKeys.put(alias, key);
        keystoreCertificates.put(alias, (X509Certificate) certificate);
    }



    public List<CertificateRO> getKeystoreEntriesList() {

        List<CertificateRO>  keystoreList = new ArrayList<>();
            keystoreCertificates.forEach( (alias, crt) -> {
                CertificateRO cro = convertToRo(crt);
                cro.setAlias(alias);
                keystoreList.add(cro);
            });
        return keystoreList;
    }


    public CertificateRO convertToRo(X509Certificate d) {
        return conversionService.convert(d, CertificateRO.class);
    }

    public Key getKey(String keyAlias) {
        if (keystoreKeys.size() == 1) {
            // don't care about configured alias in single-domain setup
            return keystoreKeys.values().iterator().next();
        }
        if (isBlank(keyAlias) || !keystoreKeys.containsKey(keyAlias)) {
            throw new IllegalStateException("Wrong configuration, missing key pair from keystore or wrong alias: " + keyAlias);
        }
        return keystoreKeys.get(keyAlias);
    }

    public X509Certificate getCert(String certAlias) {
        if (keystoreCertificates.size() == 1) {
            // don't care about configured alias in single-domain setup
            return keystoreCertificates.values().iterator().next();
        }
        if (isBlank(certAlias) || !keystoreCertificates.containsKey(certAlias)) {
            throw new IllegalStateException("Wrong configuration, missing key pair from keystore or wrong alias: " + certAlias);
        }
        return keystoreCertificates.get(certAlias);
    }

}
