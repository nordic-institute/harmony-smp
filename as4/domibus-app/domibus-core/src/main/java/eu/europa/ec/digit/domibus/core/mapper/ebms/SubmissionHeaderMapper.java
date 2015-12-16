package eu.europa.ec.digit.domibus.core.mapper.ebms;

import eu.europa.ec.digit.domibus.core.mapper.AbstractMapper;
import eu.europa.ec.digit.domibus.domain.domibus.MessageHeaderBO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.domibus.submission.Submission;

@Component
public class SubmissionHeaderMapper extends AbstractMapper<MessageHeaderBO, Submission> {

    /* ---- Constants ---- */

    /* ---- Instance Variables ---- */

    @Autowired
    private SubmissionFromPartyMapper submissionFromPartyMapper = null;

    @Autowired
    private SubmissionToPartyMapper submissionToPartyMapper = null;

    @Autowired
    private SubmissionPropertyMapper submissionPropertyMapper = null;

    /* ---- Constructors ---- */

    /* ---- Business Methods ---- */

    @Override
    public Submission mapTo(MessageHeaderBO messageHeaderBO) {

        Submission submission = new Submission();

        submission.setAction(messageHeaderBO.getAction());
        submission.setAgreementRef(messageHeaderBO.getAgreementRef());
        submission.setAgreementRefType(messageHeaderBO.getAgreementRefType());
        submission.setService(messageHeaderBO.getService());
        submission.setServiceType(messageHeaderBO.getServiceType());
        submission.setRefToMessageId(messageHeaderBO.getRefToMessageId());
        submission.setConversationId(messageHeaderBO.getConversationId());

        submission = submissionFromPartyMapper.mapTo(messageHeaderBO.getFromParty(), submission);
        submission = submissionToPartyMapper.mapTo(messageHeaderBO.getToParty(), submission);
        submission = submissionPropertyMapper.mapTo(messageHeaderBO.getMessageProperties(), submission);

        return submission;
    }

    @Override
    public MessageHeaderBO mapFrom(Submission submission) {

        MessageHeaderBO messageHeaderBO = new MessageHeaderBO();

        messageHeaderBO.setAction(submission.getAction());
        messageHeaderBO.setAgreementRef(submission.getAgreementRef());
        messageHeaderBO.setAgreementRefType(submission.getAgreementRefType());
        messageHeaderBO.setConversationId(submission.getConversationId());
        messageHeaderBO.setRefToMessageId(submission.getRefToMessageId());
        messageHeaderBO.setService(submission.getService());
        messageHeaderBO.setServiceType(submission.getServiceType());
        messageHeaderBO.setFromParty(submissionFromPartyMapper.mapFrom(submission));
        messageHeaderBO.setToParty(submissionToPartyMapper.mapFrom(submission));
        messageHeaderBO.setMessageProperties(submissionPropertyMapper.mapFrom(submission));

        return messageHeaderBO;
    }

}
