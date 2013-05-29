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
package eu.europa.ec.cipa.peppol.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.busdox._2010._02.channel.fault.StartException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2009._02.ws_tra.FaultMessage;

import com.phloc.commons.lang.StackTraceHelper;

/**
 * Misc. exception utility methods.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class ExceptionUtils {
  private static final Logger s_aLogger = LoggerFactory.getLogger (ExceptionUtils.class);

  private ExceptionUtils () {}

  /**
   * Create a {@link StartException} from an existing {@link Throwable}.
   * 
   * @param sAction
   *        The action describing what went wrong.
   * @param t
   *        The throwable to be converted. May be <code>null</code>.
   * @return The newly created {@link StartException} object. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static StartException createStartException (@Nonnull final String sAction,
                                                     @Nonnull final String sDetails,
                                                     @Nullable final Throwable t) {
    final StartException aStartEx = new StartException ();
    aStartEx.setAction (sAction);
    aStartEx.setFaultcode (t instanceof Exception ? "exception" : "error");
    aStartEx.setFaultstring (t != null ? t.getMessage () : sAction);
    aStartEx.setDetails (sDetails);
    return aStartEx;
  }

  @Nonnull
  public static FaultMessage createFaultMessage (@Nonnull final String sAction) {
    return createFaultMessage (sAction, (String) null);
  }

  @Nonnull
  public static FaultMessage createFaultMessage (@Nonnull final String sAction, @Nullable final String sDetails) {
    s_aLogger.warn ("Will throw FaultMessage: " + sAction);
    return new FaultMessage (sAction, createStartException (sAction,
                                                            sDetails != null ? sDetails : sAction,
                                                            (Throwable) null));
  }

  @Nonnull
  public static FaultMessage createFaultMessage (@Nonnull final String sAction, @Nonnull final Throwable t) {
    s_aLogger.warn ("Will throw FaultMessage: " + sAction + " caused by " + t.getMessage ());
    return new FaultMessage (sAction, createStartException (sAction, StackTraceHelper.getStackAsString (t), t));
  }
}
