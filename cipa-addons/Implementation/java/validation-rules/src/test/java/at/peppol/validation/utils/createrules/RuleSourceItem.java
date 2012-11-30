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
package at.peppol.validation.utils.createrules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import at.peppol.validation.utils.createrules.codelist.RuleSourceCodeList;
import at.peppol.validation.utils.createrules.sch.RuleSourceBusinessRule;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.id.IHasID;

public final class RuleSourceItem implements IHasID <String> {
  private final File m_aRuleSrcDir;
  private final File m_aRuleDstDir;
  private final String m_sID;
  private final List <RuleSourceCodeList> m_aCodeLists = new ArrayList <RuleSourceCodeList> ();
  private final List <RuleSourceBusinessRule> m_aBusinessRules = new ArrayList <RuleSourceBusinessRule> ();

  public RuleSourceItem (@Nonnull final File aRuleSrcDir,
                         @Nonnull final File aRuleDstDir,
                         @Nonnull @Nonempty final String sID) {
    if (!aRuleSrcDir.isDirectory ())
      throw new IllegalArgumentException (aRuleSrcDir + " is not a directory!");
    if (!aRuleDstDir.isDirectory ())
      throw new IllegalArgumentException (aRuleDstDir + " is not a directory!");
    m_aRuleSrcDir = new File (aRuleSrcDir, sID);
    m_aRuleDstDir = new File (aRuleDstDir, sID);
    m_sID = sID.toUpperCase (Locale.US);
  }

  @Nonnull
  public File getOutputCodeListDirectory () {
    return new File (m_aRuleDstDir, "codelist");
  }

  @Nonnull
  public File getOutputSchematronDirectory () {
    return m_aRuleDstDir;
  }

  @Nonnull
  public RuleSourceItem addCodeList (@Nonnull @Nonempty final String sSourceFilename) {
    m_aCodeLists.add (new RuleSourceCodeList (new File (m_aRuleSrcDir, sSourceFilename),
                                              getOutputCodeListDirectory (),
                                              getOutputSchematronDirectory (),
                                              m_sID));
    return this;
  }

  @Nonnull
  public RuleSourceItem addBussinessRule (@Nonnull @Nonempty final String sSourceFilename) {
    return addBussinessRule (sSourceFilename, null);
  }

  @Nonnull
  public RuleSourceItem addBussinessRule (@Nonnull @Nonempty final String sSourceFilename,
                                          @Nullable final String sCodeListTransaction) {
    m_aBusinessRules.add (new RuleSourceBusinessRule (new File (m_aRuleSrcDir, sSourceFilename),
                                                      getOutputSchematronDirectory (),
                                                      m_sID,
                                                      sCodeListTransaction));
    return this;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  @Nonnull
  public List <RuleSourceCodeList> getAllCodeLists () {
    return ContainerHelper.newList (m_aCodeLists);
  }

  @Nonnull
  public List <RuleSourceBusinessRule> getAllBusinessRules () {
    return ContainerHelper.newList (m_aBusinessRules);
  }
}
