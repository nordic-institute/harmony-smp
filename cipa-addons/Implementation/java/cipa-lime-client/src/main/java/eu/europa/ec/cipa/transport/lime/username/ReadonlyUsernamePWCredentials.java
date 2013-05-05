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
package eu.europa.ec.cipa.transport.lime.username;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IReadonlyUsernamePWCredentials}
 * interface. It encapsulates an object of type {@link UsernamePWCredentials}
 * and offers only the reading methods. This is done to avoid copying too much
 * business logic.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class ReadonlyUsernamePWCredentials implements IReadonlyUsernamePWCredentials {
  private final UsernamePWCredentials m_aCredentials;

  public ReadonlyUsernamePWCredentials (@Nonnull final IReadonlyUsernamePWCredentials aCredentials) {
    this (aCredentials.getUsername (), aCredentials.getPassword ());
  }

  public ReadonlyUsernamePWCredentials (@Nonnull final String sUsername, @Nullable final String sPassword) {
    m_aCredentials = new UsernamePWCredentials (sUsername, sPassword);
  }

  @Nonnull
  public String getUsername () {
    return m_aCredentials.getUsername ();
  }

  @Nullable
  public String getPassword () {
    return m_aCredentials.getPassword ();
  }

  @Override
  public boolean equals (final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof ReadonlyUsernamePWCredentials))
      return false;
    final ReadonlyUsernamePWCredentials rhs = (ReadonlyUsernamePWCredentials) o;
    return m_aCredentials.equals (rhs.m_aCredentials);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_aCredentials).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("username", getUsername ()).appendPassword ("password").toString ();
  }
}
