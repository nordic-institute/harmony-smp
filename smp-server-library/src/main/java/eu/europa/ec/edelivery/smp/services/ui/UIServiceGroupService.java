package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMLStatusEnum;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.security.ResourceGuard;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.SMLIntegrationService;
import eu.europa.ec.edelivery.smp.services.ui.filters.ResourceFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.*;

@Service
public class UIServiceGroupService extends UIServiceBase<DBResource, ServiceGroupRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIServiceGroupService.class);


    protected final DomainDao domainDao;
    protected final ResourceDao serviceGroupDao;
    protected final UserDao userDao;
    protected final IdentifierService identifierService;
    protected final SMLIntegrationService smlIntegrationService;
    protected final ConfigurationService configurationService;

    protected final ResourceGuard resourceGuard;

    public UIServiceGroupService(DomainDao domainDao,
                                 ResourceDao serviceGroupDao,
                                 UserDao userDao,
                                 IdentifierService identifierService,
                                 SMLIntegrationService smlIntegrationService,
                                 ConfigurationService configurationService,
                                 ResourceGuard resourceGuard) {
        this.domainDao = domainDao;
        this.serviceGroupDao = serviceGroupDao;
        this.userDao = userDao;
        this.identifierService = identifierService;
        this.smlIntegrationService = smlIntegrationService;
        this.configurationService = configurationService;
        this.resourceGuard = resourceGuard;
    }

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
    public ServiceResult<ServiceGroupRO> getTableList(int page, int pageSize,
                                                      String sortField,
                                                      String sortOrder, ResourceFilter filter) {

        ServiceResult<ServiceGroupRO> sg = new ServiceResult<>();
        sg.setPage(page < 0 ? 0 : page);
        sg.setPageSize(pageSize);
        long iCnt = serviceGroupDao.getServiceGroupCount(filter);
        sg.setCount(iCnt);

        if (iCnt > 0) {
            int iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            if (iStartIndex >= iCnt && page > 0) {
                page = page - 1;
                sg.setPage(page); // go back for a page
                iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            }

            List<DBResource> lst = serviceGroupDao.getServiceGroupList(iStartIndex, pageSize, sortField, sortOrder, filter);
            List<ServiceGroupRO> lstRo = new ArrayList<>();
            for (DBResource dbServiceGroup : lst) {
                ServiceGroupRO serviceGroupRo = convertToRo(dbServiceGroup);
                serviceGroupRo.setStatus(EntityROStatus.PERSISTED.getStatusNumber());
                serviceGroupRo.setIndex(iStartIndex++);
                lstRo.add(serviceGroupRo);
            }
            sg.getServiceEntities().addAll(lstRo);
        }
        return sg;
    }

    @Transactional
    public ServiceGroupRO getServiceGroupById(Long serviceGroupId) {
        DBResource dbServiceGroup = getDatabaseDao().find(serviceGroupId);
        dbServiceGroup.getSubresources().size();
        return convertToRo(dbServiceGroup);
    }

    @Transactional
    public ServiceGroupRO getOwnedServiceGroupById(Long userId, Long serviceGroupId) {
        DBResource dbServiceGroup = getDatabaseDao().find(serviceGroupId);
        if (resourceGuard.isResourceAdmin(userId, dbServiceGroup)) {
            convertToRo(dbServiceGroup);
        }
        return null;
    }

    @Transactional
    public ServiceGroupValidationRO getServiceGroupExtensionById(Long serviceGroupId) {
        /*
        ServiceGroupValidationRO ex = new ServiceGroupValidationRO();
        DBResource dbServiceGroup = getDatabaseDao().find(serviceGroupId);
        ex.setServiceGroupId(dbServiceGroup.getId());
        ex.setParticipantIdentifier(dbServiceGroup.getIdentifierValue());
        ex.setParticipantScheme(dbServiceGroup.getIdentifierScheme());

        if (dbServiceGroup.getExtension() != null) {
            ex.setExtension(getConvertExtensionToString(serviceGroupId, dbServiceGroup.getExtension()));
        }
        return ex;

         */
        return null;
    }

    private String getConvertExtensionToString(Long id, byte[] extension) {
        try {
            return new String(extension, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Error converting the extension to String for id:" + id, e);
            return null;
        }

    }


    /**
     * Method validates and converts UI resource object entity to database entity and persists it to database
     *
     * @param serviceGroupRO
     */
    protected List<ParticipantSMLRecord> addNewServiceGroup(ServiceGroupRO serviceGroupRO) {
        // normalize identifiers
        normalizeIdentifiers(serviceGroupRO);

        DBResource dbServiceGroup = new DBResource();
        dbServiceGroup.setIdentifierValue(serviceGroupRO.getParticipantIdentifier());
        dbServiceGroup.setIdentifierScheme(serviceGroupRO.getParticipantScheme());

        // add users
        updateUsersOnServiceGroup(serviceGroupRO, dbServiceGroup);

        // first update domains
        // validate (if domains are added only once) and  create domain list for service group.
        List<ParticipantSMLRecord> listOfActions = createDomainsForNewServiceGroup(serviceGroupRO, dbServiceGroup);

/*
        // sort service metadata by domain
        List<ServiceMetadataRO> serviceMetadataROList = serviceGroupRO.getServiceMetadata();
        serviceMetadataROList.forEach(serviceMetadataRO -> {
            // find the domain
            Optional<DBDomainResourceDef> dbServiceGroupDomain = dbServiceGroup.getServiceGroupForDomain(serviceMetadataRO.getDomainCode());
            if (dbServiceGroupDomain.isPresent()) {
                dbServiceGroupDomain.get().addServiceMetadata(createServiceMetadataFromRo(serviceMetadataRO));
            } else {
                throw new SMPRuntimeException(SG_NOT_REGISTRED_FOR_DOMAIN, serviceMetadataRO.getDomainCode(),
                        serviceGroupRO.getParticipantIdentifier(), serviceGroupRO.getParticipantScheme());
            }
        });

        // add extension
        if (serviceGroupRO.getExtension() != null) {
            byte[] buff = validateExtension(serviceGroupRO);
            dbServiceGroup.setExtension(buff);
        }

 */
        getDatabaseDao().persistFlushDetach(dbServiceGroup);
        return listOfActions;
    }

    private void normalizeIdentifiers(ServiceGroupRO sgo) {
        Identifier pti = identifierService.normalizeParticipant(sgo.getParticipantScheme(),
                sgo.getParticipantIdentifier());
        sgo.setParticipantScheme(pti.getScheme());
        sgo.setParticipantIdentifier(pti.getValue());
        sgo.getServiceMetadata().forEach(smd -> {
            Identifier dit = identifierService.normalizeDocument(smd.getDocumentIdentifierScheme(),
                    smd.getDocumentIdentifier());
            smd.setDocumentIdentifierScheme(dit.getScheme());
            smd.setDocumentIdentifier(dit.getValue());

        });
    }

    /**
     * Validate (if domains are added only once) and  create domain list for service group.
     *
     * @param serviceGroupRO
     * @param resource
     */
    protected List<ParticipantSMLRecord> createDomainsForNewServiceGroup(ServiceGroupRO serviceGroupRO, DBResource resource) {

        List<ParticipantSMLRecord> participantSMLRecordList = new ArrayList<>();
        // first update domains
        List<ServiceGroupDomainRO> serviceGroupDomainROList = serviceGroupRO.getServiceGroupDomains();
        /*
        // validate (if domains are added only once) and  create domain list for service group.
        serviceGroupDomainROList.forEach(dro -> {
            // everything ok  find domain and add it to service group
            Optional<DBDomain> dmn = domainDao.getDomainByCode(dro.getDomainCode());
            if (dmn.isPresent()) {
                DBDomainResourceDef domain = resource.addDomain(dmn.get());
                participantSMLRecordList.add(new ParticipantSMLRecord(SMLStatusEnum.REGISTER,
                        serviceGroupRO.getParticipantIdentifier(),
                        serviceGroupRO.getParticipantScheme(),
                        domain.getDomain()));
            } else {
                throw new SMPRuntimeException(DOMAIN_NOT_EXISTS, dro.getDomainCode());
            }
        });

         */
        return participantSMLRecordList;
    }


    /**
     * Method converts UI resource object entity to database entity and update changes  to database
     *
     * @param serviceGroupRO
     */
    protected List<ParticipantSMLRecord> updateServiceGroup(ServiceGroupRO serviceGroupRO, boolean serviceGroupAdmin) {

        // normalize identifiers
        normalizeIdentifiers(serviceGroupRO);
        // find and validate service group
        DBResource dbServiceGroup = findAndValidateServiceGroup(serviceGroupRO);
        List<ParticipantSMLRecord> participantSMLRecordList = Collections.emptyList();
        if (serviceGroupAdmin) {
            // update users
            updateUsersOnServiceGroup(serviceGroupRO, dbServiceGroup);

            // update domain
            participantSMLRecordList = updateDomainsForServiceGroup(serviceGroupRO, dbServiceGroup);
        }
/*
        //update service metadata
        List<ServiceMetadataRO> serviceMetadataROList = serviceGroupRO.getServiceMetadata();
        serviceMetadataROList.forEach(serviceMetadataRO -> {

            Optional<DBDomainResourceDef> optionalDbServiceGroupDomain =
                    dbServiceGroup.findServiceGroupDomainForMetadata(serviceMetadataRO.getDocumentIdentifier(), serviceMetadataRO.getDocumentIdentifierScheme());
            // remove service metadata
            if (serviceMetadataRO.getStatus() == EntityROStatus.REMOVE.getStatusNumber()) {
                // if the domain was not removed then remove only metadata
                if (optionalDbServiceGroupDomain.isPresent()) {
                    DBDomainResourceDef dbServiceGroupDomain = optionalDbServiceGroupDomain.get();
                    // remove from domain
                    dbServiceGroupDomain.removeServiceMetadata(serviceMetadataRO.getDocumentIdentifier(),
                            serviceMetadataRO.getDocumentIdentifierScheme());
                }

            } else if (serviceMetadataRO.getStatus() == EntityROStatus.NEW.getStatusNumber()) {
                // add to new service group.. find servicegroup domain by code
                optionalDbServiceGroupDomain = dbServiceGroup.getServiceGroupForDomain(serviceMetadataRO.getDomainCode());
                if (optionalDbServiceGroupDomain.isPresent()) {
                    optionalDbServiceGroupDomain.get().addServiceMetadata(createServiceMetadataFromRo(serviceMetadataRO));
                } else {
                    throw new SMPRuntimeException(SG_NOT_REGISTRED_FOR_DOMAIN, serviceMetadataRO.getDomainCode(),
                            serviceGroupRO.getParticipantIdentifier(), serviceGroupRO.getParticipantScheme());
                }
            } else if (serviceMetadataRO.getStatus() == EntityROStatus.UPDATED.getStatusNumber()) {
                if (optionalDbServiceGroupDomain.isPresent()) {

                    DBDomainResourceDef dbServiceGroupDomain = optionalDbServiceGroupDomain.get();
                    DBSubresource DBSubresource = dbServiceGroupDomain.getServiceMetadata(serviceMetadataRO.getDocumentIdentifier(),
                            serviceMetadataRO.getDocumentIdentifierScheme());
                    if (serviceMetadataRO.getXmlContentStatus() == EntityROStatus.UPDATED.getStatusNumber()) {
                        // get service metadata
                        byte[] buff = validateServiceMetadata(serviceMetadataRO);


                        DBSubresource.setXmlContent(buff);
                    }

                    if (!Objects.equals(serviceMetadataRO.getDomainCode(), dbServiceGroupDomain.getDomain().getDomainCode())) {
                        // remove from old domain
                        LOG.info("Move service metadata from domain {} to domain: {}", dbServiceGroupDomain.getDomain().getDomainCode(),
                                serviceMetadataRO.getDomainCode());

                        DBSubresource smd = dbServiceGroupDomain.removeServiceMetadata(serviceMetadataRO.getDocumentIdentifier(),
                                serviceMetadataRO.getDocumentIdentifierScheme());


                        // find new domain and add
                        Optional<DBDomainResourceDef> optNewDomain = dbServiceGroup.getServiceGroupForDomain(serviceMetadataRO.getDomainCode());
                        if (optNewDomain.isPresent()) {
                            LOG.info("ADD service metadata to domain {} ", optNewDomain.get().getDomain().getDomainCode(),
                                    serviceMetadataRO.getDomainCode());
                            // create new because the old service metadata will be deleted
                            DBSubresource smdNew = new DBSubresource();
                            smdNew.setDocumentIdentifier(DBSubresource.getDocumentIdentifier());
                            smdNew.setDocumentIdentifierScheme(DBSubresource.getDocumentIdentifierScheme());
                            smdNew.setServiceGroupDomain(optNewDomain.get());
                            smdNew.setServiceMetadataXml(DBSubresource.getServiceMetadataXml());
                            smdNew.setCreatedOn(DBSubresource.getCreatedOn());

                            optNewDomain.get().addServiceMetadata(smdNew);

                        } else {
                            throw new SMPRuntimeException(SG_NOT_REGISTRED_FOR_DOMAIN, serviceMetadataRO.getDomainCode(),
                                    serviceGroupRO.getParticipantIdentifier(), serviceGroupRO.getParticipantScheme());
                        }
                    }
                } else {
                    throw new SMPRuntimeException(SG_NOT_REGISTRED_FOR_DOMAIN, serviceMetadataRO.getDomainCode(),
                            serviceGroupRO.getParticipantIdentifier(), serviceGroupRO.getParticipantScheme());
                }
            }

        });

        //
        // add extension
        if (serviceGroupRO.getExtensionStatus() != EntityROStatus.PERSISTED.getStatusNumber()) {
            byte[] buff = validateExtension(serviceGroupRO);
            dbServiceGroup.setExtension(buff);
        }
*/

        // persist it to database
        getDatabaseDao().update(dbServiceGroup);
        return participantSMLRecordList;
    }

    /**
     * Validate (if domains are added only once) and  update domain list for service group.
     *
     * @param serviceGroupRO
     * @param dbServiceGroup
     */
    protected List<ParticipantSMLRecord> updateDomainsForServiceGroup(ServiceGroupRO serviceGroupRO, DBResource dbServiceGroup) {
        List<ParticipantSMLRecord> participantSMLRecordList = new ArrayList<>();
/*
        // / validate (if domains are added only once) and  create domain list for service group.
        List<ServiceGroupDomainRO> serviceGroupDomainROList = serviceGroupRO.getServiceGroupDomains();
        // copy array list of old domains and then put them back. Domain not added back will be deleted by hibernate
        // ...
        List<DBDomainResourceDef> lstOldSGDomains = new ArrayList<>();
        lstOldSGDomains.addAll(dbServiceGroup.getResourceDomains());
        dbServiceGroup.getResourceDomains().clear();


        serviceGroupDomainROList.forEach(serviceGroupDomainRO -> {
            DBDomainResourceDef dsg = getSGDomainFromList(lstOldSGDomains, serviceGroupDomainRO);
            if (dsg != null) {
                // put it back - no need to call addDomain
                dbServiceGroup.getResourceDomains().add(dsg);
                // remove from old domain list
                lstOldSGDomains.remove(dsg);
            } else {
                // new domain  - find dbDomain and add it to service group
                Optional<DBDomain> dmn = domainDao.getDomainByCode(serviceGroupDomainRO.getDomainCode());
                if (dmn.isPresent()) {

                    DBDomainResourceDef sgd = dbServiceGroup.addDomain(dmn.get());
                    participantSMLRecordList.add(new ParticipantSMLRecord(SMLStatusEnum.REGISTER,
                            sgd.getServiceGroup().getIdentifierValue(),
                            sgd.getServiceGroup().getIdentifierScheme(),
                            sgd.getDomain()));
                } else {
                    throw new SMPRuntimeException(DOMAIN_NOT_EXISTS, serviceGroupDomainRO.getDomainCode());
                }
            }
        });
        // remove old domains
        lstOldSGDomains.forEach(dbServiceGroupDomain -> {
            participantSMLRecordList.add(new ParticipantSMLRecord(SMLStatusEnum.UNREGISTER,
                    dbServiceGroupDomain.getServiceGroup().getIdentifierValue(),
                    dbServiceGroupDomain.getServiceGroup().getIdentifierScheme(),
                    dbServiceGroupDomain.getDomain()));

            dbServiceGroupDomain.setServiceGroup(null);

        }); */
        return participantSMLRecordList;
    }

    /**
     * Update users on service group. Method is OK for update and add new domain
     *
     * @param serviceGroupRO
     * @param dbServiceGroup
     */
    protected void updateUsersOnServiceGroup(ServiceGroupRO serviceGroupRO, DBResource dbServiceGroup) {
        // update users
        /* TODO!
        dbServiceGroup.getMembers().clear();
        List<UserRO> lstUsers = serviceGroupRO.getUsers();
        for (UserRO userRO : lstUsers) {
            Long userid = SessionSecurityUtils.decryptEntityId(userRO.getUserId());
            Optional<DBUser> optUser = userDao.findUser(userid);
            if (!optUser.isPresent()) {
                throw new SMPRuntimeException(INTERNAL_ERROR,
                        "Database changed", "User " + userRO.getUsername() + " not exists! (Refresh data)");
            }
            dbServiceGroup.getMembers().add(optUser.get());
        }*/
    }

    /**
     * Method retrieve servicegroup data from database and validates id and participant
     *
     * @param serviceGroupRO
     * @return
     */
    private DBResource findAndValidateServiceGroup(ServiceGroupRO serviceGroupRO) {
        // find and validate service group
        if (serviceGroupRO.getId() == null) {
            throw new SMPRuntimeException(MISSING_SG_ID, serviceGroupRO.getParticipantIdentifier(), serviceGroupRO.getParticipantScheme());
        }
        // validate service group id
        boolean schemeMandatory = configurationService.getParticipantSchemeMandatory();
        LOG.debug("Validate service group [{}] with [{}] scheme", serviceGroupRO.getParticipantIdentifier(), (schemeMandatory ? "mandatory" : "optional"));


        DBResource dbServiceGroup = getDatabaseDao().find(serviceGroupRO.getId());
        if (!Objects.equals(serviceGroupRO.getParticipantIdentifier(), dbServiceGroup.getIdentifierValue())
                || schemeMandatory &&
                !Objects.equals(serviceGroupRO.getParticipantScheme(), dbServiceGroup.getIdentifierScheme())) {
            throw new SMPRuntimeException(INVALID_SG_ID, serviceGroupRO.getParticipantIdentifier(),
                    serviceGroupRO.getParticipantScheme(), serviceGroupRO.getId());
        }
        return dbServiceGroup;
    }

    /**
     * Check if service metadata parsers and if data match servicemetadata and service group...
     *
     * @param serviceMetadataRO
     * @return
     */
    private byte[] validateServiceMetadata(ServiceMetadataRO serviceMetadataRO) {
        byte[] buff = null;
/*
        try {
            buff = serviceMetadataRO.getXmlContent().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SMPRuntimeException(INVALID_ENCODING, "UTF-8");
        }
        try {
            BdxSmpOasisValidator.validateXSD(buff);
        } catch (XmlInvalidAgainstSchemaException e) {
            throw new SMPRuntimeException(INVALID_SMD_XML, ExceptionUtils.getRootCauseMessage(e));
        }
        */
/*
        ServiceMetadata smd = ServiceMetadataConverter.unmarshal(buff);
        if (smd.getServiceInformation() != null) {
            Identifier di = identifierService.normalizeDocument(smd.getServiceInformation().getDocumentIdentifier());
            if (Objects.equals(di.getScheme(), serviceMetadataRO.getDocumentIdentifierScheme())
                    && Objects.equals(di.getValue(), serviceMetadataRO.getDocumentIdentifier())) {

            } else {
                throw new SMPRuntimeException(INVALID_SMD_DOCUMENT_DATA, di.getValue(), di.getScheme(),
                        serviceMetadataRO.getDocumentIdentifier(), serviceMetadataRO.getDocumentIdentifierScheme());
            }
        }

 */
        return buff;
    }


    /**
     * Convert Database object to Rest object for UI. It does not set blobs - extensions and metadataservice xml!
     * They are retrieved to UI when needed.
     *
     * @param dbServiceGroup - database  entity
     * @return ServiceGroupRO
     */
    public ServiceGroupRO convertToRo(DBResource dbServiceGroup) {
        ServiceGroupRO serviceGroupRo = new ServiceGroupRO();
        serviceGroupRo.setId(dbServiceGroup.getId());
        serviceGroupRo.setParticipantIdentifier(dbServiceGroup.getIdentifierValue());
        serviceGroupRo.setParticipantScheme(dbServiceGroup.getIdentifierScheme());

        dbServiceGroup.getSubresources().stream().map(this::convertServiceMetadataToRo)
                .forEach(smdro -> {
                    serviceGroupRo.getServiceMetadata().add(smdro);
                });
/*
        // add domains
        dbServiceGroup.getResourceDomains().forEach(sgd -> {
            ServiceGroupDomainRO servGrpDomain = new ServiceGroupDomainRO();
            servGrpDomain.setId(sgd.getId());
            servGrpDomain.setDomainId(sgd.getDomain().getId());
            servGrpDomain.setDomainCode(sgd.getDomain().getDomainCode());
            servGrpDomain.setSmlSubdomain(sgd.getDomain().getSmlSubdomain());
            // add service metadata to service group NOT TO service group domain
            // little different view from DB Model - all for the users :) ..
            sgd.getSubresourcesList().stream().map(this::convertServiceMetadataToRo)
                    .forEach(smdro -> {
                        smdro.setSmlSubdomain(servGrpDomain.getSmlSubdomain());
                        smdro.setDomainCode(servGrpDomain.getDomainCode());
                        smdro.setServiceGroupDomainId(servGrpDomain.getId());
                        serviceGroupRo.getServiceMetadata().add(smdro);
                    });
            //also add domain to service group
            serviceGroupRo.getServiceGroupDomains().add(servGrpDomain);
        });
*/

        /*TODO
        // add users add just encrypted ID
        dbServiceGroup.getUsers().forEach(usr -> {
            UserRO userRO = new UserRO();
            userRO.setUserId(SessionSecurityUtils.encryptedEntityId(usr.getId()));
            serviceGroupRo.getUsers().add(userRO);
        });

         */
        // do not add service extension to gain performance.
        return serviceGroupRo;
    }

    /**
     * Convert database entity to resource object ServiceMetadataRO. To gain UI performance do not copy XM for UI.
     * It is retrieved when needed!
     *
     * @param sgmd
     * @return
     */
    private ServiceMetadataRO convertServiceMetadataToRo(DBSubresource sgmd) {
        ServiceMetadataRO smdro = new ServiceMetadataRO();
        smdro.setId(sgmd.getId());
        smdro.setDocumentIdentifier(sgmd.getIdentifierValue());
        smdro.setDocumentIdentifierScheme(sgmd.getIdentifierScheme());
        return smdro;
    }

    /**
     * Create new database entity - service metadata from resource object
     *
     * @param serviceMetadataRO
     * @return new database entity DBSubresource
     */
    private DBSubresource createServiceMetadataFromRo(ServiceMetadataRO serviceMetadataRO) {

        byte[] buff = validateServiceMetadata(serviceMetadataRO);
        DBSubresource DBSubresource = new DBSubresource();
        Identifier docIdent = identifierService.normalizeDocument(serviceMetadataRO.getDocumentIdentifierScheme(),
                serviceMetadataRO.getDocumentIdentifier());
        DBSubresource.setIdentifierValue(docIdent.getValue());


        return DBSubresource;
    }


    /**
     * for ServiceGroupDomainRO returns DBServiceGroupDomain  from ServiceGroup list of domain. ServiceGroup domain is matched by Id
     * and verified by domain id.
     *
     * @param lstSGDomains
     * @param domainRo
     * @return
     */
    private DBDomainResourceDef getSGDomainFromList(List<DBDomainResourceDef> lstSGDomains, ServiceGroupDomainRO domainRo) {
        for (DBDomainResourceDef dbServiceGroupDomain : lstSGDomains) {
            if (Objects.equals(dbServiceGroupDomain.getId(), domainRo.getId())) {
                // double check for domain
                if (!Objects.equals(dbServiceGroupDomain.getDomain().getId(), domainRo.getDomainId())) {
                    throw new SMPRuntimeException(INVALID_REQUEST, "Domain mismatch!", "Domain id for does not match for servicegroup domain");
                }
                return dbServiceGroupDomain;
            }
        }
        return null;
    }

    /**
     * Validate if extension is valid by schema.
     *
     * @param serviceGroup
     * @return
     */
    public ServiceGroupValidationRO validateServiceGroup(ServiceGroupValidationRO serviceGroup) {
/**
 if (serviceGroup == null) {
 throw new SMPRuntimeException(INVALID_REQUEST, "Validate extension", "Missing Extension parameter");
 } // if new check if service group already exist

 if (serviceGroup.getStatusAction() == EntityROStatus.NEW.getStatusNumber()) {
 Identifier normalizedParticipant = identifierService
 .normalizeParticipant(
 serviceGroup.getParticipantScheme(),
 serviceGroup.getParticipantIdentifier());
 Optional<DBResource> sg = serviceGroupDao.findServiceGroup(normalizedParticipant.getValue(),
 normalizedParticipant.getScheme());
 if (sg.isPresent()) {
 serviceGroup.setErrorMessage("Service group: " + serviceGroup.getParticipantScheme() + ":" + serviceGroup.getParticipantIdentifier() +
 " already exists!");
 serviceGroup.setErrorCode(ERROR_CODE_SERVICE_GROUP_EXISTS);
 return serviceGroup;
 }
 }

 if (StringUtils.isBlank(serviceGroup.getExtension())) {
 // empty extension is also a valid extension
 serviceGroup.setErrorMessage(null);
 } else {
 try {
 byte[] buff = serviceGroup.getExtension().getBytes("UTF-8");
 ExtensionConverter.validateExtensionBySchema(buff); // validate by schema
 serviceGroup.setErrorMessage(null);
 serviceGroup.setErrorCode(ERROR_CODE_OK);
 } catch (XmlInvalidAgainstSchemaException | UnsupportedEncodingException e) {
 serviceGroup.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
 serviceGroup.setErrorCode(ERROR_CODE_INVALID_EXTENSION);
 }
 }
 */
        return serviceGroup;
    }

    /**
     * Validate if extension is valid by schema.
     *
     * @param serviceGroupRO
     * @return
     */
    public byte[] validateExtension(ServiceGroupRO serviceGroupRO) {
        /*
        if (StringUtils.isBlank(serviceGroupRO.getExtension())) {
            return null;
        }
        try {
            byte[] buff = serviceGroupRO.getExtension().getBytes("UTF-8");
            ExtensionConverter.validateExtensionBySchema(buff); // validate by schema
            return buff;
        } catch (UnsupportedEncodingException | XmlInvalidAgainstSchemaException e) {
            throw new SMPRuntimeException(INVALID_EXTENSION_FOR_SG, serviceGroupRO.getParticipantIdentifier(),
                    serviceGroupRO.getParticipantScheme(), ExceptionUtils.getRootCauseMessage(e));
        }

         */
        return null;
    }
}
