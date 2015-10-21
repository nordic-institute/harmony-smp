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
package eu.peppol.start.ocsp;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Alexander Aguirre Julcapoma(alex@alfa1lab.com)
 *          Jose Gorvenia Narvaez(jose@alfa1lab.com)
 */
public class OCSP {

    /**
     * Logger to follow this class behavior.
     */
    private static org.apache.log4j.Logger logger4J =
            org.apache.log4j.Logger.getLogger(OCSP.class);

    /**
     * Sole constructor.  (For invocation by subclass
     * constructors, typically implicit.)
     */
    protected OCSP() {}

    /**
     * Compares a thing to another thing.
     * @param certList
     *          List of Certificates.
     * @param trustedCert
     *          Trusted Certificate.
     * @param responderUrl
     *          URL which responses.
     * @throws Exception
     *          Throws an Exception if check process is not valid.
     * @return true.
     */
    public static boolean check(final List certList,
            final X509Certificate trustedCert,
            final String responderUrl) {

        boolean valid = false;

        try {
            /* Instantiate a CertificateFactory for X.509 */
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            /*
             * Extract the certification path from
             * the List of Certificates
             */
            CertPath cp = cf.generateCertPath(certList);

            /* Create CertPathValidator that implements the "PKIX" algorithm */
            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");

            /* Set the Trust anchor */
            TrustAnchor anchor = new TrustAnchor(trustedCert, null);

            /* Set the PKIX parameters */
            PKIXParameters params = new PKIXParameters(
                    Collections.singleton(anchor));
            params.setRevocationEnabled(true);

            /* list of additional signer certificates for
            populating the trust store */
            Security.setProperty("ocsp.enable", "true");
            Security.setProperty("ocsp.responderURL", responderUrl);


            // Validate and obtain results
            PKIXCertPathValidatorResult result =
                    (PKIXCertPathValidatorResult) cpv.validate(cp, params);
            PolicyNode policyTree = result.getPolicyTree();
            PublicKey subjectPublicKey = result.getPublicKey();

            logger4J.debug("PolicyTree: "+ policyTree);
            logger4J.debug("Subject Public Key: " + subjectPublicKey.toString());
            logger4J.debug("Certificate is OCSP valid");
            valid = true;
        } catch (CertificateException ex) {
            logger4J.error(ex.getMessage(),ex);
            Logger.getLogger(OCSP.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            logger4J.error(ex.getMessage(),ex);
            Logger.getLogger(OCSP.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (InvalidAlgorithmParameterException ex) {
           logger4J.error(ex.getMessage(),ex);
           Logger.getLogger(OCSP.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (CertPathValidatorException ex) {
            StringBuilder logappender = new StringBuilder();
            logappender.append("Validation failure, cert[");
            logappender.append(ex.getIndex());
            logappender.append("] :");
            logappender.append(ex.getMessage());
            Logger.getLogger(OCSP.class.getName()).log(
                    Level.SEVERE, logappender.toString());

            logger4J.error(ex.getMessage(),ex);
        }
        return valid;
    }
}