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
package eu.europa.ec.cipa.webgui.folder;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Represents a single folder like "Inbox", "Outbox" or "Drafts"
 * 
 * @author philip
 */
@NotThreadSafe
public final class UserFolder implements IUserFolder {
  private final String m_sID;
  private String m_sDisplayName;
  private final Set <String> m_aDocs = new HashSet <String> ();

  /**
   * Constructor for a new folder
   * 
   * @param sDisplayName
   *        The display name of the folder
   */
  public UserFolder (@Nonnull @Nonempty final String sDisplayName) {
    this (GlobalIDFactory.getNewPersistentStringID (), sDisplayName);
  }

  UserFolder (@Nonnull @Nonempty final String sID, @Nonnull @Nonempty final String sDisplayName) {
    if (StringHelper.hasNoText (sID))
      throw new IllegalArgumentException ("ID");
    if (StringHelper.hasNoText (sDisplayName))
      throw new IllegalArgumentException ("displayName");
    m_sID = sID;
    m_sDisplayName = sDisplayName;
  }

  /**
   * @return The globally unique folder ID
   */
  @Nonnull
  @Nonempty
  public String getID () {
    return m_sID;
  }

  /**
   * @return The folder's name
   */
  @Nonnull
  @Nonempty
  public String getDisplayName () {
    return m_sDisplayName;
  }

  /**
   * Change the display name of the folder.
   * 
   * @param sDisplayName
   *        The new display name
   * @return {@link EChange}
   */
  @Nonnull
  public EChange setDisplayName (@Nonnull @Nonempty final String sDisplayName) {
    if (StringHelper.hasNoText (sDisplayName))
      throw new IllegalArgumentException ("displayName");

    if (sDisplayName.equals (m_sDisplayName))
      return EChange.UNCHANGED;
    m_sDisplayName = sDisplayName;
    return EChange.CHANGED;
  }

  public boolean containsDocumentWithID (@Nullable final String sDocumentID) {
    return m_aDocs.contains (sDocumentID);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllDocumentIDs () {
    return ContainerHelper.newSet (m_aDocs);
  }

  @Nonnegative
  public int getDocumentCount () {
    return m_aDocs.size ();
  }

  public boolean hasDocuments () {
    return !m_aDocs.isEmpty ();
  }

  @Nonnull
  public EChange addDocument (@Nonnull final String sDocID) {
    if (StringHelper.hasNoText (sDocID))
      throw new NullPointerException ("docID");
    return EChange.valueOf (m_aDocs.add (sDocID));
  }

  @Nonnull
  public EChange removeDocument (@Nullable final String sDocID) {
    return EChange.valueOf (m_aDocs.remove (sDocID));
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof UserFolder))
      return false;
    final UserFolder rhs = (UserFolder) o;
    return m_sID.equals (rhs.m_sID);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sID).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("ID", m_sID)
                                       .append ("name", m_sDisplayName)
                                       .append ("docs", m_aDocs)
                                       .toString ();
  }
}
