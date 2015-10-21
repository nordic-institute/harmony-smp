package eu.europa.ec.cipa.bdmsl.dao;

import eu.europa.ec.cipa.bdmsl.common.bo.PageRequestBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ParticipantBO;
import eu.europa.ec.cipa.bdmsl.common.bo.ServiceMetadataPublisherBO;
import eu.europa.ec.cipa.common.exception.TechnicalException;

import java.util.List;
import java.util.Map;

/**
 * Created by feriaad on 12/06/2015.
 */
public interface IParticipantDAO {
    void createParticipant(ParticipantBO participantBO) throws TechnicalException;

    ParticipantBO findParticipant(ParticipantBO participantBO) throws TechnicalException;

    void deleteParticipant(ParticipantBO participantBO) throws TechnicalException;

    List<ParticipantBO> listParticipant(PageRequestBO pageRequestBO, int participantPerPageCount) throws TechnicalException;

    void updateParticipant(ParticipantBO participantBO, final String oldSmpId) throws TechnicalException;

    List<ParticipantBO> listParticipants() throws TechnicalException;

    List<ParticipantBO> findParticipantsForSMP(ServiceMetadataPublisherBO smpBO) throws TechnicalException;

    List<ParticipantBO> findParticipants(Map<String, List<String>> mapParticipants) throws TechnicalException;

    void deleteParticipants(Map<String, List<String>> mapParticipants) throws TechnicalException;
}
