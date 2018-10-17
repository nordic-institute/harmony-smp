package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UIServiceGroupService extends UIServiceBase<DBServiceGroup, ServiceGroupRO> {


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
                serviceGroupRo.setId(dbServiceGroup.getId());
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
                // add users
                dbServiceGroup.getUsers().forEach(usr->{
                    UserRO userRO = new UserRO();
                    userRO.setId(usr.getId());
                    userRO.setUsername(usr.getUsername());
                    userRO.setActive(usr.isActive());
                    userRO.setEmail(usr.getEmail());
                    userRO.setRole(usr.getRole());
                    serviceGroupRo.getUsers().add(userRO);
                });
                lstRo.add(serviceGroupRo);
            }

            sg.getServiceEntities().addAll(lstRo);
        }
        return sg;
    }

    @Transactional
    public ServiceGroupRO getServiceGroupById(Long serviceGroupId) {
        DBServiceGroup dbServiceGroup = getDatabaseDao().find(serviceGroupId);
        ServiceGroupRO serviceGroupRo = new ServiceGroupRO();
        serviceGroupRo.setId(dbServiceGroup.getId());
        serviceGroupRo.setParticipantIdentifier(dbServiceGroup.getParticipantIdentifier());
        serviceGroupRo.setParticipantScheme(dbServiceGroup.getParticipantScheme());
        // add service groups
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
        // add users
        dbServiceGroup.getUsers().forEach(usr->{
            UserRO userRO = new UserRO();
            userRO.setId(usr.getId());
            userRO.setUsername(usr.getUsername());
            userRO.setActive(usr.isActive());
            userRO.setEmail(usr.getEmail());
            userRO.setRole(usr.getRole());
            serviceGroupRo.getUsers().add(userRO);
        });
        return serviceGroupRo;
    }

    @Transactional
    public void updateServiceGroupList(List<ServiceGroupRO> lst) {
        boolean suc = false;
        for (ServiceGroupRO dRo: lst){


            if (dRo.getStatus() == EntityROStatus.NEW.getStatusNumber()) {
                DBServiceGroup dDb = convertFromRo(dRo);
                for (UserRO userRO: dRo.getUsers()) {
                    System.out.println("GET USER ID: " + userRO.getId());
                    DBUser du = userDao.find(userRO.getId());
                    dDb.getUsers().add(du);

                }
                getDatabaseDao().persistFlushDetach(dDb);
            } else if (dRo.getStatus() == EntityROStatus.UPDATED.getStatusNumber()) {
                DBServiceGroup upd = getDatabaseDao().find(dRo.getId());
                upd.getUsers().clear();
                for (UserRO userRO: dRo.getUsers()) {
                    System.out.println("GET USER ID: " + userRO.getId());
                    DBUser du = userDao.find(userRO.getId());
                    upd.getUsers().add(du);

                }
                // only servicegroup users can be changed__
                /*
                upd.setSmlSmpId(dRo.getSmlSmpId());
                upd.setSmlClientKeyAlias(dRo.getSmlClientKeyAlias());
                upd.setSmlClientCertHeader(dRo.getSmlClientCertHeader());
                upd.setSmlParticipantIdentifierRegExp(dRo.getSmlParticipantIdentifierRegExp());
                upd.setSmlSubdomain(dRo.getSmlSubdomain());
                upd.setDomainCode(dRo.getDomainCode());
                upd.setSignatureKeyAlias(dRo.getSignatureKeyAlias());
                upd.setLastUpdatedOn(LocalDateTime.now());*/
                getDatabaseDao().update(upd);
            } else if (dRo.getStatus() == EntityROStatus.REMOVE.getStatusNumber()) {
                DBServiceGroup upd = getDatabaseDao().find(dRo.getId());
                serviceGroupDao.removeServiceGroup(upd);
            }
        }
    }

}
