/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.edelivery.smp.data.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single user within the SMP database.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Entity
@Table (name = "smp_user")
public class DBUser implements Serializable {
  private String m_sUserName;
  private String m_sPassword;
  private boolean m_bIsAdmin;
  private Set <DBOwnership> m_aOwnerships = new HashSet <DBOwnership> ();

  public DBUser () {}

  @Id
  @Column (name = "username", unique = true, nullable = false, length = 256)
  public String getUsername () {
    return m_sUserName;
  }

  public void setUsername (final String sUserName) {
    m_sUserName = sUserName;
  }

  @Column (name = "password",length = 256)
  public String getPassword () {
    return m_sPassword;
  }

  public void setPassword (final String sPassword) {
    m_sPassword = sPassword;
  }

  @Column(name = "isadmin", nullable = false)
  public boolean isAdmin() {
    return m_bIsAdmin;
  }

  public void setAdmin(boolean isAdmin) {
    m_bIsAdmin = isAdmin;
  }

  @OneToMany (fetch = FetchType.LAZY, mappedBy = "user")
  public Set <DBOwnership> getOwnerships () {
    return m_aOwnerships;
  }

  public void setOwnerships (final Set <DBOwnership> aOwnerships) {
    m_aOwnerships = aOwnerships;
  }
}
