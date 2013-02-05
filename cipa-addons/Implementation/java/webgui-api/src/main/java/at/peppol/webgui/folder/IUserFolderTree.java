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

import at.peppol.webgui.document.IUserDocument;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.callback.INonThrowingRunnableWithParameter;
import com.phloc.commons.state.EChange;

/**
 * Base interface for a tree of {@link IUserFolder} objects.
 * 
 * @author philip
 */
public interface IUserFolderTree {
  /**
   * Create a new root folder
   * 
   * @param aUserFolder
   *        The user folder to be added. May not be <code>null</code>.
   * @return The passed user folder
   */
  @Nonnull
  IUserFolder createRootFolder (@Nonnull UserFolder aUserFolder);

  /**
   * Create a new non-root folder
   * 
   * @param sParentFolderID
   *        The ID of the parent folder to add the child folder to.
   * @param aUserFolder
   *        The user folder to be added
   * @return The passed user folder
   */
  @Nonnull
  IUserFolder createFolder (@Nonnull @Nonempty String sParentFolderID, @Nonnull UserFolder aUserFolder);

  /**
   * Delete the folder with the specified ID
   * 
   * @param sFolderID
   *        The ID of the folder to be deleted
   * @return {@link EChange}
   */
  @Nonnull
  EChange deleteFolder (@Nullable String sFolderID);

  /**
   * Specify a new name for the passed folder
   * 
   * @param sFolderID
   *        The ID of the folder to be renamed
   * @param sNewFolderName
   *        The new folder name
   * @return {@link EChange}
   */
  @Nonnull
  EChange renameFolder (@Nullable String sFolderID, @Nonnull @Nonempty String sNewFolderName);

  /**
   * Iterate all available folders and perform an arbitrary action.
   * 
   * @param aCallback
   *        The callback to be invoked for every folder. May not be
   *        <code>null</code>.
   * @param aFolderComparator
   *        An optional comparator to specify the way how folders are sorted on
   *        each level. May be <code>null</code>.
   */
  void iterateFolders (@Nonnull INonThrowingRunnableWithParameter <IUserFolder> aCallback,
                       @Nullable Comparator <? super UserFolder> aFolderComparator);

  /**
   * Assign an existing document to a folder.
   * 
   * @param sFolderID
   *        The ID of the folder to assign the document to. If it is not
   *        existing, nothing happens.
   * @param aDoc
   *        The document to be assigned. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange assignDocumentToFolder (@Nullable String sFolderID, @Nonnull IUserDocument aDoc);

  /**
   * Unassign an existing document from a folder.
   * 
   * @param sFolderID
   *        The ID of the folder to unassign the document from. If it is not
   *        existing, nothing happens.
   * @param aDoc
   *        The document to be assigned. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange unassignDocumentFromFolder (@Nullable String sFolderID, @Nonnull IUserDocument aDoc);

  /**
   * Get all assigned documents of the specified folder.
   * 
   * @param sFolderID
   *        The ID of the folder to get the documents from. May be
   *        <code>null</code>.
   * @return <code>null</code> if no such folder exists, an empty container if
   *         the folder was found, but is empty.
   */
  @Nullable
  Set <String> getAllAssignedDocumentIDs (@Nullable String sFolderID);
}
