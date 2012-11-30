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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.string.StringHelper;

@NotThreadSafe
final class CVAData {
  private final String m_sTransaction;
  private final List <CVAContextData> m_aContexts = new ArrayList <CVAContextData> ();

  public CVAData (@Nonnull @Nonempty final String sTransaction) {
    if (StringHelper.hasNoText (sTransaction))
      throw new IllegalArgumentException ("transaction");
    m_sTransaction = sTransaction;
  }

  public void addContext (@Nonnull @Nonempty final String sID,
                          @Nonnull @Nonempty final String sItem,
                          @Nonnull @Nonempty final String sCodeListName,
                          @Nonnull @Nonempty final String sSeverity,
                          @Nonnull @Nonempty final String sMessage) {
    m_aContexts.add (new CVAContextData (sID, sItem, sCodeListName, sSeverity, sMessage));
  }

  @Nonnull
  @Nonempty
  public String getTransaction () {
    return m_sTransaction;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <CVAContextData> getAllContexts () {
    return ContainerHelper.newList (m_aContexts);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllUsedCodeListNames () {
    final Set <String> ret = new TreeSet <String> ();
    for (final CVAContextData aContextData : m_aContexts)
      ret.add (aContextData.getCodeListName ());
    return ret;
  }
}
