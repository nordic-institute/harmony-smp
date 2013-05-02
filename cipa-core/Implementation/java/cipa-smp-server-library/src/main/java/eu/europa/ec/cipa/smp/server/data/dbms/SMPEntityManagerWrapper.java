package eu.europa.ec.cipa.smp.server.data.dbms;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.db.jpa.AbstractEntityManagerSingleton;

/**
 * The per-request singleton, that creates {@link EntityManager} objects from
 * {@link SMPEntityManagerFactory}.
 * 
 * @author philip
 */
public class SMPEntityManagerWrapper extends AbstractEntityManagerSingleton {
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
