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
package at.peppol.transport.lime.soapheader;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.busdox.transport.identifiers._1.ObjectFactory;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.jaxb.JAXBMarshallerUtils;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
final class SoapHeaderHandler implements SOAPHandler <SOAPMessageContext> {
  private static final Logger s_aLogger = LoggerFactory.getLogger (SoapHeaderHandler.class.getName ());

  private final String m_sChannelID;
  private final String m_sMessageID;
  private final List <Element> m_aReferenceParameters;

  public SoapHeaderHandler (final String channelID,
                            final String messageID,
                            @Nullable final List <Element> referenceParameters) {
    m_sMessageID = messageID;
    m_sChannelID = channelID;
    m_aReferenceParameters = referenceParameters;
  }

  public boolean handleMessage (final SOAPMessageContext smc) {
    if (((Boolean) smc.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue ()) {
      // Outgoing message...
      try {
        final SOAPEnvelope envelope = smc.getMessage ().getSOAPPart ().getEnvelope ();
        createSOAPHeader (envelope);
      }
      catch (final Exception ex) {
        s_aLogger.warn ("Failed to set header", ex);
      }
    }
    return true;
  }

  public Set <QName> getHeaders () {
    return null;
  }

  public boolean handleFault (final SOAPMessageContext context) {
    return true;
  }

  public void close (final MessageContext context) {}

  private static final class PrefixMapper extends NamespacePrefixMapper {
    @Override
    public String getPreferredPrefix (final String namespaceUri, final String suggestion, final boolean requirePrefix) {
      final String result = suggestion;
      return result + "STARTAP";
    }
  }

  private void createSOAPHeader (final SOAPEnvelope envelope) throws Exception {
    SOAPHeader header = envelope.getHeader ();
    if (header == null) {
      header = envelope.addHeader ();
    }

    final ObjectFactory objFactory = new ObjectFactory ();
    Marshaller marshaller = JAXBContextCache.getInstance ()
                                            .getFromCache (ParticipantIdentifierType.class.getPackage ())
                                            .createMarshaller ();

    marshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    JAXBMarshallerUtils.setSunNamespacePrefixMapper (marshaller, new PrefixMapper ());

    // java.lang. classes cannot be used with JAXBContextCache
    marshaller = JAXBContext.newInstance (String.class).createMarshaller ();
    if (m_sChannelID != null) {
      marshaller.marshal (objFactory.createChannelIdentifier (m_sChannelID), new DOMResult (header));
    }
    if (m_sMessageID != null) {
      marshaller.marshal (objFactory.createMessageIdentifier (m_sMessageID), new DOMResult (header));
    }

    if (m_aReferenceParameters != null) {
      try {
        for (final Element node : m_aReferenceParameters) {
          final SOAPElement aSOAPElement = header.addChildElement (new QName (node.getNamespaceURI (),
                                                                              node.getLocalName ()));
          aSOAPElement.setTextContent (node.getTextContent ());
        }
      }
      catch (final Exception ex) {
        s_aLogger.info ("Unable to set reference parameters", ex);
        throw ex;
      }
    }
  }
}
