/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.model.DBUserDeleteValidation;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.ILLEGAL_STATE_CERT_ID_MULTIPLE_ENTRY;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.ILLEGAL_STATE_USERNAME_MULTIPLE_ENTRY;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Repository
public class UserDao extends BaseDao<DBUser> {

    /**
     * Persists the user to the database. Before that test if user has identifiers. Usernames are saved to database in lower caps
     * @param user
     */
    @Override
    @Transactional
    public void persistFlushDetach(DBUser user) {
        if (  StringUtils.isBlank(user.getUsername())
                && (user.getCertificate() == null || StringUtils.isBlank(user.getCertificate().getCertificateId() )) ) {
            throw new SMPRuntimeException(ErrorCode.INVALID_USER_NO_IDENTIFIERS);
        }
        // update username to lower caps
        if (!StringUtils.isBlank(user.getUsername())){
            user.setUsername(user.getUsername().toLowerCase());
        }
        // if certificate id is null/empty then do not store certificate object to database
        // because of unique constraint  and empty value in mysql is also subject to the constraint!
        if (user.getCertificate() != null &&  StringUtils.isBlank(user.getCertificate().getCertificateId() )) {
            user.setCertificate(null);
        }

        super.persistFlushDetach(user);
    }

    /**
     * Searches for a user entity by its primary key and returns it if found. Returns an empty {@code Optional} if missing.
     *
     * @param userId The primary key of the user entity to find
     * @return an optional user entity
     */
    public Optional<DBUser> findUser(Long userId) {
        DBUser dbUser = memEManager.find(DBUser.class, userId);
        return Optional.ofNullable(dbUser);
    }

    /**
     * Finds a user by identifier. User identifier is username or certificateId. First it tries to find user by username
     * and than by certificate id. If user does not exists Optional with isPresent - false is returned.
     * @param identifier
     * @return resturns Optional DBUser for identifier
     */
    public Optional<DBUser> findUserByIdentifier(String identifier) {

        Optional<DBUser>  usr = findUserByUsername(identifier);
        if (!usr.isPresent()){ // try to retrieve by identifier
            usr = findUserByCertificateId(identifier);
        }

        return usr;
    }

    /**
     * Method finds user by username.If user does not exist
     * Optional  with isPresent - false is returned.
     * @param username
     * @return returns Optional DBUser for username
     */
    public Optional<DBUser> findUserByUsername(String username) {
        // check if blank
        if (StringUtils.isBlank(username)){
            return  Optional.empty();
        }
        try {
            TypedQuery<DBUser> query = memEManager.createNamedQuery("DBUser.getUserByUsernameInsensitive", DBUser.class);
            query.setParameter("username", username.trim());
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new SMPRuntimeException(ILLEGAL_STATE_USERNAME_MULTIPLE_ENTRY, username);
        }
    }

    /**
     * Method finds user by certificateId. If user does not exist
     * Optional  with isPresent - false is returned.
     * @param certificateId
     * @return returns Optional DBUser for certificateID
     */
    public Optional<DBUser> findUserByCertificateId(String certificateId) {
        return findUserByCertificateId(certificateId, false);
    }

    /**
     * Method finds user by certificateId. If user does not exist
     * Optional  with isPresent - false is returned.
     * @param certificateId
     * @param caseInsensitive
     * @return returns Optional DBUser for certificateID
     */
    public Optional<DBUser> findUserByCertificateId(String certificateId, boolean caseInsensitive) {
        try {
            String namedQuery = caseInsensitive?"DBUser.getUserByCertificateIdCaseInsensitive":"DBUser.getUserByCertificateId";
            TypedQuery<DBUser> query = memEManager.createNamedQuery(namedQuery, DBUser.class);
            query.setParameter("certificateId", certificateId);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new SMPRuntimeException(ILLEGAL_STATE_CERT_ID_MULTIPLE_ENTRY, certificateId);
        }
    }

    /**
     * Validation report for users which owns service group
     * @param userIds
     * @return
     */
    public List<DBUserDeleteValidation> validateUsersForDelete(List<Long> userIds){
        TypedQuery<DBUserDeleteValidation> query = memEManager.createNamedQuery("DBUserDeleteValidation.validateUsersForOwnership",
                DBUserDeleteValidation.class);
        query.setParameter("idList", userIds);
        return query.getResultList();
    }
}