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
package eu.europa.ec.cipa.validation.pyramid;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.compare.AbstractComparator;
import com.phloc.commons.compare.CompareUtils;
import com.phloc.commons.compare.ESortOrder;

/**
 * Sort {@link ValidationPyramidLayer} objects by their respective validation
 * level.
 * 
 * @author Philip Helger
 */
public class ComparatorValidationPyramidLayerByLevel extends AbstractComparator <ValidationPyramidLayer> {
  /**
   * Comparator with default sort order and no nested comparator.
   */
  public ComparatorValidationPyramidLayerByLevel () {
    super ();
  }

  /**
   * Constructor with sort order.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public ComparatorValidationPyramidLayerByLevel (@Nonnull final ESortOrder eSortOrder) {
    super (eSortOrder);
  }

  /**
   * Comparator with default sort order and a nested comparator.
   * 
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorValidationPyramidLayerByLevel (@Nullable final Comparator <? super ValidationPyramidLayer> aNestedComparator) {
    super (aNestedComparator);
  }

  /**
   * Comparator with sort order and a nested comparator.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorValidationPyramidLayerByLevel (@Nonnull final ESortOrder eSortOrder,
                                                  @Nullable final Comparator <? super ValidationPyramidLayer> aNestedComparator) {
    super (eSortOrder, aNestedComparator);
  }

  @Override
  protected int mainCompare (final ValidationPyramidLayer aLayer1, final ValidationPyramidLayer aLayer2) {
    final int nLevel1 = aLayer1.getValidationLevel ().getLevel ();
    final int nLevel2 = aLayer2.getValidationLevel ().getLevel ();
    int ret = CompareUtils.compare (nLevel1, nLevel2);
    if (ret == 0) {
      // XSD before Schematron
      ret = aLayer1.getValidationType ().compareTo (aLayer2.getValidationType ());
    }
    return ret;
  }
}
