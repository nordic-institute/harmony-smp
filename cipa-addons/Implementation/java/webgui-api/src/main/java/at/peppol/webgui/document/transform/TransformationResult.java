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
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlSchema;

import org.w3c.dom.Node;

import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.microdom.IMicroNode;
import com.phloc.commons.state.ISuccessIndicator;

/**
 * Represents an aggregated transformation result object. It contains the result
 * object + a type descriptor.
 * 
 * @author philip
 */
@Immutable
public final class TransformationResult implements ISuccessIndicator {
  private final ETransformationResultType m_eResultType;
  private final Object m_aResultObj;

  private TransformationResult (@Nonnull final ETransformationResultType eResultType, @Nonnull final Object aResultObj) {
    if (eResultType == null)
      throw new NullPointerException ("resultType");
    if (aResultObj == null)
      throw new NullPointerException ("resultObj");

    m_eResultType = eResultType;
    m_aResultObj = aResultObj;
  }

  public boolean isSuccess () {
    return m_eResultType.isSuccess ();
  }

  public boolean isFailure () {
    return m_eResultType.isFailure ();
  }

  /**
   * @return The type of the result object. Never <code>null</code>.
   */
  @Nonnull
  public ETransformationResultType getResultType () {
    return m_eResultType;
  }

  /**
   * @return The generic result object. Never <code>null</code>.
   */
  @Nonnull
  public Object getResultObject () {
    return m_aResultObj;
  }

  /**
   * @return The result object as a DOM node. Never <code>null</code>.
   * @throws IllegalStateException
   *         In case the result object type is not a DOM node.
   */
  @Nonnull
  public Node getResultDOMNode () {
    if (m_eResultType != ETransformationResultType.DOM_NODE)
      throw new IllegalStateException ("Result is not of type DOM node but of type " + m_eResultType);
    return (Node) m_aResultObj;
  }

  /**
   * @return The result object as a micro node. Never <code>null</code>.
   * @throws IllegalStateException
   *         In case the result object type is not a micro node.
   */
  @Nonnull
  public IMicroNode getResultMicroNode () {
    if (m_eResultType != ETransformationResultType.MICRONODE)
      throw new IllegalStateException ("Result is not of type micro node but of type " + m_eResultType);
    return (IMicroNode) m_aResultObj;
  }

  /**
   * @return The result object as a readable resource. Never <code>null</code>.
   * @throws IllegalStateException
   *         In case the result object type is not a resource.
   */
  @Nonnull
  public IReadableResource getResultResource () {
    if (m_eResultType != ETransformationResultType.RESOURCE)
      throw new IllegalStateException ("Result is not of type resource but of type " + m_eResultType);
    return (IReadableResource) m_aResultObj;
  }

  /**
   * @return The result object as the transformation error messages. Never
   *         <code>null</code>.
   * @throws IllegalStateException
   *         In case the result object type is not a failure.
   */
  @Nonnull
  public IResourceErrorGroup getResultFailure () {
    if (m_eResultType != ETransformationResultType.FAILURE)
      throw new IllegalStateException ("Result is not of type failure but of type " + m_eResultType);
    return (IResourceErrorGroup) m_aResultObj;
  }

  /**
   * Create a failure result.
   * 
   * @param aErrorMsgs
   *        The error messages that occurred during transformation
   * @return The non-<code>null</code> {@link TransformationResult}.
   */
  @Nonnull
  public static TransformationResult createFailure (@Nonnull final IResourceErrorGroup aErrorMsgs) {
    return new TransformationResult (ETransformationResultType.FAILURE, aErrorMsgs);
  }

  /**
   * Create a result using a DOM node.
   * 
   * @param aNode
   *        The DOM node. May not be <code>null</code>.
   * @return The non-<code>null</code> {@link TransformationResult}.
   */
  @Nonnull
  public static TransformationResult createResult (@Nonnull final Node aNode) {
    return new TransformationResult (ETransformationResultType.DOM_NODE, aNode);
  }

  /**
   * Create a result using a micro node.
   * 
   * @param aMicroNode
   *        The micro node. May not be <code>null</code>.
   * @return The non-<code>null</code> {@link TransformationResult}.
   */
  @Nonnull
  public static TransformationResult createResult (@Nonnull final IMicroNode aMicroNode) {
    return new TransformationResult (ETransformationResultType.MICRONODE, aMicroNode);
  }

  /**
   * Create a result using a readable resource.
   * 
   * @param aRes
   *        The readable resource. May not be <code>null</code>.
   * @return The non-<code>null</code> {@link TransformationResult}.
   */
  @Nonnull
  public static TransformationResult createResult (@Nonnull final IReadableResource aRes) {
    return new TransformationResult (ETransformationResultType.RESOURCE, aRes);
  }

  /**
   * Create a result using a UBL native object.<br>
   * Note: this method has a different name as the parameter is of a generic
   * type.<br>
   * Note: the passed object must be a native object from phloc-ubl, as the
   * namespace of the @XmlSchema annotation of the class's owning package is
   * checked!
   * 
   * @param aUBLObject
   *        The UBL object. May not be <code>null</code>.
   * @return The non-<code>null</code> {@link TransformationResult}.
   * @throws IllegalArgumentException
   *         In case the passed object is not a valid UBL object.
   */
  @Nonnull
  public static TransformationResult createUBLResult (@Nonnull final Object aUBLObject) {
    if (aUBLObject == null)
      throw new NullPointerException ("UBLObject");

    // Check if the class's owning package has the @XmlSchema annotation
    final XmlSchema aSchema = aUBLObject.getClass ().getPackage ().getAnnotation (XmlSchema.class);
    if (aSchema == null)
      throw new IllegalArgumentException ("The passed object is not a valid UBL object (no @XmlSchema)!");

    // Check the namespace of the annotation whether it is UBL
    final String sNamespace = aSchema.namespace ();
    if (sNamespace == null || !sNamespace.startsWith ("urn:oasis:names:specification:ubl:schema:xsd:"))
      throw new IllegalArgumentException ("The passed object is not a valid UBL object (invalid namespace)!");

    return new TransformationResult (ETransformationResultType.UBL_TYPE, aUBLObject);
  }
}
