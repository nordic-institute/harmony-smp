/**
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
package org.busdox.transport.start.saml;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Nonnull;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.soap.MessageFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.phloc.commons.CGlobal;
import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.base64.Base64;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.random.VerySecureRandom;
import com.sun.xml.wss.impl.callback.SAMLCallback;
import com.sun.xml.wss.impl.dsig.WSSPolicyConsumerImpl;
import com.sun.xml.wss.saml.Assertion;
import com.sun.xml.wss.saml.AttributeStatement;
import com.sun.xml.wss.saml.AuthnContext;
import com.sun.xml.wss.saml.AuthnStatement;
import com.sun.xml.wss.saml.Conditions;
import com.sun.xml.wss.saml.NameID;
import com.sun.xml.wss.saml.SAMLAssertionFactory;
import com.sun.xml.wss.saml.SAMLException;
import com.sun.xml.wss.saml.Subject;
import com.sun.xml.wss.saml.SubjectConfirmation;

import eu.europa.ec.cipa.peppol.security.KeyStoreUtils;

/**
 * The SAMLCallbackHandler is the CallbackHandler implementation used for deal
 * with SAML authentication.<br>
 * Note: the package and the class name are referenced from the WSDL.
 * 
 * @author Alexander Aguirre Julcapoma(alex@alfa1lab.com) Jose Gorvenia<br>
 *         Narvaez(jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@UsedViaReflection
public final class SAMLCallbackHandler implements CallbackHandler {
  /** Logger to follow this class behavior. */
  private static final Logger s_aLogger = LoggerFactory.getLogger (SAMLCallbackHandler.class);

  /** Assertion ID prefix. */
  private static final String SAML_ID_PREFIX = "SamlID";

  private static final String SAML_ASSURANCE_LEVEL = "3";

  /** Sender ID syntax. */
  private static final String SENDER_NAME_ID_SYNTAX = "http://busdox.org/profiles/serviceMetadata/1.0/UniversalBusinessIdentifier/1.0/";

  /** Accesspoint ID syntax. */
  private static final String ACCESSPOINT_NAME_ID_SYNTAX = "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";

  /** Authentication type. */
  private static final String CONFIRMATION_METHOD = "urn:oasis:names:tc:SAML:2.0:cm:sender-vouches";

  /** Authentication context schema type. */
  private static final String AUTHENTICATION_CONTEXT_TYPE = "urn:oasis:names:tc:SAML:2.0:ac:classes:X509";

  /** Attribute Assurance Level. */
  private static final String ATTRIBUTE_NAME = "urn:eu:busdox:attribute:assurance-level";

  /** Attribute Namespace. */
  private static final String ATTRIBUTE_NAMESPACE = "urn:oasis:names:tc:SAML:2.0:attrname-format:basic";

  /**
   * Retrieve or display the information requested in the provided Callbacks.
   * 
   * @param aCallbacks
   *        an array of Callback objects provided by an underlying security
   *        service which contains the information requested to be retrieved or
   *        displayed.
   * @throws IOException
   *         if an input or output error occurs.
   * @throws UnsupportedCallbackException
   *         if the implementation of this method does not support one or more
   *         of the Callbacks specified in the callbacks parameter.
   */
  @Override
  public void handle (@Nonnull final Callback [] aCallbacks) throws IOException, UnsupportedCallbackException {
    for (final Callback aCallback : aCallbacks) {
      if (aCallback instanceof SAMLCallback) {
        try {
          final SAMLCallback aSAMLCallback = (SAMLCallback) aCallback;
          if (aSAMLCallback.getConfirmationMethod ().equals (SAMLCallback.SV_ASSERTION_TYPE)) {
            aSAMLCallback.setAssertionElement (_createSenderVouchesSAMLAssertion (aSAMLCallback));
          }
        }
        catch (final Exception ex) {
          s_aLogger.error ("Error while handling SAML callback", ex);
        }
      }
      else
        throw new UnsupportedCallbackException (aCallback, "Unsupported Callback Type Encountered");
    }
  }

  /**
   * Gets an Element representing SAML Assertion.
   * 
   * @param aSamlCallback
   *        the SAMLCallback.
   * @return an Element.
   * @throws Exception
   *         thrown if there is a SOAP problem.
   */
  private static Element _createSenderVouchesSAMLAssertion (final SAMLCallback aSamlCallback) throws Exception {
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Creating and setting the SAML Sender Vouches Assertion.");

    // Read config file
    final SAMLConfiguration aSAMLConfig = SAMLConfiguration.getInstance ();

    // id must start with letters (WIF.NET)
    // necessary to support <sp:ProtectTokens>
    // According to EDELIVERY-112 it should by a real cryptographic value
    final byte [] aRandom = new byte [128];
    VerySecureRandom.getInstance ().nextBytes (aRandom);
    String sAssertionID = SAML_ID_PREFIX + Base64.encodeBytes (aRandom);
    // EDELIVERY-130: quick fix to problem of sAssertionID always containing two
    // '\n' at the same position [82] and [159] and hence failing later when we
    // try to sign with it.
    sAssertionID = sAssertionID.replace ('\n', '+');
    aSamlCallback.setAssertionId (sAssertionID);

    GregorianCalendar aCal = new GregorianCalendar ();
    final long nBeforeTime = aCal.getTimeInMillis ();

    // Roll the time by one hour
    final long nOffsetHours = CGlobal.MILLISECONDS_PER_HOUR;
    aCal.setTimeInMillis (nBeforeTime - nOffsetHours);
    final GregorianCalendar aBefore = (GregorianCalendar) aCal.clone ();

    aCal = new GregorianCalendar ();
    final long nAfterTime = aCal.getTimeInMillis ();
    aCal.setTimeInMillis (nAfterTime + nOffsetHours);
    final GregorianCalendar aAfter = (GregorianCalendar) aCal.clone ();

    final GregorianCalendar aIssueInstant = new GregorianCalendar ();

    // Statements
    final List <Object> aStatements = new ArrayList <Object> ();
    final SAMLAssertionFactory aSAMLFactory = SAMLAssertionFactory.newInstance (SAMLAssertionFactory.SAML2_0);

    // Setup SenderNameID
    final NameID aSenderNameID = aSAMLFactory.createNameID (aSAMLConfig.getSenderID (), null, SENDER_NAME_ID_SYNTAX);

    // Setup AccessPointID
    final NameID aAccessPointNameID = aSAMLFactory.createNameID (aSAMLConfig.getAccessPointName (),
                                                                 null,
                                                                 ACCESSPOINT_NAME_ID_SYNTAX);

    final SubjectConfirmation aSubjectConfirmation = aSAMLFactory.createSubjectConfirmation (null, CONFIRMATION_METHOD);
    final Subject aSubject = aSAMLFactory.createSubject (aSenderNameID, aSubjectConfirmation);

    final AuthnContext aCtx = aSAMLFactory.createAuthnContext (AUTHENTICATION_CONTEXT_TYPE, null);

    final AuthnStatement aStatement = aSAMLFactory.createAuthnStatement (aIssueInstant, null, aCtx, null, null);
    aStatements.add (aStatement);
    aStatements.add (_getAssuranceLevelStatement (SAML_ASSURANCE_LEVEL, aSAMLFactory));
    final Conditions aConditions = aSAMLFactory.createConditions (aBefore, aAfter, null, null, null, null);
    final Assertion aAssertion = aSAMLFactory.createAssertion (sAssertionID,
                                                               aAccessPointNameID,
                                                               aIssueInstant,
                                                               aConditions,
                                                               null,
                                                               aSubject,
                                                               aStatements);

    // Read certificates and private keys
    final KeyStore aKeyStore = KeyStoreUtils.loadKeyStore (aSAMLConfig.getKeyStorePath (),
                                                           aSAMLConfig.getKeyStorePassword ());
    final X509Certificate aAPCertificate = (X509Certificate) aKeyStore.getCertificate (aSAMLConfig.getKeyAlias ());
    final Key aKey = aKeyStore.getKey (aSAMLConfig.getKeyAlias (), aSAMLConfig.getKeyPassword ().toCharArray ());
    final PrivateKey aPrivateKey = aKey instanceof PrivateKey ? (PrivateKey) aKey : null;

    // Sign :)
    final Element aSignedAssertion = sign (aAssertion, aAPCertificate, aPrivateKey);
    return aSignedAssertion;
  }

  /**
   * Gets the Level Statement of Assurance.
   * 
   * @param sAssuranceLevel
   *        the assurance level.
   * @param aSAMLFactory
   *        the SAMLAssertionFactory.
   * @return an AttributeStatement.
   * @throws SAMLException
   *         Throws a SAMLException.
   */
  private static AttributeStatement _getAssuranceLevelStatement (final String sAssuranceLevel,
                                                                 final SAMLAssertionFactory aSAMLFactory) throws SAMLException {
    return aSAMLFactory.createAttributeStatement (ContainerHelper.newList (aSAMLFactory.createAttribute (ATTRIBUTE_NAME,
                                                                                                         ATTRIBUTE_NAMESPACE,
                                                                                                         ContainerHelper.newList (sAssuranceLevel))));
  }

  /**
   * Converts a SAML Assertion to an org.w3c.dom.Element object and signs it
   * with the X509Certificate and PrivateKey using the SHA1 DigestMethod and the
   * RSA_SHA1 SignatureMethod.
   * 
   * @param assertion
   *        SAML Assertion to be signed.
   * @param cert
   *        the X509Certificate.
   * @param privKey
   *        the certificate's private key.
   * @return a signed org.w3c.dom.Element holding the SAML Assertion.
   * @throws SAMLException
   *         Throws a SAMLException.
   */
  public static final Element sign (@Nonnull final Assertion assertion,
                                    final X509Certificate cert,
                                    @Nonnull final PrivateKey privKey) throws SAMLException {
    try {
      final XMLSignatureFactory fac = WSSPolicyConsumerImpl.getInstance ().getSignatureFactory ();
      return sign (assertion, fac.newDigestMethod (DigestMethod.SHA1, null), SignatureMethod.RSA_SHA1, cert, privKey);
    }
    catch (final Exception ex) {
      throw new SAMLException (ex);
    }
  }

  /**
   * Converts a SAML Assertion to an org.w3c.dom.Element object and signs it
   * with the X509Certificate and PrivateKey using the given DigestMethod and
   * the specified SignatureMethod.
   * 
   * @param aAssertion
   *        SAML Assertion to be signed.
   * @param aDigestMethod
   *        the digest method.
   * @param sSignatureMethod
   *        the signature method.
   * @param aCert
   *        the X509Certificate.
   * @param aPrivKey
   *        the certificate's private key.
   * @return a signed org.w3c.dom.Element holding the SAML Assertion.
   * @throws SAMLException
   *         Throws a SAMLException.
   */
  @Nonnull
  public static final Element sign (@Nonnull final Assertion aAssertion,
                                    final DigestMethod aDigestMethod,
                                    final String sSignatureMethod,
                                    final X509Certificate aCert,
                                    @Nonnull final PrivateKey aPrivKey) throws SAMLException {

    try {
      final XMLSignatureFactory aXMLSignatureFactory = WSSPolicyConsumerImpl.getInstance ().getSignatureFactory ();
      final List <Transform> transformList = new ArrayList <Transform> ();
      transformList.add (aXMLSignatureFactory.newTransform (Transform.ENVELOPED, (TransformParameterSpec) null));
      transformList.add (aXMLSignatureFactory.newTransform (CanonicalizationMethod.EXCLUSIVE,
                                                            (TransformParameterSpec) null));

      final String uri = "#" + aAssertion.getID ();
      final Reference ref = aXMLSignatureFactory.newReference (uri, aDigestMethod, transformList, null, null);

      // Create the SignedInfo
      final SignedInfo aSignedInfo = aXMLSignatureFactory.newSignedInfo (aXMLSignatureFactory.newCanonicalizationMethod (CanonicalizationMethod.EXCLUSIVE,
                                                                                                                         (C14NMethodParameterSpec) null),
                                                                         aXMLSignatureFactory.newSignatureMethod (sSignatureMethod,
                                                                                                                  null),
                                                                         Collections.singletonList (ref));

      /* Document to be signed */
      final Document aDoc = MessageFactory.newInstance ().createMessage ().getSOAPPart ();
      final KeyInfoFactory aKeyInfoFactory = aXMLSignatureFactory.getKeyInfoFactory ();

      final X509Data x509Data = aKeyInfoFactory.newX509Data (Collections.singletonList (aCert));
      final KeyInfo aKeyInfo = aKeyInfoFactory.newKeyInfo (Collections.singletonList (x509Data));

      final Element aAssertionElement = aAssertion.toElement (aDoc);
      final DOMSignContext dsc = new DOMSignContext (aPrivKey, aAssertionElement);

      final XMLSignature aXMLSignature = aXMLSignatureFactory.newXMLSignature (aSignedInfo, aKeyInfo);
      dsc.putNamespacePrefix ("http://www.w3.org/2000/09/xmldsig#", "ds");

      aXMLSignature.sign (dsc);
      _placeSignatureAfterIssuer (aAssertionElement);

      return aAssertionElement;
    }
    catch (final Exception ex) {
      throw new SAMLException (ex);
    }
  }

  /**
   * Places a Signature.
   * 
   * @param assertionElement
   *        an Element containing an Assertion.
   * @throws DOMException
   *         Throws a DOMException.
   */
  @DevelopersNote ("What is the sense of this method? [philip]")
  private static void _placeSignatureAfterIssuer (@Nonnull final Element assertionElement) throws DOMException {
    final List <Node> aMovingNodes = new ArrayList <Node> ();
    final NodeList aNodeList = assertionElement.getChildNodes ();
    for (int i = 1; i < aNodeList.getLength () - 1; i++)
      aMovingNodes.add (aNodeList.item (i));

    // Remove all selected child nodes...
    for (final Node aNode : aMovingNodes)
      assertionElement.removeChild (aNode);

    // .. and append them in the same order
    for (final Node aNode : aMovingNodes)
      assertionElement.appendChild (aNode);
  }
}
