package eu.europa.ec.edelivery.smp.services;


import eu.europa.ec.edelivery.smp.data.dao.ui.UiDaoService;
import eu.europa.ec.edelivery.smp.data.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceUIData {

    @Autowired
    private UiDaoService uiDaoService;

    /**
     *
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @return
     */

    public ServiceResult<ServiceGroupRO> getServiceGroupList(int page, int pageSize,
                                                  String sortField,
                                                  String sortOrder) {

        ServiceResult<ServiceGroupRO> sg = new  ServiceResult<>();

        sg.setPage(page);
        sg.setPageSize(pageSize);
        long iCnt = uiDaoService.getDataListCount(ServiceGroupRO.class, null);
        sg.setCount(iCnt);
        if (iCnt > 0) {
            List<ServiceGroupRO> lst = uiDaoService.getDataList(ServiceGroupRO.class, page * pageSize, pageSize, sortField, sortOrder, null);
            sg.getServiceEntities().addAll(lst);
        }
        return sg;
    }

    /**
     *
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @return
     */
    public ServiceResult<UserRO> getUserList(int page, int pageSize,
                                             String sortField,
                                             String sortOrder) {

        ServiceResult<UserRO> sg = new ServiceResult<>();
        sg.setPage(page);
        sg.setPageSize(pageSize);
        long iCnt = uiDaoService.getDataListCount(UserRO.class, null);
        sg.setCount(iCnt);
        if (iCnt > 0) {

            List<UserRO> lst = uiDaoService.getDataList(UserRO.class, page * pageSize, pageSize, sortField, sortOrder, null);
            sg.getServiceEntities().addAll(lst);
        }


        return sg;
    }

    /**
     *
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @return
     */
    public ServiceResult<DomainRO> getDomainList(int page, int pageSize,
                                                 String sortField,
                                                 String sortOrder) {

        ServiceResult<DomainRO> sg = new ServiceResult<>();
        sg.setPage(page);
        sg.setPageSize(pageSize);
        long iCnt = uiDaoService.getDataListCount(DomainRO.class, null);
        sg.setCount(iCnt);
        if (iCnt > 0) {

            List<DomainRO> lst = uiDaoService.getDataList(DomainRO.class, page * pageSize, pageSize, sortField, sortOrder, null);
            sg.getServiceEntities().addAll(lst);
        }
        return sg;
    }

    public ServiceResult<ServiceMetadataRO> getServiceMetadataList(int page, int pageSize,
                                                          String sortField,
                                                          String sortOrder) {

        ServiceResult<ServiceMetadataRO> sg = new ServiceResult<>();
        sg.setPage(page);
        sg.setPageSize(pageSize);
        long iCnt = uiDaoService.getDataListCount(ServiceMetadataRO.class, null);
        sg.setCount(iCnt);
        if (iCnt > 0) {
            List<ServiceMetadataRO> lst = uiDaoService.getDataList(ServiceMetadataRO.class, page * pageSize, pageSize, sortField, sortOrder, null);
            sg.getServiceEntities().addAll(lst);
        }
        return sg;
    }

}
