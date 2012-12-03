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
package at.peppol.transport.lime.client;

import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import at.peppol.busdox.CBusDox;
import at.peppol.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import at.peppol.busdox.identifier.IReadonlyIdentifier;
import at.peppol.busdox.identifier.IReadonlyParticipantIdentifier;
import at.peppol.busdox.identifier.IReadonlyProcessIdentifier;
import at.peppol.commons.identifier.doctype.EPredefinedDocumentTypeIdentifier;
import at.peppol.commons.identifier.doctype.SimpleDocumentTypeIdentifier;
import at.peppol.commons.identifier.participant.ReadonlyParticipantIdentifier;
import at.peppol.commons.identifier.participant.SimpleParticipantIdentifier;
import at.peppol.commons.identifier.process.EPredefinedProcessIdentifier;
import at.peppol.commons.identifier.process.SimpleProcessIdentifier;
import at.peppol.commons.utils.IReadonlyUsernamePWCredentials;
import at.peppol.commons.utils.UsernamePWCredentials;
import at.peppol.transport.lime.IEndpointReference;
import at.peppol.transport.lime.IInbox;
import at.peppol.transport.lime.IMessage;
import at.peppol.transport.lime.IMessageReference;
import at.peppol.transport.lime.MessageException;
import at.peppol.transport.lime.impl.EndpointReference;
import at.peppol.transport.lime.impl.Inbox;
import at.peppol.transport.lime.impl.Message;
import at.peppol.transport.lime.impl.MessageReference;
import at.peppol.transport.lime.impl.Outbox;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.serialize.XMLReader;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public final class MainLimeClient {
  public static final int POLL_SLEEP_MS = 3000;
  private static boolean s_bLeaveMessages = false;
  private static final IReadonlyParticipantIdentifier SENDER = ReadonlyParticipantIdentifier.createWithDefaultScheme ("9914:atu415427xx");
  private static final IReadonlyParticipantIdentifier RECEIVER = ReadonlyParticipantIdentifier.createWithDefaultScheme ("9914:iwannatest");
  private static final IReadonlyDocumentTypeIdentifier DOCID = EPredefinedDocumentTypeIdentifier.ORDER_T001_BIS6A;
  private static final IReadonlyProcessIdentifier PROCESS = EPredefinedProcessIdentifier.BIS6A;

  public static void main (final String [] args) throws Exception {
    if (false)
      CBusDox.setMetroDebugSystemProperties (true);

    final String apUrl2 = "http://localhost:8091/limeService";
    // any xml will do
    final IReadableResource xmlFile = new ClassPathResource ("xml/CENBII-Order-maximal.xml");

    _testSend (apUrl2, xmlFile, SENDER, RECEIVER);
    if (false)
      _testReadAndDelete (apUrl2, RECEIVER);
    if (false)
      _testMessageUndeliverable (apUrl2, xmlFile, SENDER, RECEIVER);
  }

  private static void _testReadAndDelete (final String apUrl, final IReadonlyIdentifier receiverID) throws Exception {
    final String channelID = receiverID.getValue ();
    final IEndpointReference endpointReference = new EndpointReference ();
    endpointReference.setAddress (apUrl);
    endpointReference.setChannelID (channelID);

    _testPollForMessages (endpointReference, s_bLeaveMessages);
  }

  private static void _testMessageUndeliverable (final String apUrl,
                                                 final IReadableResource xml,
                                                 final IReadonlyParticipantIdentifier senderID,
                                                 final IReadonlyParticipantIdentifier receiverID) throws Exception {
    final IReadonlyParticipantIdentifier unFindable = new SimpleParticipantIdentifier (receiverID.getScheme (),
                                                                                       receiverID.getValue () +
                                                                                           "UNKNOWN");
    final String channelID = senderID.getValue ();
    final IEndpointReference endpointReference = new EndpointReference ();
    endpointReference.setAddress (apUrl);
    endpointReference.setChannelID (channelID);

    try {
      final IMessage message = _createSampleMessage (xml, senderID, unFindable, DOCID, PROCESS);
      _testSendMessage (message, endpointReference);
    }
    catch (final Exception e) {
      // Exception is OK. The method sendMessage fails because of invalid
      // recipient
    }

    final String lastMessage = _testGetLastMessage (endpointReference);
    _testGetMessage (lastMessage, endpointReference);
    _testDeleteMessage (lastMessage, endpointReference);
  }

  private static String _testSend (final String apUrl,
                                   final IReadableResource xml,
                                   final IReadonlyParticipantIdentifier senderID,
                                   final IReadonlyParticipantIdentifier receiverID) throws Exception {
    final String channelID = senderID.getValue ();
    final IEndpointReference endpointReference = new EndpointReference ();
    endpointReference.setAddress (apUrl);
    endpointReference.setChannelID (channelID);

    final IMessage message = _createSampleMessage (xml, senderID, receiverID, DOCID, PROCESS);
    final String messageID = _testSendMessage (message, endpointReference);
    return messageID;
  }

  private static IReadonlyUsernamePWCredentials _createCredentials () {
    return new UsernamePWCredentials ("peppol", "peppol");
  }

  private static String _testSendMessage (final IMessage message, final IEndpointReference endpointReference) throws MessageException {
    String messageid = null;
    for (int i = 0; i < 1; i++) {
      messageid = new Outbox ().sendMessage (_createCredentials (), message, endpointReference);
      System.out.println ("OUTBOX - MESSAGE DELIVERED: " + messageid);
    }
    return messageid;
  }

  private static void _testGetMessage (final String messageID, final IEndpointReference endpointReference) throws MessageException,
                                                                                                          TransformerException,
                                                                                                          TransformerFactoryConfigurationError {
    final IMessageReference messageReference = new MessageReference ();
    messageReference.setMessageId (messageID);
    messageReference.setEndpointReference (endpointReference);
    final IMessage fetchedMessage = new Inbox ().getMessage (_createCredentials (), messageReference);
    if (fetchedMessage != null) {
      System.out.println ("INBOX - MESSAGE: " + messageID);
      System.out.println (fetchedMessage);
      _streamMessage (fetchedMessage, System.out);
    }
    else {
      System.out.println ("INBOX - MESSAGE NOT FOUND: " + messageID);
    }
  }

  private static String _testGetLastMessage (final IEndpointReference endpointReference) throws MessageException,
                                                                                        TransformerFactoryConfigurationError {
    String lastMessage = null;
    final List <IMessageReference> messageReferences = new Inbox ().getMessageList (_createCredentials (),
                                                                                    endpointReference);
    if (messageReferences != null && messageReferences.size () > 0) {
      for (final IMessageReference messageReference : messageReferences) {
        System.out.println ("INBOX - MESSAGE: " + messageReference.getMessageID ());
        lastMessage = messageReference.getMessageID ();
      }
    }
    else {
      System.out.println ("INBOX - NO MESSAGES");
    }
    return lastMessage;
  }

  private static void _testDeleteMessage (final String messageID, final IEndpointReference endpointReference) throws MessageException,
                                                                                                             TransformerFactoryConfigurationError {
    final IMessageReference messageReference = new MessageReference ();
    messageReference.setMessageId (messageID);
    messageReference.setEndpointReference (endpointReference);
    new Inbox ().deleteMessage (_createCredentials (), messageReference);
    System.out.println ("INBOX - MESSAGE DELETED: " + messageID);
  }

  private static void _testPollForMessages (final IEndpointReference endpointReference, final boolean leaveMessages) throws TransformerConfigurationException,
                                                                                                                    TransformerException {
    final IInbox inbox = new Inbox ();
    final long millis = POLL_SLEEP_MS;
    try {
      final List <IMessageReference> messageReferences = inbox.getMessageList (_createCredentials (), endpointReference);
      if (messageReferences != null && messageReferences.size () > 0) {
        System.out.println ("INBOX - RETRIEVED " +
                            messageReferences.size () +
                            " MESSAGES AT " +
                            (new Date ()).toString ());
        final int index = 1;
        for (final IMessageReference messageReference : messageReferences) {
          try {
            final IMessage message = inbox.getMessage (_createCredentials (), messageReference);
            _streamMessage (message, System.out);
            System.out.println ("INBOX - MESSAGE (" + index + "/" + messageReferences.size () + ")");
            System.out.println (message);
            if (!leaveMessages) {
              // clean up
              inbox.deleteMessage (_createCredentials (), messageReference);
              System.out.println ("INBOX - MESSAGE DELETED: " + message.getMessageID ());
            }
            System.out.println ();
          }
          catch (final MessageException ex) {
            System.out.println ("INBOX - MESSAGE GET THROWS EXCEPTION: " + ex.getMessage ());
          }
        }
        Thread.sleep (millis);
      }
      else {
        System.out.println ("INBOX - EMPTY");
      }
    }
    catch (final InterruptedException ex) {
      System.out.println ("Caught InterruptedException: " + ex.getMessage ());
    }
    catch (final MessageException ex) {
      System.out.println ("Caught MessageException: " + ex.getMessage ());
    }
  }

  private static Document _loadXML (final IReadableResource xml) throws SAXException {
    return XMLReader.readXMLDOM (xml);
  }

  private static IMessage _createSampleMessage (final IReadableResource xml,
                                                final IReadonlyParticipantIdentifier senderID,
                                                final IReadonlyParticipantIdentifier receiverID,
                                                final IReadonlyDocumentTypeIdentifier documentID,
                                                final IReadonlyProcessIdentifier processID) throws SAXException {
    final IMessage message = new Message ();
    message.setDocument (_loadXML (xml));
    message.setDocumentType (new SimpleDocumentTypeIdentifier (documentID));
    message.setSender (new SimpleParticipantIdentifier (senderID));
    message.setReceiver (new SimpleParticipantIdentifier (receiverID));
    message.setProcessType (new SimpleProcessIdentifier (processID));
    return message;
  }

  private static void _streamMessage (final IMessage fetchedMessage, final PrintStream out) throws TransformerConfigurationException,
                                                                                           TransformerException {
    final TransformerFactory transformerFactory = TransformerFactory.newInstance ();
    final Transformer transformer = transformerFactory.newTransformer ();
    final DOMSource source = new DOMSource (fetchedMessage.getDocument ());
    final StreamResult result = new StreamResult (out);
    transformer.transform (source, result);
  }
}
