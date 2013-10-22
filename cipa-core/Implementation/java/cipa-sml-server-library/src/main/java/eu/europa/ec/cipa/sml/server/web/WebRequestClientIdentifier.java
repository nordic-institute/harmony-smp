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
package eu.europa.ec.cipa.sml.server.web;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;

/**
 * Manages the client unique ID for SML requests.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class WebRequestClientIdentifier {
  /** The name of the request attribute where the client unique ID is stored. */
  private static final String REQUEST_PARAMETER_CERTIFICATE = WebRequestClientIdentifier.class.getName ();

  private WebRequestClientIdentifier () {}

  /**
   * Set the client unique ID for the specified request to the specified value.
   * 
   * @param aHttpRequest
   *        The HTTP request to set the value. May not be <code>null</code>.
   * @param sClientUniqueID
   *        The client unique ID to be set. May neither be <code>null</code> nor
   *        empty.
   */
  public static void setClientUniqueID (@Nonnull final HttpServletRequest aHttpRequest,
                                        @Nonnull @Nonempty final String sClientUniqueID) {
    if (aHttpRequest == null)
      throw new NullPointerException ("httpRequest");
    if (StringHelper.hasNoText (sClientUniqueID))
      throw new IllegalArgumentException ("clientUniqueID is empty");
    aHttpRequest.setAttribute (REQUEST_PARAMETER_CERTIFICATE, sClientUniqueID);
  }

  /**
   * Get the client unique ID from the specified request.
   * 
   * @param aHttpRequest
   *        The HTTP request to get the value from. May not be <code>null</code>
   *        .
   * @return The client unique ID and never <code>null</code>.
   */
  @Nonnull
  public static String getClientUniqueID (@Nonnull final HttpServletRequest aHttpRequest) {
    if (aHttpRequest == null)
      throw new NullPointerException ("httpRequest");

    final Object aClientUniqueID = aHttpRequest.getAttribute (REQUEST_PARAMETER_CERTIFICATE);
    if (aClientUniqueID == null)
      throw new IllegalStateException ("No client unique ID found in request");
    if (!(aClientUniqueID instanceof String))
      throw new IllegalStateException ("Invalid client unique ID found in request: " +
                                       aClientUniqueID.getClass () +
                                       " -- " +
                                       aClientUniqueID.toString ());
    return (String) aClientUniqueID;
  }

  /**
   * Get the client unique ID from the specified WebService context.
   * 
   * @param aWSContext
   *        The WebService context to get the value from. May not be
   *        <code>null</code>.
   * @return The client unique ID and never <code>null</code>.
   */
  @Nonnull
  public static String getClientUniqueID (@Nonnull final WebServiceContext aWSContext) {
    if (aWSContext == null)
      throw new NullPointerException ("WSContext");

    final MessageContext aMessageContext = aWSContext.getMessageContext ();
    final HttpServletRequest aHttpRequest = (HttpServletRequest) aMessageContext.get (MessageContext.SERVLET_REQUEST);
    return getClientUniqueID (aHttpRequest);
  }
}
