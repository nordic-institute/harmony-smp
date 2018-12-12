package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainDeleteValidation;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UIDomainService extends UIServiceBase<DBDomain, DomainRO> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIDomainService.class);
    @Autowired
    DomainDao domainDao;

    @Autowired
    SmlConnector smlConnector;

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
    @Transactional
    public ServiceResult<DomainRO> getTableList(int page, int pageSize,
                                                String sortField,
                                                String sortOrder, Object filter) {

        return super.getTableList(page, pageSize, sortField, sortOrder, filter);
    }

    @Transactional
    public void updateDomainList(List<DomainRO> lst) {
        boolean suc = false;
        for (DomainRO dRo : lst) {
            if (dRo.getStatus() == EntityROStatus.NEW.getStatusNumber()) {
                DBDomain dDb = convertFromRo(dRo);
                domainDao.persistFlushDetach(dDb);
            } else if (dRo.getStatus() == EntityROStatus.UPDATED.getStatusNumber()) {
                DBDomain upd = domainDao.find(dRo.getId());
                upd.setSmlSmpId(dRo.getSmlSmpId());
                upd.setSmlClientKeyAlias(dRo.getSmlClientKeyAlias());
                upd.setSmlClientCertHeader(dRo.getSmlClientCertHeader());
                upd.setSmlParticipantIdentifierRegExp(dRo.getSmlParticipantIdentifierRegExp());
                upd.setSmlSubdomain(dRo.getSmlSubdomain());
                upd.setDomainCode(dRo.getDomainCode());
                upd.setSignatureKeyAlias(dRo.getSignatureKeyAlias());
                upd.setSmlBlueCoatAuth(dRo.isSmlBlueCoatAuth());
                upd.setLastUpdatedOn(LocalDateTime.now());
                domainDao.update(upd);
            } else if (dRo.getStatus() == EntityROStatus.REMOVE.getStatusNumber()) {
                domainDao.removeByDomainCode(dRo.getDomainCode());
            }
        }
    }

    public DeleteEntityValidation validateDeleteRequest(DeleteEntityValidation dev) {
        List<DBDomainDeleteValidation> lstMessages = domainDao.validateDomainsForDelete(dev.getListIds());
        dev.setValidOperation(lstMessages.isEmpty());
        if (!dev.isValidOperation()) {
            StringWriter sw = new StringWriter();
            sw.write("Could not delete domains used by Service groups! ");
            lstMessages.forEach(msg -> {
                dev.getListDeleteNotPermitedIds().add(msg.getId());
                sw.write("Domain: ");
                sw.write(msg.getDomainCode());
                sw.write(" (");
                sw.write(msg.getDomainCode());
                sw.write(" )");
                sw.write(" uses by:");
                sw.write(msg.getCount().toString());
                sw.write(" SG.");

            });
            dev.setStringMessage(sw.toString());
        }
        return dev;
    }

}
