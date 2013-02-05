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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.phloc.appbasics.mock.AppBasicTestRule;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.convert.MicroTypeConverter;
import com.phloc.commons.mock.PhlocTestUtils;
import com.phloc.commons.string.StringHelper;

/**
 * Test class for class {@link UserFolder}
 * 
 * @author philip
 */
public final class UserFolderTest {
  @Rule
  public final TestRule m_aTestRule = new AppBasicTestRule ();

  @Test
  public void testBasic () {
    final UserFolder aUF = new UserFolder ("any");
    assertTrue (StringHelper.hasText (aUF.getID ()));
    assertEquals ("any", aUF.getDisplayName ());

    // Check empty
    assertFalse (aUF.hasDocuments ());
    assertEquals (0, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertTrue (aUF.getAllDocumentIDs ().isEmpty ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertFalse (aUF.containsDocumentWithID ("a"));

    // Add first document
    assertTrue (aUF.addDocument ("a").isChanged ());
    assertTrue (aUF.hasDocuments ());
    assertEquals (1, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertEquals (1, aUF.getAllDocumentIDs ().size ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertTrue (aUF.containsDocumentWithID ("a"));

    // Add again - no change
    assertFalse (aUF.addDocument ("a").isChanged ());
    assertTrue (aUF.hasDocuments ());
    assertEquals (1, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertEquals (1, aUF.getAllDocumentIDs ().size ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertTrue (aUF.containsDocumentWithID ("a"));

    // Remove invalid document - no change
    assertFalse (aUF.removeDocument ("b").isChanged ());
    assertTrue (aUF.hasDocuments ());
    assertEquals (1, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertEquals (1, aUF.getAllDocumentIDs ().size ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertTrue (aUF.containsDocumentWithID ("a"));

    // Remove valid document
    assertTrue (aUF.removeDocument ("a").isChanged ());
    assertFalse (aUF.hasDocuments ());
    assertEquals (0, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertTrue (aUF.getAllDocumentIDs ().isEmpty ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertFalse (aUF.containsDocumentWithID ("a"));

    // Remove again - no change
    assertFalse (aUF.removeDocument ("a").isChanged ());
    assertFalse (aUF.hasDocuments ());
    assertEquals (0, aUF.getDocumentCount ());
    assertNotNull (aUF.getAllDocumentIDs ());
    assertTrue (aUF.getAllDocumentIDs ().isEmpty ());
    assertFalse (aUF.containsDocumentWithID ("any"));
    assertFalse (aUF.containsDocumentWithID ("a"));
  }

  @Test
  public void testSerialize () {
    final UserFolder aUF = new UserFolder ("any");
    aUF.addDocument ("doc1");
    aUF.addDocument ("doc2");
    final IMicroElement e = MicroTypeConverter.convertToMicroElement (aUF, "x");
    final UserFolder aUF2 = MicroTypeConverter.convertToNative (e, UserFolder.class);
    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (aUF, aUF2);
  }
}
