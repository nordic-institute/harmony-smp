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
package eu.europa.ec.cipa.peppol.ipmapper;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.ValueEnforcer;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.StringParser;
import com.phloc.commons.string.ToStringGenerator;

/**
 * @author PEPPOL.AT, BRZ, Andreas Haberl
 */
@Immutable
public final class MappedDNSHost {
  private final String m_sHost;
  private final Integer m_aPort;

  /**
   * Constructor with no port
   * 
   * @param sHost
   *        The host name
   */
  public MappedDNSHost (@Nonnull @Nonempty final String sHost) {
    this (sHost, (Integer) null);
  }

  /**
   * Constructor with host and port
   * 
   * @param sHost
   *        Host name
   * @param nPort
   *        Port number
   */
  public MappedDNSHost (@Nonnull @Nonempty final String sHost, @Nonnegative final int nPort) {
    this (sHost, Integer.valueOf (nPort));
  }

  public MappedDNSHost (@Nonnull @Nonempty final String sHost, @Nullable final Integer aPort) {
    ValueEnforcer.notEmpty (sHost, "Host");
    if (aPort != null && aPort.intValue () <= 0)
      throw new IllegalArgumentException ("The passed port " + aPort + " is invalid!");
    m_sHost = sHost;
    m_aPort = aPort;
  }

  /**
   * @return The mapped host name
   */
  @Nonnull
  @Nonempty
  public String getHost () {
    return m_sHost;
  }

  /**
   * @return The mapped port. May be <code>null</code> if the original port
   *         should be used
   */
  @Nullable
  public Integer getPort () {
    return m_aPort;
  }

  public int getPortToUse (final int nDefaultPort) {
    return m_aPort != null ? m_aPort.intValue () : nDefaultPort;
  }

  @Nonnull
  @Nonempty
  public String getHostString () {
    return m_aPort == null ? m_sHost : m_sHost + ':' + m_aPort;
  }

  /**
   * @param nDefaultPort
   *        Default port to use, if none is specified
   * @return <em>host</em>:<em>port</em> if a port &gt; 0 was found,
   *         <em>host</em> otherwise
   */
  @Nonnull
  @Nonempty
  public String getHostString (final int nDefaultPort) {
    final int nRealPort = getPortToUse (nDefaultPort);
    if (nRealPort > 0)
      return m_sHost + ':' + nRealPort;
    return m_sHost;
  }

  @Override
  public boolean equals (final Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof MappedDNSHost))
      return false;
    final MappedDNSHost other = (MappedDNSHost) obj;
    return m_sHost.equals (other.m_sHost) && EqualsUtils.equals (m_aPort, other.m_aPort);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sHost).append (m_aPort).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("host", m_sHost).append ("port", m_aPort).toString ();
  }

  /**
   * Create a socket part from a string having the layout "host:port" or "host"
   * only. Port must be numeric and must be a valid port number.
   * 
   * @param sSocketString
   *        The socket string to be parsed.
   * @return The {@link MappedDNSHost} to be used.
   */
  @Nonnull
  public static MappedDNSHost create (@Nonnull @Nonempty final String sSocketString) {
    ValueEnforcer.notEmpty (sSocketString, "SocketString");

    final String [] aParts = StringHelper.getExplodedArray (':', sSocketString, 2);
    if (aParts.length == 0)
      throw new IllegalArgumentException ("invalid socketString '" +
                                          sSocketString +
                                          "' - at least the host must be defined");
    if (aParts.length == 1) {
      // No port found
      return new MappedDNSHost (aParts[0], null);
    }

    // Found a port
    final String sPort = aParts[1];
    final Integer aPort = StringParser.parseIntObj (sPort);
    if ((sPort != null && aPort == null) || aPort.intValue () <= 0)
      throw new IllegalArgumentException ("The passed port " + sPort + " cannot be converted to a number!");
    return new MappedDNSHost (aParts[0], aPort);
  }
}
