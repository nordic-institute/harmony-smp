package eu.europa.ec.edelivery.smp.config.init;

import eu.europa.ec.edelivery.security.utils.KeystoreBuilder;
import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.security.KeyStore;

/**
 * DomiSMP uses various keystores/truststores to store the keys and certifictes. This configuration builder helps to build
 * the keystores for various DomiSMP properties.
 * <p>
 * The Keystore configuration builder generates and updates SMP keystore/truststore properties.
 */
public class SMPKeystoreConfBuilder {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPKeystoreConfBuilder.class);

    SMPPropertyEnum propertySecurityToken;
    SMPPropertyEnum propertyTruststoreDecToken;
    SMPPropertyEnum propertyType;
    SMPPropertyEnum propertyFileName;
    File outputFolder;
    SecurityUtils.Secret secret;
    SMPConfigurationInitializer initPropertyService;
    String[] subjectChain;
    String[] aliasList;
    boolean testMode;

    private SMPKeystoreConfBuilder() {
    }

    public static SMPKeystoreConfBuilder create() {
        return new SMPKeystoreConfBuilder();
    }

    public SMPKeystoreConfBuilder propertySecurityToken(SMPPropertyEnum propertySecurityToken) {
        this.propertySecurityToken = propertySecurityToken;
        return this;
    }

    public SMPKeystoreConfBuilder propertyTruststoreDecToken(SMPPropertyEnum propertyTruststoreDecToken) {
        this.propertyTruststoreDecToken = propertyTruststoreDecToken;
        return this;
    }

    public SMPKeystoreConfBuilder propertyType(SMPPropertyEnum propertyType) {
        this.propertyType = propertyType;
        return this;
    }

    public SMPKeystoreConfBuilder propertyFilename(SMPPropertyEnum propertyFileName) {
        this.propertyFileName = propertyFileName;
        return this;
    }

    public SMPKeystoreConfBuilder outputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
        return this;
    }

    public SMPKeystoreConfBuilder secret(SecurityUtils.Secret secret) {
        this.secret = secret;
        return this;
    }

    public SMPKeystoreConfBuilder subjectChain(String... subjectChain) {
        this.subjectChain = subjectChain;
        return this;
    }

    public SMPKeystoreConfBuilder aliasList(String... aliasList) {
        this.aliasList = aliasList;
        return this;
    }

    public SMPKeystoreConfBuilder initPropertyService(SMPConfigurationInitializer initPropertyService) {
        this.initPropertyService = initPropertyService;
        return this;
    }

    public SMPKeystoreConfBuilder testMode(boolean testMode) {
        this.testMode = testMode;
        return this;
    }

    public KeyStore build() {
        String initFilename = initPropertyService.getApplicationInitPropertyValue(propertyFileName);
        LOG.info("Start initialization of the keystore [{}].", initFilename);
        String encTrustEncToken;
        String initSecurityToken = initPropertyService.getApplicationInitPropertyValue(propertySecurityToken);
        if (StringUtils.isNotBlank(initSecurityToken)) {
            encTrustEncToken = SecurityUtils.encryptWrappedToken(secret, initSecurityToken);
        } else {
            LOG.info("generate new truststore token");
            String trustToken = SecurityUtils.generateAuthenticationToken(testMode);
            initPropertyService.storeProperty(propertyTruststoreDecToken, trustToken);
            encTrustEncToken = SecurityUtils.encrypt(secret, trustToken);
        }

        LOG.info("Store truststore security token to database");
        // store token to database
        String trustToken = SecurityUtils.decrypt(secret, encTrustEncToken);
        String keystoreType = initPropertyService.getApplicationInitPropertyValue(propertyType);

        LOG.info("Generate new truststore to file [{}] in folder [{}]!", initFilename, outputFolder.getAbsolutePath());
        KeystoreBuilder.KeystoreData result = KeystoreBuilder.create()
                .keystoreType(keystoreType)
                .filename(initFilename)
                .folder(outputFolder)
                .secretToken(trustToken)
                .subjectChain(subjectChain)
                .aliasList(subjectChain)
                .testMode(testMode).build();

        // store file to database
        initPropertyService.storeProperty(propertyType, result.getKeystoreType());
        initPropertyService.storeProperty(propertySecurityToken, encTrustEncToken);
        initPropertyService.storeProperty(propertyFileName, result.getFilename());
        return result.getResultKeystore();
    }

    public interface PropertySerializer {
        void storeProperty(SMPPropertyEnum property, String value);
    }
}
