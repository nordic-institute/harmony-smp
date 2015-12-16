package eu.europa.ec.digit.domibus.core.mapper.ebms;

import org.springframework.stereotype.Component;

import eu.domibus.submission.Submission;
import eu.europa.ec.digit.domibus.domain.domibus.PartyBO;

@Component
public class SubmissionFromPartyMapper {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

    public Submission mapTo(PartyBO fromPartyBO, Submission submission) {

        if (fromPartyBO != null) {
            submission.addFromParty(fromPartyBO.getId(), fromPartyBO.getType());
            submission.setFromRole(fromPartyBO.getRole());
        }
        return submission;
    }

    public PartyBO mapFrom(Submission submission) {

        Submission.Party from = submission.getFromParties().iterator().next();
        PartyBO fromPartyBO = new PartyBO(from.getPartyId(), from.getPartyIdType(), submission.getFromRole());
        return fromPartyBO;
    }
}
