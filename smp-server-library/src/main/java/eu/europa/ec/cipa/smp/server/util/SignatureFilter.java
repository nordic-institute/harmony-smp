/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.util;

import eu.europa.ec.cipa.smp.server.security.Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * This class adds a XML DSIG to successful GET's for SignedServiceMetadata
 * objects.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Deprecated // TODO: This is no longer a filter, refactor this garbage.
@Component
public final class SignatureFilter {

    private static final Logger s_aLogger = LoggerFactory.getLogger(SignatureFilter.class);

    @Value("${xmldsig.keystore.classpath}")
    private String xmldsigKeystoreClasspath;

    @Value("${xmldsig.keystore.password}")
    private String xmldsigKeystorePassword;

    @Value("${xmldsig.keystore.key.alias}")
    private String xmldsigKeystoreKeyAlias;

    @Value("${xmldsig.keystore.key.password}")
    private String xmldsigKeystoreKeyPassword;

    private KeyStore.PrivateKeyEntry m_aKeyEntry;

    private X509Certificate m_aCert;

    private Signer signer;

    @PostConstruct
    public void init() {
        // Load the KeyStore and get the signing key and certificate.
        try {
            InputStream keystoreInputStream;
            try {
                keystoreInputStream = new FileInputStream(xmldsigKeystoreClasspath);
            } catch (FileNotFoundException e){
                s_aLogger.warn("Could not file keystore file, trying loading from classpath: " + xmldsigKeystoreClasspath);
                keystoreInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmldsigKeystoreClasspath);
            }
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keystoreInputStream, xmldsigKeystorePassword.toCharArray());
            final KeyStore.Entry aEntry = keyStore.getEntry(xmldsigKeystoreKeyAlias,
                    new KeyStore.PasswordProtection(xmldsigKeystoreKeyPassword.toCharArray()));
            if (aEntry == null) {
                // Alias not found
                throw new IllegalStateException("Failed to find key store alias '" +
                        xmldsigKeystoreKeyAlias +
                        "' in keystore with password '" +
                        xmldsigKeystoreKeyPassword +
                        "'. Does the alias exist? Is the password correct?");
            }
            if (!(aEntry instanceof KeyStore.PrivateKeyEntry)) {
                // Not a private key
                throw new IllegalStateException("The keystore alias '" +
                        xmldsigKeystoreKeyAlias +
                        "' was found in keystore with password '" +
                        xmldsigKeystorePassword +
                        "' but it is not a private key! The internal type is " +
                        aEntry.getClass().getName());
            }
            m_aKeyEntry = (KeyStore.PrivateKeyEntry) aEntry;
            m_aCert = (X509Certificate) m_aKeyEntry.getCertificate();
            s_aLogger.info("Signature filter initialized with keystore '" +
                    xmldsigKeystoreClasspath +
                    "' and alias '" +
                    xmldsigKeystoreKeyAlias +
                    "'");

            signer = new Signer(m_aKeyEntry.getPrivateKey(), m_aCert);
        } catch (final Throwable t) {
            s_aLogger.error("Error in constructor of SignatureFilter", t);
            throw new IllegalStateException("Error in constructor of SignatureFilter", t);
        }
    }

    public void sign(Document serviceMetadataDoc) {
        try {
            signer.signXML(serviceMetadataDoc.getDocumentElement());
        } catch (Exception e) {
            throw new RuntimeException("Could not sign serviceMetadata response", e);
        }
    }

}