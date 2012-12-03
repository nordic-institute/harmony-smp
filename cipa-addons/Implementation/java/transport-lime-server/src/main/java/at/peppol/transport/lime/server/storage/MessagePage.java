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
package at.peppol.transport.lime.server.storage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.busdox.transport.lime._1.Entry;
import org.busdox.transport.lime._1.NextPageIdentifierType;
import org.busdox.transport.lime._1.ObjectFactory;
import org.busdox.transport.lime._1.PageListType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.peppol.commons.wsaddr.W3CEndpointReferenceUtils;
import at.peppol.transport.CTransportIdentifiers;
import at.peppol.transport.lime.CLimeIdentifiers;

import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.jaxb.JAXBMarshallerUtils;
import com.phloc.commons.xml.XMLFactory;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.commons.xml.serialize.XMLWriterSettings;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class MessagePage {
  public static final int MESSAGE_PAGE_SIZE = 100;
  private static final Logger s_aLogger = LoggerFactory.getLogger (MessagePage.class);
  private static final ObjectFactory s_aObjFactory = new ObjectFactory ();

  private MessagePage () {}

  public static Document getPageList (final int nPageNum,
                                      final String sAPURL,
                                      final LimeStorage aStorage,
                                      final String sChannelID) throws JAXBException {
    // Get all message IDs
    final String [] aMessageIDs = aStorage.getMessageIDs (sChannelID);
    final int nMaxPageIndex = aMessageIDs.length / MESSAGE_PAGE_SIZE;
    if (nPageNum < 0 || nPageNum > nMaxPageIndex)
      throw new IllegalArgumentException ("Page number must be between 0 and " + nMaxPageIndex);

    s_aLogger.info ("Messages found in inbox: " + aMessageIDs.length);
    return _createPageListDocument (aMessageIDs, MESSAGE_PAGE_SIZE, nPageNum, aStorage, sChannelID, sAPURL);
  }

  private static Document _getPageListDocument (final int pageNum,
                                                final int pageSize,
                                                final String [] messageIDs,
                                                final LimeStorage channel,
                                                final String channelID,
                                                final String endpoint) throws JAXBException {
    final int fromMsg = pageNum * pageSize;
    final int toMsg = Math.min (((pageNum + 1) * pageSize) - 1, messageIDs.length - 1);

    final PageListType pageList = s_aObjFactory.createPageListType ();
    pageList.setNumberOfEntries (new Long (toMsg - fromMsg + 1));
    _addPageListEntries (fromMsg, toMsg, messageIDs, channel, channelID, endpoint, pageList);
    if ((messageIDs.length / pageSize) >= pageNum + 1) {
      _addNextPageIdentifier (endpoint, pageNum, channelID, pageList);
    }
    return _marshallPageList (pageList);
  }

  private static Document _marshallPageList (final PageListType pageList) throws JAXBException {
    final Marshaller marshaller = JAXBContextCache.getInstance ().getFromCache (PageListType.class).createMarshaller ();
    JAXBMarshallerUtils.setSunNamespacePrefixMapper (marshaller, new NamespacePrefixMapper () {
      @Override
      public String getPreferredPrefix (final String namespaceUri, final String suggestion, final boolean requirePrefix) {
        if (CLimeIdentifiers.NAMESPACE_LIME.equalsIgnoreCase (namespaceUri))
          return "peppol";
        return suggestion;
      }
    });

    final Document document = XMLFactory.newDocument ();
    marshaller.marshal (s_aObjFactory.createPageList (pageList), new DOMResult (document));
    s_aLogger.info (_xmlToString (document));
    return document;
  }

  private static void _addNextPageIdentifier (final String sEndpointURL,
                                              final int nCurPageNum,
                                              final String sChannelID,
                                              final PageListType aPageList) {
    final Document aDummyDoc = XMLFactory.newDocument ();
    final List <Element> aReferenceParameters = new ArrayList <Element> ();

    // Page identifier
    Element aElement = aDummyDoc.createElementNS (CLimeIdentifiers.NAMESPACE_LIME, CLimeIdentifiers.PAGEIDENTIFIER);
    aElement.appendChild (aDummyDoc.createTextNode (Integer.toString (nCurPageNum + 1)));
    aReferenceParameters.add (aElement);

    // Channel ID
    aElement = aDummyDoc.createElementNS (CTransportIdentifiers.NAMESPACE_TRANSPORT_IDS, CLimeIdentifiers.CHANNELID);
    aElement.appendChild (aDummyDoc.createTextNode (sChannelID));
    aReferenceParameters.add (aElement);

    final NextPageIdentifierType aNextPageIdentifier = s_aObjFactory.createNextPageIdentifierType ();
    aNextPageIdentifier.setEndpointReference (W3CEndpointReferenceUtils.createEndpointReference (sEndpointURL,
                                                                                                 aReferenceParameters));
    aPageList.setNextPageIdentifier (aNextPageIdentifier);
  }

  private static void _addPageListEntries (final int nFromMsg,
                                           final int nToMsg,
                                           final String [] aMessageIDs,
                                           final LimeStorage aChannel,
                                           final String sChannelID,
                                           final String sEndpoint,
                                           final PageListType aPageList) {
    aPageList.setEntryList (s_aObjFactory.createEntryListType ());

    for (int i = nFromMsg; i <= nToMsg; i++) {
      final String sMessageID = aMessageIDs[i];
      final Entry aEntry = s_aObjFactory.createEntry ();
      aEntry.setSize (Long.valueOf (aChannel.getSize (sChannelID, sMessageID)));
      aEntry.setCreationTime (aChannel.getCreationTime (sChannelID, sMessageID));

      final List <Element> aReferenceParametersType = new ArrayList <Element> ();
      final Document aDummyDoc = XMLFactory.newDocument ();
      Element aElement = aDummyDoc.createElementNS (CTransportIdentifiers.NAMESPACE_TRANSPORT_IDS,
                                                    CLimeIdentifiers.CHANNELID);
      aElement.appendChild (aDummyDoc.createTextNode (sChannelID));
      aReferenceParametersType.add (aElement);

      aElement = aDummyDoc.createElementNS (CTransportIdentifiers.NAMESPACE_TRANSPORT_IDS, CLimeIdentifiers.MESSAGEID);
      aElement.appendChild (aDummyDoc.createTextNode (sMessageID));
      aReferenceParametersType.add (aElement);

      final W3CEndpointReference endpointReferenceType = W3CEndpointReferenceUtils.createEndpointReference (sEndpoint,
                                                                                                            aReferenceParametersType);
      aEntry.setEndpointReference (endpointReferenceType);
      aPageList.getEntryList ().getEntry ().add (aEntry);
    }
  }

  @Nullable
  private static Document _createPageListDocument (final String [] aMessageIDs,
                                                   final int nPageSize,
                                                   final int nPageNum,
                                                   final LimeStorage aChannel,
                                                   final String sChannelID,
                                                   final String sEndpoint) throws JAXBException {
    Document pageListDocument = null;
    if (aMessageIDs.length > 0 && (aMessageIDs.length / nPageSize) >= nPageNum) {

      s_aLogger.info ("Messages in inbox: " + aMessageIDs.length);

      pageListDocument = _getPageListDocument (nPageNum, nPageSize, aMessageIDs, aChannel, sChannelID, sEndpoint);

      s_aLogger.info ("Page List created. MessageIDs=" +
                      aMessageIDs.length +
                      " pageSize=" +
                      nPageSize +
                      " pageNum=" +
                      nPageNum);
    }
    else {
      s_aLogger.info ("Page List not created. MessageIDs=" +
                      aMessageIDs.length +
                      " pageSize=" +
                      nPageSize +
                      " pageNum=" +
                      nPageNum);
    }
    return pageListDocument;
  }

  @Nullable
  private static String _xmlToString (@Nonnull final Node aNode) {
    return XMLWriter.getNodeAsString (aNode, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }
}
