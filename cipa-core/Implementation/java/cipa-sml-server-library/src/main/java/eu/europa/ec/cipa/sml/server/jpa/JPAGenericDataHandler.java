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

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.busdox.servicemetadata.locator._1.ParticipantIdentifierPageType;
import org.busdox.servicemetadata.locator._1.PublisherEndpointType;
import org.busdox.servicemetadata.locator._1.ServiceMetadataPublisherServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.UsedViaReflection;

import eu.europa.ec.cipa.busdox.identifier.IReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.commons.jpa.AbstractJPAEnabledManager;
import eu.europa.ec.cipa.sml.server.IGenericDataHandler;
import eu.europa.ec.cipa.sml.server.datamodel.DBParticipantIdentifier;
import eu.europa.ec.cipa.sml.server.datamodel.DBParticipantIdentifierID;
import eu.europa.ec.cipa.sml.server.datamodel.DBServiceMetadataPublisher;
import eu.europa.ec.cipa.sml.server.datamodel.DBUser;
import eu.europa.ec.cipa.sml.server.exceptions.InternalErrorException;
import eu.europa.ec.cipa.sml.server.exceptions.NotFoundException;
import eu.europa.ec.cipa.sml.server.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.sml.server.exceptions.UnknownUserException;

/**
 * A JPA implementation of the {@link IGenericDataHandler} interface.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@UsedViaReflection
public final class JPAGenericDataHandler extends AbstractJPAEnabledManager
		implements IGenericDataHandler {
	private static final Logger s_aLogger = LoggerFactory
			.getLogger(JPAGenericDataHandler.class);

	private final ObjectFactory m_aObjFactory = new ObjectFactory();

	public JPAGenericDataHandler() {
		super(SMLJPAWrapper.getInstance());
	}

	/*
	 * ==== Helper methods for getting information without having a username.
	 * These methods must not be called directly by the service interface.
	 */

	public ServiceMetadataPublisherServiceType getSMPDataOfParticipant(
			final IReadonlyParticipantIdentifier aRecipientIdentifier)
			throws NotFoundException, InternalErrorException {
		final EntityTransaction aTransaction = getEntityManager()
				.getTransaction();
		aTransaction.begin();
		try {
			// Find identifier in DB
			final DBParticipantIdentifier aDBIdentifier = getEntityManager()
					.find(DBParticipantIdentifier.class,
							new DBParticipantIdentifierID(aRecipientIdentifier));
			if (aDBIdentifier == null)
				throw new NotFoundException(
						"The given identifier was not found.");

			final DBServiceMetadataPublisher aPublisher = aDBIdentifier
					.getServiceMetadataPublisher();

			// Build result object
			final ServiceMetadataPublisherServiceType aJAXBSMPData = m_aObjFactory
					.createServiceMetadataPublisherServiceType();
			aJAXBSMPData.setServiceMetadataPublisherID(aPublisher.getSmpId());
			final PublisherEndpointType aJAXBEndpoint = m_aObjFactory
					.createPublisherEndpointType();
			aJAXBEndpoint.setLogicalAddress(aPublisher.getLogicalAddress());
			aJAXBEndpoint.setPhysicalAddress(aPublisher.getPhysicalAddress());
			aJAXBSMPData.setPublisherEndpoint(aJAXBEndpoint);

			aTransaction.commit();
			return aJAXBSMPData;
		} catch (final RuntimeException ex) {
			s_aLogger.error(
					"error readServiceMetadata " + aRecipientIdentifier, ex);
			throw new InternalErrorException(ex);
		} finally {
			if (aTransaction.isActive()) {
				aTransaction.rollback();
				s_aLogger.warn("Rolled back transaction!");
			}
		}
	}

	public List<String> getAllSMPIDs() throws InternalErrorException {
		final EntityTransaction aTransaction = getEntityManager()
				.getTransaction();
		aTransaction.begin();
		try {
			final List<String> ret = getEntityManager().createQuery(
					"SELECT p.smpId FROM DBServiceMetadataPublisher p",
					String.class).getResultList();
			aTransaction.commit();
			return ret;
		} catch (final NoResultException ex) {
			return Collections.<String> emptyList();
		} catch (final RuntimeException ex) {
			s_aLogger.error("exception", ex);
			throw new InternalErrorException(ex);
		} finally {
			if (aTransaction.isActive()) {
				aTransaction.rollback();
				s_aLogger.warn("Rolled back transaction!");
			}
		}
	}

	public PublisherEndpointType getSMPEndpointAddressOfSMPID(
			final String sSMPID) throws InternalErrorException,
			NotFoundException {
		final EntityTransaction aTransaction = getEntityManager()
				.getTransaction();
		aTransaction.begin();
		try {
			final DBServiceMetadataPublisher aPublisher = getEntityManager()
					.find(DBServiceMetadataPublisher.class, sSMPID);
			if (aPublisher == null)
				throw new NotFoundException("The smp was not found: '" + sSMPID
						+ "'");

			final PublisherEndpointType aJAXBEndpoint = m_aObjFactory
					.createPublisherEndpointType();
			aJAXBEndpoint.setLogicalAddress(aPublisher.getLogicalAddress());
			aJAXBEndpoint.setPhysicalAddress(aPublisher.getPhysicalAddress());
			aTransaction.commit();
			return aJAXBEndpoint;
		} catch (final RuntimeException ex) {
			s_aLogger.error("exception", ex);
			throw new InternalErrorException(ex);
		} finally {
			if (aTransaction.isActive()) {
				aTransaction.rollback();
				s_aLogger.warn("Rolled back transaction!");
			}
		}
	}

	public ParticipantIdentifierPageType listParticipantIdentifiers(
			final String sPageID, final String sSMPID)
			throws NotFoundException, UnauthorizedException,
			UnknownUserException, InternalErrorException {
		final EntityTransaction aTransaction = getEntityManager()
				.getTransaction();
		aTransaction.begin();
		try {
			// Check that the smp exists.
			final DBServiceMetadataPublisher aPublisher = getEntityManager()
					.find(DBServiceMetadataPublisher.class, sSMPID);
			if (aPublisher == null)
				throw new NotFoundException(
						"The given service metadata publisher does not exist.");

			final ParticipantIdentifierPageType aJAXBPage = m_aObjFactory
					.createParticipantIdentifierPageType();
			for (final DBParticipantIdentifier aDBIdentifier : aPublisher
					.getRecipientParticipantIdentifiers())
				aJAXBPage.getParticipantIdentifier().add(
						aDBIdentifier.getId().asParticipantIdentifier());
			aJAXBPage.setServiceMetadataPublisherID(sSMPID);

			aTransaction.commit();
			return aJAXBPage;
		} catch (final RuntimeException ex) {
			s_aLogger.error("exception", ex);
			throw new InternalErrorException(ex);
		} finally {
			if (aTransaction.isActive()) {
				aTransaction.rollback();
				s_aLogger.warn("Rolled back transaction!");
			}
		}
	}

	public void verifyExistingUser(final String sClientUniqueID)
			throws UnknownUserException, InternalErrorException {
		DBUser aDBUser = null;
		try {
			aDBUser = getEntityManager().find(DBUser.class, sClientUniqueID);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
		if (aDBUser == null)
			throw new UnknownUserException();
		}
	}
