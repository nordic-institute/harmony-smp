package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.user.DBDomainMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.DomainPublicRO;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
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
import java.util.stream.Collectors;

/**
 * Service bean provides only public domain entity data for the Domain.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Service
public class UIDomainPublicService extends UIServiceBase<DBDomain, DomainPublicRO> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIDomainPublicService.class);
    private final DomainDao domainDao;

    private final DomainMemberDao domainMemberDao;
    private final UserDao userDao;
    private final ConversionService conversionService;


    public UIDomainPublicService(DomainDao domainDao, DomainMemberDao domainMemberDao,ConversionService conversionService, UserDao userDao) {
        this.domainDao = domainDao;
        this.domainMemberDao = domainMemberDao;
        this.conversionService = conversionService;
        this.userDao = userDao;
    }

    @Override
    protected BaseDao<DBDomain> getDatabaseDao() {
        return domainDao;
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
    public ServiceResult<DomainPublicRO> getTableList(int page, int pageSize,
                                                      String sortField,
                                                      String sortOrder, Object filter) {
        LOG.debug("Query for public domain data: page: [{}], page size [{}], sort: [{}], filter: [{}].", page, pageSize, sortField, filter);
        return super.getTableList(page, pageSize, sortField, sortOrder, filter);
    }


    @Transactional
    public ServiceResult<MemberRO> getDomainMembers(Long domainId, int page, int pageSize,
                                                   String filter) {
        Long count = domainMemberDao.getDomainMemberCount(domainId, filter);
        ServiceResult<MemberRO> result =  new ServiceResult<>();
        result.setPage(page);
        result.setPageSize(pageSize);
        if (count<1) {
            result.setCount(0L);
            return result;
        }
        result.setCount(count);
        List<DBDomainMember> memberROS = domainMemberDao.getDomainMembers(domainId, page, pageSize, filter);
        List<MemberRO> memberList = memberROS.stream().map(member-> conversionService.convert(member, MemberRO.class)).collect(Collectors.toList());

        result.getServiceEntities().addAll(memberList);
        return result;
    }

    @Transactional
    public MemberRO addMemberToDomain(Long domainId, MemberRO memberRO, Long memberId) {
        LOG.info("Add member [{}] to domain [{}]", memberRO.getUsername(), domainId);
        DBUser user = userDao.findUserByUsername(memberRO.getUsername())
                .orElseThrow(() -> new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Add/edit membership", "User ["+memberRO.getUsername()+"] does not exists!"));

        DBDomainMember domainMember;
        if (memberId !=null) {
            domainMember = domainMemberDao.find(memberId);
            domainMember.setRole(memberRO.getRoleType());
        } else {
            DBDomain domain = domainDao.find(domainId);
            if (domainMemberDao.isUserDomainMember(user, domain)) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Add membership", "User ["+memberRO.getUsername()+"] is already a member!");
            }
            domainMember = domainMemberDao.addMemberToDomain(domain, user,memberRO.getRoleType() );
        }
        return conversionService.convert(domainMember, MemberRO.class);
    }

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
}
