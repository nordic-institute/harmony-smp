/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.cipa.smp.server.data.dbms.model;

import com.helger.commons.annotations.UsedViaReflection;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import eu.europa.ec.cipa.smp.server.util.IdentifierUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;

import static eu.europa.ec.cipa.smp.server.data.dbms.model.CommonColumnsLengths.MAX_IDENTIFIER_SCHEME_LENGTH;
import static eu.europa.ec.cipa.smp.server.data.dbms.model.CommonColumnsLengths.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH;

/**
 * ServiceGroupId == participant ID
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Embeddable
public class DBServiceGroupID implements Serializable {
  private String m_sParticipantIdentifierScheme;
  private String m_sParticipantIdentifier;

  @Deprecated
  @UsedViaReflection
  public DBServiceGroupID () {}

  public DBServiceGroupID (@Nonnull final ParticipantIdentifierType aBusinessID) {
    setBusinessIdentifierScheme (aBusinessID.getScheme ());
    setBusinessIdentifier (aBusinessID.getValue ());
  }

  @Column (name = "businessIdentifierScheme", nullable = false, length = MAX_IDENTIFIER_SCHEME_LENGTH)
  public String getBusinessIdentifierScheme () {
    return m_sParticipantIdentifierScheme;
  }

  public void setBusinessIdentifierScheme (final String sBusinessIdentifierScheme) {
    m_sParticipantIdentifierScheme = sBusinessIdentifierScheme;
  }

  @Column (name = "businessIdentifier", nullable = false, length = MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH)
  public String getBusinessIdentifier () {
    return m_sParticipantIdentifier;
  }

  public void setBusinessIdentifier (final String sBusinessIdentifier) {
    m_sParticipantIdentifier = sBusinessIdentifier;
  }

  @Transient
  @Nonnull
  public ParticipantIdentifierType asBusinessIdentifier() {
      return new ParticipantIdentifierType(m_sParticipantIdentifier, m_sParticipantIdentifierScheme);
  }

  @Override
  public boolean equals (final Object o) {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final DBServiceGroupID rhs = (DBServiceGroupID) o;
    return EqualsUtils.equals (m_sParticipantIdentifierScheme, rhs.m_sParticipantIdentifierScheme) &&
           EqualsUtils.equals (m_sParticipantIdentifier, rhs.m_sParticipantIdentifier);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sParticipantIdentifierScheme)
                                       .append (m_sParticipantIdentifier)
                                       .getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("participantIDScheme", m_sParticipantIdentifierScheme)
                                       .append ("participantIDValue", m_sParticipantIdentifier)
                                       .toString ();
  }
}
