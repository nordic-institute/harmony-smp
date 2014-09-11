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
package eu.europa.ec.cipa.transport.start.client;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.state.ESuccess;
import com.helger.commons.state.ISuccessIndicator;
import com.helger.commons.string.ToStringGenerator;

/**
 * The result of the START client sending a document
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public class AccessPointClientSendResult implements ISuccessIndicator {
  private final ESuccess m_eSuccess;
  private final List <String> m_aErrorMessages = new ArrayList <String> ();

  /**
   * Constructor
   * 
   * @param eSuccess
   *        The sending result. Either success or failure. May not be
   *        <code>null</code>.
   */
  public AccessPointClientSendResult (@Nonnull final ESuccess eSuccess) {
    m_eSuccess = ValueEnforcer.notNull (eSuccess, "Success");
  }

  public boolean isSuccess () {
    return m_eSuccess.isSuccess ();
  }

  public boolean isFailure () {
    return m_eSuccess.isFailure ();
  }

  /**
   * @return A copy of all contained error messages. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <String> getAllErrorMessages () {
    return ContainerHelper.newList (m_aErrorMessages);
  }

  /**
   * @return The number of contained error messages. Always &ge; 0.
   */
  @Nonnegative
  public int getErrorMessageCount () {
    return m_aErrorMessages.size ();
  }

  /**
   * Add an error message
   * 
   * @param sErrorMsg
   *        The message to be added. May neither be <code>null</code> nor empty.
   * @return this
   */
  @Nonnull
  public AccessPointClientSendResult addErrorMessage (@Nonnull @Nonempty final String sErrorMsg) {
    ValueEnforcer.notEmpty (sErrorMsg, "ErrorMsg");
    m_aErrorMessages.add (sErrorMsg);
    return this;
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("success", m_eSuccess)
                                       .append ("errorMessages", m_aErrorMessages)
                                       .toString ();
  }
}
