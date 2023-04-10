package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.*;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.BadRequestException;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */

@Service
public class UIDomainService extends UIServiceBase<DBDomain, DomainRO> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIDomainService.class);


    private DomainDao domainDao;
    private ResourceDao resourceDao;
    private ResourceDefDao resourceDefDao;
    private DomainResourceDefDao domainResourceDefDao;
    private ConversionService conversionService;
    private SmlConnector smlConnector;

    public UIDomainService(ConversionService conversionService, DomainDao domainDao, ResourceDao resourceDao, ResourceDefDao resourceDefDao, DomainResourceDefDao domainResourceDefDao, SmlConnector smlConnector) {
        this.conversionService = conversionService;
        this.domainDao = domainDao;
        this.resourceDao = resourceDao;
        this.resourceDefDao = resourceDefDao;
        this.domainResourceDefDao = domainResourceDefDao;
        this.smlConnector = smlConnector;
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
    @Transactional
    @Override
    public ServiceResult<DomainRO> getTableList(int page, int pageSize,
                                                String sortField,
                                                String sortOrder, Object filter) {

        return super.getTableList(page, pageSize, sortField, sortOrder, filter);
    }

    @Transactional
    public List<DomainRO> getAllDomains() {
        List<DBDomain> domains = domainDao.getAllDomains();
        return domains.stream().map(domain -> conversionService.convert(domain, DomainRO.class))
                .collect(Collectors.toList());
    }


    @Transactional
    public void createDomainData(DomainRO data) {
        DBDomain domain = new DBDomain();
        domain.setDomainCode(data.getDomainCode());
        domain.setDefaultResourceTypeIdentifier(data.getDefaultResourceTypeIdentifier());
        domain.setSignatureKeyAlias(data.getSignatureKeyAlias());
        domain.setVisibility(data.getVisibility() == null? VisibilityType.PUBLIC:data.getVisibility());
        domainDao.persistFlushDetach(domain);
    }


    /**
     * Update only basic domain data from DomainRO object. Ignore other
     * @param domainId
     * @param data
     * @return
     */
    @Transactional
    public void updateBasicDomainData(Long domainId, DomainRO data) {
        DBDomain domain = domainDao.find(domainId);
        if (domain == null) {
            LOG.warn("Can not delete domain for ID [{}], because it does not exists!", domainId);
            throw new BadRequestException(ErrorBusinessCode.NOT_FOUND, "Domain does not exist in database!");
        }
        domain.setDomainCode(data.getDomainCode());
        domain.setDefaultResourceTypeIdentifier(data.getDefaultResourceTypeIdentifier());
        domain.setSignatureKeyAlias(data.getSignatureKeyAlias());
        domain.setVisibility(data.getVisibility());
    }

    @Transactional
    public void updateDomainSmlIntegrationData(Long domainId, DomainRO data) {
        DBDomain domain = domainDao.find(domainId);
        if (domain == null) {
            throw new BadRequestException(ErrorBusinessCode.NOT_FOUND, "Domain does not exist in database!");
        }
        if (domain.isSmlRegistered() && StringUtils.equals(data.getSmlSmpId(), domain.getSmlSmpId())){
            String msg = "SMP-SML identifier must not change for registered domain ["+domain.getDomainCode()+"]!";
            throw new BadRequestException(ErrorBusinessCode.NOT_FOUND, msg);
        }

        domain.setSmlSubdomain(data.getSmlSubdomain());
        domain.setSmlSmpId(data.getSmlSmpId());
        domain.setSmlClientKeyAlias(data.getSmlClientKeyAlias());
        domain.setSmlClientCertHeader(data.getSmlClientCertHeader());
        domain.setSmlClientCertAuth(data.isSmlClientCertAuth());
    }

    @Transactional
    public void updateResourceDefDomainList(Long domainId, List<String> resourceDefIds) {
        DBDomain domain = domainDao.find(domainId);
        if (domain == null) {
            LOG.warn("Can not delete domain for ID [{}], because it does not exists!", domainId);
            throw new BadRequestException(ErrorBusinessCode.NOT_FOUND, "Domain does not exist in database!");
        }

        //filter and validate resources to be removed
        List<DBDomainResourceDef> removedDoReDef = domain.getDomainResourceDefs().stream()
                .filter(doredef -> !resourceDefIds.contains(doredef.getResourceDef().getIdentifier())
                        && validateRemoveDomainResourceDef(domain, doredef.getResourceDef())
                ).collect(Collectors.toList());

        removedDoReDef.forEach(domainResourceDef -> domain.getDomainResourceDefs().remove(domainResourceDef));
        List<String> currentIdentifiers = domain.getDomainResourceDefs().stream().map(domainResourceDef -> domainResourceDef.getResourceDef().getIdentifier()).collect(Collectors.toList());

        resourceDefIds.stream()
                .filter(identifier -> !currentIdentifiers.contains(identifier))
                .map(identifier -> resourceDefDao.getResourceDefByIdentifier(identifier)
                        .orElseThrow(() -> new BadRequestException(ErrorBusinessCode.INVALID_INPUT_DATA, "Identifier [" + identifier + "] does not exists")))
                .forEach(resourceDef ->
                        domainResourceDefDao.create(domain, resourceDef)
                );
    }

    @Transactional
    public DomainRO getDomainData(Long domainId){
        DBDomain domain =  domainDao.find(domainId);
        return conversionService.convert(domain, DomainRO.class);
    }

    @Transactional
    public DomainRO getDomainDataByDomainCode(String domainCode){
        DBDomain domain =  domainDao.getDomainByCode(domainCode).orElse(null);
        return conversionService.convert(domain, DomainRO.class);
    }

    private boolean validateRemoveDomainResourceDef(DBDomain domain, DBResourceDef resourceDef) {

        Long count = resourceDao.getResourceCountForDomainIdAndResourceDefId(domain.getId(), resourceDef.getId());
        if (count > 0) {
            String msg = "Can not remove resource definition [" + resourceDef.getIdentifier() + "] from domain [" + domain.getDomainCode()
                    + "], because it has resources. Resource count [" + count + "]!";
            throw new BadRequestException(ErrorBusinessCode.INVALID_INPUT_DATA, msg);
        }
        return true;
    }

    @Transactional
    public DomainRO deleteDomain(Long domainId) {

        DBDomain domain = domainDao.find(domainId);
        if (domain == null) {
            LOG.warn("Can not delete domain for ID [{}], because it does not exists!", domainId);
            throw new BadRequestException(ErrorBusinessCode.NOT_FOUND, "Domain does not exist in database!");
        }
        if (domain.isSmlRegistered()) {
            LOG.info("Can not delete domain for ID [{}], is registered to SML!", domainId);
            throw new BadRequestException(ErrorBusinessCode.INVALID_INPUT_DATA, "Can not delete domain because it is registered to SML service! Unregister domain from SML service!");
        }

        Long count = domainDao.getResourceCountForDomain(domainId);
        if (count > 0) {
            LOG.info("Can not delete domain for ID [{}], because it has resources. Resource count [{}]!", domainId, count);
            throw new BadRequestException(ErrorBusinessCode.INVALID_INPUT_DATA, "Can not delete domain because it has resources [" + count + "]! Delete resources first!");
        }

        domainDao.remove(domain);
        DomainRO domainRO = conversionService.convert(domain, DomainRO.class);
        domainRO.setStatus(EntityROStatus.REMOVE.getStatusNumber());
        return domainRO;
    }


}
