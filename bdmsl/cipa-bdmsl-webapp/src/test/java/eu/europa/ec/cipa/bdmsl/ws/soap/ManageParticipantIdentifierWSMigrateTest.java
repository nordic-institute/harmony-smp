package eu.europa.ec.cipa.bdmsl.ws.soap;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.common.bo.MigrationRecordBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.dao.IMigrationDAO;
import eu.europa.ec.cipa.bdmsl.dao.IParticipantDAO;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
import eu.europa.ec.cipa.bdmsl.service.dns.IDnsMessageSenderService;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.busdox.servicemetadata.locator._1.MigrationRecordType;
import org.busdox.transport.identifiers._1.ParticipantIdentifierType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Created by feriaad on 16/06/2015.
 */
public class ManageParticipantIdentifierWSMigrateTest extends AbstractTest {

    @Autowired
    private IManageParticipantIdentifierWS manageParticipantIdentifierWS;

    @Autowired
    private IMigrationDAO migrationDAO;

    @Autowired
    private IParticipantDAO participantDAO;

    @Autowired
    private IDnsMessageSenderService dnsMessageSenderService;

    @BeforeClass
    public static void beforeClass() throws TechnicalException {
        UnsecureAuthentication authentication = new UnsecureAuthentication();
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test(expected = NotFoundFault.class)
    public void testMigrateButPrepareToMigrateWasNotYetCalledOk() throws Exception {
        MigrationRecordType migrationRecordType = createMigrationRecord();
        migrationRecordType.getParticipantIdentifier().setValue("0009:123456789AlwaysPresent");
        migrationRecordType.setServiceMetadataPublisherID("foundUnsecure");
        manageParticipantIdentifierWS.migrate(migrationRecordType);
    }

    @Test
    public void testMigrateOk() throws Exception {
        String smpId = "smpForMigrateTest";
        String participantId = "0009:223456789MigrateTest1";

        // verify initial data
        ParticipantBO participantBO = new ParticipantBO();
        participantBO.setParticipantId(participantId);
        participantBO.setScheme("iso6523-actorid-upis");
        participantBO.setSmpId("foundUnsecure");
        Assert.assertNotNull(participantDAO.findParticipant(participantBO));

        MigrationRecordBO migrationRecordBO = new MigrationRecordBO();
        migrationRecordBO.setOldSmpId(smpId);
        migrationRecordBO.setParticipantId(participantId);
        migrationRecordBO.setScheme("iso6523-actorid-upis");
        MigrationRecordBO found = migrationDAO.findMigrationRecord(migrationRecordBO);
        Assert.assertFalse(found.isMigrated());

        // Perform the migration
        MigrationRecordType migrationRecordType = createMigrationRecord();
        migrationRecordType.getParticipantIdentifier().setValue(participantId);
        migrationRecordType.setServiceMetadataPublisherID(smpId);
        migrationRecordType.setMigrationKey("123456789");
        manageParticipantIdentifierWS.migrate(migrationRecordType);

        // check that the migration is done
        found = migrationDAO.findMigrationRecord(migrationRecordBO);
        Assert.assertTrue(found.isMigrated());

        // check that the participant has been updated
        participantBO.setSmpId(smpId);
        Assert.assertNotNull(participantDAO.findParticipant(participantBO));
    }

    @Test(expected = BadRequestFault.class)
    public void testMigrateEmpty() throws Exception {
        MigrationRecordType migrationRecordType = new MigrationRecordType();
        manageParticipantIdentifierWS.migrate(migrationRecordType);
    }

    @Test(expected = BadRequestFault.class)
    public void testMigrateNull() throws Exception {
        manageParticipantIdentifierWS.migrate(null);
    }

    @Test(expected = NotFoundFault.class)
    public void testMigrateSMPNotExist() throws Exception {
        MigrationRecordType migrationRecordType = createMigrationRecord();
        migrationRecordType.getParticipantIdentifier().setValue("0007:123456789");
        migrationRecordType.setServiceMetadataPublisherID("NotFound");
        manageParticipantIdentifierWS.migrate(migrationRecordType);
    }

    @Test(expected = NotFoundFault.class)
    public void testMigrateParticipantNotExistAnymore() throws Exception {
        MigrationRecordType migrationRecordType = createMigrationRecord();
        migrationRecordType.getParticipantIdentifier().setValue("0007:notAlreadyExist");
        migrationRecordType.setServiceMetadataPublisherID("foundUnsecure");
        manageParticipantIdentifierWS.migrate(migrationRecordType);
    }

    @Test(expected = BadRequestFault.class)
    public void testMigrateMigrationCodeTooLong() throws Exception {
        MigrationRecordType migrationRecordType = createMigrationRecord();
        migrationRecordType.getParticipantIdentifier().setValue("0007:123456789");
        migrationRecordType.setServiceMetadataPublisherID("foundUnsecure");
        migrationRecordType.setMigrationKey("TooLongTooLongTooLongTooLongTooLongTooLongTooLongTooLongTooLongTooLong");
        manageParticipantIdentifierWS.migrate(migrationRecordType);
    }

    private MigrationRecordType createMigrationRecord() {
        MigrationRecordType migrationRecordType = new MigrationRecordType();
        migrationRecordType.setMigrationKey("123456789123456879");
        ParticipantIdentifierType participantIdentifierType = new ParticipantIdentifierType();
        participantIdentifierType.setValue("0007:123456789");
        participantIdentifierType.setScheme("iso6523-actorid-upis");
        migrationRecordType.setParticipantIdentifier(participantIdentifierType);
        migrationRecordType.setServiceMetadataPublisherID("found");
        return migrationRecordType;
    }
}

