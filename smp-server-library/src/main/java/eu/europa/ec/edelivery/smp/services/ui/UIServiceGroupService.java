package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UIServiceGroupService extends UIServiceBase<DBServiceGroup, ServiceGroupRO> {

    @Autowired
    ServiceGroupDao serviceGroupDao;

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
    public ServiceResult<ServiceGroupRO> getTableList(int page, int pageSize,
                                                 String sortField,
                                                 String sortOrder, Object filter) {

        ServiceResult<ServiceGroupRO> sg = new ServiceResult<>();
        sg.setPage(page < 0 ? 0 : page);
        sg.setPageSize(pageSize);
        long iCnt = getDatabaseDao().getDataListCount(filter);
        sg.setCount(iCnt);

        if (iCnt > 0) {
            int iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            List<DBServiceGroup> lst = getDatabaseDao().getDataList(iStartIndex, pageSize, sortField, sortOrder, filter);

            List<ServiceGroupRO> lstRo = new ArrayList<>();
            for (DBServiceGroup dbServiceGroup : lst) {
                ServiceGroupRO serviceGroupRo = new ServiceGroupRO();
                serviceGroupRo.setIndex(iStartIndex++);
                serviceGroupRo.setParticipantIdentifier(dbServiceGroup.getParticipantIdentifier());
                serviceGroupRo.setParticipantScheme(dbServiceGroup.getParticipantScheme());

                dbServiceGroup.getServiceGroupDomains().forEach(sgd -> {
                    sgd.getServiceMetadata().forEach(sgmd -> {
                        ServiceMetadataRO smdro = new ServiceMetadataRO();
                        smdro.setDocumentIdentifier(sgmd.getDocumentIdentifier());
                        smdro.setDocumentIdentifierScheme(sgmd.getDocumentIdentifierScheme());
                        smdro.setDomainCode(sgd.getDomain().getDomainCode());
                        smdro.setSmlSubdomain(sgd.getDomain().getSmlSubdomain());
                        serviceGroupRo.getServiceMetadata().add(smdro);
                    });
                });
                lstRo.add(serviceGroupRo);
            }

            sg.getServiceEntities().addAll(lstRo);
        }
        return sg;
    }

}
