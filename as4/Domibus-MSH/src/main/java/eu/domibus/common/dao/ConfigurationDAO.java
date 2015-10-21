/*
 * Copyright 2015 e-CODEX Project
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl5
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.domibus.common.dao;

import eu.domibus.common.configuration.model.Configuration;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * Created by kochc01 on 23.10.2014.
 */
@Repository

public class ConfigurationDAO extends BasicDao<Configuration> {

    public ConfigurationDAO() {
        super(Configuration.class);
    }


    public boolean configurationExists() {
        TypedQuery<Long> query = this.em.createNamedQuery("Configuration.count", Long.class);

        return query.getSingleResult() != 0;
    }

    public Configuration read() {
        TypedQuery<Configuration> query = this.em.createNamedQuery("Configuration.getConfiguration", Configuration.class);
        return query.getSingleResult();
    }


    @Transactional
    //FIXME: PMode update instead of wipe
    public void updateConfiguration(Configuration configuration) {
        if (this.configurationExists()) {
            this.delete(this.read());
        }
        this.create(configuration);

    }
}
