package eu.europa.ec.cipa.bdmsl.business;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.common.exception.BadRequestException;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by feriaad on 26/06/2015.
 */
public class ManageParticipantIdentifierBusinessImplTest extends AbstractTest {

    @Autowired
    private IManageParticipantIdentifierBusiness manageParticipantIdentifierBusiness;

    @Test(expected = BadRequestException.class)
    public void testValidateParticipantEmpty() throws TechnicalException, BusinessException {
        manageParticipantIdentifierBusiness.validateParticipant(new ParticipantBO());
    }

    @Test
    public void testValidateParticipantWrongScheme() throws TechnicalException, BusinessException {
        ParticipantBO participantBO = new ParticipantBO();
        participantBO.setParticipantId("participantId");
        participantBO.setSmpId("smpId");
        participantBO.setScheme("&é'"); // must be like '<domain>-<identifierArea>-<identifier type>'
        try {
            manageParticipantIdentifierBusiness.validateParticipant(participantBO);
        } catch (final BadRequestException exc) {
            // ok, expected
        }

        participantBO.setScheme("");
        try {
            manageParticipantIdentifierBusiness.validateParticipant(participantBO);
        } catch (final BadRequestException exc) {
            // ok, expected
        }

        participantBO.setScheme(null);
        try {
            manageParticipantIdentifierBusiness.validateParticipant(participantBO);
        } catch (final BadRequestException exc) {
            // ok, expected
        }
    }

    @Test(expected = BadRequestException.class)
    public void testValidateParticipantWrongParticipantIdentifierForDefaultScheme() throws TechnicalException, BusinessException {
        ParticipantBO participantBO = new ParticipantBO();
        participantBO.setParticipantId("participantId"); // must be like '0010:5798000000001'
        participantBO.setSmpId("smpId");
        participantBO.setScheme("iso6523-actorid-upis");
        manageParticipantIdentifierBusiness.validateParticipant(participantBO);
    }

    @Test
    public void testValidateParticipantOk() throws TechnicalException, BusinessException {
        ParticipantBO participantBO = new ParticipantBO();
        participantBO.setParticipantId("9952:5798000000001");
        participantBO.setSmpId("smpId");
        participantBO.setScheme("iso6523-actorid-upis");
        manageParticipantIdentifierBusiness.validateParticipant(participantBO);
    }

    @Test(expected = BadRequestException.class)
    public void testValidateParticipantDefaultSchemeWrongParticipantIdentifier() throws TechnicalException, BusinessException {
        ParticipantBO participantBO = new ParticipantBO();
        participantBO.setParticipantId("unknown:5798000000001");
        participantBO.setSmpId("smpId");
        participantBO.setScheme("iso6523-actorid-upis");
        manageParticipantIdentifierBusiness.validateParticipant(participantBO);
    }
}
