package eu.domibus.security.module;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.log4j.Logger;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.policy.model.CryptoConfig;
import org.apache.rampart.policy.model.RampartConfig;
import org.apache.ws.security.components.crypto.Merlin;
import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.JNDIUtil;
import eu.domibus.security.config.generated.PrivateKeystore;
import eu.domibus.security.config.generated.PublicKeystore;
import eu.domibus.security.config.generated.Security;
import eu.domibus.security.config.generated.SecurityConfig;
import eu.domibus.security.config.model.RemoteSecurityConfig;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration extends SecurityUtil {
    private final static Logger log = Logger.getLogger(Configuration.class);
    private static Map<String, RemoteSecurityConfig> remoteSecurities = null;
    private static long securityConfigFileLastModified;

    public static void loadSecurityConfiguration() {
        loadSecurityConfigFileIfModified();
    }

    static void loadSecurityConfigFileIfModified() {
        loadSecurityConfigFileIfModified(JNDIUtil.getStringEnvironmentParameter(Constants.CONFIG_FILE_PARAMETER));
    }

    static void loadSecurityConfigFileIfModified(String securityFilePath) {
        loadSecurityConfigFileIfModified(new File(securityFilePath), JNDIUtil.getStringEnvironmentParameter(Constants.POLICIES_FOLDER_PARAMETER));
    }

    public static void loadSecurityConfigFileIfModified(File securityFile, String policyPath) {
        final File securityConfigFile = securityFile;
        final long newSecurityConfigFileLastModified = securityConfigFile.lastModified();


        if (securityConfigFileLastModified != newSecurityConfigFileLastModified) {
            Map<String, RemoteSecurityConfig> newSecurities = new HashMap<String, RemoteSecurityConfig>();

            try {
                final JAXBContext jc = JAXBContext.newInstance("eu.domibus.security.config.generated");
                final Unmarshaller u = jc.createUnmarshaller();
                SecurityConfig securityConfig = (SecurityConfig) u.unmarshal(securityConfigFile);
                for (final Security securityEntry : securityConfig.getSecurities().getSecurity()) {
                    final Policy policy = loadPolicy(policyPath, securityEntry.getPolicyFile());
                    attachRampartConfig(policy, securityEntry, securityConfig);
                    newSecurities.put(securityEntry.getName(), new RemoteSecurityConfig(securityEntry, securityConfig.getKeystores().getPublicKeystore(), policy));
                    log.debug(securityEntry.getName() + " loaded.");
                }
                remoteSecurities = newSecurities;

            } catch (JAXBException e) {
                throw new ConfigurationException(e);
            }
        }
        log.debug("Security Config file " + securityConfigFile.getName() + " has been loaded");
        securityConfigFileLastModified = newSecurityConfigFileLastModified;
    }

    private static void attachRampartConfig(final Policy policy, final Security securityEntry, final SecurityConfig securityConfig) {
        final PrivateKeystore prK = securityConfig.getKeystores().getPrivateKeystore();
        final PublicKeystore puK = securityConfig.getKeystores().getPublicKeystore();
        final RampartConfig rc = new RampartConfig();
        rc.setPwCbClass("eu.domibus.security.handlers.PWCBHandler");

        rc.setEncryptionUser(securityEntry.getRemoteAlias());

        rc.setUser(prK.getLocalAlias());

        final CryptoConfig sigCryptoConfig = new CryptoConfig();
        final Properties sigProps = new Properties();
        sigProps.setProperty(Merlin.KEYSTORE_FILE, prK.getFile());
        sigProps.setProperty(Merlin.KEYSTORE_TYPE, prK.getStoreType());
        sigProps.setProperty(Merlin.KEYSTORE_PASSWORD, prK.getStorepwd());

        sigProps.setProperty(Merlin.TRUSTSTORE_FILE, puK.getFile());
        sigProps.setProperty(Merlin.TRUSTSTORE_TYPE, puK.getStoreType());
        sigProps.setProperty(Merlin.TRUSTSTORE_PASSWORD, puK.getStorepwd());

        sigCryptoConfig.setProvider("org.apache.ws.security.components.crypto.Merlin");
        sigCryptoConfig.setProp(sigProps);


        final CryptoConfig encCryptoConfig = new CryptoConfig();
        final Properties encProps = new Properties();
        encProps.setProperty(Merlin.KEYSTORE_FILE, prK.getFile());
        encProps.setProperty(Merlin.KEYSTORE_TYPE, prK.getStoreType());
        encProps.setProperty(Merlin.KEYSTORE_PASSWORD, prK.getStorepwd());

        encProps.setProperty(Merlin.TRUSTSTORE_FILE, puK.getFile());
        encProps.setProperty(Merlin.TRUSTSTORE_TYPE, puK.getStoreType());
        encProps.setProperty(Merlin.TRUSTSTORE_PASSWORD, puK.getStorepwd());

        encCryptoConfig.setProvider("org.apache.ws.security.components.crypto.Merlin");
        encCryptoConfig.setProp(encProps);

        rc.setSigCryptoConfig(sigCryptoConfig);
        rc.setEncrCryptoConfig(encCryptoConfig);

        policy.addAssertion(rc);


    }

    public static RemoteSecurityConfig getRemoteSecurity(final String securityName) {
        if (remoteSecurities == null) {
            throw new NullPointerException("List of RemoteSecurities is null");
        }

        final RemoteSecurityConfig rsc = remoteSecurities.get(securityName);
        if (rsc == null) {
            throw new SecurityException("RemoteSecurityConfig for " + securityName + " not found");
        }

        return remoteSecurities.get(securityName);
    }


    /**
     * Load policy from default location. the location is determined via jndi with the following value {@link eu.domibus.security.module.Constants#POLICIES_FOLDER_PARAMETER}
     *
     * @param policyFile
     * @return
     */
    private static Policy loadPolicy(final String policyFile) {
        return loadPolicy(JNDIUtil.getStringEnvironmentParameter(Constants.POLICIES_FOLDER_PARAMETER), policyFile);
    }

    /**
     * Load policy with name policyFile from path.
     *
     * @param path
     * @param policyFile
     * @return
     */
    private static Policy loadPolicy(final String path, final String policyFile) {

        Policy policy = null;
        {
            final StAXOMBuilder builder;
            try {
                builder = new StAXOMBuilder(path + "/" + policyFile);
                policy = PolicyEngine.getPolicy(builder.getDocumentElement());
            } catch (FileNotFoundException e) {
                throw new ConfigurationException(e);
            } catch (XMLStreamException e) {
                throw new ConfigurationException(e);
            }
        }
        return policy;
    }

}