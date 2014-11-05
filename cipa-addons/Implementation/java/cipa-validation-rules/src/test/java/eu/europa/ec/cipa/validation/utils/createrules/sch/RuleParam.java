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
package eu.europa.ec.cipa.validation.utils.createrules.sch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.string.StringHelper;

import eu.europa.ec.cipa.validation.utils.createrules.utils.Utils;

@Immutable
final class RuleParam {
  private final String m_sRuleID;
  private final String m_sTest;
  private final String m_sPrerequisite;
  private final String m_sPrerequisiteVarName;

  public RuleParam (@Nonnull @Nonempty final String sRuleID, @Nonnull @Nonempty final String sTest) {
    this (sRuleID, sTest, null, null);
  }

  public RuleParam (@Nonnull @Nonempty final String sRuleID,
                    @Nonnull @Nonempty final String sTest,
                    @Nullable final String sPrerequisite,
                    @Nullable final String sPrerequisiteVarName) {
    ValueEnforcer.notEmpty (sRuleID, "RuleID");
    ValueEnforcer.notEmpty (sTest, "Test");

    m_sRuleID = Utils.makeID (sRuleID);
    m_sTest = sTest;
    m_sPrerequisite = sPrerequisite;
    m_sPrerequisiteVarName = sPrerequisiteVarName;
  }

  @Nonnull
  @Nonempty
  public String getRuleID () {
    return m_sRuleID;
  }

  @Nullable
  public String getRulePrerequisiteVarName () {
    return m_sPrerequisiteVarName;
  }

  public boolean hasPrerequisite () {
    return StringHelper.hasText (m_sPrerequisite);
  }

  @Nullable
  public String getPrerequisite () {
    return m_sPrerequisite;
  }

  @Nonnull
  @Nonempty
  public String getTestWithPrequisiteInline () {
    if (hasPrerequisite ())
      return "(" + m_sPrerequisite + " and " + m_sTest + ") or not (" + m_sPrerequisite + ")";
    return m_sTest;
  }

  @Nonnull
  @Nonempty
  public String getTestWithPrequisiteParameter () {
    if (hasPrerequisite ())
      return "($" +
             getRulePrerequisiteVarName () +
             " and " +
             m_sTest +
             ") or not ($" +
             getRulePrerequisiteVarName () +
             ")";
    return m_sTest;
  }
}
