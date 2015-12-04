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
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.busdox.servicemetadata.locator._1.MigrationRecordType;
import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.busdox.servicemetadata.locator._1.PageRequestType;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.callback.DoNothingExceptionHandler;
import com.helger.commons.callback.IThrowingRunnable;
import com.helger.commons.collections.CollectionHelper;
import com.helger.db.jpa.IEntityManagerProvider;
import com.helger.db.jpa.JPAEnabledManager;
import com.helger.db.jpa.JPAExecutionResult;

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
	private final ObjectFactory m_aObjFactory = new ObjectFactory();
	private IParticipantDataHandlerCallback m_aCallback;
	private static final Logger s_aLogger = LoggerFactory.getLogger(SMLDataHandlerParticipant.class);

	public SMLDataHandlerParticipant() {
		super(new IEntityManagerProvider() {
			// This additional indirection level is required!!!
			// So that for every request the correct getInstance is invoked!
			@Nonnull
			public EntityManager getEntityManager() {
				return SMLEntityManagerWrapper.getInstance().getEntityManager();
			}
		});
		// Exceptions are handled by re-throwing them
		setCustomExceptionHandler(new DoNothingExceptionHandler());
	}

	public void setCallback(@Nullable final IParticipantDataHandlerCallback aCallback) {
		m_aCallback = aCallback;
	}

	private void _internalCreateParticipantIdentifier(@Nonnull final IReadonlyParticipantIdentifier aParticipantIdentifier, @Nonnull final DBServiceMetadataPublisher aSMP,
			@Nonnull final String sClientUniqueID) throws Exception {
		// Then make sure that the participant identifier doesn't exists.
		final DBParticipantIdentifierID aDBIdentifierID = new DBParticipantIdentifierID(aParticipantIdentifier);
		final DBParticipantIdentifier aDBIdentifier = getEntityManager().find(DBParticipantIdentifier.class, aDBIdentifierID);
		if (aDBIdentifier != null)
			throw new BadRequestException("The participant identifier '" + IdentifierUtils.getIdentifierURIEncoded(aParticipantIdentifier) + "' does already exist.");

		// Find out if this is a Wildcard scheme
		final List<DBAllowedWildcardSchemes> aWildcardSchemes = getEntityManager().createQuery("SELECT p FROM DBAllowedWildcardSchemes p WHERE p.id.scheme=:scheme", DBAllowedWildcardSchemes.class)
				.setParameter("scheme", aParticipantIdentifier.getScheme()).getResultList();
		if (!aWildcardSchemes.isEmpty()) {
			// This is Wildcard scheme - validate user!
			if (!sClientUniqueID.equals(aWildcardSchemes.get(0).getUser().getUsername())) {
				throw new UnauthorizedException("The user is not allowed to register ParticipantIdentifiers for this scheme: " + aParticipantIdentifier.getScheme());
			}
			// User is allowed - verify that this is wildcard
			if (!"*".equals(aParticipantIdentifier.getValue())) {
				throw new BadRequestException("Only ParticipantIdentifier Wildcards can be registered for this scheme: " + aParticipantIdentifier.getScheme());
			}
		} else {
			// Not a wild card scheme -> check that no wildcard is queried
			if ("*".equals(aParticipantIdentifier.getValue())) {
				// This is not wildcard scheme - wildcard not allowed
				throw new UnauthorizedException("The user is not allowed to register Wildcard for this scheme: " + aParticipantIdentifier.getScheme());
			}
		}

		// insertion problem
		// Persist participant itself
		// final DBParticipantIdentifier aDBParticipant = new
		// DBParticipantIdentifier(aDBIdentifierID, aSMP);
		// getEntityManager().persist(aDBParticipant);

		Query query = getEntityManager().createNativeQuery("INSERT INTO RECIPIENT_PART_IDENTIFIER (REC_VALUE, SCHEME, SMP_ID) VALUES ( ?, ?, ? )");
		query.setParameter(1, aDBIdentifierID.getRecipientParticipantIdentifierValue());
		query.setParameter(2, aDBIdentifierID.getRecipientParticipantIdentifierScheme());
		query.setParameter(3, aSMP.getSmpId());

		query.executeUpdate();
		getEntityManager().refresh(aSMP);

		// code has performance problem:
		// Add participant to SMP
		// aSMP.getRecipientParticipantIdentifiers ().add (aDBParticipant);
		// getEntityManager ().merge (aSMP);
	}

	public void createParticipantIdentifiers(@Nonnull final ParticipantIdentifierPageType aJAXBPage, @Nonnull final String sClientUniqueID) throws Throwable {
		JPAExecutionResult<?> ret;
		ret = doInTransaction(new IThrowingRunnable() {
			public void run() throws Exception {

				final String sSMPID = aJAXBPage.getServiceMetadataPublisherID();
				long timer = System.currentTimeMillis();
				s_aLogger.debug("Start identifier creation in DB");
				// Check that the user owns the smp
				final DBServiceMetadataPublisher aSMP = getEntityManager().find(DBServiceMetadataPublisher.class, sSMPID);
				if (aSMP == null)
					throw new NotFoundException("The service metadata publisher ID '" + sSMPID + "' does not exist.");

				if (!aSMP.getUser().getUsername().equals(sClientUniqueID))
					throw new UnauthorizedException("The current user does not own the service metadata publisher ID '" + sSMPID + "'!");
				s_aLogger.debug("IDB queries executed in" + (System.currentTimeMillis() - timer) + " ms");

				// iterate participant identifiers
				for (final IReadonlyParticipantIdentifier aParticipantIdentifier : aJAXBPage.getParticipantIdentifier())
					_internalCreateParticipantIdentifier(aParticipantIdentifier, aSMP, sClientUniqueID);
				s_aLogger.debug("PArtcicpants created in DB in :" + (System.currentTimeMillis() - timer) + " ms");
				// Create identifier in DNS
				if (m_aCallback != null)
					m_aCallback.identifiersCreated(aJAXBPage);
			}
		});
		if (ret.hasThrowable())
			throw ret.getThrowable();
	}

	// public void createParticipantIdentifiers(
	// @Nonnull final ParticipantIdentifierPageType aJAXBPage,
	// @Nonnull final String sClientUniqueID) throws Throwable {
	// EntityManager em = getEntityManager();
	// EntityTransaction tx = em.getTransaction();
	// final String sSMPID = aJAXBPage.getServiceMetadataPublisherID();
	//
	// // Check that the user owns the smp
	// final DBServiceMetadataPublisher aSMP = getEntityManager()
	// .find(DBServiceMetadataPublisher.class, sSMPID);
	// if (aSMP == null)
	// throw new NotFoundException(
	// "The service metadata publisher ID '" + sSMPID
	// + "' does not exist.");
	//
	// if (!aSMP.getUser().getUsername().equals(sClientUniqueID))
	// throw new UnauthorizedException(
	// "The current user does not own the service metadata publisher ID '"
	// + sSMPID + "'!");
	//
	// // iterate participant identifiers
	// for (final IReadonlyParticipantIdentifier aParticipantIdentifier :
	// aJAXBPage
	// .getParticipantIdentifier())
	// _internalCreateParticipantIdentifier(
	// aParticipantIdentifier, aSMP, sClientUniqueID);
	//
	// // Create identifier in DNS
	// if (m_aCallback != null)
	// m_aCallback.identifiersCreated(aJAXBPage);
	//
	// }

	private void _internalDeleteParticipant(@Nonnull final ParticipantIdentifierType aParticipantID, @Nonnull final String sClientUniqueID) throws Exception {
		// Then make sure that the participant identifier exists.
		final DBParticipantIdentifier aDBIdentifier = getEntityManager().find(DBParticipantIdentifier.class, new DBParticipantIdentifierID(aParticipantID));
		if (aDBIdentifier == null)
			throw new NotFoundException("The participant identifier " + aParticipantID + " does not exist.");

		// Check that the user owns the identifier
		if (!aDBIdentifier.getServiceMetadataPublisher().getUser().getUsername().equals(sClientUniqueID))
			throw new UnauthorizedException("The user does not own the identifier " + IdentifierUtils.getIdentifierURIEncoded(aParticipantID));

		// performance problem
		// Remove from SMP as well
		final DBServiceMetadataPublisher aSMP = aDBIdentifier.getServiceMetadataPublisher();

		// aSMP.getRecipientParticipantIdentifiers().remove(aDBIdentifier);
		// getEntityManager().merge(aSMP);

		// No wildcard test - as delete is allowed if user owns it...
		// getEntityManager().remove(aDBIdentifier);

		Query query = getEntityManager().createNativeQuery("DELETE FROM RECIPIENT_PART_IDENTIFIER WHERE REC_VALUE= ? AND SCHEME= ?");
		query.setParameter(1, aDBIdentifier.getId().getRecipientParticipantIdentifierValue());
		query.setParameter(2, aDBIdentifier.getId().getRecipientParticipantIdentifierScheme());
		query.executeUpdate();
		getEntityManager().refresh(aSMP);
	}

	public void deleteParticipantIdentifiers(@Nonnull final List<ParticipantIdentifierType> aParticipantIdentifiers, @Nonnull final String sClientUniqueID) throws Throwable {
		JPAExecutionResult<?> ret;
		ret = doInTransaction(new IThrowingRunnable() {
			public void run() throws Exception {
				for (final ParticipantIdentifierType aPI : aParticipantIdentifiers)
					_internalDeleteParticipant(aPI, sClientUniqueID);

				// Delete the identifier in the DNS system, and only delete it
				// from
				// database if this succeeds.
				if (m_aCallback != null)
					m_aCallback.identifiersDeleted(aParticipantIdentifiers);
			}
		});
		if (ret.hasThrowable())
			throw ret.getThrowable();
	}

	public void prepareToMigrate(@Nonnull final MigrationRecordType aMigrationRecord, @Nonnull final String sClientUniqueID) throws Throwable {
		JPAExecutionResult<?> ret;
		ret = doInTransaction(new IThrowingRunnable() {
			public void run() throws Exception {
				// Then make sure that the participant identifier exists.
				final ParticipantIdentifierType aParticipantID = aMigrationRecord.getParticipantIdentifier();
				final DBParticipantIdentifier aDBIdentifier = getEntityManager().find(DBParticipantIdentifier.class, new DBParticipantIdentifierID(aParticipantID));
				if (aDBIdentifier == null)
					throw new NotFoundException("The participant identifier " + aParticipantID.toString() + " does not exist.");

				// Check that the user owns the identifier
				if (!aDBIdentifier.getServiceMetadataPublisher().getUser().getUsername().equals(sClientUniqueID))
					throw new UnauthorizedException("The user does not own the identifier " + IdentifierUtils.getIdentifierURIEncoded(aParticipantID));

				// Update entry, if already present - this solution seems to be
				// a more
				// appealing fix for EDELIVERY-118
				// JLB: this doesn´t merge with other rows containing same
				// Participant but different MigrationCode, as in the DB the 3
				// columns are primary keys
				// final DBMigrate aMigrate = new DBMigrate (new DBMigrateID
				// (aMigrationRecord));
				// getEntityManager ().merge (aMigrate);

				// alternative solution: first we delete the row with similar
				// Participant (in case that exists)
				// EDELIVERY - 289 Fix the query needs to be executed with the
				// UnifiedParticipantDBValue (LoweCase US.locale)

				final Query query = getEntityManager().createNativeQuery("DELETE FROM migrate WHERE scheme = ?1 AND rec_value = ?2");
				query.setParameter(1, IdentifierUtils.getUnifiedParticipantDBValue(aMigrationRecord.getParticipantIdentifier().getScheme()));
				query.setParameter(2, IdentifierUtils.getUnifiedParticipantDBValue(aMigrationRecord.getParticipantIdentifier().getValue()));
				query.executeUpdate();
				// and now we make the actual insert
				final DBMigrate aMigrate = new DBMigrate(new DBMigrateID(aMigrationRecord));
				getEntityManager().persist(aMigrate);

			}
		});
		if (ret.hasThrowable())
			throw ret.getThrowable();
	}

	public void migrate(@Nonnull final MigrationRecordType aMigrationRecord, @Nonnull final String sClientUniqueID) throws Throwable {
		JPAExecutionResult<?> ret;
		ret = doInTransaction(new IThrowingRunnable() {
			public void run() throws Exception {
				// Change the owner of the identifier
				final ParticipantIdentifierType aParticipantID = aMigrationRecord.getParticipantIdentifier();
				final DBParticipantIdentifier aDBIdentifier = getEntityManager().find(DBParticipantIdentifier.class, new DBParticipantIdentifierID(aParticipantID));
				if (aDBIdentifier == null)
					throw new NotFoundException("The participant identifier '" + IdentifierUtils.getIdentifierURIEncoded(aParticipantID) + "' was not registered in the SML.");

				// Get the old SMP
				final DBServiceMetadataPublisher aOldSMP = aDBIdentifier.getServiceMetadataPublisher();

				// And the new SMP
				final String sNewSMPID = aMigrationRecord.getServiceMetadataPublisherID();
				final DBServiceMetadataPublisher aNewSMP = getEntityManager().find(DBServiceMetadataPublisher.class, sNewSMPID);
				if (aNewSMP == null)
					throw new NotFoundException("The new SMP with ID '" + sNewSMPID + "' was not found!");

				// Check that the user owns the smp
				if (!aNewSMP.getUser().getUsername().equals(sClientUniqueID))
					throw new UnauthorizedException("The user does not own the identifier.");

				// Check that the migration code exists and is correct
				final DBMigrate aDBMigrate = getEntityManager().find(DBMigrate.class, new DBMigrateID(aMigrationRecord));
				if (aDBMigrate == null)
					throw new NotFoundException("No migration information exists for the given participant identifier plus key.");

				// Remove participant from old SMP
				if (!aOldSMP.getRecipientParticipantIdentifiers().remove(aDBIdentifier))
					throw new InternalErrorException("Failed to remove participant from old SMP");
				getEntityManager().merge(aOldSMP);

				// Add participant to new SMP
				aNewSMP.getRecipientParticipantIdentifiers().add(aDBIdentifier);
				getEntityManager().merge(aNewSMP);

				// Update participant
				aDBIdentifier.setServiceMetadataPublisher(aNewSMP);
				getEntityManager().merge(aDBIdentifier);

				// Delete the migration record.
				getEntityManager().remove(aDBMigrate);

				// The database is only updated, if both DNS updates goes well.
				if (m_aCallback != null)
					m_aCallback.identifiersDeleted(CollectionHelper.newList(aMigrationRecord.getParticipantIdentifier()));

				final ParticipantIdentifierPageType aJAXBPage = m_aObjFactory.createParticipantIdentifierPageType();
				aJAXBPage.getParticipantIdentifier().add(aMigrationRecord.getParticipantIdentifier());
				aJAXBPage.setServiceMetadataPublisherID(aMigrationRecord.getServiceMetadataPublisherID());

				if (m_aCallback != null)
					m_aCallback.identifiersCreated(aJAXBPage);
			}
		});
		if (ret.hasThrowable())
			throw ret.getThrowable();
	}

	@Nonnull
	public ParticipantIdentifierPageType listParticipantIdentifiers(@Nonnull final PageRequestType aPageRequest, @Nonnull final String sClientUniqueID) throws Throwable {
		JPAExecutionResult<ParticipantIdentifierPageType> ret;
		ret = doSelect(new Callable<ParticipantIdentifierPageType>() {
			@Nonnull
			public ParticipantIdentifierPageType call() throws Exception {
				// Check that the smp exists.
				final String sSMPID = aPageRequest.getServiceMetadataPublisherID();
				final DBServiceMetadataPublisher aSMP = getEntityManager().find(DBServiceMetadataPublisher.class, sSMPID);
				if (aSMP == null)
					throw new NotFoundException("The given service metadata publisher '" + sSMPID + "' does not exist.");

				// Check that the user owns the smp
				if (!aSMP.getUser().getUsername().equals(sClientUniqueID))
					throw new UnauthorizedException("The user does not own the identifier.");

				// Get all participant identifiers
				final ParticipantIdentifierPageType aJAXBPage = m_aObjFactory.createParticipantIdentifierPageType();
				for (final DBParticipantIdentifier aDBIdentifier : aSMP.getRecipientParticipantIdentifiers())
					aJAXBPage.getParticipantIdentifier().add(aDBIdentifier.getId().asParticipantIdentifier());
				return aJAXBPage;
			}
		});
		return ret.getOrThrow();
	}
}