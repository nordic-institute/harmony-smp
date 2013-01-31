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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.busdox.transport.identifiers._1.ProcessIdentifierType;
import org.busdox.transport.lime._1.MessageUndeliverableType;
import org.busdox.transport.lime._1.ObjectFactory;
import org.busdox.transport.lime._1.ReasonCodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.CreateResponse;
import org.w3._2009._02.ws_tra.Delete;
import org.w3._2009._02.ws_tra.DeleteResponse;
import org.w3._2009._02.ws_tra.Get;
import org.w3._2009._02.ws_tra.GetResponse;
import org.w3._2009._02.ws_tra.Put;
import org.w3._2009._02.ws_tra.PutResponse;
import org.w3._2009._02.ws_tra.ResourceCreated;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


import com.phloc.commons.CGlobal;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.jaxb.JAXBContextCache;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.StringParser;
import com.phloc.commons.xml.XMLFactory;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.developer.JAXWSProperties;

import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.wsaddr.W3CEndpointReferenceUtils;
import eu.europa.ec.cipa.smp.client.SMPServiceCaller;
import eu.europa.ec.cipa.transport.CTransportIdentifiers;
import eu.europa.ec.cipa.transport.IMessageMetadata;
import eu.europa.ec.cipa.transport.MessageMetadata;
import eu.europa.ec.cipa.transport.MessageMetadataHelper;
import eu.europa.ec.cipa.transport.lime.CLimeIdentifiers;
import eu.europa.ec.cipa.transport.lime.server.exception.MessageIdReusedException;
import eu.europa.ec.cipa.transport.lime.server.exception.RecipientUnreachableException;
import eu.europa.ec.cipa.transport.lime.server.storage.LimeStorage;
import eu.europa.ec.cipa.transport.lime.server.storage.MessagePage;
import eu.europa.ec.cipa.transport.start.client.AccessPointClient;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
@WebService (serviceName = "limeService",
             portName = "ResourceBindingPort",
             endpointInterface = "org.w3._2009._02.ws_tra.Resource",
             targetNamespace = "http://www.w3.org/2009/02/ws-tra",
             wsdlLocation = "WEB-INF/wsdl/peppol-lime-1.0.wsdl")
@HandlerChain (file = "WSTransferService_handler.xml")
public class LimeService {
  private static final String FAULT_UNKNOWN_ENDPOINT = "The endpoint is not known";
  private static final String FAULT_SERVER_ERROR = "ServerError";
  private static final String SERVICENAME = LimeService.class.getAnnotation (WebService.class).serviceName ();
  private static final QName QNAME_PAGEIDENTIFIER = new QName (CLimeIdentifiers.NAMESPACE_LIME,
                                                               CLimeIdentifiers.PAGEIDENTIFIER);
  private static final Logger s_aLogger = LoggerFactory.getLogger (LimeService.class);

  private static final ObjectFactory s_aObjFactory = new ObjectFactory ();

  @Resource
  private WebServiceContext webServiceContext;

  @Nonnull
  private HeaderList _getInboundHeaderList () {
    return (HeaderList) webServiceContext.getMessageContext ().get (JAXWSProperties.INBOUND_HEADER_LIST_PROPERTY);
  }

  @Nonnull
  private static W3CEndpointReference _createW3CEndpointReference (@Nonnull final String sOurAPURL,
                                                                   @Nonnull final String sChannelID,
                                                                   @Nonnull final String sMessageID) {
    final Document aDummyDoc = XMLFactory.newDocument ();
    final List <Element> aReferenceParameters = new ArrayList <Element> ();

    // Channel ID
    Element aElement = aDummyDoc.createElementNS (CTransportIdentifiers.NAMESPACE_TRANSPORT_IDS,
                                                  CLimeIdentifiers.CHANNELID);
    aElement.appendChild (aDummyDoc.createTextNode (sChannelID));
    aReferenceParameters.add (aElement);

    // Message ID
    aElement = aDummyDoc.createElementNS (CTransportIdentifiers.NAMESPACE_TRANSPORT_IDS, CLimeIdentifiers.MESSAGEID);
    aElement.appendChild (aDummyDoc.createTextNode (sMessageID));
    aReferenceParameters.add (aElement);

    return W3CEndpointReferenceUtils.createEndpointReference (sOurAPURL, aReferenceParameters);
  }

  @Nonnull
  private static CreateResponse _createCreateResponse (@Nonnull final String sOurAPURL,
                                                       @Nonnull final String sChannelID,
                                                       @Nonnull final String sMessageID) {
    final W3CEndpointReference w3CEndpointReference = _createW3CEndpointReference (sOurAPURL, sChannelID, sMessageID);

    final CreateResponse ret = new CreateResponse ();
    final ResourceCreated aResourceCreated = new ResourceCreated ();
    aResourceCreated.getEndpointReference ().add (w3CEndpointReference);
    ret.setResourceCreated (aResourceCreated);
    return ret;
  }

  /**
   * Called to initiate a new message. All standard PEPPOL SOAP headers except
   * messageID must be passed in. The messageID is created in this method and
   * returned.
   * 
   * @param body
   *        The body - is ignored. May be <code>null</code>.
   * @return A non-<code>null</code> response containing a
   *         {@link ResourceCreated} object containing our AP URL, message ID
   *         and channel ID.
   */
  @Nonnull
  public CreateResponse create (@Nullable final Create body) {
    // Create a new unique messageID
    final String sMessageID = "uuid:" + UUID.randomUUID ().toString ();
    final String sOurAPURL = _getOurAPURL ();

    IMessageMetadata aMetadata = null;

    try {
      // Grabs the list of headers from the SOAP message
      final HeaderList aHeaderList = _getInboundHeaderList ();
      aMetadata = MessageMetadataHelper.createMetadataFromHeadersWithCustomMessageID (aHeaderList, sMessageID);

      if (ResourceMemoryStore.getInstance ().createResource (sMessageID, sOurAPURL, aMetadata).isUnchanged ())
        throw new MessageIdReusedException ("Message id '" +
                                            sMessageID +
                                            "' is reused by this LIME service. Seems like we have a problem with the UUID generator");
    }
    catch (final Exception ex) {
      throw _createSoapFault (FAULT_SERVER_ERROR, ex);
    }

    // Will not happen
    if (aMetadata == null)
      throw _createSoapFault (FAULT_SERVER_ERROR, new IllegalStateException ());

    return _createCreateResponse (sOurAPURL, aMetadata.getChannelID (), sMessageID);
  }

  /**
   * After {@link #create(Create)} the main document can be transmitted using
   * this method. Expects the message ID from {@link #create(Create)} as a SOAP
   * header.
   * 
   * @param aBody
   *        The message to be put.
   * @return An empty, non-<code>null</code> put response.
   */
  @Nonnull
  public PutResponse put (@Nonnull final Put aBody) {
    final HeaderList aHeaderList = _getInboundHeaderList ();
    final String sMessageID = MessageMetadataHelper.getMessageID (aHeaderList);
    final String sOwnAPURL = _getOurAPURL ();
    final IMessageMetadata aMetadata = ResourceMemoryStore.getInstance ().getMessage (sMessageID, sOwnAPURL);

    try {
      if (aMetadata == null)
        throw new IllegalStateException ("No such message ID found: " + sMessageID);

      final String sRecipientAccessPointURLstr = _getAccessPointUrl (aMetadata.getRecipientID (),
                                                                     aMetadata.getDocumentTypeID (),
                                                                     aMetadata.getProcessID ());
      final String sSenderAccessPointURLstr = _getAccessPointUrl (aMetadata.getSenderID (),
                                                                  aMetadata.getDocumentTypeID (),
                                                                  aMetadata.getProcessID ());

      if (sRecipientAccessPointURLstr.equalsIgnoreCase (sSenderAccessPointURLstr)) {
        _logRequest ("This is a local request - sending directly to inbox",
                     sOwnAPURL,
                     aMetadata,
                     "INBOX: " + aMetadata.getRecipientID ().getValue ());
        _sendToInbox (aMetadata, aBody);
      }
      else {
        _logRequest ("This is a request for a remote access point",
                     sSenderAccessPointURLstr,
                     aMetadata,
                     sRecipientAccessPointURLstr);
        _sendToAccessPoint (aBody, sRecipientAccessPointURLstr, aMetadata);
      }
    }
    catch (final RecipientUnreachableException ex) {
      _sendMessageUndeliverable (ex, sMessageID, ReasonCodeType.TRANSPORT_ERROR, aMetadata);
      throw _createSoapFault (FAULT_UNKNOWN_ENDPOINT, ex);
    }
    catch (final Exception ex) {
      _sendMessageUndeliverable (ex, sMessageID, ReasonCodeType.OTHER_ERROR, aMetadata);
      throw _createSoapFault (FAULT_SERVER_ERROR, ex);
    }
    return new PutResponse ();
  }

  /**
   * Retrieve a list of messages, or a certain message from the inbox.
   * 
   * @param body
   *        Ignored - may be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public GetResponse get (@Nullable final Get body) {
    final HeaderList aHeaderList = _getInboundHeaderList ();
    final String sChannelID = MessageMetadataHelper.getChannelID (aHeaderList);
    final String sMessageID = MessageMetadataHelper.getMessageID (aHeaderList);
    final String sPageIdentifier = MessageMetadataHelper.getStringContent (aHeaderList.get (QNAME_PAGEIDENTIFIER, false));

    final GetResponse aGetResponse = new GetResponse ();
    try {
      final String sStorageRoot = ((ServletContext) webServiceContext.getMessageContext ()
                                                                     .get (MessageContext.SERVLET_CONTEXT)).getRealPath ("/");
      if (StringHelper.hasNoText (sMessageID))
        _addPageListToResponse (sStorageRoot, sPageIdentifier, sChannelID, aGetResponse);
      else
        _addSingleMessageToResponse (sStorageRoot, sChannelID, sMessageID, aGetResponse);
    }
    catch (final Exception ex) {
      s_aLogger.error ("Error on get", ex);
    }
    return aGetResponse;
  }

  /**
   * Delete
   * 
   * @param body
   *        delete body
   * @return response
   */
  public DeleteResponse delete (final Delete body) {
    final HeaderList aHeaderList = _getInboundHeaderList ();
    final String channelID = MessageMetadataHelper.getChannelID (aHeaderList);
    final String messageID = MessageMetadataHelper.getMessageID (aHeaderList);
    try {
      final String sStorageRoot = ((ServletContext) webServiceContext.getMessageContext ()
                                                                     .get (MessageContext.SERVLET_CONTEXT)).getRealPath ("/");
      new LimeStorage (sStorageRoot).deleteDocument (channelID, messageID);
    }
    catch (final Exception ex) {
      s_aLogger.error ("Error deleting document", ex);
    }
    return new DeleteResponse ();
  }

  private void _sendMessageUndeliverable (@Nonnull final Exception ex,
                                          @Nullable final String sMessageID,
                                          @Nonnull final ReasonCodeType eReasonCode,
                                          @Nullable final IMessageMetadata aMetadata) {
    if (aMetadata == null) {
      s_aLogger.error ("No message metadata found. Unable to send MessageUndeliverable for Message ID: " + sMessageID);
    }
    else {
      try {
        s_aLogger.warn ("Unable to send MessageUndeliverable for Message ID: " +
                        sMessageID +
                        " Reason: " +
                        ex.getMessage ());

        final MessageUndeliverableType aMsgUndeliverable = s_aObjFactory.createMessageUndeliverableType ();
        aMsgUndeliverable.setMessageIdentifier (sMessageID);
        aMsgUndeliverable.setReasonCode (eReasonCode);
        aMsgUndeliverable.setDetails ("(" +
                                      aMetadata.getRecipientID ().getScheme () +
                                      "," +
                                      aMetadata.getRecipientID ().getValue () +
                                      ") " +
                                      ex.getMessage ());

        final IMessageMetadata aRealMetadata = new MessageMetadata (aMetadata.getMessageID (),
                                                                    aMetadata.getChannelID (),
                                                                    CLimeIdentifiers.MESSAGEUNDELIVERABLE_SENDER,
                                                                    aMetadata.getSenderID (),
                                                                    CLimeIdentifiers.MESSAGEUNDELIVERABLE_DOCUMENT,
                                                                    CLimeIdentifiers.MESSAGEUNDELIVERABLE_PROCESS);

        final Document aDocument = XMLFactory.newDocument ();
        final Marshaller aMarshaller = JAXBContextCache.getInstance ()
                                                       .getFromCache (MessageUndeliverableType.class)
                                                       .createMarshaller ();
        aMarshaller.marshal (s_aObjFactory.createMessageUndeliverable (aMsgUndeliverable), new DOMResult (aDocument));

        // Create a dummy "put" and send it to the inbox of the sender
        final Put put = new Put ();
        put.getAny ().add (aDocument.getDocumentElement ());
        _sendToInbox (aRealMetadata, put);
      }
      catch (final Exception ex1) {
        s_aLogger.error ("Unable to send MessageUndeliverable for Message ID: " + sMessageID, ex1);
      }
    }
  }

  @Nonnull
  private static SOAPFaultException _createSoapFault (final String faultMessage, final Exception e) throws RuntimeException {
    try {
      s_aLogger.info ("Server error", e);
      final SOAPFault soapFault = SOAPFactory.newInstance ().createFault ();
      soapFault.setFaultString (faultMessage);
      soapFault.setFaultCode (new QName (SOAPConstants.URI_NS_SOAP_ENVELOPE, "Sender"));
      soapFault.setFaultActor ("LIME AP");
      return new SOAPFaultException (soapFault);
    }
    catch (final SOAPException e2) {
      throw new RuntimeException ("Problem processing SOAP Fault on service-side", e2);
    }
  }

  private static void _addSingleMessageToResponse (final String sStorageRoot,
                                                   final String sChannelID,
                                                   final String sMessageID,
                                                   final GetResponse getResponse) throws SAXException {
    final LimeStorage aStorage = new LimeStorage (sStorageRoot);
    final Document documentMetadata = aStorage.getDocumentMetadata (sChannelID, sMessageID);
    final Document document = aStorage.getDocument (sChannelID, sMessageID);
    getResponse.getAny ().add (documentMetadata.getDocumentElement ());
    getResponse.getAny ().add (document.getDocumentElement ());
  }

  @Nonnull
  private String _getOurAPURL () {
    // FIXME read this from the configuration file for easily correct handling
    // of the endpoint URL
    final ServletRequest servletRequest = (ServletRequest) webServiceContext.getMessageContext ()
                                                                            .get (MessageContext.SERVLET_REQUEST);
    final String contextPath = ((ServletContext) webServiceContext.getMessageContext ()
                                                                  .get (MessageContext.SERVLET_CONTEXT)).getContextPath ();
    final String thisAccessPointURLstr = servletRequest.getScheme () +
                                         "://" +
                                         servletRequest.getServerName () +
                                         ":" +
                                         servletRequest.getLocalPort () +
                                         contextPath +
                                         '/';
    return thisAccessPointURLstr + SERVICENAME;
  }

  private void _addPageListToResponse (@Nonnull @Nonempty final String sStorageRoot,
                                       @Nullable final String sPageNumber,
                                       final String sChannelID,
                                       final GetResponse aGetResponse) throws Exception {
    final String sOwnAPURL = _getOurAPURL ();
    final int nPageNumber = StringParser.parseInt (StringHelper.trim (sPageNumber), 0);
    final Document aDocument = MessagePage.getPageList (nPageNumber,
                                                        sOwnAPURL,
                                                        new LimeStorage (sStorageRoot),
                                                        sChannelID);
    if (aDocument != null)
      aGetResponse.getAny ().add (aDocument.getDocumentElement ());
  }

  private static void _logRequest (@Nullable final String sAction,
                                   @Nullable final String sOwnUrl,
                                   @Nonnull final IMessageMetadata aMetadata,
                                   @Nullable final String sReceiverID) {
    final String s = "REQUEST start--------------------------------------------------" +
                     CGlobal.LINE_SEPARATOR +
                     "Action: " +
                     sAction +
                     CGlobal.LINE_SEPARATOR +
                     "Own URL: " +
                     sOwnUrl +
                     CGlobal.LINE_SEPARATOR +
                     "Sending to : " +
                     sReceiverID +
                     CGlobal.LINE_SEPARATOR +
                     "Messsage ID: " +
                     aMetadata.getMessageID () +
                     CGlobal.LINE_SEPARATOR +
                     "Sender ID: " +
                     aMetadata.getSenderID ().getValue () +
                     CGlobal.LINE_SEPARATOR +
                     "Sender type: " +
                     aMetadata.getSenderID ().getScheme () +
                     CGlobal.LINE_SEPARATOR +
                     "Recipient ID: " +
                     aMetadata.getRecipientID ().getValue () +
                     CGlobal.LINE_SEPARATOR +
                     "Recipient type: " +
                     aMetadata.getRecipientID ().getScheme () +
                     CGlobal.LINE_SEPARATOR +
                     "Document ID: " +
                     aMetadata.getDocumentTypeID ().getValue () +
                     CGlobal.LINE_SEPARATOR +
                     "Document type: " +
                     aMetadata.getDocumentTypeID ().getScheme () +
                     CGlobal.LINE_SEPARATOR +
                     "Process ID: " +
                     aMetadata.getProcessID ().getValue () +
                     CGlobal.LINE_SEPARATOR +
                     "Process type: " +
                     aMetadata.getProcessID ().getScheme () +
                     CGlobal.LINE_SEPARATOR +
                     "REQUEST end----------------------------------------------------" +
                     CGlobal.LINE_SEPARATOR;
    s_aLogger.info (s);
  }

  private static void _sendToAccessPoint (@Nonnull final Put aBody,
                                          @Nonnull final String sStartAPEndpointAddress,
                                          final IMessageMetadata aMetadata) throws Exception {
    final Create createBody = new Create ();
    createBody.getAny ().addAll (aBody.getAny ());
    AccessPointClient.send (sStartAPEndpointAddress, aMetadata, createBody);
  }

  private void _sendToInbox (@Nonnull final IMessageMetadata aMetadata, @Nonnull final Put aBody) throws RecipientUnreachableException {
    final String sStorageChannelID = aMetadata.getRecipientID ().getValue ();
    if (sStorageChannelID == null)
      throw new RecipientUnreachableException ("Unknown recipient at LIME-AP: " + aMetadata.getRecipientID ());

    // Extract the message ID from the incoming message SOAP headers
    final HeaderList aHeaderList = _getInboundHeaderList ();
    final String sMessageID = MessageMetadataHelper.getMessageID (aHeaderList);

    s_aLogger.info ("Recipient: " + aMetadata.getRecipientID () + "; ChannelID: " + sStorageChannelID);

    try {
      final List <Object> aObjects = aBody.getAny ();
      if (ContainerHelper.getSize (aObjects) == 1) {
        final Node aElement = (Node) ContainerHelper.getFirstElement (aObjects);
        final Document aDocument = aElement.getOwnerDocument ();
        final Document metadataDocument = MessageMetadataHelper.createHeadersDocument (aMetadata);

        final String sStorageRoot = ((ServletContext) webServiceContext.getMessageContext ()
                                                                       .get (MessageContext.SERVLET_CONTEXT)).getRealPath ("/");
        new LimeStorage (sStorageRoot).saveDocument (sStorageChannelID, sMessageID, metadataDocument, aDocument);
      }
    }
    catch (final Exception ex) {
      s_aLogger.error ("Failed to handle incoming LIME document", ex);
      throw new RecipientUnreachableException ("Failed to handle incoming LIME document", ex);
    }
  }

  @Nullable
  private static String _getAccessPointUrl (final ParticipantIdentifierType aRecipientId,
                                            final DocumentIdentifierType aDocumentID,
                                            final ProcessIdentifierType aProcessID) throws Exception {
    final String ret = new SMPServiceCaller (aRecipientId, ESML.PRODUCTION).getEndpointAddress (aRecipientId,
                                                                                                aDocumentID,
                                                                                                aProcessID);
    if (ret == null)
      s_aLogger.warn ("Failed to resolve AP endpoint url for recipient " +
                      aRecipientId +
                      ", document type " +
                      aDocumentID +
                      " and process " +
                      aProcessID);
    return ret;
  }
}
