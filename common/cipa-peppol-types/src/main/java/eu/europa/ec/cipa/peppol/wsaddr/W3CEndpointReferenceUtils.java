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
package eu.europa.ec.cipa.peppol.wsaddr;

import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.xml.ChildElementIterator;
import com.helger.commons.xml.XMLFactory;
import com.helger.commons.xml.XMLHelper;

/**
 * As the default WS-Addressing binding since JAXB 2.1 uses the
 * {@link W3CEndpointReference} class, we must also use this class, otherwise
 * JAXB would complain, that there are 2 contexts for the same namespace+element
 * combination.<br>
 * The issue with {@link W3CEndpointReference} is that it can easily be created
 * using the {@link W3CEndpointReferenceBuilder} class, but it's not possible to
 * extract information from it (get....). This class offers a workaround by
 * using DOM serialization to access the content of a
 * {@link W3CEndpointReference}. In case the serialization tag names of
 * {@link W3CEndpointReference} change, this implementation has to be adopted!<br>
 * The JIRA issue JAX_WS-1132 was filed to help dealing with this issue.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class W3CEndpointReferenceUtils {
  @PresentForCodeCoverage
  private static final W3CEndpointReferenceUtils s_aInstance = new W3CEndpointReferenceUtils ();

  private W3CEndpointReferenceUtils () {}

  /**
   * Create a new endpoint reference for the given address without reference
   * parameters.
   *
   * @param sAddress
   *        The address to use. May not be <code>null</code>.
   * @return The non-<code>null</code> endpoint reference for the given address
   */
  @Nonnull
  public static W3CEndpointReference createEndpointReference (@Nonnull final String sAddress) {
    return new W3CEndpointReferenceBuilder ().address (sAddress).build ();
  }

  /**
   * Create a new endpoint reference for the given address, using the specified
   * reference parameters.
   *
   * @param sAddress
   *        The address to use. May not be <code>null</code>.
   * @param aReferenceParameters
   *        The non-<code>null</code> list of reference parameters. May not be
   *        <code>null</code>.
   * @return The non-<code>null</code> endpoint reference for the given address
   */
  @Nonnull
  public static W3CEndpointReference createEndpointReference (@Nonnull final String sAddress,
                                                              @Nonnull final List <Element> aReferenceParameters) {
    W3CEndpointReferenceBuilder aBuilder = new W3CEndpointReferenceBuilder ().address (sAddress);
    for (final Element aReferenceParameter : aReferenceParameters)
      aBuilder = aBuilder.referenceParameter (aReferenceParameter);
    return aBuilder.build ();
  }

  /**
   * Marshal the passed endpoint reference to a DOM document and return the
   * document element.<br>
   * This is necessary, as {@link W3CEndpointReference} does not provide read
   * access methods.
   *
   * @param aEndpointReference
   *        The endpoint to be marshaled. May not be <code>null</code>.
   * @return The document element called "EndpointReference"
   */
  @Nonnull
  private static Element _convertReferenceToXML (@Nonnull final W3CEndpointReference aEndpointReference) {
    final Document aDoc = XMLFactory.newDocument ();
    final DOMResult ret = new DOMResult (aDoc);
    aEndpointReference.writeTo (ret);
    return aDoc.getDocumentElement ();
  }

  /**
   * Get the address contained in the passed endpoint reference.
   *
   * @param aEndpointReference
   *        The endpoint reference to retrieve the address from. May not be
   *        <code>null</code>.
   * @return The contained address.
   */
  @Nullable
  public static String getAddress (@Nonnull final W3CEndpointReference aEndpointReference) {
    ValueEnforcer.notNull (aEndpointReference, "EndpointReference");

    final Element eAddress = getFirstChildElementOfName (_convertReferenceToXML (aEndpointReference), "Address");
    return eAddress == null ? null : eAddress.getTextContent ();
  }

  /**
   * Search all child nodes of the given for the first element that has the
   * specified tag name.
   *
   * @param aStartNode
   *        The parent element to be searched. May not be <code>null</code>.
   * @param sName
   *        The tag name to search.
   * @return <code>null</code> if the parent element has no such child element.
   */
  @Nullable
  public static Element getFirstChildElementOfName (@Nonnull final Node aStartNode, @Nullable final String sName) {
    final NodeList aNodeList = aStartNode.getChildNodes ();
    final int nLen = aNodeList.getLength ();
    for (int i = 0; i < nLen; ++i) {
      final Node aNode = aNodeList.item (i);
      if (aNode.getNodeType () == Node.ELEMENT_NODE) {
        final Element aElement = (Element) aNode;
        if (aElement.getTagName ().equals (sName)) {
          return aElement;
        }
        else
          if (aElement.getLocalName ().equals (sName)) {
            return aElement;
          }

      }
    }
    return null;
  }

  /**
   * Get a list of all reference parameters contained in the passed endpoint
   * reference.
   *
   * @param aEndpointReference
   *        The endpoint reference to retrieve the reference parameters. May not
   *        be <code>null</code>.
   * @return A mutable element list
   */
  @Nullable
  public static List <Element> getReferenceParameters (@Nonnull final W3CEndpointReference aEndpointReference) {
    ValueEnforcer.notNull (aEndpointReference, "EndpointReference");

    final Element eRefParams = XMLHelper.getFirstChildElementOfName (_convertReferenceToXML (aEndpointReference),
                                                                     "ReferenceParameters");
    if (eRefParams == null)
      return null;

    return CollectionHelper.newList (new ChildElementIterator (eRefParams));
  }

  /**
   * Get the reference parameter at the given index
   *
   * @param aEndpointReference
   *        The object to retrieve the reference parameter from. May not be
   *        <code>null</code>.
   * @param nIndex
   *        The index to retrieve. Must be &ge; 0.
   * @return <code>null</code> if the index is invalid
   */
  @Nullable
  public static Element getReferenceParameter (@Nonnull final W3CEndpointReference aEndpointReference,
                                               @Nonnegative final int nIndex) {
    ValueEnforcer.notNull (aEndpointReference, "EndpointReference");
    ValueEnforcer.isGE0 (nIndex, "Index");

    // Get all reference parameters
    final List <Element> aReferenceParameters = getReferenceParameters (aEndpointReference);

    // And extract the one at the desired index.
    return CollectionHelper.getSafe (aReferenceParameters, nIndex);
  }
}