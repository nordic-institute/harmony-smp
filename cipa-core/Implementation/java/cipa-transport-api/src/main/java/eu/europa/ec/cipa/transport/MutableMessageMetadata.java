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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

import eu.europa.ec.cipa.busdox.identifier.IReadonlyDocumentTypeIdentifier;
import eu.europa.ec.cipa.busdox.identifier.IReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.busdox.identifier.IReadonlyProcessIdentifier;
import eu.europa.ec.cipa.peppol.identifier.doctype.SimpleDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.SimpleProcessIdentifier;

/**
 * A MessageMetadata object is used to storage the message addressing data
 * incoming in the SOAP header through a SOAPHeaderObject object.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public final class MutableMessageMetadata implements IMessageMetadata {
  /** The message identifier. */
  private String m_sMessageID;

  /** The channel identifier. */
  private String m_sChannelID;

  /** Sender identifier value. */
  private SimpleParticipantIdentifier m_aSenderID;

  /** Recipient identifier value. */
  private SimpleParticipantIdentifier m_aRecipientID;

  /** Document identifier value. */
  private SimpleDocumentTypeIdentifier m_aDocumentTypeID;

  /** Process identifier value. */
  private SimpleProcessIdentifier m_aProcessID;

  public MutableMessageMetadata () {}

  /**
   * Set the values of the MessageMetadata object received in a SOAPHeaderObject
   * object.
   *
   * @param sMessageID
   *        message ID
   * @param sChannelID
   *        channel ID
   * @param aSenderID
   *        sender ID
   * @param aRecipientID
   *        recipient ID
   * @param aDocumentTypeID
   *        document type ID
   * @param aProcessID
   *        process ID
   */
  public MutableMessageMetadata (@Nullable final String sMessageID,
                                 @Nullable final String sChannelID,
                                 @Nonnull final IReadonlyParticipantIdentifier aSenderID,
                                 @Nonnull final IReadonlyParticipantIdentifier aRecipientID,
                                 @Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID,
                                 @Nonnull final IReadonlyProcessIdentifier aProcessID) {
    m_sMessageID = sMessageID;
    m_sChannelID = sChannelID;
    m_aSenderID = new SimpleParticipantIdentifier (aSenderID);
    m_aRecipientID = new SimpleParticipantIdentifier (aRecipientID);
    m_aDocumentTypeID = new SimpleDocumentTypeIdentifier (aDocumentTypeID);
    m_aProcessID = new SimpleProcessIdentifier (aProcessID);
  }

  public void setMessageID (@Nullable final String sMessageID) {
    m_sMessageID = sMessageID;
  }

  /**
   * Get message identifier value.
   *
   * @return the messageId the value of the message identifier.
   */
  @Nullable
  public final String getMessageID () {
    return m_sMessageID;
  }

  public void setChannelID (@Nullable final String sChannelID) {
    m_sChannelID = sChannelID;
  }

  /**
   * @return the channelId
   */
  @Nullable
  public final String getChannelID () {
    return m_sChannelID;
  }

  public void setSenderID (@Nonnull final IReadonlyParticipantIdentifier aSenderID) {
    m_aSenderID = new SimpleParticipantIdentifier (aSenderID);
  }

  /**
   * @return the senderValue
   */
  @Nullable
  public final SimpleParticipantIdentifier getSenderID () {
    return m_aSenderID;
  }

  public void setRecipientID (@Nonnull final IReadonlyParticipantIdentifier aRecipientID) {
    m_aRecipientID = new SimpleParticipantIdentifier (aRecipientID);
  }

  /**
   * @return the recipientValue
   */
  @Nullable
  public final SimpleParticipantIdentifier getRecipientID () {
    return m_aRecipientID;
  }

  public void setDocumentTypeID (@Nonnull final IReadonlyDocumentTypeIdentifier aDocumentTypeID) {
    m_aDocumentTypeID = new SimpleDocumentTypeIdentifier (aDocumentTypeID);
  }

  /**
   * @return the documentIdValue
   */
  @Nullable
  public final SimpleDocumentTypeIdentifier getDocumentTypeID () {
    return m_aDocumentTypeID;
  }

  public void setProcessID (@Nonnull final IReadonlyProcessIdentifier aProcessID) {
    m_aProcessID = new SimpleProcessIdentifier (aProcessID);
  }

  /**
   * @return the processIdValue
   */
  @Nullable
  public final SimpleProcessIdentifier getProcessID () {
    return m_aProcessID;
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MutableMessageMetadata rhs = (MutableMessageMetadata) o;
    return EqualsUtils.equals (m_sMessageID, rhs.m_sMessageID) &&
           EqualsUtils.equals (m_sChannelID, rhs.m_sChannelID) &&
           EqualsUtils.equals (m_aSenderID, rhs.m_aSenderID) &&
           EqualsUtils.equals (m_aRecipientID, rhs.m_aRecipientID) &&
           EqualsUtils.equals (m_aDocumentTypeID, rhs.m_aDocumentTypeID) &&
           EqualsUtils.equals (m_aProcessID, rhs.m_aProcessID);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sMessageID)
                                       .append (m_sChannelID)
                                       .append (m_aSenderID)
                                       .append (m_aRecipientID)
                                       .append (m_aDocumentTypeID)
                                       .append (m_aProcessID)
                                       .getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("messageID", m_sMessageID)
                                       .append ("channelID", m_sChannelID)
                                       .append ("senderID", m_aSenderID)
                                       .append ("recipientID", m_aRecipientID)
                                       .append ("documentID", m_aDocumentTypeID)
                                       .append ("processID", m_aProcessID)
                                       .toString ();
  }
}
