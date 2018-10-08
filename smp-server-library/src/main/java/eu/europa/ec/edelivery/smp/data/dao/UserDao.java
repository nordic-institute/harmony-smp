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
import eu.europa.ec.edelivery.smp.data.model.DBUserAuthority;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.ILLEGAL_STATE_CERT_ID_MULTIPLE_ENTRY;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.ILLEGAL_STATE_USERNAME_MULTIPLE_ENTRY;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Repository
public class UserDao extends BaseDao<DBUser> {


    public List<GrantedAuthority> getUserRoles(String username) {

        List<GrantedAuthority> lstRes = new ArrayList<>();
        // all users are  SERVICEGROUP_ADMIN
        lstRes.add(DBUserAuthority.S_ROLE_SERVICEGROUP_ADMIN);

        List<DBUserAuthority> lst = em
                .createNamedQuery("DBUserAuthority.getRolesForUsernameNativeQuery")
                .setParameter( "username",username)
                .getResultList();

        if (!lst.isEmpty()){
            lstRes.addAll(lst);
        }
        System.out.println("Got roles: " + lstRes.size() + " " + lstRes);
        return lstRes;
    }

    /**
     * Perstis user to database. Before that test if user has identifiers
     * @param user
     */
    @Override
    @Transactional
    public void persistFlushDetach(DBUser user) {
        if (StringUtils.isBlank(user.getUsername())
                && (user.getCertificate()==null || StringUtils.isBlank(user.getCertificate().getCertificateId() )) ) {
            throw new SMPRuntimeException(ErrorCode.INVALID_USER_NO_IDENTIFIERS);
        }
        super.persistFlushDetach(user);
    }

    /**
     * Method finds user by identifier. User identifier is username or certificateId. First it tries to find user by username
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
            TypedQuery<DBUser> query = em.createNamedQuery("DBUser.getUserByUsernameInsensitive", DBUser.class);
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
        try {
            TypedQuery<DBUser> query = em.createNamedQuery("DBUser.getUserByCertificateId", DBUser.class);
            query.setParameter("certificateId", certificateId);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new SMPRuntimeException(ILLEGAL_STATE_CERT_ID_MULTIPLE_ENTRY, certificateId);
        }
    }

}