package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupSearchRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.filters.ResourceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UIServiceGroupSearchService extends UIServiceBase<DBResource, ServiceGroupSearchRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIServiceGroupSearchService.class);

    @Autowired
    DomainDao domainDao;

    @Autowired
    ResourceDao serviceGroupDao;

    @Autowired
    UserDao userDao;


    @Override
    protected BaseDao<DBResource> getDatabaseDao() {
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
                                                            String sortOrder, ResourceFilter filter) {

        ServiceResult<ServiceGroupSearchRO> sg = new ServiceResult<>();
        sg.setPage(page < 0 ? 0 : page);
        sg.setPageSize(pageSize);
        long iCnt = serviceGroupDao.getServiceGroupCount(filter);
        sg.setCount(iCnt);

        if (iCnt > 0) {
            int iStartIndex = pageSize<0?-1:page * pageSize;
            if (iStartIndex >= iCnt && page > 0){
                page = page -1;
                sg.setPage(page); // go back for a page
                iStartIndex = pageSize<0?-1:page * pageSize;
            }


            List<DBResource> lst = serviceGroupDao.getServiceGroupList(iStartIndex, pageSize, sortField, sortOrder, filter);
            List<ServiceGroupSearchRO> lstRo = new ArrayList<>();
            for (DBResource dbServiceGroup : lst) {
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
    public ServiceGroupSearchRO convertToRo(DBResource dbServiceGroup) {
        ServiceGroupSearchRO serviceGroupRo = new ServiceGroupSearchRO();

        serviceGroupRo.setId(dbServiceGroup.getId());
        serviceGroupRo.setParticipantIdentifier(dbServiceGroup.getIdentifierValue());
        serviceGroupRo.setParticipantScheme(dbServiceGroup.getIdentifierScheme());
        /*
        dbServiceGroup.getResourceDomains().forEach(sgd -> {
            DomainRO dmn = new DomainRO();
            sgd.getSubresourcesList().forEach(sgmd -> {
                ServiceMetadataRO smdro = new ServiceMetadataRO();
                smdro.setDocumentIdentifier(sgmd.getDocumentIdentifier());
                smdro.setDocumentIdentifierScheme(sgmd.getDocumentIdentifierScheme());
                smdro.setDomainCode(sgd.getDomain().getDomainCode());
                smdro.setSmlSubdomain(sgd.getDomain().getSmlSubdomain());
                serviceGroupRo.getServiceMetadata().add(smdro);
            });
        });

         */
        return serviceGroupRo;
    }
}
