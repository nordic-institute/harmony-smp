package eu.europa.ec.cipa.validation.pyramid;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.Source;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.io.IReadableResource;

import eu.europa.ec.cipa.validation.rules.IValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.IValidationTransaction;

/**
 * Base interface for the validation pyramid
 * 
 * @author Philip Helger
 */
public interface IValidationPyramid {
  /**
   * @return The validation document to which the pyramid can be applied. Never
   *         <code>null</code>.
   */
  @Nonnull
  IValidationDocumentType getValidationDocumentType ();

  /**
   * @return The validation transaction to which the pyramid can be applied.
   *         Never <code>null</code>.
   */
  @Nonnull
  IValidationTransaction getValidationTransaction ();

  /**
   * @return <code>true</code> if the validation pyramid is country independent,
   *         <code>false</code> if a specific country is defined
   * @see #getValidationCountry()
   */
  boolean isValidationCountryIndependent ();

  /**
   * @return <code>null</code> if no specific country is used in validation.
   * @see #isValidationCountryIndependent()
   */
  @Nullable
  Locale getValidationCountry ();

  /**
   * @return A non-<code>null</code> list of all contained validation layers in
   *         the order they are executed.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <ValidationPyramidLayer> getAllValidationLayers ();

  /**
   * Apply the validation pyramid on the passed XML resource.
   * 
   * @param aRes
   *        The XML resource to apply the validation pyramid on. May not be
   *        <code>null</code>.
   * @return The validation pyramid result. Never <code>null</code>.
   */
  @Nonnull
  ValidationPyramidResult applyValidation (@Nonnull IReadableResource aRes);

  /**
   * Apply the pyramid on the passed {@link Source} object.
   * 
   * @param aXML
   *        The XML {@link Source}. IMPORTANT: Must be a {@link Source} that can
   *        be opened multiple times. Using e.g. a StreamSource with a
   *        StringReader will result in an error!
   * @return a non-<code>null</code> validation pyramid result.
   */
  @Nonnull
  ValidationPyramidResult applyValidation (@Nonnull Source aXML);

  /**
   * Apply the pyramid on the passed {@link Source} object.
   * 
   * @param sResourceName
   *        The optional name of the source. Only used for error messages. May
   *        be <code>null</code>.
   * @param aXML
   *        The XML {@link Source}. IMPORTANT: Must be a {@link Source} that can
   *        be opened multiple times. Using e.g. a StreamSource with a
   *        StringReader will result in an error!
   * @return a non-<code>null</code> validation pyramid result.
   */
  @Nonnull
  ValidationPyramidResult applyValidation (@Nullable String sResourceName, @Nonnull Source aXML);
}
