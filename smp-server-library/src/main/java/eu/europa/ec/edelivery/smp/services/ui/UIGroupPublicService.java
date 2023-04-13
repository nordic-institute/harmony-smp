package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.GroupDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.ui.GroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service bean provides only public group management methods.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Service
public class UIGroupPublicService extends UIServiceBase<DBGroup, GroupRO> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIGroupPublicService.class);
    private final GroupDao groupDao;
    private final DomainDao domainDao;
    private final ConversionService conversionService;

    public UIGroupPublicService(GroupDao groupDao, DomainDao domainDao, ConversionService conversionService) {
        this.groupDao = groupDao;
        this.domainDao = domainDao;
        this.conversionService = conversionService;
    }

    @Override
    protected BaseDao<DBGroup> getDatabaseDao() {
        return groupDao;
    }

    /**
     * Method returns Domain resource object list for page.
     *
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filter
     * @return
     */
    public ServiceResult<GroupRO> getTableList(int page, int pageSize,
                                               String sortField,
                                               String sortOrder, Object filter) {
        LOG.debug("Query for public domain data: page: [{}], page size [{}], sort: [{}], filter: [{}].", page, pageSize, sortField, filter);
        return super.getTableList(page, pageSize, sortField, sortOrder, filter);
    }

    @Transactional
    public List<GroupRO> getAllGroupsForDomain(Long domainId) {
        List<DBGroup> domainGroups = groupDao.getAllGroupsForDomain(domainId);
        return domainGroups.stream().map(domain -> conversionService.convert(domain, GroupRO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupRO createGroupForDomain(Long domainId, GroupRO groupRO) {
        LOG.info("create group [{}] to domain [{}]", groupRO, domainId);

        Optional<DBGroup> optGroup = groupDao.getGroupByNameAndDomain(groupRO.getGroupName(), domainId);
        if (optGroup.isPresent()) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "CreateGroup", "Group with name [" + groupRO.getGroupName() + "] already exists!");
        }
        DBDomain domain = domainDao.find(domainId);

        DBGroup group = new DBGroup();
        group.setGroupName(groupRO.getGroupName());
        group.setGroupDescription(groupRO.getGroupDescription());
        group.setVisibility(groupRO.getVisibility());
        group.setDomain(domain);
        // to get ID for conversion
        domainDao.persistFlushDetach(domain);

        return conversionService.convert(group, GroupRO.class);
    }

    @Transactional
    public GroupRO deleteGroupFromDomain(Long domainId, Long groupId) {
        LOG.info("delete group [{}] from domain [{}]", groupId, domainId);

        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DeleteGroup", "Can not find group to delete");
        }

        if (Objects.equals(group.getDomain().getId(), domainId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DeleteGroup", "Group does not belong to domain");
        }
        groupDao.remove(group);
        return conversionService.convert(group, GroupRO.class);
    }
/*
    @Transactional
    public MemberRO deleteMemberFromDomain(Long domainId, Long memberId) {
        LOG.info("Delete member [{}] from domain [{}]", memberId, domainId);
        DBDomainMember domainMember = domainMemberDao.find(memberId);
        if (domainMember == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Membership", "Membership does not exists!");
        }
        if (!Objects.equals(domainMember.getDomain().getId(),domainId  )){
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Membership", "Membership does not belong to domain!");
        }

        domainMemberDao.remove(domainMember);
        return conversionService.convert(domainMember, MemberRO.class);
    }

 */
}
