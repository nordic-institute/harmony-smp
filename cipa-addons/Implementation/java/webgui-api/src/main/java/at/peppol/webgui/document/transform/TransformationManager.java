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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.webgui.document.EDocumentType;

import com.phloc.commons.lang.CGStringHelper;
import com.phloc.commons.lang.ServiceLoaderUtils;
import com.phloc.commons.typeconvert.TypeConverterException;

/**
 * This is the central class for handling the transformation of resources.
 * 
 * @author philip
 */
@Immutable
public final class TransformationManager {
  private static final Logger s_aLogger = LoggerFactory.getLogger (TransformationManager.class);
  private static final List <ITransformCatalogueToUBLSPI> s_aCatalogueTransformers;
  private static final List <ITransformOrderToUBLSPI> s_aOrderTransformers;
  private static final List <ITransformOrderResponseToUBLSPI> s_aOrderResponseTransformers;
  private static final List <ITransformInvoiceToUBLSPI> s_aInvoiceTransformers;

  static {
    // Resolve all SPI implementations
    s_aCatalogueTransformers = ServiceLoaderUtils.getAllSPIImplementations (ITransformCatalogueToUBLSPI.class);
    s_aLogger.info ("Found " + s_aCatalogueTransformers.size () + " catalogue transformer(s)");

    s_aOrderTransformers = ServiceLoaderUtils.getAllSPIImplementations (ITransformOrderToUBLSPI.class);
    s_aLogger.info ("Found " + s_aOrderTransformers.size () + " order transformer(s)");

    s_aOrderResponseTransformers = ServiceLoaderUtils.getAllSPIImplementations (ITransformOrderResponseToUBLSPI.class);
    s_aLogger.info ("Found " + s_aOrderResponseTransformers.size () + " order response transformer(s)");

    s_aInvoiceTransformers = ServiceLoaderUtils.getAllSPIImplementations (ITransformInvoiceToUBLSPI.class);
    s_aLogger.info ("Found " + s_aInvoiceTransformers.size () + " invoice transformer(s)");
  }

  private TransformationManager () {}

  /**
   * Convert the passed resource to a UBL document.
   * 
   * @param eDocType
   *        The desired document type. May not be <code>null</code>.
   * @param aSource
   *        The transformation input parameters. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformDocumentToUBL (@Nonnull final EDocumentType eDocType,
                                                             @Nonnull final TransformationSource aSource) {
    if (eDocType == null)
      throw new NullPointerException ("docType");

    switch (eDocType) {
      case CATALOGUE:
        return transformCatalogueToUBL (aSource);
      case ORDER:
        return transformOrderToUBL (aSource);
      case ORDER_RESPONSE:
        return transformOrderResponseToUBL (aSource);
      case INVOICE:
        return transformInvoiceToUBL (aSource);
      default:
        throw new IllegalArgumentException ("Unsupported document type " + eDocType);
    }
  }

  /**
   * Convert the passed resource to a UBL catalogue.
   * 
   * @param aSource
   *        The transformation input parameters. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformCatalogueToUBL (@Nonnull final TransformationSource aSource) {
    for (final ITransformCatalogueToUBLSPI aTransformer : s_aCatalogueTransformers)
      if (aTransformer.canConvertCatalogue (aSource)) {
        s_aLogger.info ("Found matching catalogue transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertCatalogueToUBL (aSource);
        }
        catch (final TypeConverterException ex) {
          s_aLogger.warn ("Transformer failed to convert catalogue - ignoring");
        }
      }
    return null;
  }

  /**
   * Convert the passed resource to a UBL order.
   * 
   * @param aSource
   *        The transformation input parameters. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformOrderToUBL (@Nonnull final TransformationSource aSource) {
    for (final ITransformOrderToUBLSPI aTransformer : s_aOrderTransformers)
      if (aTransformer.canConvertOrder (aSource)) {
        s_aLogger.info ("Found matching order transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertOrderToUBL (aSource);
        }
        catch (final TypeConverterException ex) {
          s_aLogger.warn ("Transformer failed to convert order - ignoring");
        }
      }
    return null;
  }

  /**
   * Convert the passed resource to a UBL order response.
   * 
   * @param aSource
   *        The transformation input parameters. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformOrderResponseToUBL (@Nonnull final TransformationSource aSource) {
    for (final ITransformOrderResponseToUBLSPI aTransformer : s_aOrderResponseTransformers)
      if (aTransformer.canConvertOrderResponse (aSource)) {
        s_aLogger.info ("Found matching order response transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertOrderResponseToUBL (aSource);
        }
        catch (final TypeConverterException ex) {
          s_aLogger.warn ("Transformer failed to convert order response - ignoring");
        }
      }
    return null;
  }

  /**
   * Convert the passed resource to a UBL invoice.
   * 
   * @param aSource
   *        The transformation input parameters. May not be <code>null</code>.
   * @return <code>null</code> if no converter can be found.
   */
  @Nullable
  public static TransformationResult transformInvoiceToUBL (@Nonnull final TransformationSource aSource) {
    for (final ITransformInvoiceToUBLSPI aTransformer : s_aInvoiceTransformers)
      if (aTransformer.canConvertInvoice (aSource)) {
        s_aLogger.info ("Found matching invoice transformer " + CGStringHelper.getClassLocalName (aTransformer));
        try {
          return aTransformer.convertInvoiceToUBL (aSource);
        }
        catch (final TypeConverterException ex) {
          s_aLogger.warn ("Transformer failed to convert invoice - ignoring");
        }
      }
    return null;
  }
}
