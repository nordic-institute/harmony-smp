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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.IMicroTypeConverter;
import com.phloc.commons.microdom.impl.MicroElement;

/**
 * Convert {@link IUserFolder} from and to XML.
 * 
 * @author philip
 */
public final class UserFolderMicroTypeConverter implements IMicroTypeConverter {
  private static final String ATTR_ID = "id";
  private static final String ATTR_NAME = "name";
  private static final String ELEMENT_DOC = "doc";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName) {
    final IUserFolder aUserFolder = (IUserFolder) aObject;
    final IMicroElement eUserFolder = new MicroElement (sNamespaceURI, sTagName);
    eUserFolder.setAttribute (ATTR_ID, aUserFolder.getID ());
    eUserFolder.setAttribute (ATTR_NAME, aUserFolder.getDisplayName ());
    // Sort for reproducible results
    for (final String sDocID : ContainerHelper.getSorted (aUserFolder.getAllDocumentIDs ()))
      eUserFolder.appendElement (ELEMENT_DOC).setAttribute (ATTR_ID, sDocID);
    return eUserFolder;
  }

  @Nonnull
  public IUserFolder convertToNative (@Nonnull final IMicroElement eUserFolder) {
    final String sID = eUserFolder.getAttribute (ATTR_ID);
    final String sName = eUserFolder.getAttribute (ATTR_NAME);
    final UserFolder aUserFolder = new UserFolder (sID, sName);
    for (final IMicroElement eDoc : eUserFolder.getChildElements (ELEMENT_DOC))
      aUserFolder.addDocument (eDoc.getAttribute (ATTR_ID));
    return aUserFolder;
  }
}
