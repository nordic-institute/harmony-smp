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
package eu.europa.ec.cipa.transport.lime.server;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.europa.ec.cipa.transport.CTransportIdentifiers;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public class SoapResponseHeaderHandler implements SOAPHandler <SOAPMessageContext> {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SoapResponseHeaderHandler.class);

  public boolean handleMessage (final SOAPMessageContext aMessageContext) {
    final SOAPMessage aMessage = aMessageContext.getMessage ();

    if (((Boolean) aMessageContext.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue ()) {
      // It's an outgoing message
      try {
        final SOAPEnvelope aEnvelope = aMessage.getSOAPPart ().getEnvelope ();
        final NodeList aChildNodes = aEnvelope.getBody ().getChildNodes ();
        if (aChildNodes != null && aChildNodes.getLength () > 0) {
          // a child is present
          final Node aChildNode = aChildNodes.item (0);
          if (aChildNode != null && aChildNode.getChildNodes () != null && aChildNode.getChildNodes ().getLength () > 0) {
            // Child has children present
            final Node aChildChildeNode = aChildNode.getChildNodes ().item (0);
            if (aChildChildeNode != null &&
                aChildChildeNode.getNodeName ().indexOf (CTransportIdentifiers.ELEMENT_HEADERS) >= 0) {
              final SOAPHeader aHeader = _attachIncomingHeaders (aEnvelope);
              _moveHeaderFromBodyToSoapHeader (aEnvelope, aHeader);
            }
          }
        }
        aMessage.saveChanges ();
      }
      catch (final SOAPException ex) {
        s_aLogger.error ("Failed to set outgoing headers", ex);
        return false;
      }
    }
    return true;
  }

  public boolean handleFault (final SOAPMessageContext messageContext) {
    // Continue processing
    return true;
  }

  public void close (final MessageContext messageContext) {}

  public Set <QName> getHeaders () {
    return null;
  }

  @Nonnull
  private static SOAPHeader _attachIncomingHeaders (final SOAPEnvelope aSoapEnv) throws SOAPException {
    final SOAPHeader aOldHeader = aSoapEnv.getHeader ();
    if (aOldHeader == null)
      return aSoapEnv.addHeader ();

    // ???? what is the sense of this code? remove all headers, and clone them
    // back in into a new header???? [philip]
    final Iterator <?> iter = aOldHeader.extractAllHeaderElements ();
    aOldHeader.detachNode ();
    final SOAPHeader aNewHeader = aSoapEnv.addHeader ();
    for (; iter.hasNext ();) {
      final SOAPHeaderElement soapHeaderElement = (SOAPHeaderElement) iter.next ();
      aNewHeader.addChildElement ((SOAPHeaderElement) soapHeaderElement.cloneNode (true));
    }
    return aNewHeader;
  }

  private static void _moveHeaderFromBodyToSoapHeader (@Nonnull final SOAPEnvelope aSoapEnv,
                                                       @Nonnull final SOAPHeader aSoapHeader) throws DOMException,
                                                                                             SOAPException {
    final Node aSoapBodyBaseNode = aSoapEnv.getBody ().getChildNodes ().item (0);
    final Node aBaseNode = aSoapBodyBaseNode.getChildNodes ().item (0);
    for (int i = 0; i < aBaseNode.getChildNodes ().getLength (); i++) {
      final Node aChildNode = aBaseNode.getChildNodes ().item (i);
      // local name, no prefix and namespace URI
      final SOAPElement aSoapElement = aSoapHeader.addHeaderElement (aSoapEnv.createName (aChildNode.getLocalName (),
                                                                                          "",
                                                                                          aChildNode.getNamespaceURI ()));
      final NodeList aChildChildNodes = aChildNode.getChildNodes ();
      if (aChildChildNodes != null) {
        final int nMax = aChildChildNodes.getLength ();
        for (int j = 0; j < nMax; j++)
          aSoapElement.appendChild (aChildChildNodes.item (j).cloneNode (true));
      }

      // Extract all "scheme" attributes
      final NamedNodeMap aAttributes = aChildNode.getAttributes ();
      if (aAttributes != null)
        for (int a = 0; a < aAttributes.getLength (); a++) {
          final Node aAttr = aAttributes.item (a);
          if (aAttr.getLocalName ().equals (CTransportIdentifiers.SCHEME_ATTR))
            aSoapElement.setAttribute (aAttr.getLocalName (), aAttr.getNodeValue ());
        }
    }
    aSoapBodyBaseNode.removeChild (aBaseNode);
  }
}
