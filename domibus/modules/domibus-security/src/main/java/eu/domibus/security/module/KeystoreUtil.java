package eu.domibus.security.module;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class KeystoreUtil {
    private static final Logger s_aLogger = Logger.getLogger(KeystoreUtil.class);
    private KeyStore keyStore = null;
    private String keystorePath;
    private String keystorePwd;


    public KeystoreUtil(String keystorePath, String keystorePwd) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        this.keystorePath = keystorePath;
        this.keystorePwd = keystorePwd;

        s_aLogger.info("The keystore file is " + keystorePath);

        String[] keystoreTypes = {"pkcs12", "CaseExactJKS", "jceks"};

        File inFile = new File(keystorePath);
        FileInputStream inStream = null;
        try {
            if (inFile.exists()) {
                inStream = new FileInputStream(inFile);
                boolean success = true;
                for (int i = 0; i < keystoreTypes.length; i++) {
                    success = true;
                    if (keystoreTypes[i].equals("pkcs12"))
                        keyStore = KeyStore.getInstance(keystoreTypes[i], "BC");  // BouncyCastle implementation of a keystore allows for the PKCS12 keystore to accept trusted certificates. The default Java provider doesn't.
                    else
                        keyStore = KeyStore.getInstance(keystoreTypes[i]);      //but BouncyCastle doesn't have an implementation for jks! so we try with the default implementation
                    try {
                        keyStore.load(inStream, keystorePwd.toCharArray());
                    } catch (IOException e) {
                        s_aLogger.info("Unable to load the keystore using type " + keystoreTypes[i] + ". Trying another one...");
                        success = false;
                        inStream.close();
                        inStream = new FileInputStream(inFile);  //we close and reopen the stream so it can be read again
                    }
                    if (success) {
                        s_aLogger.info("The keystore has been loaded with the type " + keystoreTypes[i]);
                        break;
                    }
                }
                if (!success) {
                    s_aLogger.error("Unable to load Keystore because the type is not compatible with any of the following: " + Arrays.asList(keystoreTypes).toString());
                    throw new Exception("Couldn't load the keystore");
                }

            }
        } finally {
            if (inStream != null)
                inStream.close();
        }

    }

    /**
     * Creates a new certificate in the AS4 endpoint's truststore
     *
     * @return
     */
    public void installNewPartnerCertificate(X509Certificate cert, String alias) throws Exception {
        keyStore.setCertificateEntry(alias, cert);
        FileOutputStream output = new FileOutputStream(keystorePath);
        keyStore.store(output, keystorePwd.toCharArray());
        output.close();
    }

    public static String extractCN(X509Certificate cert) {
        Principal principal = cert.getSubjectDN();
        if (principal == null)
            principal = cert.getSubjectX500Principal();

        String commonName = null;
        String[] names = principal.getName().split(",");
        for (String s : names)
            if (s.trim().startsWith("CN="))
                commonName = s.trim().substring(3);

        return commonName.trim();
    }
}
