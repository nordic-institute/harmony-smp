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

import java.util.Comparator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.ValueEnforcer;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.callback.INonThrowingRunnableWithParameter;
import com.phloc.commons.compare.AbstractComparator;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.hierarchy.DefaultHierarchyWalkerCallback;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.parent.IChildrenProvider;
import com.phloc.commons.parent.impl.ChildrenProviderHasChildren;
import com.phloc.commons.parent.impl.ChildrenProviderSortingWithUniqueID;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.tree.utils.walk.TreeWalker;
import com.phloc.commons.tree.utils.xml.MicroTypeConverterTreeXML;
import com.phloc.commons.tree.utils.xml.TreeXMLConverter;
import com.phloc.commons.tree.withid.DefaultTreeItemWithID;
import com.phloc.commons.tree.withid.unique.DefaultTreeWithGlobalUniqueID;

import eu.europa.ec.cipa.webgui.document.IUserDocument;

/**
 * Represents a folder tree having {@link IUserDocument} elements. Default
 * implementation of {@link IUserFolder}.
 * 
 * @author philip
 */
public final class UserFolderTree implements IUserFolderTree {
  private static final Logger s_aLogger = LoggerFactory.getLogger (UserFolderTree.class);
  private static final String ELEMENT_USERFOLDER = "userfolder";

  /** The main tree used. The key is the ID of the contained user folder */
  private final DefaultTreeWithGlobalUniqueID <String, UserFolder> m_aTree;

  /**
   * Constructor
   */
  public UserFolderTree () {
    // Create an empty new tree
    m_aTree = new DefaultTreeWithGlobalUniqueID <String, UserFolder> ();
  }

  UserFolderTree (@Nonnull final IMicroElement aElement) {
    ValueEnforcer.notNull (aElement, "Element");

    // Parse XML to user folder tree
    m_aTree = TreeXMLConverter.getXMLAsTreeWithUniqueStringID (aElement,
                                                               new MicroTypeConverterTreeXML <UserFolder> (ELEMENT_USERFOLDER,
                                                                                                           UserFolder.class));
    if (m_aTree == null)
      throw new IllegalStateException ("Deserialization of XML to user folder tree failed!");
  }

  @Nonnull
  public UserFolder createRootFolder (@Nonnull final UserFolder aUserFolder) {
    ValueEnforcer.notNull (aUserFolder, "UserFolder");

    return m_aTree.getRootItem ().createChildItem (aUserFolder.getID (), aUserFolder).getData ();
  }

  @Nonnull
  public UserFolder createFolder (@Nonnull @Nonempty final String sParentFolderID, @Nonnull final UserFolder aUserFolder) {
    ValueEnforcer.notNull (aUserFolder, "UserFolder");

    // Resolve parent item
    final DefaultTreeItemWithID <String, UserFolder> aParentItem = m_aTree.getItemWithID (sParentFolderID);
    if (aParentItem == null)
      throw new IllegalArgumentException ("No such parent item '" + sParentFolderID + "'");

    // Add to parent
    return aParentItem.createChildItem (aUserFolder.getID (), aUserFolder).getData ();
  }

  @Nonnull
  public EChange deleteFolder (@Nullable final String sFolderID) {
    final DefaultTreeItemWithID <String, UserFolder> aItem = m_aTree.getItemWithID (sFolderID);
    if (aItem == null || aItem.isRootItem ())
      return EChange.UNCHANGED;
    final EChange eChange = aItem.getParent ().removeChild (sFolderID);
    if (eChange.isUnchanged ())
      s_aLogger.error ("Internal inconsistency in folder tree!");
    return eChange;
  }

  @Nonnull
  public EChange renameFolder (@Nullable final String sFolderID, @Nonnull @Nonempty final String sNewFolderName) {
    final DefaultTreeItemWithID <String, UserFolder> aItem = m_aTree.getItemWithID (sFolderID);
    if (aItem == null)
      return EChange.UNCHANGED;
    return aItem.getData ().setDisplayName (sNewFolderName);
  }

  public void iterateFolders (@Nonnull final INonThrowingRunnableWithParameter <IUserFolder> aCallback,
                              @Nullable final Comparator <? super UserFolder> aFolderComparator) {
    IChildrenProvider <DefaultTreeItemWithID <String, UserFolder>> aChildrenProvider;
    if (aFolderComparator == null) {
      // No sorting required
      aChildrenProvider = new ChildrenProviderHasChildren <DefaultTreeItemWithID <String, UserFolder>> ();
    }
    else {
      // Sorting is required
      final Comparator <DefaultTreeItemWithID <String, UserFolder>> aItemComparator = new AbstractComparator <DefaultTreeItemWithID <String, UserFolder>> () {
        @Override
        protected int mainCompare (final DefaultTreeItemWithID <String, UserFolder> aItem1,
                                   final DefaultTreeItemWithID <String, UserFolder> aItem2) {
          return aFolderComparator.compare (aItem1.getData (), aItem2.getData ());
        }
      };
      aChildrenProvider = new ChildrenProviderSortingWithUniqueID <String, DefaultTreeItemWithID <String, UserFolder>> (m_aTree,
                                                                                                                        aItemComparator);
    }

    // Main tree walking
    TreeWalker.walkTree (m_aTree,
                         aChildrenProvider,
                         new DefaultHierarchyWalkerCallback <DefaultTreeItemWithID <String, UserFolder>> () {
                           @Override
                           @OverrideOnDemand
                           public void onItemBeforeChildren (@Nullable final DefaultTreeItemWithID <String, UserFolder> aItem) {
                             final IUserFolder aUserFolder = aItem.getData ();
                             aCallback.run (aUserFolder);
                           }
                         });
  }

  @Nonnull
  public EChange assignDocumentToFolder (@Nullable final String sFolderID, @Nonnull final IUserDocument aDoc) {
    ValueEnforcer.notNull (aDoc, "Doc");

    // Resolve folder ID
    final DefaultTreeItemWithID <String, UserFolder> aItem = m_aTree.getItemWithID (sFolderID);
    if (aItem == null) {
      s_aLogger.info ("Failed to resolve folder with ID '" + sFolderID + "'");
      return EChange.UNCHANGED;
    }

    return aItem.getData ().addDocument (aDoc.getID ());
  }

  @Nonnull
  public EChange unassignDocumentFromFolder (@Nullable final String sFolderID, @Nonnull final IUserDocument aDoc) {
    ValueEnforcer.notNull (aDoc, "Doc");

    // Resolve folder ID
    final DefaultTreeItemWithID <String, UserFolder> aItem = m_aTree.getItemWithID (sFolderID);
    if (aItem == null) {
      s_aLogger.info ("Failed to resolve folder with ID '" + sFolderID + "'");
      return EChange.UNCHANGED;
    }

    return aItem.getData ().removeDocument (aDoc.getID ());
  }

  @Nullable
  public Set <String> getAllAssignedDocumentIDs (@Nullable final String sFolderID) {
    // Resolve folder ID
    final DefaultTreeItemWithID <String, UserFolder> aItem = m_aTree.getItemWithID (sFolderID);
    if (aItem == null) {
      s_aLogger.info ("Failed to resolve folder with ID '" + sFolderID + "'");
      return null;
    }
    return aItem.getData ().getAllDocumentIDs ();
  }

  @Nonnull
  public IMicroElement getAsXML () {
    return TreeXMLConverter.getTreeWithStringIDAsXML (m_aTree,
                                                      new MicroTypeConverterTreeXML <UserFolder> (ELEMENT_USERFOLDER,
                                                                                                  UserFolder.class));
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof UserFolderTree))
      return false;
    final UserFolderTree rhs = (UserFolderTree) o;
    return m_aTree.equals (rhs.m_aTree);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_aTree).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("tree", m_aTree).toString ();
  }
}
