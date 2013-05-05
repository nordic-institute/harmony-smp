package eu.europa.ec.cipa.sml.server.jpa;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.db.jpa.AbstractPerRequestEntityManager;

/**
 * The per-request singleton, that creates {@link EntityManager} objects from
 * {@link SMLEntityManagerFactory}.
 * 
 * @author philip
 */
public final class SMLEntityManagerWrapper extends AbstractPerRequestEntityManager {
  @Deprecated
  @UsedViaReflection
  public SMLEntityManagerWrapper () {}

  public static SMLEntityManagerWrapper getInstance () {
    return getRequestSingleton (SMLEntityManagerWrapper.class);
  }

  @Override
  @Nonnull
  protected EntityManager createEntityManager () {
    return SMLEntityManagerFactory.getInstance ().createEntityManager ();
  }
}
