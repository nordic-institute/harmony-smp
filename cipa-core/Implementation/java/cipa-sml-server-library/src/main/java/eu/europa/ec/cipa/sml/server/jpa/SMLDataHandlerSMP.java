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
package eu.europa.ec.cipa.sml.server.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;

import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;

import com.phloc.commons.callback.DoNothingExceptionHandler;
import com.phloc.commons.callback.IThrowingRunnable;
import com.phloc.db.jpa.IEntityManagerProvider;
import com.phloc.db.jpa.JPAEnabledManager;
import com.phloc.db.jpa.JPAExecutionResult;

import eu.europa.ec.cipa.sml.server.ISMPDataHandler;
import eu.europa.ec.cipa.sml.server.ISMPDataHandlerCallback;
import eu.europa.ec.cipa.sml.server.datamodel.DBParticipantIdentifier;
import eu.europa.ec.cipa.sml.server.datamodel.DBServiceMetadataPublisher;
import eu.europa.ec.cipa.sml.server.datamodel.DBUser;
import eu.europa.ec.cipa.sml.server.exceptions.BadRequestException;
import eu.europa.ec.cipa.sml.server.exceptions.NotFoundException;
import eu.europa.ec.cipa.sml.server.exceptions.UnauthorizedException;

/**
 * A JPA implementation of the {@link ISMPDataHandler} interface.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SMLDataHandlerSMP extends JPAEnabledManager implements ISMPDataHandler {
  private final ObjectFactory m_aObjFactory = new ObjectFactory ();
  private ISMPDataHandlerCallback m_aCallback;

  public SMLDataHandlerSMP () {
    super (new IEntityManagerProvider () {
      // This additional indirection level is required!!!
      // So that for every request the correct getInstance is invoked!
      @Nonnull
      public EntityManager getEntityManager () {
        return SMLEntityManagerWrapper.getInstance ().getEntityManager ();
      }
    });
    // Exceptions are handled by re-throwing them
    setCustomExceptionHandler (new DoNothingExceptionHandler ());
  }

  public void setCallback (@Nullable final ISMPDataHandlerCallback aCallback) {
    m_aCallback = aCallback;
  }

  public void createSMPData (@Nonnull final ServiceMetadataPublisherServiceType aSMPData, final String sClientUniqueID) throws Throwable {
    JPAExecutionResult <?> ret;
    ret = doInTransaction (new IThrowingRunnable () {
      public void run () throws Exception {
        // This is the only place, where the user is automatically created if it
        // does not exist, because this action must be the very first one!
        DBUser aDBUser = getEntityManager ().find (DBUser.class, sClientUniqueID);
        if (aDBUser == null) {
          // Password may not be null -> work around hack; password is unused!
          aDBUser = new DBUser (sClientUniqueID, "");
          getEntityManager ().persist (aDBUser);
        }

        // Then make sure that the smp does not exist.
        final String sSMPID = aSMPData.getServiceMetadataPublisherID ();
        DBServiceMetadataPublisher aSMP = getEntityManager ().find (DBServiceMetadataPublisher.class, sSMPID);
        if (aSMP != null)
          throw new BadRequestException ("The SMP '" + sSMPID + "' is already existing.");

        // Save the service metadata
        aSMP = new DBServiceMetadataPublisher (sSMPID,
                                               aDBUser,
                                               aSMPData.getPublisherEndpoint ().getPhysicalAddress (),
                                               aSMPData.getPublisherEndpoint ().getLogicalAddress ());
        getEntityManager ().persist (aSMP);

        // If DNS create fails, then try to rollback database update.
        if (m_aCallback != null)
          m_aCallback.serviceMetadataCreated (aSMPData);
      }
    });
    if (ret.hasThrowable ())
      throw ret.getThrowable ();
  }

  @Nonnull
  public ServiceMetadataPublisherServiceType getSMPData (final String sSMPID, final String sClientUniqueID) throws Throwable {
    JPAExecutionResult <ServiceMetadataPublisherServiceType> ret;
    ret = doSelect (new Callable <ServiceMetadataPublisherServiceType> () {
      @Nonnull
      public ServiceMetadataPublisherServiceType call () throws Exception {
        // Then make sure that the smp exist.
        final DBServiceMetadataPublisher aSMP = getEntityManager ().find (DBServiceMetadataPublisher.class, sSMPID);
        if (aSMP == null)
          throw new NotFoundException ("The service metadata does not exist.");

        // Make sure that the smp is owned by the user
        if (!aSMP.getUser ().getUsername ().equals (sClientUniqueID))
          throw new UnauthorizedException ("The SMP '" +
                                           sSMPID +
                                           "' is not owned by the given username '" +
                                           sClientUniqueID +
                                           "'");

        // Convert the SMP.
        final ServiceMetadataPublisherServiceType aJAXBSMPData = m_aObjFactory.createServiceMetadataPublisherServiceType ();
        aJAXBSMPData.setServiceMetadataPublisherID (aSMP.getSmpId ());
        final PublisherEndpointType aJAXBEndpoint = m_aObjFactory.createPublisherEndpointType ();
        aJAXBEndpoint.setLogicalAddress (aSMP.getLogicalAddress ());
        aJAXBEndpoint.setPhysicalAddress (aSMP.getPhysicalAddress ());
        aJAXBSMPData.setPublisherEndpoint (aJAXBEndpoint);

        return aJAXBSMPData;
      }
    });
    return ret.getOrThrow ();
  }

  public void updateSMPData (@Nonnull final ServiceMetadataPublisherServiceType aSMPData, final String sClientUniqueID) throws Throwable {
    JPAExecutionResult <?> ret;
    ret = doInTransaction (new IThrowingRunnable () {
      public void run () throws Exception {
        // Then make sure that the smp exist.
        final String sSMPID = aSMPData.getServiceMetadataPublisherID ();
        final DBServiceMetadataPublisher aSMP = getEntityManager ().find (DBServiceMetadataPublisher.class, sSMPID);
        if (aSMP == null)
          throw new NotFoundException ("The service metadata publisher ID '" + sSMPID + "' does not exist.");

        // Make sure that the smp is owned by the user
        if (!aSMP.getUser ().getUsername ().equals (sClientUniqueID))
          throw new UnauthorizedException ("The SMP '" +
                                           sSMPID +
                                           "' is not owned by the given username '" +
                                           sClientUniqueID +
                                           "'");

        // Save the SMP.
        aSMP.setLogicalAddress (aSMPData.getPublisherEndpoint ().getLogicalAddress ());
        aSMP.setPhysicalAddress (aSMPData.getPublisherEndpoint ().getPhysicalAddress ());
        getEntityManager ().merge (aSMP);

        // Only if the DNS goes well
        if (m_aCallback != null)
          m_aCallback.serviceMetadataUpdated (aSMPData);
      }
    });
    if (ret.hasThrowable ())
      throw ret.getThrowable ();
  }

  public void deleteSMPData (final String sSMPID, final String sClientUniqueID) throws Throwable {
    JPAExecutionResult <?> ret;
    ret = doInTransaction (new IThrowingRunnable () {
      public void run () throws Exception {
        // Then make sure that the smp exist.
        final DBServiceMetadataPublisher aSMP = getEntityManager ().find (DBServiceMetadataPublisher.class, sSMPID);
        if (aSMP == null)
          throw new NotFoundException ("The service metadata publisher ID '" + sSMPID + "' does not exist.");

        // Make sure that the smp is owned by the user
        if (!aSMP.getUser ().getUsername ().equals (sClientUniqueID))
          throw new UnauthorizedException ("The SMP '" +
                                           sSMPID +
                                           "' is not owned by the given username '" +
                                           sClientUniqueID +
                                           "'");

        // Delete the SMP.
        getEntityManager ().remove (aSMP);

        // Delete in DNS
        if (m_aCallback != null) {
          final List <ParticipantIdentifierType> aPIs = new ArrayList <ParticipantIdentifierType> ();
          for (final DBParticipantIdentifier aDBPI : aSMP.getRecipientParticipantIdentifiers ())
            aPIs.add (aDBPI.getId ().asParticipantIdentifier ());
          m_aCallback.serviceMetadataDeleted (sSMPID, aPIs);
        }
      }
    });
    if (ret.hasThrowable ())
      throw ret.getThrowable ();
  }
}
