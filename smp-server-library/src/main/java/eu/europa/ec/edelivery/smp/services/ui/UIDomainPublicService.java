package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainDeleteValidation;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.DomainPublicRO;
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

/**
 * Service bean provides only public domain entity data for the Domain.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Service
public class UIDomainPublicService extends UIServiceBase<DBDomain, DomainPublicRO> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIDomainPublicService.class);
    @Autowired
    DomainDao domainDao;

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
}
