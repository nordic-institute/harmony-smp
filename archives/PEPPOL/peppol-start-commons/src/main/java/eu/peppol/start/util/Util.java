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
package eu.peppol.start.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility Class.
 *
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)
 */
public class Util {

    /**
     * Create a Certificate X509 from String.
     * @param   certEntry Certificate as String.
     * @return Certificate X509.
     */
    public static X509Certificate generateX509Certificate(String certEntry) {

        InputStream in = null;
        X509Certificate cert = null;
        try {
            byte[] certEntryBytes = certEntry.getBytes();
            in = new ByteArrayInputStream(certEntryBytes);
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

            cert = (X509Certificate) certFactory.generateCertificate(in);
        } catch (CertificateException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(Util.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            }
        }
        return cert;
    }

    /**
     * Add begin and end to a certificate as string.
     * @param certEntry     Certificate as String
     * @return Certificate as String.
     */
    public static String completeCertificateEntry(String certEntry) {

        if (certEntry != null) {
            certEntry = "-----BEGIN CERTIFICATE-----\n"
                    + certEntry
                    + "\n-----END CERTIFICATE-----";
        }
        return certEntry;
    }

    /**
     * Generates a MD5 hash given a String value.
     *
     * @param value Input String.
     *
     * @return Generated MD5 hash.
     *
     * @throws NoSuchAlgorithmException Thrown if the MD5 algorithm is not available in this environment.
     * @throws UnsupportedEncodingException Thrown if the iso-8859-1 character encoding is not supported.
     */
    public static String calculateMD5(String value) throws
            NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(value.getBytes("iso-8859-1"), 0, value.length());
        byte[] messageDigest = algorithm.digest();

        StringBuilder hexStrig = new StringBuilder();
        String hex;

        for (byte b : messageDigest) {
            hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexStrig.append('0');
            }

            hexStrig.append(hex);
        }
        return hexStrig.toString();
    }

    /**
     * Transforms the given String into a org.w3c.dom.Document object.
     *
     * @param content String which will be transformed.
     *
     * @return Parsed Document.
     */
    public static Document parseStringtoDocument(String content) {

        Document document = null;

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            document = docBuilder.parse(new InputSource(new StringReader(content)));
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (SAXException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return document;
    }
}
