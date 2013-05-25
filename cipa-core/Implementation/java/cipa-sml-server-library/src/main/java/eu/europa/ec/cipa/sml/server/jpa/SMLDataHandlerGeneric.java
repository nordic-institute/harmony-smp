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

import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;

import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.commons.callback.DoNothingExceptionHandler;
import com.phloc.db.jpa.IEntityManagerProvider;
import com.phloc.db.jpa.JPAEnabledManager;
import com.phloc.db.jpa.JPAExecutionResult;

import eu.europa.ec.cipa.busdox.identifier.IReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.sml.server.IGenericDataHandler;
import eu.europa.ec.cipa.sml.server.datamodel.DBParticipantIdentifier;
import eu.europa.ec.cipa.sml.server.datamodel.DBParticipantIdentifierID;
import eu.europa.ec.cipa.sml.server.datamodel.DBServiceMetadataPublisher;
import eu.europa.ec.cipa.sml.server.datamodel.DBUser;
import eu.europa.ec.cipa.sml.server.exceptions.NotFoundException;
import eu.europa.ec.cipa.sml.server.exceptions.UnknownUserException;

/**
 * A JPA implementation of the {@link IGenericDataHandler} interface.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@UsedViaReflection
public final class SMLDataHandlerGeneric extends JPAEnabledManager implements IGenericDataHandler {
  private final ObjectFactory m_aObjFactory = new ObjectFactory ();

  public SMLDataHandlerGeneric () {
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

  @Nonnull
  public ServiceMetadataPublisherServiceType getSMPDataOfParticipant (@Nonnull final IReadonlyParticipantIdentifier aRecipientIdentifier) throws Throwable {
    JPAExecutionResult <ServiceMetadataPublisherServiceType> ret;
    ret = doInTransaction (new Callable <ServiceMetadataPublisherServiceType> () {
      @Nonnull
      public ServiceMetadataPublisherServiceType call () throws Exception {
        // Find identifier in DB
        final DBParticipantIdentifier aDBIdentifier = getEntityManager ().find (DBParticipantIdentifier.class,
                                                                                new DBParticipantIdentifierID (aRecipientIdentifier));
        if (aDBIdentifier == null)
          throw new NotFoundException ("The given identifier '" +
                                       IdentifierUtils.getIdentifierURIEncoded (aRecipientIdentifier) +
                                       "' was not found.");

        final DBServiceMetadataPublisher aPublisher = aDBIdentifier.getServiceMetadataPublisher ();

        // Build result object
        final ServiceMetadataPublisherServiceType aJAXBSMPData = m_aObjFactory.createServiceMetadataPublisherServiceType ();
        aJAXBSMPData.setServiceMetadataPublisherID (aPublisher.getSmpId ());
        final PublisherEndpointType aJAXBEndpoint = m_aObjFactory.createPublisherEndpointType ();
        aJAXBEndpoint.setLogicalAddress (aPublisher.getLogicalAddress ());
        aJAXBEndpoint.setPhysicalAddress (aPublisher.getPhysicalAddress ());
        aJAXBSMPData.setPublisherEndpoint (aJAXBEndpoint);
        return aJAXBSMPData;
      }
    });
    return ret.getOrThrow ();
  }

  @Nonnull
  public List <String> getAllSMPIDs () throws Throwable {
    JPAExecutionResult <List <String>> ret;
    ret = doSelect (new Callable <List <String>> () {
      @Nonnull
      public List <String> call () throws Exception {
        return getEntityManager ().createQuery ("SELECT p.smpId FROM DBServiceMetadataPublisher p", String.class)
                                  .getResultList ();
      }
    });
    return ret.getOrThrow ();
  }

  @Nonnull
  public PublisherEndpointType getSMPEndpointAddressOfSMPID (@Nonnull final String sSMPID) throws Throwable {
    if (sSMPID == null)
      throw new NotFoundException ("The service metadata publisher ID may not be null");

    JPAExecutionResult <PublisherEndpointType> ret;
    ret = doInTransaction (new Callable <PublisherEndpointType> () {
      @Nonnull
      public PublisherEndpointType call () throws Exception {
        final DBServiceMetadataPublisher aPublisher = getEntityManager ().find (DBServiceMetadataPublisher.class,
                                                                                sSMPID);
        if (aPublisher == null)
          throw new NotFoundException ("The service metadata publisher ID '" + sSMPID + "' was not found");

        final PublisherEndpointType aJAXBEndpoint = m_aObjFactory.createPublisherEndpointType ();
        aJAXBEndpoint.setLogicalAddress (aPublisher.getLogicalAddress ());
        aJAXBEndpoint.setPhysicalAddress (aPublisher.getPhysicalAddress ());
        return aJAXBEndpoint;
      }
    });
    return ret.getOrThrow ();
  }

  @Nonnull
  public ParticipantIdentifierPageType listParticipantIdentifiers (final String sPageID, final String sSMPID) throws Throwable {
    JPAExecutionResult <ParticipantIdentifierPageType> ret;
    ret = doInTransaction (new Callable <ParticipantIdentifierPageType> () {
      @Nonnull
      public ParticipantIdentifierPageType call () throws Exception {
        // Check that the smp exists.
        final DBServiceMetadataPublisher aPublisher = getEntityManager ().find (DBServiceMetadataPublisher.class,
                                                                                sSMPID);
        if (aPublisher == null)
          throw new NotFoundException ("The service metadata publisher ID '" + sSMPID + "' was not found");

        final ParticipantIdentifierPageType aJAXBPage = m_aObjFactory.createParticipantIdentifierPageType ();
        for (final DBParticipantIdentifier aDBIdentifier : aPublisher.getRecipientParticipantIdentifiers ())
          aJAXBPage.getParticipantIdentifier ().add (aDBIdentifier.getId ().asParticipantIdentifier ());
        aJAXBPage.setServiceMetadataPublisherID (sSMPID);

        return aJAXBPage;
      }
    });
    return ret.getOrThrow ();
  }

  public void verifyExistingUser (final String sClientUniqueID) throws UnknownUserException {
    final DBUser aDBUser = doSelect (new Callable <DBUser> () {
      @Nullable
      public DBUser call () throws Exception {
        return getEntityManager ().find (DBUser.class, sClientUniqueID);
      }
    }).get ();
    if (aDBUser == null)
      throw new UnknownUserException (sClientUniqueID);
  }
}
