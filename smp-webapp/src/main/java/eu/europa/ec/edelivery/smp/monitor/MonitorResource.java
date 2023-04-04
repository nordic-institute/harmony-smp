package eu.europa.ec.edelivery.smp.monitor;


import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ServiceGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
public class MonitorResource {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(MonitorResource.class);


    @Autowired
    private ServiceGroupService serviceGroupService;

    @Autowired
    private DomainDao domainDao;

    @Autowired
    private ResourceDao serviceGroupDao;

    private static final String TEST_PART_SCHEMA = "test-actorid-qns";
    private static final String TEST_PART_ID = "urn:test:is:alive";
    private static final String TEST_EXTENSION_XML = "<Extension xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"><ex:dummynode xmlns:ex=\"http://test.eu\">Sample not mandatory extension</ex:dummynode></Extension>";
    private static final String TEST_DB_SUCCESSFUL_ROLLBACK = "TEST_DB_SUCCESSFUL_ROLLBACK MESSAGE";

    @GetMapping(path = "/is-alive")
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_USER, SMPAuthority.S_AUTHORITY_TOKEN_WS_SMP_ADMIN})
    public ResponseEntity isAlive() {
        boolean suc = false;
/*
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

        try {
            suc = testDatabase();
        } catch (SMPTestIsALiveException ex) {
            suc = Objects.equals(TEST_DB_SUCCESSFUL_ROLLBACK, ex.getMessage());
        } catch (RuntimeException th) {
            LOG.error("Error occurred while testing database connection: Msg:" + ExceptionUtils.getRootCauseMessage(th), th);
        }

 */
        return suc ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    protected boolean testDatabase() {
        List<DBDomain> lstDomain = domainDao.getAllDomains();
        if (lstDomain.isEmpty()) {
            LOG.error("Bad configuration! At least one domain must be configured!");
            return false;
        }

        DBResource newSg = new DBResource();
        newSg.setIdentifierValue(TEST_PART_ID);
        newSg.setIdentifierScheme(TEST_PART_SCHEMA);
        //  newSg.setExtension(TEST_EXTENSION_XML.getBytes());
        //newSg.addDomain(lstDomain.get(0)); // add initial domain
        // persist (make sure this is not in transaction)
        serviceGroupDao.testPersist(newSg, true, TEST_DB_SUCCESSFUL_ROLLBACK);
        return true;
    }


}
