package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UIServiceGroupService extends UIServiceBase<DBServiceGroup, ServiceGroupRO> {

    @Autowired
    ServiceGroupDao serviceGroupDao;

    @Override
    protected BaseDao<DBServiceGroup> getDatabaseDao() {
        return serviceGroupDao;
    }

    /**
     * Method returns Domain resource object list for page.
     *
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @return
     */
    @Transactional
    public ServiceResult<ServiceGroupRO> getTableList(int page, int pageSize,
                                                 String sortField,
                                                 String sortOrder) {

        return super.getTableList(page, pageSize, sortField, sortOrder);
    }

}
