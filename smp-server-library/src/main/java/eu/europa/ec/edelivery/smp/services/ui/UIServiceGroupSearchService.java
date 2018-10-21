package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupSearchRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.filters.ServiceGroupFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.DOMAIN_NOT_EXISTS;

@Service
public class UIServiceGroupSearchService extends UIServiceBase<DBServiceGroup, ServiceGroupSearchRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIServiceGroupSearchService.class);

    @Autowired
    DomainDao domainDao;

    @Autowired
    ServiceGroupDao serviceGroupDao;

    @Autowired
    UserDao userDao;


    @Override
    protected BaseDao<DBServiceGroup> getDatabaseDao() {
        return serviceGroupDao;
    }

    /**
     * Method return list of service group entities with service metadata for given search parameters and page.
     *
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filter
     * @return
     */
    @Transactional
    public ServiceResult<ServiceGroupSearchRO> getTableList(int page, int pageSize,
                                                            String sortField,
                                                            String sortOrder, ServiceGroupFilter filter, String domainCode) {

        DBDomain d = null;
        if (!StringUtils.isBlank(domainCode)) {
            Optional<DBDomain> od = domainDao.getDomainByCode(domainCode);
            if (od.isPresent()) {
                d = od.get();
            } else {
                throw new SMPRuntimeException(DOMAIN_NOT_EXISTS, domainCode);
            }
        }

        ServiceResult<ServiceGroupSearchRO> sg = new ServiceResult<>();
        sg.setPage(page < 0 ? 0 : page);
        sg.setPageSize(pageSize);
        long iCnt = serviceGroupDao.getServiceGroupCount(filter, d);
        sg.setCount(iCnt);

        if (iCnt > 0) {
            int iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            List<DBServiceGroup> lst = serviceGroupDao.getServiceGroupList(iStartIndex, pageSize, sortField, sortOrder, filter, d);
            List<ServiceGroupSearchRO> lstRo = new ArrayList<>();
            for (DBServiceGroup dbServiceGroup : lst) {
                ServiceGroupSearchRO serviceGroupRo = convertToRo(dbServiceGroup);
                serviceGroupRo.setIndex(iStartIndex++);
                lstRo.add(serviceGroupRo);
            }
            sg.getServiceEntities().addAll(lstRo);
        }
        return sg;
    }

    /**
     * Convert Database object to Rest object for UI
     *
     * @param dbServiceGroup - database  entity
     * @return ServiceGroupRO
     */
    public ServiceGroupSearchRO convertToRo(DBServiceGroup dbServiceGroup) {
        ServiceGroupSearchRO serviceGroupRo = new ServiceGroupSearchRO();
        serviceGroupRo.setId(dbServiceGroup.getId());
        serviceGroupRo.setParticipantIdentifier(dbServiceGroup.getParticipantIdentifier());
        serviceGroupRo.setParticipantScheme(dbServiceGroup.getParticipantScheme());
        dbServiceGroup.getServiceGroupDomains().forEach(sgd -> {
            DomainRO dmn = new DomainRO();
            sgd.getServiceMetadata().forEach(sgmd -> {
                ServiceMetadataRO smdro = new ServiceMetadataRO();
                smdro.setDocumentIdentifier(sgmd.getDocumentIdentifier());
                smdro.setDocumentIdentifierScheme(sgmd.getDocumentIdentifierScheme());
                smdro.setDomainCode(sgd.getDomain().getDomainCode());
                smdro.setSmlSubdomain(sgd.getDomain().getSmlSubdomain());
                serviceGroupRo.getServiceMetadata().add(smdro);
            });
        });
        return serviceGroupRo;
    }
}
