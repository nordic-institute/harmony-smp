/*-
 * #%L
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.monitor;


import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/monitor")
public class MonitorController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(MonitorController.class);

    private final DomainDao domainDao;

    public MonitorController(DomainDao domainDao) {
        this.domainDao = domainDao;
    }

    @GetMapping(path = "/is-alive")
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_WS_SYSTEM_ADMIN})
    public ResponseEntity isAlive() {
        boolean suc = false;

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        LOG.debug("Start isAlive function for user: [{}]", user);
        try {
            suc = testDatabase();
        } catch (RuntimeException th) {
            LOG.error("Error occurred while testing database connection: Msg: [{}]", ExceptionUtils.getRootCauseMessage(th));
        }

        return suc ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    protected boolean testDatabase() {
        List<DBDomain> lstDomain = domainDao.getAllDomains();
        if (lstDomain.isEmpty()) {
            LOG.error("Bad configuration! At least one domain must be configured!");
            return false;
        }
        return true;
    }


}
