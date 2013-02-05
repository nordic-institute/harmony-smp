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
package at.peppol.webgui.document.transform;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import at.peppol.test.ETestFileType;
import at.peppol.test.TestFiles;
import at.peppol.webgui.document.EDocumentMetaType;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.serialize.XMLReader;

/**
 * Test class for class {@link TransformationManager}.
 * 
 * @author philip
 */
public final class TransformationManagerTest {
  @Test
  public void testNoConverter () {
    assertNull (TransformationManager.transformInvoiceToUBL (new TransformationSource (EDocumentMetaType.BINARY,
                                                                                       new ClassPathResource ("dummy.xml"),
                                                                                       null)));
  }

  @Test
  public void testUBLCatalogues () throws SAXException {
    for (final IReadableResource aTestFile : TestFiles.getSuccessFiles (ETestFileType.CATALOGUE)) {
      final Document aDoc = XMLReader.readXMLDOM (aTestFile);
      assertNotNull (TransformationManager.transformCatalogueToUBL (new TransformationSource (EDocumentMetaType.XML,
                                                                                              aTestFile,
                                                                                              aDoc)));
    }
  }

  @Test
  public void testUBLInvoices () throws SAXException {
    for (final IReadableResource aTestFile : TestFiles.getSuccessFiles (ETestFileType.INVOICE)) {
      final Document aDoc = XMLReader.readXMLDOM (aTestFile);
      assertNotNull (TransformationManager.transformInvoiceToUBL (new TransformationSource (EDocumentMetaType.XML,
                                                                                            aTestFile,
                                                                                            aDoc)));
    }
  }

  @Test
  public void testUBLOrders () throws SAXException {
    for (final IReadableResource aTestFile : TestFiles.getSuccessFiles (ETestFileType.ORDER)) {
      final Document aDoc = XMLReader.readXMLDOM (aTestFile);
      assertNotNull (TransformationManager.transformOrderToUBL (new TransformationSource (EDocumentMetaType.XML,
                                                                                          aTestFile,
                                                                                          aDoc)));
    }
  }
}
