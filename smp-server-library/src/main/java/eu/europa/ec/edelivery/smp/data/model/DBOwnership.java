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

/**
 * Define the ownership of a service group -&gt; relates DB user to DB service
 * group.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Entity
@Table (name = "smp_ownership")
public class DBOwnership implements Serializable {
  private DBOwnershipID m_aID;
  private DBUser m_aUser;
  private DBServiceGroup m_aServiceGroup;

  public DBOwnership () {}

  public DBOwnership (final DBOwnershipID aID, final DBUser aUser, final DBServiceGroup aServiceGroup) {
    m_aID = aID;
    m_aUser = aUser;
    m_aServiceGroup = aServiceGroup;
  }

  @EmbeddedId
  public DBOwnershipID getId () {
    return m_aID;
  }

  public void setId (final DBOwnershipID aID) {
    m_aID = aID;
  }

  @ManyToOne (fetch = FetchType.LAZY)
  @JoinColumn (name = "username", nullable = false, insertable = false, updatable = false)
  public DBUser getUser () {
    return m_aUser;
  }

  public void setUser (final DBUser aUser) {
    m_aUser = aUser;
  }

  @ManyToOne (fetch = FetchType.LAZY)
  @JoinColumns ({ @JoinColumn (name = "businessIdentifierScheme",
                               referencedColumnName = "businessIdentifierScheme",
                               nullable = false,
                               insertable = false,
                               updatable = false),
                 @JoinColumn (name = "businessIdentifier",
                              referencedColumnName = "businessIdentifier",
                              nullable = false,
                              insertable = false,
                              updatable = false) })
  public DBServiceGroup getServiceGroup () {
    return m_aServiceGroup;
  }

  public void setServiceGroup (final DBServiceGroup aServiceGroup) {
    m_aServiceGroup = aServiceGroup;
  }
}
