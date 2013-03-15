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
package at.peppol.webgui.folder;

import java.util.Comparator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import at.peppol.webgui.document.IUserDocument;

import com.phloc.appbasics.app.dao.impl.AbstractSimpleDAO;
import com.phloc.appbasics.app.dao.impl.DAOException;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.callback.INonThrowingRunnableWithParameter;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.impl.MicroDocument;
import com.phloc.commons.state.EChange;

/**
 * Thread-safe DAO for user folder tree.
 * 
 * @author philip
 */
@ThreadSafe
public final class UserFolderTreeManager extends AbstractSimpleDAO implements IUserFolderTree {
  private UserFolderTree m_aTree = new UserFolderTree ();

  public UserFolderTreeManager (@Nonnull final String sFilename) throws DAOException {
    super (sFilename);
    initialRead ();
  }

  @Override
  @Nonnull
  protected EChange onRead (final IMicroDocument aDoc) {
    m_aTree = new UserFolderTree (aDoc.getDocumentElement ());
    return EChange.UNCHANGED;
  }

  @Override
  protected IMicroDocument createWriteData () {
    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.appendChild (m_aTree.getAsXML ());
    return aDoc;
  }

  @Nonnull
  public IUserFolder createRootFolder (@Nonnull final UserFolder aUserFolder) {
    m_aRWLock.writeLock ().lock ();
    try {
      final IUserFolder ret = m_aTree.createRootFolder (aUserFolder);
      markAsChanged ();
      return ret;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public IUserFolder createFolder (@Nonnull final String sParentFolderID, @Nonnull final UserFolder aUserFolder) {
    m_aRWLock.writeLock ().lock ();
    try {
      final IUserFolder ret = m_aTree.createFolder (sParentFolderID, aUserFolder);
      markAsChanged ();
      return ret;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange deleteFolder (@Nullable final String sFolderID) {
    m_aRWLock.writeLock ().lock ();
    try {
      if (m_aTree.deleteFolder (sFolderID).isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange renameFolder (@Nullable final String sFolderID, @Nonnull @Nonempty final String sNewFolderName) {
    m_aRWLock.writeLock ().lock ();
    try {
      if (m_aTree.renameFolder (sFolderID, sNewFolderName).isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  public void iterateFolders (final INonThrowingRunnableWithParameter <IUserFolder> aCallback,
                              @Nullable final Comparator <? super UserFolder> aFolderComparator) {
    m_aRWLock.readLock ().lock ();
    try {
      m_aTree.iterateFolders (aCallback, aFolderComparator);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange assignDocumentToFolder (@Nullable final String sFolderID, @Nonnull final IUserDocument aDoc) {
    m_aRWLock.writeLock ().lock ();
    try {
      if (m_aTree.assignDocumentToFolder (sFolderID, aDoc).isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange unassignDocumentFromFolder (@Nullable final String sFolderID, @Nonnull final IUserDocument aDoc) {
    m_aRWLock.writeLock ().lock ();
    try {
      if (m_aTree.unassignDocumentFromFolder (sFolderID, aDoc).isUnchanged ())
        return EChange.UNCHANGED;
      markAsChanged ();
      return EChange.CHANGED;
    }
    finally {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nullable
  public Set <String> getAllAssignedDocumentIDs (@Nullable final String sFolderID) {
    m_aRWLock.readLock ().lock ();
    try {
      return m_aTree.getAllAssignedDocumentIDs (sFolderID);
    }
    finally {
      m_aRWLock.readLock ().unlock ();
    }
  }
}
