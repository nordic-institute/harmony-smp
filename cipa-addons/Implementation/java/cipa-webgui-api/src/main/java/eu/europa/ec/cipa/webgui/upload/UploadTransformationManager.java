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
package eu.europa.ec.cipa.webgui.upload;

import javax.annotation.Nonnull;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.error.ResourceError;
import com.phloc.commons.error.ResourceErrorGroup;
import com.phloc.commons.error.ResourceLocation;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.xml.serialize.XMLReader;

import eu.europa.ec.cipa.webgui.document.EDocumentMetaType;
import eu.europa.ec.cipa.webgui.document.EDocumentType;
import eu.europa.ec.cipa.webgui.document.transform.TransformationManager;
import eu.europa.ec.cipa.webgui.document.transform.TransformationResult;
import eu.europa.ec.cipa.webgui.document.transform.TransformationSource;

/**
 * This class should be first class to be called after an upload finished
 * successful.
 * 
 * @author philip
 */
public final class UploadTransformationManager {
  private UploadTransformationManager () {}

  /**
   * Perform the transformation of an uploaded document.
   * 
   * @param eDocType
   *        The document type that was uploaded
   * @param aUploadedResource
   *        The uploaded resource descriptor
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static TransformationResult tryToTransformUploadedResource (@Nonnull final EDocumentType eDocType,
                                                                     @Nonnull final IUploadedResource aUploadedResource) {
    if (eDocType == null)
      throw new NullPointerException ("docType");
    if (aUploadedResource == null)
      throw new NullPointerException ("uploadedResource");
    if (!aUploadedResource.isSuccess ())
      throw new IllegalArgumentException ("Cannot handle failed uploads!");

    // Convert to a resource
    final ResourceErrorGroup aErrorMsgs = new ResourceErrorGroup ();
    final IReadableResource aRes = new FileSystemResource (aUploadedResource.getTemporaryFile ());
    if (!aRes.exists ()) {
      aErrorMsgs.addResourceError (new ResourceError (new ResourceLocation (aRes.getPath ()),
                                                      EErrorLevel.ERROR,
                                                      "Temporary file " +
                                                          aUploadedResource.getTemporaryFile ().getAbsolutePath () +
                                                          " does not exist!"));
      return TransformationResult.createFailure (aErrorMsgs);
    }

    // Check if it is valid XML or not
    Document aXMLDoc = null;
    EDocumentMetaType eDocMetaType = EDocumentMetaType.BINARY;
    try {
      // Parse as arbitrary XML
      aXMLDoc = XMLReader.readXMLDOM (aRes);
      if (aXMLDoc != null) {
        // Parsing was successful -> it's XML
        eDocMetaType = EDocumentMetaType.XML;
      }
    }
    catch (final SAXException ex) {}

    // Do the transformation
    final TransformationSource aSource = new TransformationSource (eDocMetaType, aRes, aXMLDoc);
    final TransformationResult aRet = TransformationManager.transformDocumentToUBL (eDocType, aSource);
    if (aRet == null) {
      aErrorMsgs.addResourceError (new ResourceError (new ResourceLocation (aRes.getPath ()),
                                                      EErrorLevel.ERROR,
                                                      "No transformer was able to transform uploaded file " +
                                                          aUploadedResource.getTemporaryFile ().getAbsolutePath () +
                                                          "!"));
      return TransformationResult.createFailure (aErrorMsgs);
    }
    return aRet;
  }
}
