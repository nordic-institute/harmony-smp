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
package eu.europa.ec.cipa.sml.server.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;

import eu.europa.ec.cipa.peppol.identifier.CIdentifier;

/**
 * AllowedWildcardSchemesId generated by hbm2java
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Embeddable
public class DBAllowedWildcardSchemesID implements Serializable {
  private String m_sScheme;
  private String m_sUsername;

  @Deprecated
  @DevelopersNote ("Used only by Hibernate")
  public DBAllowedWildcardSchemesID () {}

  public DBAllowedWildcardSchemesID (final String scheme, final String username) {
    setScheme (scheme);
    setUsername (username);
  }

  @Column (name = "scheme", nullable = false, length = CIdentifier.MAX_IDENTIFIER_SCHEME_LENGTH)
  public String getScheme () {
    return m_sScheme;
  }

  public void setScheme (final String scheme) {
    m_sScheme = scheme;
  }

  @Column (name = "username", nullable = false, length = CDB.MAX_USERNAME_LENGTH)
  public String getUsername () {
    return m_sUsername;
  }

  public void setUsername (final String username) {
    m_sUsername = username;
  }

  @Override
  public boolean equals (final Object other) {
    if ((this == other))
      return true;
    if (!(other instanceof DBAllowedWildcardSchemesID))
      return false;
    final DBAllowedWildcardSchemesID rhs = (DBAllowedWildcardSchemesID) other;
    return EqualsUtils.equals (m_sScheme, rhs.m_sScheme) && EqualsUtils.equals (m_sUsername, rhs.m_sUsername);
  }

  @Override
  public int hashCode () {
    return new HashCodeGenerator (this).append (m_sScheme).append (m_sUsername).getHashCode ();
  }

  @Override
  public String toString () {
    return new ToStringGenerator (this).append ("scheme", m_sScheme).append ("username", m_sUsername).toString ();
  }
}
