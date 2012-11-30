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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import eu.europa.ec.cipa.peppol.identifier.CIdentifier;
import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.identifier.doctype.ReadonlyDocumentTypeIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.ReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.process.ReadonlyProcessIdentifier;

/**
 * This file contains the PING message specific codes
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class PingMessageHelper {
  public static final String PING_SENDER_SCHEME = "busdox-actorid-transport";
  public static final String PING_SENDER_VALUE = "busdox:sender";
  public static final ReadonlyParticipantIdentifier PING_SENDER = new ReadonlyParticipantIdentifier (PING_SENDER_SCHEME,
                                                                                                     PING_SENDER_VALUE);
  public static final String PING_RECIPIENT_SCHEME = "busdox-actorid-transport";
  public static final String PING_RECIPIENT_VALUE = "busdox:recipient";
  public static final ReadonlyParticipantIdentifier PING_RECIPIENT = new ReadonlyParticipantIdentifier (PING_RECIPIENT_SCHEME,
                                                                                                        PING_RECIPIENT_VALUE);
  public static final String PING_DOCUMENT_TYPE_SCHEME = CIdentifier.DEFAULT_DOCUMENT_TYPE_IDENTIFIER_SCHEME;
  public static final String PING_DOCUMENT_TYPE_VALUE = "busdox:ping";
  public static final ReadonlyDocumentTypeIdentifier PING_DOCUMENT_TYPE = new ReadonlyDocumentTypeIdentifier (PING_DOCUMENT_TYPE_SCHEME,
                                                                                                              PING_DOCUMENT_TYPE_VALUE);
  @Deprecated
  public static final String PING_DOCUMENT_SCHEME = PING_DOCUMENT_TYPE_SCHEME;
  @Deprecated
  public static final String PING_DOCUMENT_VALUE = PING_DOCUMENT_TYPE_VALUE;
  @Deprecated
  public static final ReadonlyDocumentTypeIdentifier PING_DOCUMENT = PING_DOCUMENT_TYPE;
  public static final String PING_PROCESS_SCHEME = "busdox-procid-transport";
  public static final String PING_PROCESS_VALUE = CIdentifier.DEFAULT_PROCESS_IDENTIFIER_NOPROCESS;
  public static final ReadonlyProcessIdentifier PING_PROCESS = new ReadonlyProcessIdentifier (PING_PROCESS_SCHEME,
                                                                                              PING_PROCESS_VALUE);

  private PingMessageHelper () {}

  public static boolean isPingMessage (@Nullable final IMessageMetadata aMetadata) {
    return aMetadata != null &&
           aMetadata.getSenderID () != null &&
           IdentifierUtils.areIdentifiersEqual (aMetadata.getSenderID (), PING_SENDER) &&
           aMetadata.getRecipientID () != null &&
           IdentifierUtils.areIdentifiersEqual (aMetadata.getRecipientID (), PING_RECIPIENT) &&
           aMetadata.getDocumentTypeID () != null &&
           IdentifierUtils.areIdentifiersEqual (aMetadata.getDocumentTypeID (), PING_DOCUMENT) &&
           aMetadata.getProcessID () != null &&
           IdentifierUtils.areIdentifiersEqual (aMetadata.getProcessID (), PING_PROCESS);
  }
}
