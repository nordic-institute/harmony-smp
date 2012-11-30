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
package at.peppol.validation.utils.createrules.sch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.io.file.FileOperations;
import com.phloc.commons.string.StringHelper;

public final class RuleSourceBusinessRule {
  private final File m_aSourceFile;
  private final File m_aOutputDirectory;
  private final String m_sID;
  private final String m_sCodeListTransaction;
  private final List <File> m_aResultSCHFiles = new ArrayList <File> ();

  public RuleSourceBusinessRule (@Nonnull final File aSourceFilename,
                                 @Nonnull final File aOutputDirectory,
                                 @Nonnull @Nonempty final String sID,
                                 @Nullable final String sCodeListTransaction) {
    if (aSourceFilename == null)
      throw new NullPointerException ("sourceFilename");
    if (!aSourceFilename.isFile ())
      throw new IllegalArgumentException ("Source file does not exist: " + aSourceFilename);
    if (aOutputDirectory == null)
      throw new NullPointerException ("outputDirectory");
    if (StringHelper.hasNoText (sID))
      throw new IllegalArgumentException ("ID");

    FileOperations.createDirIfNotExisting (aOutputDirectory);
    FileOperations.createDirIfNotExisting (new File (aOutputDirectory, "include"));
    m_aSourceFile = aSourceFilename;
    m_aOutputDirectory = aOutputDirectory;
    m_sID = sID;
    m_sCodeListTransaction = sCodeListTransaction;
  }

  @Nonnull
  public File getSourceFile () {
    return m_aSourceFile;
  }

  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  @Nonnull
  public File getSchematronAbstractFile (@Nonnull @Nonempty final String sTransaction) {
    return new File (m_aOutputDirectory, "include/" + m_sID + "-" + sTransaction + "-abstract.sch");
  }

  @Nonnull
  public File getSchematronBindingFile (@Nonnull @Nonempty final String sBindingName,
                                        @Nonnull @Nonempty final String sTransaction) {
    return new File (m_aOutputDirectory, "include/" + m_sID + "-" + sBindingName + "-" + sTransaction + "-test.sch");
  }

  @Nonnull
  public File getSchematronCodeListFile () {
    return new File (m_aOutputDirectory, "include/" + m_sID + "-" + m_sCodeListTransaction + "-codes.sch");
  }

  @Nonnull
  public File getSchematronAssemblyFile (@Nonnull @Nonempty final String sBindingName,
                                         @Nonnull @Nonempty final String sTransaction) {
    return new File (m_aOutputDirectory, m_sID + "-" + sBindingName + "-" + sTransaction + ".sch");
  }

  public boolean hasCodeList () {
    return StringHelper.hasText (m_sCodeListTransaction);
  }

  public String getCodeList () {
    return m_sCodeListTransaction;
  }

  public void addResultSchematronFile (@Nonnull final File aSCHFile) {
    m_aResultSCHFiles.add (aSCHFile);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <File> getAllResultSchematronFiles () {
    return ContainerHelper.newList (m_aResultSCHFiles);
  }
}
