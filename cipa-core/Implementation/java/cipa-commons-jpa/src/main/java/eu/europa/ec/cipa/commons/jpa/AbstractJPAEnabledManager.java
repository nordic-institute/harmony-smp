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
package eu.europa.ec.cipa.commons.jpa;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.state.ESuccess;

/**
 * Abstract base class for entity managers. Provides the
 * {@link #doInTransaction(Runnable)} method and other sanity methods.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@ThreadSafe
public abstract class AbstractJPAEnabledManager {
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractJPAEnabledManager.class);

  protected final Lock m_aLock = new ReentrantLock ();
  private final AbstractJPAWrapper m_aWrapper;

  protected AbstractJPAEnabledManager (@Nonnull final AbstractJPAWrapper aWrapper) {
    if (aWrapper == null)
      throw new NullPointerException ("wrapper");
    m_aWrapper = aWrapper;
  }

  @Nonnull
  protected final EntityManager getEntityManager () {
    return m_aWrapper.getEntityManager ();
  }

  @Nonnull
  public static final ESuccess doInTransaction (@Nonnull final EntityManager aEntityMgr,
                                                @Nonnull final Runnable aRunnable) {
    final EntityTransaction aTransaction = aEntityMgr.getTransaction ();
    aTransaction.begin ();
    try {
      // Execute whatever you want to do
      aRunnable.run ();
      // And if no exception was thrown, commit it
      aTransaction.commit ();
      return ESuccess.SUCCESS;
    }
    catch (final RollbackException t) {
      s_aLogger.error ("Failed to perform sthg. in transaction!", t);
    }
    finally {
      if (aTransaction.isActive ()) {
        // We got an exception -> rollback
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction!");
      }
    }
    return ESuccess.FAILURE;
  }

  @Nonnull
  public final ESuccess doInTransaction (@Nonnull final Runnable aRunnable) {
    // Ensure that only one transaction is active for all users!
    m_aLock.lock ();
    try {
      return doInTransaction (getEntityManager (), aRunnable);
    }
    finally {
      m_aLock.unlock ();
    }
  }
}
