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
package eu.europa.ec.cipa.transport.lime.impl;

import java.util.Date;
import java.util.UUID;

import org.w3c.dom.Document;

import eu.europa.ec.cipa.busdox.identifier.IDocumentTypeIdentifier;
import eu.europa.ec.cipa.busdox.identifier.IParticipantIdentifier;
import eu.europa.ec.cipa.busdox.identifier.IProcessIdentifier;
import eu.europa.ec.cipa.transport.lime.IMessage;

/**
 * @author Ravnholt<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public class Message implements IMessage {

  private final Date m_aCreatedTime;
  private String m_sMessageID;
  private Document m_aDocument;
  private IParticipantIdentifier m_aSender;
  private IParticipantIdentifier m_aReceiver;
  private IDocumentTypeIdentifier m_aDocumentType;
  private IProcessIdentifier m_aProcessType;

  public Message () {
    m_sMessageID = UUID.randomUUID ().toString ();
    m_aCreatedTime = new Date ();
  }

  void setMessageID (final String messageID) {
    m_sMessageID = messageID;
  }

  public Document getDocument () {
    return m_aDocument;
  }

  public void setDocument (final Document document) {
    m_aDocument = document;
  }

  public Date getCreatedTime () {
    return m_aCreatedTime;
  }

  public String getMessageID () {
    return m_sMessageID;
  }

  public IParticipantIdentifier getSender () {
    return m_aSender;
  }

  public void setSender (final IParticipantIdentifier sender) {
    m_aSender = sender;
  }

  public IParticipantIdentifier getReceiver () {
    return m_aReceiver;
  }

  public void setReceiver (final IParticipantIdentifier reciever) {
    m_aReceiver = reciever;
  }

  public IDocumentTypeIdentifier getDocumentType () {
    return m_aDocumentType;
  }

  public void setDocumentType (final IDocumentTypeIdentifier aDocumentType) {
    m_aDocumentType = aDocumentType;
  }

  public IProcessIdentifier getProcessType () {
    return m_aProcessType;
  }

  public void setProcessType (final IProcessIdentifier aProcessType) {
    m_aProcessType = aProcessType;
  }

  @Override
  public String toString () {
    final StringBuilder strBuf = new StringBuilder ();
    strBuf.append ("MESSAGE ID: " + (getMessageID () != null ? getMessageID () : ""));
    strBuf.append ("\nSENDER: " + (getSender () != null ? getSender ().getValue () : ""));
    strBuf.append ("\nSENDER TYPE: " + (getSender () != null ? getSender ().getScheme () : ""));
    strBuf.append ("\nRECIEVER: " + (getReceiver () != null ? getReceiver ().getValue () : ""));
    strBuf.append ("\nRECIEVER TYPE: " + (getReceiver () != null ? getReceiver ().getScheme () : ""));
    strBuf.append ("\nDOC: " + (getDocumentType () != null ? getDocumentType ().getValue () : ""));
    strBuf.append ("\nDOC TYPE: " + (getDocumentType () != null ? getDocumentType ().getScheme () : ""));
    strBuf.append ("\nPROCESS: " + (getProcessType () != null ? getProcessType ().getValue () : ""));
    strBuf.append ("\nPROCESS TYPE: " + (getProcessType () != null ? getProcessType ().getScheme () : ""));

    return strBuf.toString ();
  }

}
