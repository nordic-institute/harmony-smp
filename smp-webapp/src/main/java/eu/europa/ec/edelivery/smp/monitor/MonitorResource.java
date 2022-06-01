package eu.europa.ec.edelivery.smp.monitor;


import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.exceptions.SMPTestIsALiveException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ServiceGroupService;
import eu.europa.ec.edelivery.smp.validation.ServiceGroupValidator;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.cxf.helpers.IOUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@RestController
@RequestMapping(value = "/monitor")
public class MonitorResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(MonitorResource.class);

    @Autowired
    private ServiceGroupValidator serviceGroupValidator;

    @Autowired
    private ServiceGroupService serviceGroupService;

    @Autowired
    private DomainDao domainDao;

    @Autowired
    private ServiceGroupDao serviceGroupDao;

    private static final String TEST_PART_SCHEMA = "test-actorid-qns";
    private static final String TEST_PART_ID = "urn:test:is:alive";
    private static final String TEST_EXTENSION_XML = "<Extension xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"><ex:dummynode xmlns:ex=\"http://test.eu\">Sample not mandatory extension</ex:dummynode></Extension>";
    private static final String TEST_DB_SUCCESSFUL_ROLLBACK = "TEST_DB_SUCCESSFUL_ROLLBACK MESSAGE";


    @RequestMapping(method = RequestMethod.GET, path = "/is-alive")
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN,SMPAuthority.S_AUTHORITY_TOKEN_WS_SMP_ADMIN})
    public ResponseEntity isAlive() {

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        LOG.debug("Start isAlive function for user: " + SecurityContextHolder.getContext().getAuthentication().getName());
        byte[] bServiceGroup = null;
        try {
            bServiceGroup = IOUtils.readBytesFromStream(
                    MonitorResource.class.getResourceAsStream("/isAliveTestFiles/ServiceGroupTest.xml"));

        } catch (IOException e) {
            LOG.error("Error reading test resource file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        ServiceGroup serviceGroup;
        try {
            // Validations
            BdxSmpOasisValidator.validateXSD(bServiceGroup);
            serviceGroup = ServiceGroupConverter.unmarshal(bServiceGroup);
            serviceGroupValidator
                    .validate(TEST_PART_SCHEMA + "::" + TEST_PART_ID, serviceGroup);
        } catch (XmlInvalidAgainstSchemaException ex) {
            LOG.error("Error reading testing resource file", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        boolean suc = false;
        try {
            suc = testDatabase();
        } catch (SMPTestIsALiveException ex) {
            suc = Objects.equals(TEST_DB_SUCCESSFUL_ROLLBACK, ex.getMessage());
        } catch (RuntimeException th) {
            LOG.error("Error occurred while testing database connection: Msg:" + ExceptionUtils.getRootCauseMessage(th), th);
        }
        return suc ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    protected boolean testDatabase() {
        List<DBDomain> lstDomain = domainDao.getAllDomains();
        if (lstDomain.isEmpty()) {
            LOG.error("Bad configuration! At least one domain must be configured!");
            return false;
        }

        DBServiceGroup newSg = new DBServiceGroup();
        newSg.setParticipantIdentifier(TEST_PART_ID);
        newSg.setParticipantScheme(TEST_PART_SCHEMA);
        newSg.setExtension(TEST_EXTENSION_XML.getBytes());
        newSg.addDomain(lstDomain.get(0)); // add initial domain
        // persist (make sure this is not in transaction)
        serviceGroupDao.testPersist(newSg, true, TEST_DB_SUCCESSFUL_ROLLBACK);
        return true;
    }


}
