/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.*;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResourceFilter;
import eu.europa.ec.edelivery.smp.data.model.user.DBGroupMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.GroupRO;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

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
    private final ResourceDao resourceDao;
    private final UserDao userDao;
    private final ConversionService conversionService;

    public UIGroupPublicService(GroupDao groupDao, DomainDao domainDao, ResourceDao resourceDao, GroupMemberDao groupMemberDao, UserDao userDao, ConversionService conversionService) {
        this.groupDao = groupDao;
        this.domainDao = domainDao;
        this.resourceDao = resourceDao;
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
    @Override
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
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.group.create.already.exists",
                    Map.of("groupName", groupRO.getGroupName()));
        }
        DBDomain domain = domainDao.find(domainId);

        DBGroup group = new DBGroup();
        group.setGroupName(lowerCase(trim(groupRO.getGroupName())));
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
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.group.delete.not.exists");
        }

        if (!Objects.equals(group.getDomain().getId(), domainId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.group.delete.not.part.of.domain");
        }

        DBResourceFilter resourceFilter = DBResourceFilter.createBuilder().group(group).domain(group.getDomain()).build();
        Long resCount = resourceDao.getResourcesForFilterCount(resourceFilter);

        if (resCount > 0) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.group.delete.contains.resources",
                    Map.of("resourcesCount", resCount));
        }

        groupDao.remove(group);
        return conversionService.convert(group, GroupRO.class);
    }

    @Transactional
    public GroupRO saveGroupForDomain(Long domainId, Long groupId, GroupRO groupRO) {
        LOG.info("save group [{}] to domain [{}]", groupRO, domainId);

        if (StringUtils.isBlank(groupRO.getGroupName())) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.group.update.blank.name");
        }

        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.group.update.not.exists",
                    Map.of("groupId", groupId));
        }

        group.setGroupName(lowerCase(trim(groupRO.getGroupName())));
        group.setGroupDescription(groupRO.getGroupDescription());
        group.setVisibility(groupRO.getVisibility());
        // to get ID for conversion
        groupDao.persistFlushDetach(group);

        return conversionService.convert(group, GroupRO.class);
    }

    public DBGroup validateDomainAndGroup(Long groupId, Long domainId, String nonexistentGroupTranslationMessageCode, String groupNotPartOfDomainTranslationMessageCode) {
        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, nonexistentGroupTranslationMessageCode);
        }
        if (!Objects.equals(domainId, group.getDomain().getId())) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, groupNotPartOfDomainTranslationMessageCode);
        }
        return group;
    }

    @Transactional
    public ServiceResult<MemberRO> getGroupMembers(Long groupId, Long domainId, int page, int pageSize,
                                                   String filter) {
        validateDomainAndGroup(groupId, domainId,
                "error.invalid.request.group.membership.get.members.group.not.exists",
                "error.invalid.request.group.membership.get.members.group.not.part.of.domain");

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
    public MemberRO addMemberToGroup(Long groupId, Long domainId, MemberRO memberRO, Long memberId) {
        LOG.info("Add member [{}] to group [{}]", memberRO.getUsername(), groupId);
        validateDomainAndGroup(groupId, domainId,
                "error.invalid.request.group.membership.add.member.group.not.exists",
                "error.invalid.request.group.membership.add.member.group.not.part.of.domain");

        DBUser user = userDao.findUserByUsername(memberRO.getUsername())
                .orElseThrow(() -> new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.group.membership.add.user.not.exists",
                        Map.of("username", memberRO.getUsername())));

        DBGroupMember member;
        if (memberId != null) {
            member = groupMemberDao.find(memberId);
            member.setRole(memberRO.getRoleType());
        } else {
            DBGroup group = groupDao.find(groupId);
            if (groupMemberDao.isUserGroupMember(user, Collections.singletonList(group))) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.group.membership.add.user.already.member",
                        Map.of("username", memberRO.getUsername()));
            }
            member = groupMemberDao.addMemberToGroup(group, user, memberRO.getRoleType());
        }
        return conversionService.convert(member, MemberRO.class);
    }

    @Transactional
    public MemberRO deleteMemberFromGroup(Long groupId, Long domainId, Long memberId) {
        LOG.info("Delete member [{}] from group [{}]", memberId, groupId);

        validateDomainAndGroup(groupId, domainId,
                "error.invalid.request.group.membership.remove.member.group.not.exists",
                "error.invalid.request.group.membership.remove.member.group.not.part.of.domain");

        DBGroupMember groupMember = groupMemberDao.find(memberId);
        if (groupMember == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.group.membership.remove.user.not.member");
        }
        if (!Objects.equals(groupMember.getGroup().getId(), groupId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.group.membership.remove.user.not.part.of.group");
        }

        groupMemberDao.remove(groupMember);
        return conversionService.convert(groupMember, MemberRO.class);
    }
}
