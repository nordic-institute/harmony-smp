/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.PropertyUtils;

import javax.persistence.EntityManager;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.trim;


/**
 * Reads all Database configurations and sets read timestamp.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class DatabaseProperties extends Properties {
    @Transient
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DatabaseProperties.class);

    private OffsetDateTime lastUpdate;

    public DatabaseProperties(EntityManager em) {
        super();
        TypedQuery<DBConfiguration> tq = em.createNamedQuery("DBConfiguration.getAll", DBConfiguration.class);
        List<DBConfiguration> lst = tq.getResultList();
        for (DBConfiguration dc : lst) {
            if (dc.getValue() != null) {
                String prop =trim(dc.getProperty());
                String value =trim(dc.getValue());
                setProperty(prop, value);
                LOG.info("Database property: [{}] value: [{}]", prop,PropertyUtils.getMaskedData(prop, value) );
            }
            lastUpdate = (lastUpdate == null || lastUpdate.isBefore(dc.getLastUpdatedOn())) ? dc.getLastUpdatedOn() : lastUpdate;
        }
    }
    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


}
