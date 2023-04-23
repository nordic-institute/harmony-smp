/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.edelivery.smp.services.spi;

import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.smp.spi.api.SmpXmlSignatureApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.exceptions.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static javax.xml.crypto.dsig.CanonicalizationMethod.INCLUSIVE;
import static javax.xml.crypto.dsig.Transform.ENVELOPED;

@Component
public final class SmpXmlSignatureService implements SmpXmlSignatureApi {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SmpXmlSignatureService.class);
    private static final String DEFAULT_HASH_METHOD = javax.xml.crypto.dsig.DigestMethod.SHA256;

    DomainDao domainDao;
    UIKeystoreService uiKeystoreService;


    public SmpXmlSignatureService(DomainDao domainDao, UIKeystoreService uiKeystoreService) {
        this.domainDao = domainDao;
        this.uiKeystoreService = uiKeystoreService;
    }

    private static XMLSignatureFactory getDomSigFactory() {
        // According to Javadoc, only static methods of this factory are thread-safe
        // We cannot share and re-use the same instance in every place
        // set apache santuario xmlsec signature factory
        return XMLSignatureFactory.getInstance("DOM",  new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
    }

    /**
     * Creates an Enveloped XML signature which is embed to the specified node (parentSignatureNode) of the document.
     * The marshalled <code>XMLSignature</code> will be added as the last
     * child element of the specified parentSignatureNode.
     *
     * @param parentSignatureNode  the parent of the signing node. The element must be part of the XML document to be signed
     * @param signedElementURIList the parent node the list of URIs to be signed. If List is empty then the whole document is signed
     * @throws NullPointerException if <code>signingKey</code> or
     *                              <code>parent</code> is <code>null</code>
     */
    public void createEnvelopedSignature(RequestData resourceMetadata, Element parentSignatureNode, List<String> signedElementURIList) throws SignatureException {
        // Find the domain for the identifier

        if (resourceMetadata == null || resourceMetadata.getResourceIdentifier() == null) {
            throw new SignatureException(SignatureException.ErrorCode.INVALID_PARAMETERS, "Missing resource identifier");
        }

        if (StringUtils.isEmpty(resourceMetadata.getDomainCode())) {
            throw new SignatureException(SignatureException.ErrorCode.INVALID_PARAMETERS, "Missing resource domain code");
        }
        Optional<DBDomain> optDomain = domainDao.getDomainByCode(resourceMetadata.getDomainCode());
        DBDomain domain = optDomain.orElseThrow(
                () -> new SignatureException(SignatureException.ErrorCode.INVALID_PARAMETERS, "Domain for the domain code [" + resourceMetadata.getDomainCode() + "] does not exists!"));

        createEnvelopedSignature(parentSignatureNode,
                Collections.emptyList(),
                domain.getSignatureKeyAlias(),
                domain.getSignatureAlgorithm(),
                domain.getSignatureDigestMethod());
    }

    public void sign(Document documentToSign, String keyAlias, String signatureAlgorithm, String signatureHashMethod) {
        createEnvelopedSignature(documentToSign.getDocumentElement(), Collections.emptyList(), keyAlias, signatureAlgorithm, signatureHashMethod);
    }

    public void createEnvelopedSignature(Element parentSignatureNode, List<String> signedElementURIList, String keyAlias,
                                         String signatureAlgorithm, String signatureHashMethod) {
        LOG.info("Sing document with alias {}", keyAlias);
        try {
            if (StringUtils.isBlank(keyAlias) && uiKeystoreService.getKeystoreEntriesList().size() > 1) {
                LOG.warn("Undefined certificate for signing service metadata reposes! Define key in configuration!");
                return;
            }
            XMLSignatureFactory domSigFactory = getDomSigFactory();

            Key signingKey = uiKeystoreService.getKey(keyAlias);
            String signAlg = getSignatureAlgorithmForKey(signingKey, signatureAlgorithm);
            String referenceHash = StringUtils.defaultIfEmpty(signatureHashMethod, DEFAULT_HASH_METHOD);


            List<Reference> referenceList;
            if (signedElementURIList.isEmpty()) {
                // Create a Reference to the ENVELOPED document
                // URI "" means that the whole document is signed
                referenceList = singletonList(createReferenceForUri("", domSigFactory, referenceHash));
            } else {
                referenceList = signedElementURIList.stream().map(uri -> createReferenceForUri(uri, domSigFactory, referenceHash)).collect(Collectors.toList());
            }
            LOG.info("Create signature with signature algorithm : [{}]", signAlg);
            SignedInfo singedInfo = domSigFactory.newSignedInfo(
                    domSigFactory.newCanonicalizationMethod(INCLUSIVE, (C14NMethodParameterSpec) null),
                    domSigFactory.newSignatureMethod(signAlg, null),
                    referenceList);


            DOMSignContext domSignContext = new DOMSignContext(uiKeystoreService.getKey(keyAlias), parentSignatureNode);

            // Create the XMLSignature, but don't sign it yet
            KeyInfo keyInfo = createKeyInfo(keyAlias);
            XMLSignature signature = domSigFactory.newXMLSignature(singedInfo, keyInfo);

            // Marshal, generate, and sign the enveloped signature
            signature.sign(domSignContext);
        } catch (Exception e) {
            throw new SMPRuntimeException(ErrorCode.XML_SIGNING_EXCEPTION, e);
        }
    }

    private Reference createReferenceForUri(String elementUri, XMLSignatureFactory domSigFactory, String signatureHashMethod) {
        try {
            return domSigFactory.newReference(
                    elementUri,
                    domSigFactory.newDigestMethod(signatureHashMethod, null),
                    singletonList(domSigFactory.newTransform(ENVELOPED, (TransformParameterSpec) null)),
                    null,
                    null);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new SMPRuntimeException(ErrorCode.XML_SIGNING_EXCEPTION, e);
        }
    }

    private KeyInfo createKeyInfo(String alias) {
        KeyInfoFactory keyInfoFactory = getDomSigFactory().getKeyInfoFactory();
        List content = new ArrayList();
        X509Certificate cert = uiKeystoreService.getCert(alias);
        content.add(cert.getSubjectX500Principal().getName());
        content.add(cert);
        X509Data x509Data = keyInfoFactory.newX509Data(content);
        return keyInfoFactory.newKeyInfo(singletonList(x509Data));
    }

    public String  getSignatureAlgorithmForKey(Key key, String algorithm) {
        if (StringUtils.isNotBlank(algorithm)) {
            return algorithm;
        }

        if (StringUtils.equalsAnyIgnoreCase(key.getAlgorithm(), "1.3.101.112","ed25519")) {
            return org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_EDDSA_ED25519;
        }

        if (StringUtils.equalsAnyIgnoreCase(key.getAlgorithm(), "1.3.101.113","ed448")) {
            return org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_EDDSA_ED448;
        }

        if (StringUtils.equalsIgnoreCase(key.getAlgorithm(), "ec")) {
            return org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256;
        }
        return org.apache.xml.security.signature.XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256;
    }

}
