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
package at.peppol.validation.utils.createrules.codelist;

import java.io.File;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.file.FileOperations;

public final class RuleSourceCodeList {
  private final File m_aSourceFile;
  private final File m_aCodeListOutputDirectory;
  private final File m_aSchematronOutputDirectory;
  private final String m_sID;

  public RuleSourceCodeList (@Nonnull final File aSourceFilename,
                             @Nonnull final File aCodeListOutputDirectory,
                             @Nonnull final File aSchematronOutputDirectory,
                             @Nonnull @Nonempty final String sID) {
    if (!aSourceFilename.isFile ())
      throw new IllegalArgumentException ("Source file does not exist: " + aSourceFilename);
    FileOperations.createDirIfNotExisting (aCodeListOutputDirectory);
    FileOperations.createDirIfNotExisting (aSchematronOutputDirectory);
    FileOperations.createDirIfNotExisting (new File (aSchematronOutputDirectory, "include"));
    m_aSourceFile = aSourceFilename;
    m_aCodeListOutputDirectory = aCodeListOutputDirectory;
    m_aSchematronOutputDirectory = aSchematronOutputDirectory;
    m_sID = sID;
  }

  @Nonnull
  public File getSourceFile () {
    return m_aSourceFile;
  }

  @Nonnull
  public File getGCFile (@Nonnull @Nonempty final String sCodeListName) {
    return new File (m_aCodeListOutputDirectory, sCodeListName + ".gc");
  }

  @Nonnull
  public File getCVAFile (@Nonnull @Nonempty final String sTransaction) {
    return new File (m_aCodeListOutputDirectory, m_sID + "-" + sTransaction + "-codes.cva");
  }

  @Nonnull
  public File getXSLTFile (@Nonnull @Nonempty final String sTransaction) {
    return new File (m_aCodeListOutputDirectory, m_sID + "-" + sTransaction + "-codes.sch.xslt");
  }

  @Nonnull
  public File getSchematronFile (@Nonnull @Nonempty final String sTransaction) {
    return new File (m_aSchematronOutputDirectory, "include/" + m_sID + "-" + sTransaction + "-codes.sch");
  }
}
