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
package eu.europa.ec.cipa.sml.server.datamodel;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.busdox.servicemetadata.locator._1.MigrationRecordType;

import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.db.jpa.annotations.UsedOnlyByJPA;

import eu.europa.ec.cipa.peppol.identifier.CIdentifier;
import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;

/**
 * MigrateId generated by hbm2java
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Embeddable
public class DBMigrateID implements Serializable {
  private String m_sParticipantIDScheme;
  private String m_sParticipantIDValue;
  private String m_sMigrationCode;

  @Deprecated
  @UsedOnlyByJPA
  public DBMigrateID () {}

  public DBMigrateID (@Nonnull final MigrationRecordType aMigrationRecord) {
    setRecipientParticipantIdentifierScheme (aMigrationRecord.getParticipantIdentifier ().getScheme ());
    setRecipientParticipantIdentifierValue (aMigrationRecord.getParticipantIdentifier ().getValue ());
    setMigrationCode (aMigrationRecord.getMigrationKey ());
  }

  @Column (name = "recipient_participant_identifier_scheme",
           nullable = false,
           length = CIdentifier.MAX_IDENTIFIER_SCHEME_LENGTH)
  public String getRecipientParticipantIdentifierScheme () {
    return m_sParticipantIDScheme;
  }

  public void setRecipientParticipantIdentifierScheme (final String sScheme) {
    m_sParticipantIDScheme = IdentifierUtils.getUnifiedParticipantDBValue (sScheme);
  }

  @Column (name = "recipient_participant_identifier_value",
           nullable = false,
           length = CIdentifier.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH)
  public String getRecipientParticipantIdentifierValue () {
    return m_sParticipantIDValue;
  }

  public void setRecipientParticipantIdentifierValue (final String sValue) {
    m_sParticipantIDValue = IdentifierUtils.getUnifiedParticipantDBValue (sValue);
  }

  @Column (name = "migration_code", nullable = false, length = CDB.MAX_MIGRATION_CODE_LENGTH)
  public String getMigrationCode () {
    return m_sMigrationCode;
  }

  public void setMigrationCode (final String sMigrationCode) {
    m_sMigrationCode = sMigrationCode;
  }

  @Nonnull
  public SimpleParticipantIdentifier asParticipantIdentifier () {
    return new SimpleParticipantIdentifier (m_sParticipantIDScheme, m_sParticipantIDValue);
  }

  @Override
  public boolean equals (final Object other) {
    if (this == other)
      return true;
    if (!(other instanceof DBMigrateID))
      return false;
    final DBMigrateID rhs = (DBMigrateID) other;
    return EqualsUtils.equals (m_sParticipantIDScheme, rhs.m_sParticipantIDScheme) &&
           EqualsUtils.equals (m_sParticipantIDValue, rhs.m_sParticipantIDValue) &&
           EqualsUtils.equals (m_sMigrationCode, rhs.m_sMigrationCode);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sParticipantIDScheme)
                                       .append (m_sParticipantIDValue)
                                       .append (m_sMigrationCode)
                                       .getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("participantIDScheme", m_sParticipantIDScheme)
                                       .append ("participantIDValue", m_sParticipantIDValue)
                                       .append ("migrationCode", m_sMigrationCode)
                                       .toString ();
  }
}
