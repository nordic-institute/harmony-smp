/*
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.peppol.start.cert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;


import com.sun.xml.wss.impl.callback.KeyStoreCallback;
import com.sun.xml.wss.impl.callback.PrivateKeyCallback;
import eu.peppol.start.util.Configuration;
import org.apache.log4j.Logger;

/**
 * This class was developed for test purposes.
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)
 */
public class KeyStoreCallbackHandler implements CallbackHandler {

    /** Represents the logger*/
    private static final Logger log = Logger.getLogger(KeyStoreCallbackHandler.class);

    /**
     * Method to handle a callback.
     * @param callbacks as an array.
     * @throws IOException
     * @throws UnsupportedCallbackException
     */
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        log.info("Handling keystore on: " + KeyStoreCallbackHandler.class.getName());
        Configuration conf = Configuration.getInstance();
        final String keystorePath = conf.getProperty("server.keystore");
        final String keyStorePassword = conf.getProperty("server.Keystore.password");
        final String keyStoreAlias = conf.getProperty("server.keystore.alias");

        for (final Callback callback : callbacks) {
            if (callback instanceof KeyStoreCallback) {
                try {
                    final KeyStoreCallback keyStoreCallBack = (KeyStoreCallback)callback;
                    keyStoreCallBack.setKeystore(getKeystore(keystorePath, keyStorePassword));
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
            } else if (callback instanceof PrivateKeyCallback) {
                try {
                    final KeyStore keystore = getKeystore(keystorePath, keyStorePassword);
                    PrivateKey privateKey = null;
                    privateKey = (PrivateKey) keystore.getKey(keyStoreAlias, keyStorePassword.toCharArray());
                    ((PrivateKeyCallback)callback).setKey(privateKey);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns a keystore based on the parameters given
     * @param trustStoreName as the name of the truststore.
     * @param keyStorePassword that represents the password for certificate.
     * @return keystore object type.
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws NoSuchProviderException
     */
    public static KeyStore getKeystore(final String trustStoreName, final String keyStorePassword) throws KeyStoreException,
            IOException,
            NoSuchAlgorithmException,
            CertificateException,
            NoSuchProviderException {
        return loadStore(trustStoreName, keyStorePassword);
    }

    /**
     * Method to access the content of the keystore.
     * @param storeFileName the file that contains the keystore.
     * @param keyStorePassword the password for keystore.
     * @return Keystore with open data.
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws NoSuchProviderException
     */
    private static KeyStore loadStore(final String storeFileName, final String keyStorePassword) throws KeyStoreException,
            IOException,
            NoSuchAlgorithmException,
            CertificateException,
            NoSuchProviderException {
        final KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(new File(storeFileName)), keyStorePassword.toCharArray());
        return ks;
    }
}
