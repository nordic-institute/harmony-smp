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

import javax.annotation.Nullable;
import javax.persistence.EntityTransaction;

import org.busdox.servicemetadata.locator._1.MigrationRecordType;
import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.busdox.servicemetadata.locator._1.PageRequestType;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.collections.ContainerHelper;

import eu.europa.ec.cipa.busdox.identifier.IReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.commons.jpa.AbstractJPAEnabledManager;
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
import eu.europa.ec.cipa.sml.server.exceptions.UnknownUserException;

/**
 * A JPA of the {@link IParticipantDataHandler} interface.<br>
 * TODO: Implement paging. Currently all found results are returned.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class JPAParticipantDataHandler extends AbstractJPAEnabledManager implements IParticipantDataHandler {
  private static final Logger s_aLogger = LoggerFactory.getLogger (JPAParticipantDataHandler.class);

  private final ObjectFactory m_aObjFactory = new ObjectFactory ();
  private IParticipantDataHandlerCallback m_aCallback;

  public JPAParticipantDataHandler () {
    super (SMLJPAWrapper.getInstance ());
  }

  public void setCallback (@Nullable final IParticipantDataHandlerCallback aCallback) {
    m_aCallback = aCallback;
  }

  public void createParticipantIdentifiers (final ParticipantIdentifierPageType aJAXBPage, final String sClientUniqueID) throws NotFoundException,
                                                                                                                        UnauthorizedException,
                                                                                                                        UnknownUserException,
                                                                                                                        InternalErrorException,
                                                                                                                        BadRequestException {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      final String sSMPID = aJAXBPage.getServiceMetadataPublisherID ();

      // Check that the user owns the smp
      final DBServiceMetadataPublisher aSMP = getEntityManager ().find (DBServiceMetadataPublisher.class, sSMPID);
      if (aSMP == null)
        throw new NotFoundException("The service metadata publisher does not exist. ID: " + sSMPID);

      if (!aSMP.getUser ().getUsername ().equals (sClientUniqueID))
        throw new UnauthorizedException ("The user does not own the publisher.");

      // iterate participant identifiers
      for (final IReadonlyParticipantIdentifier aParticipantIdentifier : aJAXBPage.getParticipantIdentifier ())
        _internalCreateParticipantIdentifier (aParticipantIdentifier, aSMP, sClientUniqueID);

      aTransaction.commit ();
    }
    catch (final RuntimeException ex) {
      s_aLogger.error ("exception", ex);
      throw new InternalErrorException (ex);
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in createParticipantIdentifiers");
      }
    }

    if (m_aCallback != null)
      m_aCallback.identifiersCreated (aJAXBPage);
  }

  private void _internalCreateParticipantIdentifier (final IReadonlyParticipantIdentifier aParticipantIdentifier,
                                                     final DBServiceMetadataPublisher aSMP,
                                                     final String sClientUniqueID) throws BadRequestException,
                                                                                  UnauthorizedException {
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

  public void deleteParticipantIdentifiers (final List <ParticipantIdentifierType> aParticipantIdentifiers,
                                            final String sClientUniqueID) throws UnauthorizedException,
                                                                         UnknownUserException,
                                                                         InternalErrorException,
                                                                         NotFoundException,
                                                                         BadRequestException {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      for (final ParticipantIdentifierType aPI : aParticipantIdentifiers)
        _internalDeleteParticipant (aPI, sClientUniqueID);

      // Delete the identifier in the DNS system, and only delete it from
      // database if this succeeds.
      if (m_aCallback != null)
        m_aCallback.identifiersDeleted (aParticipantIdentifiers);

      aTransaction.commit ();
    }
    catch (final RuntimeException ex) {
      s_aLogger.error ("exception", ex);
      throw new InternalErrorException (ex);
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in deleteParticipantIdentifiers");
      }
    }
  }

  private void _internalDeleteParticipant (final ParticipantIdentifierType aPI, final String sClientUniqueID) throws NotFoundException,
                                                                                                             UnauthorizedException {
    // Then make sure that the participant identifier exists.
    final DBParticipantIdentifier aDBIdentifier = getEntityManager ().find (DBParticipantIdentifier.class,
                                                                            new DBParticipantIdentifierID (aPI));
    if (aDBIdentifier == null)
      throw new NotFoundException ("The participant identifier " + aPI + " does not exist.");

    // Check that the user owns the identifier
    if (!aDBIdentifier.getServiceMetadataPublisher ().getUser ().getUsername ().equals (sClientUniqueID))
      throw new UnauthorizedException ("The user does not own the identifier " + aPI);

    // Remove from SMP as well
    final DBServiceMetadataPublisher aSMP = aDBIdentifier.getServiceMetadataPublisher ();
    aSMP.getRecipientParticipantIdentifiers ().remove (aDBIdentifier);
    getEntityManager ().merge (aSMP);

    // No wildcard test - as delete is allowed if user owns it...
    getEntityManager ().remove (aDBIdentifier);
  }

  public void prepareToMigrate (final MigrationRecordType aMigrationRecord, final String sClientUniqueID) throws NotFoundException,
                                                                                                         UnauthorizedException,
                                                                                                         UnknownUserException,
                                                                                                         InternalErrorException {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      // Then make sure that the participant identifier exists.
      final DBParticipantIdentifier aDBIdentifier = getEntityManager ().find (DBParticipantIdentifier.class,
                                                                              new DBParticipantIdentifierID (aMigrationRecord.getParticipantIdentifier ()));
      if (aDBIdentifier == null)
        throw new NotFoundException ("The participant identifier does not exist.");

      // Check that the user owns the identifier
      if (!aDBIdentifier.getServiceMetadataPublisher ().getUser ().getUsername ().equals (sClientUniqueID))
        throw new UnauthorizedException ("The user does not own the identifier.");

      getEntityManager ().persist (new DBMigrate (new DBMigrateID (aMigrationRecord)));

      aTransaction.commit ();
    }
    catch (final RuntimeException ex) {
      s_aLogger.error ("exception", ex);
      throw new InternalErrorException (ex);
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in prepareToMigrate");
      }
    }
  }

  public void migrate (final MigrationRecordType aMigrationRecord, final String sClientUniqueID) throws NotFoundException,
                                                                                                UnauthorizedException,
                                                                                                UnknownUserException,
                                                                                                InternalErrorException,
                                                                                                BadRequestException {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      // Change the owner of the identifier
      final DBParticipantIdentifier aDBIdentifier = getEntityManager ().find (DBParticipantIdentifier.class,
                                                                              new DBParticipantIdentifierID (aMigrationRecord.getParticipantIdentifier ()));
      if (aDBIdentifier == null)
        throw new NotFoundException ("The participant identifier was not registered in the SML.");

      // Get the old SMP
      final DBServiceMetadataPublisher aOldSMP = aDBIdentifier.getServiceMetadataPublisher ();

      // And the new SMP
      final DBServiceMetadataPublisher aNewSMP = getEntityManager ().find (DBServiceMetadataPublisher.class,
                                                                           aMigrationRecord.getServiceMetadataPublisherID ());

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

      aTransaction.commit ();
    }
    catch (final RuntimeException ex) {
      s_aLogger.error ("exception", ex);
      throw new InternalErrorException (ex);
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in migrate");
      }
    }
  }

  public ParticipantIdentifierPageType listParticipantIdentifiers (final PageRequestType aPageRequest,
                                                                   final String sClientUniqueID) throws NotFoundException,
                                                                                                UnauthorizedException,
                                                                                                UnknownUserException,
                                                                                                InternalErrorException {
    final EntityTransaction aTransaction = getEntityManager ().getTransaction ();
    aTransaction.begin ();
    try {
      // Check that the smp exists.
      final DBServiceMetadataPublisher aSMP = getEntityManager ().find (DBServiceMetadataPublisher.class,
                                                                        aPageRequest.getServiceMetadataPublisherID ());
      if (aSMP == null)
        throw new NotFoundException ("The given service metadata publisher does not exist.");

      // Check that the user owns the smp
      if (!aSMP.getUser ().getUsername ().equals (sClientUniqueID))
        throw new UnauthorizedException ("The user does not own the identifier.");

      final ParticipantIdentifierPageType aJAXBPage = m_aObjFactory.createParticipantIdentifierPageType ();
      for (final DBParticipantIdentifier aDBIdentifier : aSMP.getRecipientParticipantIdentifiers ())
        aJAXBPage.getParticipantIdentifier ().add (aDBIdentifier.getId ().asParticipantIdentifier ());

      aTransaction.commit ();
      return aJAXBPage;
    }
    catch (final RuntimeException ex) {
      s_aLogger.error ("exception", ex);
      throw new InternalErrorException (ex);
    }
    finally {
      if (aTransaction.isActive ()) {
        aTransaction.rollback ();
        s_aLogger.warn ("Rolled back transaction in listParticipantIdentifiers");
      }
    }
  }
}
