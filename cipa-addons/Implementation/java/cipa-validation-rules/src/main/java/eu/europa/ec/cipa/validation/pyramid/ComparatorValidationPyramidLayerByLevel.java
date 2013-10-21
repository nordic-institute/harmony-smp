package eu.europa.ec.cipa.validation.pyramid;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.compare.AbstractIntegerComparator;
import com.phloc.commons.compare.ESortOrder;

/**
 * Sort {@link ValidationPyramidLayer} objects by their respective validation
 * level.
 * 
 * @author Philip Helger
 */
public class ComparatorValidationPyramidLayerByLevel extends AbstractIntegerComparator <ValidationPyramidLayer> {
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
  protected long asLong (@Nonnull final ValidationPyramidLayer aLayer) {
    return aLayer.getValidationLevel ().getLevel ();
  }
}
