package eu.europa.ec.cipa.bdmsl.ws.soap;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.common.bo.MigrationRecordBO;
import eu.europa.ec.cipa.bdmsl.dao.IMigrationDAO;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
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
public class ManageParticipantIdentifierWSPrepareToMigrateTest extends AbstractTest {
    @Autowired
    private IManageParticipantIdentifierWS manageParticipantIdentifierWS;

    @Autowired
    private IMigrationDAO migrationDAO;

    @BeforeClass
    public static void beforeClass() throws TechnicalException {
        UnsecureAuthentication authentication = new UnsecureAuthentication();
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test(expected = BadRequestFault.class)
    public void testPrepareToMigrateEmpty() throws Exception {
        MigrationRecordType migrationRecordType = new MigrationRecordType();
        manageParticipantIdentifierWS.prepareToMigrate(migrationRecordType);
    }

    @Test(expected = BadRequestFault.class)
    public void testPrepareToMigrateNull() throws Exception {
        manageParticipantIdentifierWS.prepareToMigrate(null);
    }

    @Test(expected = UnauthorizedFault.class)
    public void testPrepareToMigrateParticipantWithSMPNotCreatedByUser() throws Exception {
        MigrationRecordType migrationRecordType = createMigrationRecord();
        migrationRecordType.getParticipantIdentifier().setValue("0007:123456789");
        migrationRecordType.setServiceMetadataPublisherID("found");
        manageParticipantIdentifierWS.prepareToMigrate(migrationRecordType);
    }

    @Test(expected = NotFoundFault.class)
    public void testPrepareToMigrateSMPNotExist() throws Exception {
        MigrationRecordType migrationRecordType = createMigrationRecord();
        migrationRecordType.getParticipantIdentifier().setValue("0007:123456789");
        migrationRecordType.setServiceMetadataPublisherID("NotFound");
        manageParticipantIdentifierWS.prepareToMigrate(migrationRecordType);
    }

    @Test(expected = NotFoundFault.class)
    public void testPrepareToMigrateParticipantNotExist() throws Exception {
        MigrationRecordType migrationRecordType = createMigrationRecord();
        migrationRecordType.getParticipantIdentifier().setValue("0007:notAlreadyExist");
        migrationRecordType.setServiceMetadataPublisherID("foundUnsecure");
        manageParticipantIdentifierWS.prepareToMigrate(migrationRecordType);
    }

    @Test
    public void testPrepareToMigrateParticipantCreateOk() throws Exception {
        MigrationRecordType migrationRecordType = createMigrationRecord();
        migrationRecordType.setServiceMetadataPublisherID("foundUnsecure");
        migrationRecordType.getParticipantIdentifier().setValue("0009:123456789");
        manageParticipantIdentifierWS.prepareToMigrate(migrationRecordType);

        // Finally we verify that the migrationRecord exists
        MigrationRecordBO migrationRecordBO = new MigrationRecordBO();
        migrationRecordBO.setOldSmpId(migrationRecordType.getServiceMetadataPublisherID());
        migrationRecordBO.setParticipantId(migrationRecordType.getParticipantIdentifier().getValue());
        migrationRecordBO.setScheme(migrationRecordType.getParticipantIdentifier().getScheme());
        MigrationRecordBO found = migrationDAO.findMigrationRecord(migrationRecordBO);
        Assert.assertNotNull(found);
    }

    @Test(expected = BadRequestFault.class)
    public void testPrepareToMigrateMigrationCodeTooLong() throws Exception {
        MigrationRecordType migrationRecordType = createMigrationRecord();
        migrationRecordType.getParticipantIdentifier().setValue("0007:123456789");
        migrationRecordType.setServiceMetadataPublisherID("foundUnsecure");
        migrationRecordType.setMigrationKey("TooLongTooLongTooLongTooLongTooLongTooLongTooLongTooLongTooLongTooLong");
        manageParticipantIdentifierWS.prepareToMigrate(migrationRecordType);
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

    @Test
    public void testPrepareToMigrateParticipantUpdateOk() throws Exception {
        // first we ensure that the migration record exists
        MigrationRecordBO migrationRecordBO = new MigrationRecordBO();
        String smpId = "foundUnsecure";
        migrationRecordBO.setOldSmpId(smpId);
        String participantId = "0009:223456789MigrateTest";
        migrationRecordBO.setParticipantId(participantId);
        migrationRecordBO.setScheme("iso6523-actorid-upis");
        MigrationRecordBO found = migrationDAO.findMigrationRecord(migrationRecordBO);
        Assert.assertEquals("123456789", found.getMigrationCode());

        // update the migration key
        MigrationRecordType migrationRecordType = createMigrationRecord();
        migrationRecordType.getParticipantIdentifier().setValue(participantId);
        migrationRecordType.setServiceMetadataPublisherID(smpId);
        migrationRecordType.setMigrationKey("987654321");
        manageParticipantIdentifierWS.prepareToMigrate(migrationRecordType);

        // check that the migration key is updated
        found = migrationDAO.findMigrationRecord(migrationRecordBO);
        Assert.assertEquals("987654321", found.getMigrationCode());
    }
}




