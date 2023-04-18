package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.*;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.user.DBGroupMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBResourceMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.GroupRO;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ResourceRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    private final GroupMemberDao groupMemberDao;
    private final DomainDao domainDao;
    private final UserDao userDao;
    private final ConversionService conversionService;

    public UIGroupPublicService(GroupDao groupDao, DomainDao domainDao, GroupMemberDao groupMemberDao, UserDao userDao, ConversionService conversionService) {
        this.groupDao = groupDao;
        this.domainDao = domainDao;
        this.conversionService = conversionService;
        this.groupMemberDao = groupMemberDao;
        this.userDao = userDao;
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
    public List<GroupRO> getAllGroupsForDomainAndUser(Long userId, MembershipRoleType role) {
        List<DBGroup> domainGroups = groupDao.getGroupsByUserIdAndRoles(userId, role);

        return domainGroups.stream().map(domain -> conversionService.convert(domain, GroupRO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<GroupRO> getAllGroupsForDomainAndUserAndGroupRole(Long domainId, Long userId, MembershipRoleType role) {
        List<DBGroup> domainGroups = groupDao.getGroupsByDomainUserIdAndGroupRoles(domainId, userId, role);

        return domainGroups.stream().map(domain -> conversionService.convert(domain, GroupRO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<GroupRO> getAllGroupsForDomainAndUserAndResourceRole(Long domainId, Long userId, MembershipRoleType role) {
        List<DBGroup> domainGroups = groupDao.getGroupsByDomainUserIdAndResourceRoles(domainId, userId, role);
        return domainGroups.stream().map(domain -> conversionService.convert(domain, GroupRO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupRO createGroupForDomain(GroupRO groupRO, Long domainId, Long userId) {
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
        groupDao.persistFlushDetach(group);

        // create first member as admin user
        DBUser user = userDao.find(userId);
        DBGroupMember dbMember = new DBGroupMember();
        dbMember.setRole(MembershipRoleType.ADMIN);
        dbMember.setGroup(group);
        dbMember.setUser(user);
        groupMemberDao.persist(dbMember);
        return conversionService.convert(group, GroupRO.class);
    }

    @Transactional
    public GroupRO deleteGroupFromDomain(Long domainId, Long groupId) {
        LOG.info("delete group [{}] from domain [{}]", groupId, domainId);

        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DeleteGroup", "Can not find group to delete");
        }

        if (!Objects.equals(group.getDomain().getId(), domainId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DeleteGroup", "Group does not belong to domain");
        }
        Long userCount = groupMemberDao.getGroupMemberCount(groupId, null);
        if (userCount > 0) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DeleteGroup", "Group has members [" + userCount + "] and can not be deleted");
        }

        groupDao.remove(group);
        return conversionService.convert(group, GroupRO.class);
    }

    @Transactional
    public GroupRO saveGroupForDomain(Long domainId, Long groupId, GroupRO groupRO) {
        LOG.info("save group [{}] to domain [{}]", groupRO, domainId);

        if (StringUtils.isBlank(groupRO.getGroupName())) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "UpdateGroup", "Group name must not be blank!");
        }

        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "UpdateGroup", "Group with does not exists!");
        }

        group.setGroupName(groupRO.getGroupName());
        group.setGroupDescription(groupRO.getGroupDescription());
        group.setVisibility(groupRO.getVisibility());
        // to get ID for conversion
        groupDao.persistFlushDetach(group);

        return conversionService.convert(group, GroupRO.class);
    }

    @Transactional
    public ServiceResult<MemberRO> getGroupMembers(Long groupId, int page, int pageSize,
                                                   String filter) {
        Long count = groupMemberDao.getGroupMemberCount(groupId, filter);
        ServiceResult<MemberRO> result = new ServiceResult<>();
        result.setPage(page);
        result.setPageSize(pageSize);
        if (count < 1) {
            result.setCount(0L);
            return result;
        }
        result.setCount(count);
        List<DBGroupMember> memberROS = groupMemberDao.getGroupMembers(groupId, page, pageSize, filter);
        List<MemberRO> memberList = memberROS.stream().map(member -> conversionService.convert(member, MemberRO.class)).collect(Collectors.toList());

        result.getServiceEntities().addAll(memberList);
        return result;
    }

    @Transactional
    public MemberRO addMemberToGroup(Long groupId, MemberRO memberRO, Long memberId) {
        LOG.info("Add member [{}] to group [{}]", memberRO.getUsername(), groupId);
        DBUser user = userDao.findUserByUsername(memberRO.getUsername())
                .orElseThrow(() -> new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Add/edit membership", "User [" + memberRO.getUsername() + "] does not exists!"));

        DBGroupMember member;
        if (memberId != null) {
            member = groupMemberDao.find(memberId);
            member.setRole(memberRO.getRoleType());
        } else {
            DBGroup group = groupDao.find(groupId);
            if (groupMemberDao.isUserGroupMember(user, Collections.singletonList(group))) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Add membership", "User [" + memberRO.getUsername() + "] is already a member!");
            }
            member = groupMemberDao.addMemberToGroup(group, user, memberRO.getRoleType());
        }
        return conversionService.convert(member, MemberRO.class);
    }

    @Transactional
    public MemberRO deleteMemberFromGroup(Long groupId, Long memberId) {
        LOG.info("Delete member [{}] from group [{}]", memberId, groupId);
        DBGroupMember groupMember = groupMemberDao.find(memberId);
        if (groupMember == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Membership", "Membership does not exists!");
        }
        if (!Objects.equals(groupMember.getGroup().getId(), groupId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Membership", "Membership does not belong to group!");
        }

        groupMemberDao.remove(groupMember);
        return conversionService.convert(groupMember, MemberRO.class);
    }
}
