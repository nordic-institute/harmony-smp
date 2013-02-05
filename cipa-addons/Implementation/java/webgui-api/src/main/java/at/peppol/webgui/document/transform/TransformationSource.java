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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.w3c.dom.Document;

import at.peppol.webgui.document.EDocumentMetaType;

import com.phloc.commons.io.IReadableResource;

/**
 * Encapsulate all parameters required to start a transformation
 * 
 * @author philip
 */
@Immutable
public final class TransformationSource {
  private final EDocumentMetaType m_eDocMetaType;
  private final IReadableResource m_aRes;
  private final Document m_aXMLDoc;

  /**
   * Constructor
   * 
   * @param eDocMetaType
   *        Document meta type. May not be <code>null</code>.
   * @param aRes
   *        The source resource. May not be <code>null</code>.
   * @param aXMLDoc
   *        A pre-parsed XML document, in case it is of type XML. May not be
   *        <code>null</code> if the meta type is XML.
   */
  public TransformationSource (@Nonnull final EDocumentMetaType eDocMetaType,
                               @Nonnull final IReadableResource aRes,
                               @Nullable final Document aXMLDoc) {
    if (eDocMetaType == null)
      throw new NullPointerException ("docMetaType");
    if (aRes == null)
      throw new NullPointerException ("resource");
    if (eDocMetaType.equals (EDocumentMetaType.XML) && aXMLDoc == null)
      throw new IllegalArgumentException ("XML document may not be null for meta type XML");
    m_eDocMetaType = eDocMetaType;
    m_aRes = aRes;
    m_aXMLDoc = aXMLDoc;
  }

  /**
   * @return The document meta type. Never <code>null</code>.
   */
  @Nonnull
  public EDocumentMetaType getDocumentMetaType () {
    return m_eDocMetaType;
  }

  /**
   * @return <code>true</code> if the source's document meta type is XML.
   */
  public boolean isXMLSource () {
    return EDocumentMetaType.XML.equals (m_eDocMetaType);
  }

  /**
   * @return The underlying resource. Never <code>null</code>.
   */
  @Nonnull
  public IReadableResource getResource () {
    return m_aRes;
  }

  /**
   * @return The pre-parsed XML document in case the meta type is XML
   */
  @Nullable
  public Document getXMLDocument () {
    return m_aXMLDoc;
  }
}
