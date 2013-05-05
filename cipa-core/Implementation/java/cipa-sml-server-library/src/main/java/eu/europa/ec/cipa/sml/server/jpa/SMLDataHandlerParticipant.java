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

import org.busdox.servicemetadata.locator._1.MigrationRecordType;
import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.busdox.servicemetadata.locator._1.PageRequestType;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;

import com.phloc.commons.callback.DoNothingExceptionHandler;
import com.phloc.commons.callback.IThrowingRunnable;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.db.jpa.IEntityManagerProvider;
import com.phloc.db.jpa.JPAEnabledManager;
import com.phloc.db.jpa.JPAExecutionResult;

import eu.europa.ec.cipa.busdox.identifier.IReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.IdentifierUtils;
import eu.europa.ec.cipa.sml.server.IParticipantDataHandler;
import eu.europa.ec.cipa.sml.server.IParticipantDataHandlerCallback;
import eu.europa.ec.cipa.sml.server.datamodel.DBAllowedWildcardSchemes;
import eu.europa.ec.cipa.sml.server.datamodel.DBMigrate;
import eu.europa.ec.cipa.sml.server.datamodel.DBMigrateID;
import eu.europa.ec.cipa.sml.server.datamodel.DBParticipantIdentifier;
import eu.europa.ec.cipa.sml.server.datamodel.DBParticipantIdentifierID;
import eu.europa.ec.cipa.sml.server.datamodel.DBServiceMetadataPublisher;
import eu.europa.ec.cipa.sml.server.exceptions.BadRequestException;
import eu.europa.ec.cipa.sml.server.exceptions.InternalErrorException;
import eu.europa.ec.cipa.sml.server.exceptions.NotFoundException;
import eu.europa.ec.cipa.sml.server.exceptions.UnauthorizedException;

/**
 * A JPA of the {@link IParticipantDataHandler} interface.<br>
 * TODO: Implement paging. Currently all found results are returned.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SMLDataHandlerParticipant extends JPAEnabledManager implements IParticipantDataHandler {
  private final ObjectFactory m_aObjFactory = new ObjectFactory ();
  private IParticipantDataHandlerCallback m_aCallback;

  public SMLDataHandlerParticipant () {
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

  public void setCallback (@Nullable final IParticipantDataHandlerCallback aCallback) {
    m_aCallback = aCallback;
  }

  private void _internalCreateParticipantIdentifier (@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier,
                                                     @Nonnull final DBServiceMetadataPublisher aSMP,
                                                     @Nonnull final String sClientUniqueID) throws Exception {
    // Then make sure that the participant identifier doesn't exists.
    final DBParticipantIdentifierID aDBIdentifierID = new DBParticipantIdentifierID (aParticipantIdentifier);
    final DBParticipantIdentifier aDBIdentifier = getEntityManager ().find (DBParticipantIdentifier.class,
                                                                            aDBIdentifierID);
    if (aDBIdentifier != null)
      throw new BadRequestException ("The participant identifier '" +
                                     IdentifierUtils.getIdentifierURIEncoded (aParticipantIdentifier) +
                                     "' does already exist.");

    // Find out if this is a Wildcard scheme
    final List <DBAllowedWildcardSchemes> aWildcardSchemes = getEntityManager ().createQuery ("SELECT p FROM DBAllowedWildcardSchemes p WHERE p.id.scheme=:scheme",
                                                                                              DBAllowedWildcardSchemes.class)
                                                                                .setParameter ("scheme",
                                                                                               aParticipantIdentifier.getScheme ())
                                                                                .getResultList ();
    if (!aWildcardSchemes.isEmpty ()) {
      // This is Wildcard scheme - validate user!
      if (!sClientUniqueID.equals (aWildcardSchemes.get (0).getUser ().getUsername ())) {
        throw new UnauthorizedException ("The user is not allowed to register ParticipantIdentifiers for this scheme: " +
                                         aParticipantIdentifier.getScheme ());
      }
      // User is allowed - verify that this is wildcard
      if (!"*".equals (aParticipantIdentifier.getValue ())) {
        throw new BadRequestException ("Only ParticipantIdentifier Wildcards can be registered for this scheme: " +
                                       aParticipantIdentifier.getScheme ());
      }
    }
    else {
      // Not a wild card scheme -> check that no wildcard is queried
      if ("*".equals (aParticipantIdentifier.getValue ())) {
        // This is not wildcard scheme - wildcard not allowed
        throw new UnauthorizedException ("The user is not allowed to register Wildcard for this scheme: " +
                                         aParticipantIdentifier.getScheme ());
      }
    }

    // Persist participant itself
    final DBParticipantIdentifier aDBParticipant = new DBParticipantIdentifier (aDBIdentifierID, aSMP);
    getEntityManager ().persist (aDBParticipant);

    // Add participant to SMP
    aSMP.getRecipientParticipantIdentifiers ().add (aDBParticipant);
    getEntityManager ().merge (aSMP);
  }

  public void createParticipantIdentifiers (@Nonnull final ParticipantIdentifierPageType aJAXBPage,
                                            @Nonnull final String sClientUniqueID) throws Throwable {
    JPAExecutionResult <?> ret;
    ret = doInTransaction (new IThrowingRunnable () {
      public void run () throws Exception {
        final String sSMPID = aJAXBPage.getServiceMetadataPublisherID ();

        // Check that the user owns the smp
        final DBServiceMetadataPublisher aSMP = getEntityManager ().find (DBServiceMetadataPublisher.class, sSMPID);
        if (aSMP == null)
          throw new NotFoundException ("The service metadata publisher ID '" + sSMPID + "' does not exist.");

        if (!aSMP.getUser ().getUsername ().equals (sClientUniqueID))
          throw new UnauthorizedException ("The current user does not own the service metadata publisher ID '" +
                                           sSMPID +
                                           "'!");

        // iterate participant identifiers
        for (final IReadonlyParticipantIdentifier aParticipantIdentifier : aJAXBPage.getParticipantIdentifier ())
          _internalCreateParticipantIdentifier (aParticipantIdentifier, aSMP, sClientUniqueID);

        // Create identifier in DNS
        if (m_aCallback != null)
          m_aCallback.identifiersCreated (aJAXBPage);
      }
    });
    if (ret.hasThrowable ())
      throw ret.getThrowable ();
  }

  private void _internalDeleteParticipant (@Nonnull final ParticipantIdentifierType aParticipantID,
                                           @Nonnull final String sClientUniqueID) throws Exception {
    // Then make sure that the participant identifier exists.
    final DBParticipantIdentifier aDBIdentifier = getEntityManager ().find (DBParticipantIdentifier.class,
                                                                            new DBParticipantIdentifierID (aParticipantID));
    if (aDBIdentifier == null)
      throw new NotFoundException ("The participant identifier " + aParticipantID + " does not exist.");

    // Check that the user owns the identifier
    if (!aDBIdentifier.getServiceMetadataPublisher ().getUser ().getUsername ().equals (sClientUniqueID))
      throw new UnauthorizedException ("The user does not own the identifier " + aParticipantID);

    // Remove from SMP as well
    final DBServiceMetadataPublisher aSMP = aDBIdentifier.getServiceMetadataPublisher ();
    aSMP.getRecipientParticipantIdentifiers ().remove (aDBIdentifier);
    getEntityManager ().merge (aSMP);

    // No wildcard test - as delete is allowed if user owns it...
    getEntityManager ().remove (aDBIdentifier);
  }

  public void deleteParticipantIdentifiers (@Nonnull final List <ParticipantIdentifierType> aParticipantIdentifiers,
                                            @Nonnull final String sClientUniqueID) throws Throwable {
    JPAExecutionResult <?> ret;
    ret = doInTransaction (new IThrowingRunnable () {
      public void run () throws Exception {
        for (final ParticipantIdentifierType aPI : aParticipantIdentifiers)
          _internalDeleteParticipant (aPI, sClientUniqueID);

        // Delete the identifier in the DNS system, and only delete it from
        // database if this succeeds.
        if (m_aCallback != null)
          m_aCallback.identifiersDeleted (aParticipantIdentifiers);
      }
    });
    if (ret.hasThrowable ())
      throw ret.getThrowable ();
  }

  public void prepareToMigrate (@Nonnull final MigrationRecordType aMigrationRecord,
                                @Nonnull final String sClientUniqueID) throws Throwable {
    JPAExecutionResult <?> ret;
    ret = doInTransaction (new IThrowingRunnable () {
      public void run () throws Exception {
        // Then make sure that the participant identifier exists.
        final ParticipantIdentifierType aParticipantID = aMigrationRecord.getParticipantIdentifier ();
        final DBParticipantIdentifier aDBIdentifier = getEntityManager ().find (DBParticipantIdentifier.class,
                                                                                new DBParticipantIdentifierID (aParticipantID));
        if (aDBIdentifier == null)
          throw new NotFoundException ("The participant identifier " + aParticipantID.toString () + " does not exist.");

        // Check that the user owns the identifier
        if (!aDBIdentifier.getServiceMetadataPublisher ().getUser ().getUsername ().equals (sClientUniqueID))
          throw new UnauthorizedException ("The user does not own the identifier " + aParticipantID.toString ());

        getEntityManager ().persist (new DBMigrate (new DBMigrateID (aMigrationRecord)));
      }
    });
    if (ret.hasThrowable ())
      throw ret.getThrowable ();
  }

  public void migrate (@Nonnull final MigrationRecordType aMigrationRecord, @Nonnull final String sClientUniqueID) throws Throwable {
    JPAExecutionResult <?> ret;
    ret = doInTransaction (new IThrowingRunnable () {
      public void run () throws Exception {
        // Change the owner of the identifier
        final ParticipantIdentifierType aParticipantID = aMigrationRecord.getParticipantIdentifier ();
        final DBParticipantIdentifier aDBIdentifier = getEntityManager ().find (DBParticipantIdentifier.class,
                                                                                new DBParticipantIdentifierID (aParticipantID));
        if (aDBIdentifier == null)
          throw new NotFoundException ("The participant identifier '" +
                                       aParticipantID.getScheme () +
                                       "::" +
                                       aParticipantID.getValue () +
                                       "' was not registered in the SML.");

        // Get the old SMP
        final DBServiceMetadataPublisher aOldSMP = aDBIdentifier.getServiceMetadataPublisher ();

        // And the new SMP
        final String sNewSMPID = aMigrationRecord.getServiceMetadataPublisherID ();
        final DBServiceMetadataPublisher aNewSMP = getEntityManager ().find (DBServiceMetadataPublisher.class,
                                                                             sNewSMPID);
        if (aNewSMP == null)
          throw new NotFoundException ("The new SMP with ID '" + sNewSMPID + "' was not found!");

        // Check that the user owns the smp
        if (!aNewSMP.getUser ().getUsername ().equals (sClientUniqueID))
          throw new UnauthorizedException ("The user does not own the identifier.");

        // Check that the migration code exists and is correct
        final DBMigrate aDBMigrate = getEntityManager ().find (DBMigrate.class, new DBMigrateID (aMigrationRecord));
        if (aDBMigrate == null)
          throw new NotFoundException ("No migration information exists for the given participant identifier plus key.");

        // Remove participant from old SMP
        if (!aOldSMP.getRecipientParticipantIdentifiers ().remove (aDBIdentifier))
          throw new InternalErrorException ("Failed to remove participant from old SMP");
        getEntityManager ().merge (aOldSMP);

        // Add participant to new SMP
        aNewSMP.getRecipientParticipantIdentifiers ().add (aDBIdentifier);
        getEntityManager ().merge (aNewSMP);

        // Update participant
        aDBIdentifier.setServiceMetadataPublisher (aNewSMP);
        getEntityManager ().merge (aDBIdentifier);

        // Delete the migration record.
        getEntityManager ().remove (aDBMigrate);

        // The database is only updated, if both DNS updates goes well.
        if (m_aCallback != null)
          m_aCallback.identifiersDeleted (ContainerHelper.newList (aMigrationRecord.getParticipantIdentifier ()));

        final ParticipantIdentifierPageType aJAXBPage = m_aObjFactory.createParticipantIdentifierPageType ();
        aJAXBPage.getParticipantIdentifier ().add (aMigrationRecord.getParticipantIdentifier ());
        aJAXBPage.setServiceMetadataPublisherID (aMigrationRecord.getServiceMetadataPublisherID ());

        if (m_aCallback != null)
          m_aCallback.identifiersCreated (aJAXBPage);
      }
    });
    if (ret.hasThrowable ())
      throw ret.getThrowable ();
  }

  @Nonnull
  public ParticipantIdentifierPageType listParticipantIdentifiers (@Nonnull final PageRequestType aPageRequest,
                                                                   @Nonnull final String sClientUniqueID) throws Throwable {
    JPAExecutionResult <ParticipantIdentifierPageType> ret;
    ret = doSelect (new Callable <ParticipantIdentifierPageType> () {
      @Nonnull
      public ParticipantIdentifierPageType call () throws Exception {
        // Check that the smp exists.
        final String sSMPID = aPageRequest.getServiceMetadataPublisherID ();
        final DBServiceMetadataPublisher aSMP = getEntityManager ().find (DBServiceMetadataPublisher.class, sSMPID);
        if (aSMP == null)
          throw new NotFoundException ("The given service metadata publisher '" + sSMPID + "' does not exist.");

        // Check that the user owns the smp
        if (!aSMP.getUser ().getUsername ().equals (sClientUniqueID))
          throw new UnauthorizedException ("The user does not own the identifier.");

        // Get all participant identifiers
        final ParticipantIdentifierPageType aJAXBPage = m_aObjFactory.createParticipantIdentifierPageType ();
        for (final DBParticipantIdentifier aDBIdentifier : aSMP.getRecipientParticipantIdentifiers ())
          aJAXBPage.getParticipantIdentifier ().add (aDBIdentifier.getId ().asParticipantIdentifier ());
        return aJAXBPage;
      }
    });
    return ret.getOrThrow ();
  }
}
