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
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessIdentifier;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;

import static eu.europa.ec.cipa.smp.server.data.dbms.model.CommonColumnsLengths.*;

/**
 * The ID of a single process.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Embeddable
public class DBProcessID implements Serializable {
  private String m_sParticipantIdentifierScheme;
  private String m_sParticipantIdentifier;
  private String m_sDocumentTypeIdentifierScheme;
  private String m_sDocumentTypeIdentifier;
  private String m_sProcessIdentifierScheme;
  private String m_sProcessIdentifier;

  @Deprecated
  @UsedViaReflection
  public DBProcessID () {}

  public DBProcessID (@Nonnull final DBServiceMetadataID aSMID, @Nonnull final DocumentIdentifier aPrI) {
    setBusinessIdentifier (aSMID.asBusinessIdentifier ());
    setDocumentTypeIdentifier (aSMID.asDocumentTypeIdentifier ());
    setProcessIdentifier (aPrI);
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
  public void setBusinessIdentifier (@Nonnull final ParticipantIdentifierType aBusinessIdentifier) {
    setBusinessIdentifierScheme (aBusinessIdentifier.getScheme ());
    setBusinessIdentifier (aBusinessIdentifier.getValue ());
  }

  @Column (name = "documentIdentifierScheme", nullable = false, length = MAX_IDENTIFIER_SCHEME_LENGTH)
  public String getDocumentIdentifierScheme () {
    return m_sDocumentTypeIdentifierScheme;
  }

  public void setDocumentIdentifierScheme (final String sDocumentIdentifierScheme) {
    m_sDocumentTypeIdentifierScheme = sDocumentIdentifierScheme;
  }

  @Column (name = "documentIdentifier", nullable = false, length = MAX_DOCUMENT_TYPE_IDENTIFIER_VALUE_LENGTH)
  public String getDocumentIdentifier () {
    return m_sDocumentTypeIdentifier;
  }

  public void setDocumentIdentifier (final String sDocumentIdentifier) {
    m_sDocumentTypeIdentifier = sDocumentIdentifier;
  }

  @Transient
  public void setDocumentTypeIdentifier (@Nonnull final DocumentIdentifier aDocumentTypeID) {
    setDocumentIdentifierScheme (aDocumentTypeID.getScheme ());
    setDocumentIdentifier (aDocumentTypeID.getValue ());
  }

  @Column (name = "processIdentifierType", nullable = false, length = MAX_IDENTIFIER_SCHEME_LENGTH)
  public String getProcessIdentifierScheme () {
    return m_sProcessIdentifierScheme;
  }

  public void setProcessIdentifierScheme (final String sProcessIdentifierScheme) {
    m_sProcessIdentifierScheme = sProcessIdentifierScheme;
  }

  @Column (name = "processIdentifier", nullable = false, length = MAX_PROCESS_IDENTIFIER_VALUE_LENGTH)
  public String getProcessIdentifier () {
    return m_sProcessIdentifier;
  }

  public void setProcessIdentifier (final String sProcessIdentifier) {
    m_sProcessIdentifier = sProcessIdentifier;
  }

  @Transient
  public void setProcessIdentifier (@Nonnull final DocumentIdentifier aProcessID) {
    setProcessIdentifierScheme (aProcessID.getScheme ());
    setProcessIdentifier (aProcessID.getValue ());
  }

  @Transient
  @Nonnull
  public ParticipantIdentifierType asBusinessIdentifier () {
    return new ParticipantIdentifierType(m_sParticipantIdentifier, m_sParticipantIdentifierScheme);
  }

  @Transient
  @Nonnull
  public DocumentIdentifier asDocumentTypeIdentifier () {
    return new DocumentIdentifier(m_sDocumentTypeIdentifier, m_sDocumentTypeIdentifierScheme);
  }

  @Transient
  @Nonnull
  public ProcessIdentifier asProcessIdentifier () {
    return new ProcessIdentifier(m_sProcessIdentifier, m_sProcessIdentifierScheme);
  }

  @Override
  public boolean equals (final Object o) {
    if (this == o)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final DBProcessID rhs = (DBProcessID) o;
    return EqualsUtils.equals (m_sParticipantIdentifierScheme, rhs.m_sParticipantIdentifierScheme) &&
           EqualsUtils.equals (m_sParticipantIdentifier, rhs.m_sParticipantIdentifier) &&
           EqualsUtils.equals (m_sDocumentTypeIdentifierScheme, rhs.m_sDocumentTypeIdentifierScheme) &&
           EqualsUtils.equals (m_sDocumentTypeIdentifier, rhs.m_sDocumentTypeIdentifier) &&
           EqualsUtils.equals (m_sProcessIdentifierScheme, rhs.m_sProcessIdentifierScheme) &&
           EqualsUtils.equals (m_sProcessIdentifier, rhs.m_sProcessIdentifier);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sParticipantIdentifierScheme)
                                       .append (m_sParticipantIdentifier)
                                       .append (m_sDocumentTypeIdentifierScheme)
                                       .append (m_sDocumentTypeIdentifier)
                                       .append (m_sProcessIdentifierScheme)
                                       .append (m_sProcessIdentifier)
                                       .getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("participantIDScheme", m_sParticipantIdentifierScheme)
                                       .append ("participantIDValue", m_sParticipantIdentifier)
                                       .append ("documentTypeIDScheme", m_sDocumentTypeIdentifierScheme)
                                       .append ("documentTypeIDValue", m_sDocumentTypeIdentifier)
                                       .append ("processIDScheme", m_sProcessIdentifierScheme)
                                       .append ("processIDValue", m_sProcessIdentifier)
                                       .toString ();
  }
}
