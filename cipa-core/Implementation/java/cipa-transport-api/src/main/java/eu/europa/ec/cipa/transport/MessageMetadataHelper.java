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
package eu.europa.ec.cipa.transport;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ObjectFactory;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.ChildElementIterator;
import com.phloc.commons.xml.XMLFactory;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Headers;

import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.identifier.doctype.SimpleDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.SimpleProcessIdentifier;

/**
 * Utility class for handling {@link IMessageMetadata} objects.
 * 
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class MessageMetadataHelper {
  private static final QName QNAME_MESSAGEID;
  private static final QName QNAME_CHANNELID;
  private static final QName QNAME_RECIPIENTID;
  private static final QName QNAME_SENDERID;
  private static final QName QNAME_DOCUMENTID;
  private static final QName QNAME_PROCESSID;
  // Must match the attribute name used in ParticipantIdentifierType etc.
  private static final QName QNAME_SCHEME = new QName (null, CTransportIdentifiers.SCHEME_ATTR);

  static {
    final ObjectFactory aIdentifierObjFactory = new ObjectFactory ();
    QNAME_MESSAGEID = aIdentifierObjFactory.createMessageIdentifier (null).getName ();
    QNAME_CHANNELID = aIdentifierObjFactory.createChannelIdentifier (null).getName ();
    QNAME_RECIPIENTID = aIdentifierObjFactory.createRecipientIdentifier (null).getName ();
    QNAME_SENDERID = aIdentifierObjFactory.createSenderIdentifier (null).getName ();
    QNAME_DOCUMENTID = aIdentifierObjFactory.createDocumentIdentifier (null).getName ();
    QNAME_PROCESSID = aIdentifierObjFactory.createProcessIdentifier (null).getName ();
  }

  private MessageMetadataHelper () {}

  @Nonnull
  public static String getDebugInfo (@Nonnull final IMessageMetadata aMetadata) {
    return "\tMessageID:\t" +
           aMetadata.getMessageID () +
           "\n\tChannelID:\t" +
           aMetadata.getChannelID () +
           "\n\tSenderID:\t" +
           IdentifierUtils.getIdentifierURIEncoded (aMetadata.getSenderID ()) +
           "\n\tRecipientID:\t" +
           IdentifierUtils.getIdentifierURIEncoded (aMetadata.getRecipientID ()) +
           "\n\tDocumentTypeID:\t" +
           IdentifierUtils.getIdentifierURIEncoded (aMetadata.getDocumentTypeID ()) +
           "\n\tProcessID:\t" +
           IdentifierUtils.getIdentifierURIEncoded (aMetadata.getProcessID ());
  }

  @Nonnull
  public static Document createHeadersDocument (@Nonnull final IMessageMetadata aMetadata) throws JAXBException {
    // Set headers direct, without a handler
    final ObjectFactory aObjFactory = new ObjectFactory ();
    final Document aDoc = XMLFactory.newDocument ();
    final Element eRoot = (Element) aDoc.appendChild (aDoc.createElementNS (CTransportIdentifiers.NAMESPACE_TRANSPORT_IDS,
                                                                            CTransportIdentifiers.ELEMENT_HEADERS));
    Marshaller aMarshaller;

    // Write message ID (may be null for LIME)
    if (StringHelper.hasText (aMetadata.getMessageID ())) {
      aMarshaller = JAXBContext.newInstance (String.class).createMarshaller ();
      aMarshaller.marshal (aObjFactory.createMessageIdentifier (aMetadata.getMessageID ()), eRoot);
    }

    // Write channel ID
    if (StringHelper.hasText (aMetadata.getChannelID ())) {
      aMarshaller = JAXBContext.newInstance (String.class).createMarshaller ();
      aMarshaller.marshal (aObjFactory.createChannelIdentifier (aMetadata.getChannelID ()), eRoot);
    }

    // Write sender
    if (aMetadata.getSenderID () != null) {
      aMarshaller = JAXBContextCache.getInstance ().getFromCache (ParticipantIdentifierType.class).createMarshaller ();
      aMarshaller.marshal (aObjFactory.createSenderIdentifier (aMetadata.getSenderID ()), eRoot);
    }

    // Write recipient
    if (aMetadata.getRecipientID () != null) {
      aMarshaller = JAXBContextCache.getInstance ().getFromCache (ParticipantIdentifierType.class).createMarshaller ();
      aMarshaller.marshal (aObjFactory.createRecipientIdentifier (aMetadata.getRecipientID ()), eRoot);
    }

    // Write document type
    if (aMetadata.getDocumentTypeID () != null) {
      aMarshaller = JAXBContextCache.getInstance ().getFromCache (DocumentIdentifierType.class).createMarshaller ();
      aMarshaller.marshal (aObjFactory.createDocumentIdentifier (aMetadata.getDocumentTypeID ()), eRoot);
    }

    // Write process type
    if (aMetadata.getProcessID () != null) {
      aMarshaller = JAXBContextCache.getInstance ().getFromCache (ProcessIdentifierType.class).createMarshaller ();
      aMarshaller.marshal (aObjFactory.createProcessIdentifier (aMetadata.getProcessID ()), eRoot);
    }

    return aDoc;
  }

  @Nonnull
  public static List <Header> createHeadersFromMetadata (@Nonnull final IMessageMetadata aMetadata) throws JAXBException {
    final Document aDoc = createHeadersDocument (aMetadata);

    // Collect headers
    final List <Header> aHeaders = new ArrayList <Header> ();
    for (final Element eHeader : new ChildElementIterator (aDoc.getDocumentElement ()))
      aHeaders.add (Headers.create (eHeader));
    return aHeaders;
  }

  @Nullable
  public static String getStringContent (@Nullable final Header aHeader) {
    if (aHeader == null)
      return null;
    final String sContent = aHeader.getStringContent ();
    return StringHelper.hasNoText (sContent) ? null : sContent;
  }

  @Nullable
  public static String getMessageID (@Nonnull final HeaderList aHeaderList) {
    return getStringContent (aHeaderList.get (QNAME_MESSAGEID, false));
  }

  @Nullable
  public static String getChannelID (@Nonnull final HeaderList aHeaderList) {
    return getStringContent (aHeaderList.get (QNAME_CHANNELID, false));
  }

  @Nullable
  private static ParticipantIdentifierType _getParticipantID (@Nonnull final HeaderList aHeaderList, final QName aQName) {
    final Header hPartID = aHeaderList.get (aQName, false);
    return hPartID == null ? null : new SimpleParticipantIdentifier (hPartID.getAttribute (QNAME_SCHEME),
                                                                     hPartID.getStringContent ());
  }

  @Nullable
  private static DocumentIdentifierType _getDocumentTypeID (@Nonnull final HeaderList aHeaderList) {
    final Header hDocumentID = aHeaderList.get (QNAME_DOCUMENTID, false);
    return hDocumentID == null ? null : new SimpleDocumentTypeIdentifier (hDocumentID.getAttribute (QNAME_SCHEME),
                                                                          hDocumentID.getStringContent ());
  }

  @Nullable
  private static ProcessIdentifierType _getProcessID (@Nonnull final HeaderList aHeaderList) {
    final Header hProcessID = aHeaderList.get (QNAME_PROCESSID, false);
    return hProcessID == null ? null : new SimpleProcessIdentifier (hProcessID.getAttribute (QNAME_SCHEME),
                                                                    hProcessID.getStringContent ());
  }

  @Nonnull
  public static IMessageMetadata createMetadataFromHeaders (@Nonnull final HeaderList aHeaderList) {
    return new MessageMetadata (getMessageID (aHeaderList),
                                getChannelID (aHeaderList),
                                _getParticipantID (aHeaderList, QNAME_SENDERID),
                                _getParticipantID (aHeaderList, QNAME_RECIPIENTID),
                                _getDocumentTypeID (aHeaderList),
                                _getProcessID (aHeaderList));
  }

  /**
   * For LIME the message ID is created on the AP side.
   * 
   * @param aHeaderList
   *        The incoming SOAP headers
   * @param sMessageID
   *        The message ID to be used
   * @return The metadata object
   */
  @Nonnull
  public static IMessageMetadata createMetadataFromHeadersWithCustomMessageID (@Nonnull final HeaderList aHeaderList,
                                                                               final String sMessageID) {
    return new MessageMetadata (sMessageID,
                                getChannelID (aHeaderList),
                                _getParticipantID (aHeaderList, QNAME_SENDERID),
                                _getParticipantID (aHeaderList, QNAME_RECIPIENTID),
                                _getDocumentTypeID (aHeaderList),
                                _getProcessID (aHeaderList));
  }
}
