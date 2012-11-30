/**
 * Copyright (C) 2010 Bundesrechenzentrum GmbH
 * http://www.brz.gv.at
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.peppol.validation.schematron.svrl;

import java.util.List;
import java.util.regex.Matcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.oclc.purl.dsdl.svrl.DiagnosticReferenceType;
import org.oclc.purl.dsdl.svrl.FailedAssertType;

import com.phloc.commons.annotations.ReturnsImmutableObject;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.error.ResourceLocation;
import com.phloc.commons.regex.RegExHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * A wrapper around {@link FailedAssertType} with easier error level handling.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SVRLFailedAssert {
  private final List <DiagnosticReferenceType> m_aDiagnisticReferences;
  private final String m_sText;
  private final String m_sLocation;
  private final String m_sTest;
  private final String m_sRole;
  private final EErrorLevel m_eFlag;

  private static String _getBeautifiedLocation (final String sLocation) {
    String sResult = sLocation;
    // Handle namespaces:
    // Search for "*:xx[namespace-uri()='yy']" where xx is the localname and yy
    // is the namespace URI
    final Matcher m = RegExHelper.getMatcher ("\\Q*:\\E([a-zA-Z0-9_]+)\\Q[namespace-uri()='\\E([^']+)\\Q']\\E", sResult);
    while (m.find ()) {
      final String sLocalName = m.group (1);
      final String sNamespaceURI = m.group (2);

      // Check if there is a known beautifier for this pair of namespace and
      // local name
      final String sBeautified = SVRLLocationBeautifierRegistry.getBeautifiedLocation (sNamespaceURI, sLocalName);
      if (sBeautified != null)
        sResult = sResult.replace (m.group (), sBeautified);
    }
    return sResult;
  }

  public SVRLFailedAssert (@Nonnull final FailedAssertType aFailedAssert) {
    if (aFailedAssert == null)
      throw new NullPointerException ("failedAssert");

    m_aDiagnisticReferences = ContainerHelper.newUnmodifiableList (aFailedAssert.getDiagnosticReference ());
    m_sText = aFailedAssert.getText ();
    m_sLocation = _getBeautifiedLocation (aFailedAssert.getLocation ());
    m_sTest = aFailedAssert.getTest ();
    m_sRole = aFailedAssert.getRole ();
    m_eFlag = SVRLUtils.getErrorLevelFromFailedAssert (aFailedAssert);
  }

  @Nonnull
  @ReturnsImmutableObject
  public List <DiagnosticReferenceType> getDiagnisticReferences () {
    return m_aDiagnisticReferences;
  }

  public String getText () {
    return m_sText;
  }

  public String getLocation () {
    return m_sLocation;
  }

  public String getTest () {
    return m_sTest;
  }

  @Nullable
  public String getRole () {
    return m_sRole;
  }

  @Nonnull
  public EErrorLevel getFlag () {
    return m_eFlag;
  }

  @Nonnull
  public SVRLResourceError getAsResourceError (@Nullable final String sResourceName) {
    return new SVRLResourceError (new ResourceLocation (sResourceName, m_sLocation), m_eFlag, m_sText, m_sTest);
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("diagnosticRefs", m_aDiagnisticReferences)
                                       .append ("text", m_sText)
                                       .append ("location", m_sLocation)
                                       .append ("test", m_sTest)
                                       .appendIfNotNull ("role", m_sRole)
                                       .append ("flag", m_eFlag)
                                       .toString ();
  }
}
