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
