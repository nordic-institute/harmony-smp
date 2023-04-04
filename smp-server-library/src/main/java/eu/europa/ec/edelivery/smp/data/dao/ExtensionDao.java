/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.ext.DBExtension;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY;

/**
 * The Extension repository class
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class ExtensionDao extends BaseDao<DBExtension> {

    /**
     * Returns extension records from the database.
     *
     * @return the list of extension records from smp_extension table
     */
    public List<DBExtension> getAllExtensions() {
        TypedQuery<DBExtension> query = memEManager.createNamedQuery(QUERY_EXTENSION_ALL, DBExtension.class);
        return query.getResultList();
    }

    /**
     * Returns the extension by implementation name (spring bean name).
     * Returns the extension or Optional.empty() if there is no extension.
     *
     * @return Returns the extension or Optional.empty() if there is no extension.
     * @throws IllegalStateException if no domain is not configured
     */
    public Optional<DBExtension> getExtensionByImplementationName(String extensionName) {
        try {
            TypedQuery<DBExtension> query = memEManager.createNamedQuery(QUERY_EXTENSION_IMPLEMENTATION_NAME, DBExtension.class);
            query.setParameter(PARAM_NAME, extensionName);
            DBExtension extension =query.getSingleResult();
            extension.getResourceDefs();
            return Optional.of(extension);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY.getMessage(extensionName));
        }
    }
}
