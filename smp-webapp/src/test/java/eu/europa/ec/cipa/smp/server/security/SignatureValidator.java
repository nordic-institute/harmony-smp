package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.cipa.smp.server.errors.exceptions.SignatureException;
import org.w3c.dom.Element;
import sun.misc.IOUtils;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import java.io.InputStream;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;

/**
 * This code was copied&pasted, apologizes for not refactoring.
 */
public class SignatureValidator {

    public static void validateSignature(Element sigPointer) throws Exception {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Create a DOMValidateContext and specify a KeySelector and document context.
        DOMValidateContext valContext = new DOMValidateContext(new SignatureValidator.X509KeySelector(), sigPointer);

        valContext.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
        // Unmarshal the XMLSignature.
        XMLSignature signature = fac.unmarshalXMLSignature(valContext);
        // Validate the XMLSignature.
        boolean coreValidity = signature.validate(valContext);

        Iterator i = signature.getSignedInfo().getReferences().iterator();
        for (int j = 0; i.hasNext(); j++) {
            InputStream is = ((Reference) i.next()).getDigestInputStream();
            // Display the data.
            byte[] a = IOUtils.readFully(is, 0, true);
            System.out.println(new String(a));
        }

        // Check core validation status.
        if (coreValidity == false) {
            printErrorDetails(valContext, signature);
            throw new SignatureException("+++ Signature not valild +++");
        } else {
            System.out.println("+++ Signature passed core validation +++");
        }

    }

    private static void printErrorDetails(DOMValidateContext valContext, XMLSignature signature) throws XMLSignatureException {
        System.err.println("Signature failed core validation");
        boolean sv = signature.getSignatureValue().validate(valContext);
        System.out.println("signature validation status: " + sv);
        if (sv == false) {
            // Check the validation status of each Reference.
            Iterator i1 = signature.getSignedInfo().getReferences().iterator();
            for (int j = 0; i1.hasNext(); j++) {
                boolean refValid = ((Reference) i1.next()).validate(valContext);
                System.out.println("ref[" + j + "] validity status: " + refValid);
            }
        }
    }


    private static class X509KeySelector extends KeySelector {
        public KeySelectorResult select(KeyInfo keyInfo, Purpose purpose, AlgorithmMethod method,
                                        XMLCryptoContext context) throws KeySelectorException {
            Iterator ki = keyInfo.getContent().iterator();
            while (ki.hasNext()) {
                XMLStructure info = (XMLStructure) ki.next();
                if (!(info instanceof X509Data))
                    continue;
                X509Data x509Data = (X509Data) info;
                Iterator xi = x509Data.getContent().iterator();
                while (xi.hasNext()) {
                    Object o = xi.next();
                    if (!(o instanceof X509Certificate))
                        continue;
                    final PublicKey key = ((X509Certificate) o).getPublicKey();
                    // Make sure the algorithm is compatible
                    // with the method.
                    if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
                        return new KeySelectorResult() {
                            public Key getKey() {
                                return key;
                            }
                        };
                    }
                }
            }
            throw new KeySelectorException("No key found!");
        }

        boolean algEquals(String algURI, String algName) {
            if ((algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1))
                    || (algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1))) {
                return true;
            } else {
                return false;
            }
        }
    }
}
