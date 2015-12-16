package eu.europa.ec.digit.domibus.core.mapper.ebms;

import org.springframework.stereotype.Component;

import eu.domibus.submission.Submission;
import eu.europa.ec.digit.domibus.domain.domibus.PartyBO;

@Component
public class SubmissionToPartyMapper {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

    public Submission mapTo(PartyBO toPartyBO, Submission submission) {
        if (toPartyBO != null) {
            submission.addToParty(toPartyBO.getId(), toPartyBO.getType());
            submission.setToRole(toPartyBO.getRole());
        }
        return submission;
    }

    public PartyBO mapFrom(Submission submission) {
        Submission.Party to = submission.getToParties().iterator().next();
        PartyBO toPartyBO = new PartyBO(to.getPartyId(), to.getPartyIdType(), submission.getToRole());
        return toPartyBO;
    }
}
