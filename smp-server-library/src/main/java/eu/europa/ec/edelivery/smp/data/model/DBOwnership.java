/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table (name = "smp_ownership")
public class DBOwnership implements Serializable {

  private DBOwnershipId ownershipId;
  private DBUser user;
  private DBServiceGroup serviceGroup;

  public DBOwnership () {}

  public DBOwnership (final DBOwnershipId ownershipId, final DBUser user, final DBServiceGroup serviceGroup) {
    this.ownershipId = ownershipId;
    this.user = user;
    this.serviceGroup = serviceGroup;
  }

  @EmbeddedId
  public DBOwnershipId getId () {
    return ownershipId;
  }

  @ManyToOne (fetch = FetchType.LAZY)
  @JoinColumn (name = "username", nullable = false, insertable = false, updatable = false)
  public DBUser getUser () {
    return user;
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
    return serviceGroup;
  }

  public void setId (final DBOwnershipId ownershipId) {
    this.ownershipId = ownershipId;
  }

  public void setUser (final DBUser user) {
    this.user = user;
  }

  public void setServiceGroup (final DBServiceGroup serviceGroup) {
    this.serviceGroup = serviceGroup;
  }
}
