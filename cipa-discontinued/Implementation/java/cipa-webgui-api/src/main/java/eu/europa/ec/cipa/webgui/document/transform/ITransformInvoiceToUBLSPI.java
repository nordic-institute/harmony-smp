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
package eu.europa.ec.cipa.webgui.document.transform;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.IsSPIInterface;
import com.phloc.commons.typeconvert.TypeConverterException;

/**
 * SPI interface for converting an invoice to UBL.
 * 
 * @author philip
 */
@IsSPIInterface
public interface ITransformInvoiceToUBLSPI {
  /**
   * Check if this converter can handle the passed source object. If this
   * converter can handle the passed source, <code>true</code> must be returned
   * and only in this case {@link #convertInvoiceToUBL(TransformationSource)} is
   * called. If <code>false</code> is returned,
   * {@link #convertInvoiceToUBL(TransformationSource)} is never called!
   * 
   * @param aSource
   *        The source object
   * @return <code>true</code> if this converter can convert the passed
   *         document.
   */
  boolean canConvertInvoice (@Nonnull TransformationSource aSource);

  /**
   * Convert the passed source object to a UBL invoice. This method is only
   * called, if {@link #canConvertInvoice(TransformationSource)} returned
   * <code>true</code> for this object.
   * 
   * @param aSource
   *        The source object to be converted. May not be <code>null</code>.
   * @return The converted invoice in a supported type. May not be
   *         <code>null</code>.
   * @throws TypeConverterException
   *         in case the conversion fails. The caller must handle this exception
   *         gracefully and check for further converters who can also handle the
   *         source object.
   */
  @Nonnull
  TransformationResult convertInvoiceToUBL (@Nonnull TransformationSource aSource) throws TypeConverterException;
}