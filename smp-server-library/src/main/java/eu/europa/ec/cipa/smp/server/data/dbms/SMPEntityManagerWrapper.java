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
package eu.europa.ec.cipa.smp.server.data.dbms;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import com.helger.commons.annotations.UsedViaReflection;
import com.helger.db.jpa.AbstractPerRequestEntityManager;

/**
 * The per-request singleton, that creates {@link EntityManager} objects from
 * {@link SMPEntityManagerFactory}.
 * 
 * @author philip
 */
public final class SMPEntityManagerWrapper extends AbstractPerRequestEntityManager {
  @Deprecated
  @UsedViaReflection
  public SMPEntityManagerWrapper () {}

  public static SMPEntityManagerWrapper getInstance () {
    return getRequestSingleton (SMPEntityManagerWrapper.class);
  }

  @Override
  @Nonnull
  protected EntityManager createEntityManager () {
    return SMPEntityManagerFactory.getInstance ().createEntityManager ();
  }
}
