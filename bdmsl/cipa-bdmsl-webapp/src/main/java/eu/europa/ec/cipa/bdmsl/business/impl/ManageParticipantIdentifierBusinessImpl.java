package eu.europa.ec.cipa.bdmsl.business.impl;

import com.google.common.base.Strings;
import eu.europa.ec.cipa.bdmsl.business.IManageParticipantIdentifierBusiness;
import eu.europa.ec.cipa.bdmsl.business.IManageServiceMetadataBusiness;
import eu.europa.ec.cipa.bdmsl.common.bo.*;
import eu.europa.ec.cipa.bdmsl.common.exception.BadRequestException;
import eu.europa.ec.cipa.bdmsl.common.exception.MigrationPlannedException;
import eu.europa.ec.cipa.bdmsl.dao.IMigrationDAO;
import eu.europa.ec.cipa.bdmsl.dao.IParticipantDAO;
import eu.europa.ec.cipa.bdmsl.util.EPeppolPredefinedIdentifierIssuingAgency;
import eu.europa.ec.cipa.common.business.AbstractBusinessImpl;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by feriaad on 15/06/2015.
 */
@Component
public class ManageParticipantIdentifierBusinessImpl extends AbstractBusinessImpl implements IManageParticipantIdentifierBusiness {

    /**
     * The maximum number of participants when calling the CreateList or DeleteList operations
     */
    private final static int MAX_PARTICIPANT_LIST = 100;

    /**
     * The maximum length of an identifier scheme. This applies to all identifier
     * schemes (participant, document type and process).
     */
    private static final int MAX_IDENTIFIER_SCHEME_LENGTH = 25;

    /**
     * The regular expression to be used for validating participant identifier
     * schemes (not values!).
     */
    private static final String PARTICIPANT_IDENTIFIER_SCHEME_REGEX = "[a-z0-9\\-]+";

    /**
     * The default identifier scheme ID to be used for participants/businesses.<br>
     * The matching values have the format "agency:id" whereas agency should be
     * within the code-list.<br>
     * Please note that this is a change to the PEPPOL Common definitions chapter
     * 3.4!
     */
    private static final String DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME = "iso6523-actorid-upis";

    /**
     * The default number of participants per page when calling the 'list' service
     */
    private static final int DEFAULT_PARTICIPANT_PER_PAGE = 50;

    @Value("${paginationListRequest}")
    private String paginationListRequest;

    @Autowired
    private IMigrationDAO migrationDAO;

    @Autowired
    private IParticipantDAO participantDAO;

    @Autowired
    private IManageServiceMetadataBusiness manageServiceMetadataBusiness;

    @Override
    public void validateParticipant(ParticipantBO participantBO) throws BusinessException, TechnicalException {

        manageServiceMetadataBusiness.validateSMPId(participantBO.getSmpId());

        final String scheme = participantBO.getScheme();
        if (Strings.isNullOrEmpty(scheme)) {
            throw new BadRequestException("Participant Identifier Scheme cannot be 'null' or empty");
        }

        if (scheme.length() > MAX_IDENTIFIER_SCHEME_LENGTH) {
            throw new BadRequestException("A scheme identifier MUST NOT exceed 25 characters");
        }

        if (!scheme.matches(PARTICIPANT_IDENTIFIER_SCHEME_REGEX)) {
            throw new BadRequestException("The Scheme Identifier MUST take the form <domain>-<identifierArea>-<identifier type> such as for example 'busdox-actorid-upis'. It may only contain the following characters: [a-z], [A-Z], [0-9], [-]");
        }

        if (Strings.isNullOrEmpty(participantBO.getParticipantId())) {
            throw new BadRequestException("Participant Identifier Value cannot be 'null' or empty");
        }

        if (participantBO.getParticipantId().trim().length() !=  participantBO.getParticipantId().length()) {
            throw new BadRequestException("Space characters are forbidden before or after the participant identifier");
        }

        // PEPPOL specific validation
        // If it is the default identifier scheme, check if the agency is valid.
        if (scheme.equals(DEFAULT_PARTICIPANT_IDENTIFIER_SCHEME) && !"*".equals(participantBO.getParticipantId())) {
            // Check if the value matches the format "agency:id" and whether the
            // agency code is known

            final String[] parts = participantBO.getParticipantId().split(":");
            if (parts.length != 2) {
                throw new BadRequestException("Participant Identifier Value is not valid for the default scheme");
            }

            final String issuingAgency = parts[0];
            boolean agencyFound = false;
            for (final EPeppolPredefinedIdentifierIssuingAgency agency : EPeppolPredefinedIdentifierIssuingAgency.values()) {
                agencyFound |= agency.getISO6523Code().equalsIgnoreCase(issuingAgency);
            }
            if (!agencyFound) {
                throw new BadRequestException("Participant Identifier Value contains the illegal issuing agency '" + issuingAgency + "'");
            }
        }
        loggingService.debug("Participant " + participantBO + " is valid");
    }

    @Override
    public void createParticipant(ParticipantBO participantBO) throws BusinessException, TechnicalException {
        participantDAO.createParticipant(participantBO);
    }

    @Override
    public ParticipantBO findParticipant(ParticipantBO participantBO) throws BusinessException, TechnicalException {
        return participantDAO.findParticipant(participantBO);
    }

    @Override
    public void deleteParticipant(ParticipantBO participantBO) throws BusinessException, TechnicalException {
        participantDAO.deleteParticipant(participantBO);
    }

    @Override
    public void validatePageRequest(PageRequestBO pageRequestBO) throws BusinessException, TechnicalException {
        manageServiceMetadataBusiness.validateSMPId(pageRequestBO.getSmpId());
        if (!Strings.isNullOrEmpty(pageRequestBO.getPage())) {
            try {
                int value = Integer.valueOf(pageRequestBO.getPage());
                if (value < 1) {
                    throw new BadRequestException("The NextPageIdentifier must be a positive integer");
                }
            } catch (final NumberFormatException exc) {
                throw new BadRequestException("The NextPageIdentifier must be a positive integer", exc);
            }
        }
        loggingService.debug("Page request " + pageRequestBO + " is valid");
    }

    @Override
    public ParticipantListBO list(PageRequestBO pageRequestBO) throws BusinessException, TechnicalException {
        int participantPerPageCount = DEFAULT_PARTICIPANT_PER_PAGE;
        if (!Strings.isNullOrEmpty(paginationListRequest)) {
            participantPerPageCount = Integer.valueOf(paginationListRequest);
        }
        ParticipantListBO participantListBO = null;
        final List<ParticipantBO> participantResultList = participantDAO.listParticipant(pageRequestBO, participantPerPageCount);
        if (participantResultList != null && !participantResultList.isEmpty()) {
            participantListBO = new ParticipantListBO();
            participantListBO.setParticipantBOList(participantResultList);

            // Is it the last page?
            int currentPage;
            if (!Strings.isNullOrEmpty(pageRequestBO.getPage())) {
                currentPage = Integer.valueOf(pageRequestBO.getPage());
            } else {
                currentPage = 1;
            }
            pageRequestBO.setPage(String.valueOf(currentPage + 1));
            List<ParticipantBO> nextParticipantResultList = participantDAO.listParticipant(pageRequestBO, participantPerPageCount + 1);
            if (nextParticipantResultList != null && !nextParticipantResultList.isEmpty()) {
                participantListBO.setNextPage(String.valueOf(currentPage + 1));
            } else {
                participantListBO.setNextPage(null);
            }
        }
        return participantListBO;
    }

    @Override
    public void validateMigrationRecord(MigrationRecordBO prepareToMigrateBO) throws BusinessException, TechnicalException {
        ParticipantBO participantBO = new ParticipantBO();
        participantBO.setSmpId(prepareToMigrateBO.getOldSmpId());
        participantBO.setScheme(prepareToMigrateBO.getScheme());
        participantBO.setParticipantId(prepareToMigrateBO.getParticipantId());

        if (!Strings.isNullOrEmpty(prepareToMigrateBO.getNewSmpId())) {
            manageServiceMetadataBusiness.validateSMPId(prepareToMigrateBO.getNewSmpId());
            participantBO.setSmpId(prepareToMigrateBO.getNewSmpId());
        }

        this.validateParticipant(participantBO);

        if (Strings.isNullOrEmpty(prepareToMigrateBO.getMigrationCode())) {
            throw new BadRequestException("The migration code can not be null or empty");
        }

        if (prepareToMigrateBO.getMigrationCode().length() > 24) {
            throw new BadRequestException("The maximum length of the migration code is 24 characters");
        }
        loggingService.debug("Migration record " + prepareToMigrateBO + " is valid");
    }

    @Override
    public void prepareToMigrate(MigrationRecordBO prepareToMigrateBO) throws BusinessException, TechnicalException {
        // find if a migration record already exists
        MigrationRecordBO found = migrationDAO.findMigrationRecord(prepareToMigrateBO);

        // a migration record already exists, then update it
        if (found != null) {
            migrationDAO.updateMigrationRecord(prepareToMigrateBO);
        } else {
            // create a new migration record
            migrationDAO.createMigrationRecord(prepareToMigrateBO);
        }
    }

    @Override
    public void performMigration(MigrationRecordBO migrateBO) throws BusinessException, TechnicalException {
        ParticipantBO participantBO = new ParticipantBO();
        participantBO.setSmpId(migrateBO.getNewSmpId());
        participantBO.setScheme(migrateBO.getScheme());
        participantBO.setParticipantId(migrateBO.getParticipantId());
        participantDAO.updateParticipant(participantBO, migrateBO.getOldSmpId());
        migrateBO.setMigrated(true);
        migrationDAO.updateMigrationRecord(migrateBO);
    }

    @Override
    public MigrationRecordBO findMigrationRecord(MigrationRecordBO migrateBO) throws BusinessException, TechnicalException {
        return migrationDAO.findMigrationRecord(migrateBO);
    }

    @Override
    public ParticipantListBO list() throws BusinessException, TechnicalException {
        ParticipantListBO participantListBO = null;
        final List<ParticipantBO> participantResultList = participantDAO.listParticipants();
        if (participantResultList != null && !participantResultList.isEmpty()) {
            participantListBO = new ParticipantListBO();
            participantListBO.setParticipantBOList(participantResultList);
        }
        return participantListBO;
    }

    @Override
    public List<ParticipantBO> findParticipantsForSMP(ServiceMetadataPublisherBO smpBO) throws BusinessException, TechnicalException {
        return participantDAO.findParticipantsForSMP(smpBO);
    }

    @Override
    public List<ParticipantBO> findParticipants(List<ParticipantBO> participantBOList) throws BusinessException, TechnicalException {
        Map<String, List<String>> mapParticipants = toMap(participantBOList);
        return participantDAO.findParticipants(mapParticipants);
    }

    @Override
    public void deleteParticipants(List<ParticipantBO> participantBOList) throws BusinessException, TechnicalException {
        Map<String, List<String>> mapParticipants = toMap(participantBOList);
        participantDAO.deleteParticipants(mapParticipants);
    }

    @Override
    public void validateParticipantBOList(ParticipantListBO participantListBO) throws BusinessException, TechnicalException {
        if (participantListBO.getParticipantBOList() == null || participantListBO.getParticipantBOList().isEmpty()) {
            throw new BadRequestException("The list of participants can not be empty");
        } else if (participantListBO.getNextPage() != null) {
            throw new BadRequestException("The NextPageIdentifier must be null or empty");
        } else if (new HashSet<>(participantListBO.getParticipantBOList()).size() != participantListBO.getParticipantBOList().size()) {
            // all participant identifiers must be different
            throw new BadRequestException("All the participant identifiers must be different");
        } else if (participantListBO.getParticipantBOList().size() > MAX_PARTICIPANT_LIST) {
            throw new BadRequestException("A maximum of " + MAX_PARTICIPANT_LIST + " participants is allowed");
        }
    }

    @Override
    public void checkNoMigrationPlanned(String smpId, List<ParticipantBO> participantBOList) throws BusinessException, TechnicalException {
        List<MigrationRecordBO> migrationRecordBOs = migrationDAO.findMigrationsRecordsForParticipants(smpId, participantBOList);
        if (migrationRecordBOs!= null && !migrationRecordBOs.isEmpty()) {
            String participants = "";
            for (int i = 0; i < migrationRecordBOs.size(); i++) {
                participants += migrationRecordBOs.get(i).getParticipantId();
                if (i != migrationRecordBOs.size() - 1) {
                    participants += ", ";
                }
            }
            throw new MigrationPlannedException("A migration is planned for the participants [" + participants + "] of the SMP " + smpId + ". Please contact your system administrator.");
        }
    }

    /**
     * Convert a list of participant to a Map with the key being the scheme
     *
     * @param participantBOList the list to be converted
     * @return a Map with the key being the scheme
     */
    private Map<String, List<String>> toMap(List<ParticipantBO> participantBOList) {
        // transform from List to Map
        Map<String, List<String>> mapParticipants = new HashMap<>();
        if (participantBOList != null) {
            for (final ParticipantBO participantBO : participantBOList) {
                if (mapParticipants.get(participantBO.getScheme()) == null) {
                    List<String> partListForScheme = new ArrayList<>();
                    mapParticipants.put(participantBO.getScheme(), partListForScheme);
                }
                mapParticipants.get(participantBO.getScheme()).add(participantBO.getParticipantId());
            }
        }
        return mapParticipants;
    }

}
