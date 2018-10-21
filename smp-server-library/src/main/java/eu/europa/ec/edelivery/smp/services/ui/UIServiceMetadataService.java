package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.edelivery.smp.conversion.ExtensionConverter;
import eu.europa.ec.edelivery.smp.conversion.ServiceMetadataConverter;
import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.*;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.filters.ServiceGroupFilter;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.*;

@Service
public class UIServiceGroupService extends UIServiceBase<DBServiceGroup, ServiceGroupRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIServiceGroupService.class);

    @Autowired
    DomainDao domainDao;

    @Autowired
    ServiceGroupDao serviceGroupDao;

    @Autowired
    UserDao userDao;

    @Autowired
    private CaseSensitivityNormalizer caseSensitivityNormalizer;


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
        ServiceResult<ServiceGroupRO> sg = new ServiceResult<>();
        sg.setPage(page < 0 ? 0 : page);
        sg.setPageSize(pageSize);
        long iCnt = serviceGroupDao.getServiceGroupCount(filter, d);
        sg.setCount(iCnt);

        if (iCnt > 0) {
            int iStartIndex = pageSize < 0 ? -1 : page * pageSize;
            List<DBServiceGroup> lst = serviceGroupDao.getServiceGroupList(iStartIndex, pageSize, sortField, sortOrder, filter, d);
            List<ServiceGroupRO> lstRo = new ArrayList<>();
            for (DBServiceGroup dbServiceGroup : lst) {
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
        DBServiceGroup dbServiceGroup = getDatabaseDao().find(serviceGroupId);
        return convertToRo(dbServiceGroup);
    }

    @Transactional
    public ServiceGroupExtensionRO getServiceGroupExtensionById(Long serviceGroupId) {
        ServiceGroupExtensionRO ex = new ServiceGroupExtensionRO();
        DBServiceGroup dbServiceGroup = getDatabaseDao().find(serviceGroupId);
        ex.setServiceGroupId(dbServiceGroup.getId());
        if (dbServiceGroup.getExtension() != null) {
            ex.setExtension(new String(dbServiceGroup.getExtension()));
        }
        return ex;
    }

    @Transactional
    public void updateServiceGroupList(List<ServiceGroupRO> lst) {
        boolean suc = false;
        for (ServiceGroupRO dRo : lst) {
            if (dRo.getStatus() == EntityROStatus.NEW.getStatusNumber()) {
                addNewServiceGroup(dRo);
            } else if (dRo.getStatus() == EntityROStatus.UPDATED.getStatusNumber()) {
                updateServiceGroup(dRo);
            } else if (dRo.getStatus() == EntityROStatus.REMOVE.getStatusNumber()) {
                DBServiceGroup upd = getDatabaseDao().find(dRo.getId());
                serviceGroupDao.removeServiceGroup(upd);
            }
        }
    }

    /**
     * Method validates and converts UI resource object entity to database entity and persists it to database
     *
     * @param serviceGroupRO
     */
    private void addNewServiceGroup(ServiceGroupRO serviceGroupRO) {
        // normalize indentifiers
        normalizeIdentifiers(serviceGroupRO);

        DBServiceGroup dbServiceGroup = new DBServiceGroup();
        dbServiceGroup.setParticipantIdentifier(serviceGroupRO.getParticipantIdentifier());
        dbServiceGroup.setParticipantScheme(serviceGroupRO.getParticipantScheme());

        // add users
        updateUsersOnServiceGroup(serviceGroupRO, dbServiceGroup);

        // first update domains
        // validate (if domains are added only once) and  create domain list for service group.
        createDomainsForNewServiceGroup(serviceGroupRO, dbServiceGroup);


        // sort service metadata by domain
        List<ServiceMetadataRO> serviceMetadataROList = serviceGroupRO.getServiceMetadata();
        serviceMetadataROList.forEach(serviceMetadataRO -> {
            // find the domain
            Optional<DBServiceGroupDomain> dbServiceGroupDomain = dbServiceGroup.getServiceGroupForDomain(serviceMetadataRO.getDomainCode());
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
        getDatabaseDao().persistFlushDetach(dbServiceGroup);
    }

    private void normalizeIdentifiers(ServiceGroupRO sgo){
        ParticipantIdentifierType pti = caseSensitivityNormalizer.normalizeParticipant(sgo.getParticipantScheme()+"::"+sgo.getParticipantIdentifier());
        sgo.setParticipantScheme(pti.getScheme());
        sgo.setParticipantIdentifier(pti.getValue());
        sgo.getServiceMetadata().forEach(smd->{
            DocumentIdentifier dit = caseSensitivityNormalizer.normalizeDocumentIdentifier(smd.getDocumentIdentifierScheme(), smd.getDocumentIdentifier());
            smd.setDocumentIdentifierScheme(dit.getScheme());
            smd.setDocumentIdentifier(dit.getValue());

        });

    }

    /**
     * Validate (if domains are added only once) and  create domain list for service group.
     *
     * @param serviceGroupRO
     * @param dbServiceGroup
     */
    protected void createDomainsForNewServiceGroup(ServiceGroupRO serviceGroupRO, DBServiceGroup dbServiceGroup) {
        // first update domains
        List<ServiceGroupDomainRO> serviceGroupDomainROList = validateDomainList(serviceGroupRO);
        // validate (if domains are added only once) and  create domain list for service group.
        serviceGroupDomainROList.forEach(dro -> {
            // everting ok  find domain and add it to service group
            Optional<DBDomain> dmn = domainDao.getDomainByCode(dro.getDomainCode());
            if (dmn.isPresent()) {
                dbServiceGroup.addDomain(dmn.get());
            } else {
                throw new SMPRuntimeException(DOMAIN_NOT_EXISTS, dro.getDomainCode());
            }
        });
    }


    /**
     * Method converts UI resource object entity to database entity and update changes  to database
     *
     * @param serviceGroupRO
     */
    protected void updateServiceGroup(ServiceGroupRO serviceGroupRO) {
        // normalize indentifiers
        normalizeIdentifiers(serviceGroupRO);
        // find and validate service group
        DBServiceGroup dbServiceGroup = findAndValidateServiceGroup(serviceGroupRO);

        // update users
        updateUsersOnServiceGroup(serviceGroupRO, dbServiceGroup);

        // update domain
        updateDomainsForServiceGroup(serviceGroupRO, dbServiceGroup);

        //update service metadata
        List<ServiceMetadataRO> serviceMetadataROList = serviceGroupRO.getServiceMetadata();
        serviceMetadataROList.forEach(serviceMetadataRO -> {
            Optional<DBServiceGroupDomain> optionalDbServiceGroupDomain = dbServiceGroup.getServiceGroupForDomain(serviceMetadataRO.getDomainCode());
            // remove service metadata
            if (serviceMetadataRO.getStatus() == EntityROStatus.REMOVE.getStatusNumber()) {
                // if the domain was not removed then remove only metadata
                if (optionalDbServiceGroupDomain.isPresent()) {
                    DBServiceGroupDomain dbServiceGroupDomain = optionalDbServiceGroupDomain.get();
                    // remove from domain
                    dbServiceGroupDomain.removeServiceMetadata(serviceMetadataRO.getDocumentIdentifier(),
                            serviceMetadataRO.getDocumentIdentifierScheme());
                }

            } else if (serviceMetadataRO.getStatus() == EntityROStatus.NEW.getStatusNumber()) {
                if (optionalDbServiceGroupDomain.isPresent()) {
                    optionalDbServiceGroupDomain.get().addServiceMetadata(createServiceMetadataFromRo(serviceMetadataRO));
                } else {
                    throw new SMPRuntimeException(SG_NOT_REGISTRED_FOR_DOMAIN, serviceMetadataRO.getDomainCode(),
                            serviceGroupRO.getParticipantIdentifier(), serviceGroupRO.getParticipantScheme());
                }
            } else if (serviceMetadataRO.getStatus() == EntityROStatus.UPDATED.getStatusNumber()) {
                if (optionalDbServiceGroupDomain.isPresent()) {
                    // get service metadata
                    byte[] buff = validateServiceMetadata(serviceMetadataRO);

                    DBServiceGroupDomain dbServiceGroupDomain = optionalDbServiceGroupDomain.get();
                    DBServiceMetadata dbServiceMetadata = dbServiceGroupDomain.getServiceMetadata(serviceMetadataRO.getDocumentIdentifier(),
                            serviceMetadataRO.getDocumentIdentifierScheme());


                    dbServiceMetadata.setXmlContent(buff);

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


        // persist it to databse
        getDatabaseDao().update(dbServiceGroup);
    }

    /**
     * Validate (if domains are added only once) and  update domain list for service group.
     *
     * @param serviceGroupRO
     * @param dbServiceGroup
     */
    protected void updateDomainsForServiceGroup(ServiceGroupRO serviceGroupRO, DBServiceGroup dbServiceGroup) {
        // / validate (if domains are added only once) and  create domain list for service group.
        List<ServiceGroupDomainRO> serviceGroupDomainROList = validateDomainList(serviceGroupRO);
        // copy array list of old domains and then put them back. Domain not added back will be deleted by hibernate
        // ...
        List<DBServiceGroupDomain> lstOldSGDomains = new ArrayList<>();
        lstOldSGDomains.addAll(dbServiceGroup.getServiceGroupDomains());
        dbServiceGroup.getServiceGroupDomains().clear();


        serviceGroupDomainROList.forEach(serviceGroupDomainRO -> {
            DBServiceGroupDomain dsg = getSGDomainFromList(lstOldSGDomains, serviceGroupDomainRO);
            if (dsg != null) {
                // put it back - no need to call addDomain
                dbServiceGroup.getServiceGroupDomains().add(dsg);
                // remove from list
                lstOldSGDomains.remove(dsg);
            } else {
                // everting ok  find domain and add it to service group
                Optional<DBDomain> dmn = domainDao.getDomainByCode(serviceGroupDomainRO.getDomainCode());
                if (dmn.isPresent()) {
                    dbServiceGroup.addDomain(dmn.get());
                } else {
                    throw new SMPRuntimeException(DOMAIN_NOT_EXISTS, serviceGroupDomainRO.getDomainCode());
                }
            }
        });
        // remove references
        lstOldSGDomains.forEach(dbServiceGroupDomain -> {
            dbServiceGroupDomain.setServiceGroup(null);
        });
    }

    /**
     * Method validates if domain list in consistent - code and sml subdomain are used only oncet
     *
     * @param serviceGroupRO
     * @return
     */
    protected List<ServiceGroupDomainRO> validateDomainList(ServiceGroupRO serviceGroupRO) {
        List<ServiceGroupDomainRO> serviceGroupDomainROList = serviceGroupRO.getServiceGroupDomains();
        // validate (if domains are added only once) and  create domain list for service group.
        serviceGroupDomainROList.forEach(dro -> {
            List<ServiceGroupDomainRO> result = serviceGroupDomainROList.stream()
                    .filter(domainToAdd -> Objects.equals(domainToAdd.getDomainCode(), dro.getDomainCode())
                            || Objects.equals(domainToAdd.getSmlSubdomain(), dro.getSmlSubdomain()))
                    .collect(Collectors.toList());
            if (result.size() != 1) {
                throw new SMPRuntimeException(DUPLICATE_DOMAIN_FOR_SG, serviceGroupRO.getParticipantIdentifier(),
                        serviceGroupRO.getParticipantScheme(), dro.getDomainCode(), dro.getSmlSubdomain());
            }
        });
        return serviceGroupDomainROList;
    }

    /**
     * Update users on service group. Method is OK for update and add new domain
     *
     * @param serviceGroupRO
     * @param dbServiceGroup
     */
    protected void updateUsersOnServiceGroup(ServiceGroupRO serviceGroupRO, DBServiceGroup dbServiceGroup) {
        // update users
        dbServiceGroup.getUsers().clear();
        List<UserRO> lstUsers = serviceGroupRO.getUsers();
        for (UserRO userRO : lstUsers) {
            DBUser du = userDao.find(userRO.getId());
            dbServiceGroup.getUsers().add(du);
        }
    }

    /**
     * Method retrieve servicegroup data from database and validates id and participant
     *
     * @param serviceGroupRO
     * @return
     */
    private DBServiceGroup findAndValidateServiceGroup(ServiceGroupRO serviceGroupRO) {
        // find and validate service group
        if (serviceGroupRO.getId() == null) {
            throw new SMPRuntimeException(MISSING_SG_ID, serviceGroupRO.getParticipantIdentifier(), serviceGroupRO.getParticipantScheme());
        }
        // validate service group id
        DBServiceGroup dbServiceGroup = getDatabaseDao().find(serviceGroupRO.getId());
        if (!Objects.equals(serviceGroupRO.getParticipantIdentifier(), dbServiceGroup.getParticipantIdentifier())
                || !Objects.equals(serviceGroupRO.getParticipantScheme(), dbServiceGroup.getParticipantScheme())) {
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
        byte[] buff;

        try {
            buff = serviceMetadataRO.getXmlContent().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SMPRuntimeException(INVALID_ENCODING, "UTF-8");
        }
        ServiceMetadata smd = ServiceMetadataConverter.unmarshal(buff);
        DocumentIdentifier di = caseSensitivityNormalizer.normalize(smd.getServiceInformation().getDocumentIdentifier());
        if (Objects.equals(di.getScheme(), serviceMetadataRO.getDocumentIdentifierScheme())
                && Objects.equals(di.getValue(), serviceMetadataRO.getDocumentIdentifier()))
        {
            return buff;
        } else {
            throw new SMPRuntimeException(IVALID_SMD_DOCUMENT_DATA, di.getValue(), di.getScheme(),
                    serviceMetadataRO.getDocumentIdentifier(), serviceMetadataRO.getDocumentIdentifierScheme());
        }
    }


    /**
     * Convert Database object to Rest object for UI. It does not set blobs - extensions and metadataservice xml!
     * They are retrieved to UI when needed.
     *
     * @param dbServiceGroup - database  entity
     * @return ServiceGroupRO
     */
    public ServiceGroupRO convertToRo(DBServiceGroup dbServiceGroup) {
        ServiceGroupRO serviceGroupRo = new ServiceGroupRO();
        serviceGroupRo.setId(dbServiceGroup.getId());
        serviceGroupRo.setParticipantIdentifier(dbServiceGroup.getParticipantIdentifier());
        serviceGroupRo.setParticipantScheme(dbServiceGroup.getParticipantScheme());
        // add domains
        dbServiceGroup.getServiceGroupDomains().forEach(sgd -> {
            ServiceGroupDomainRO servGrpDomain = new ServiceGroupDomainRO();
            servGrpDomain.setId(sgd.getId());
            servGrpDomain.setDomainId(sgd.getDomain().getId());
            servGrpDomain.setDomainCode(sgd.getDomain().getDomainCode());
            servGrpDomain.setSmlSubdomain(sgd.getDomain().getSmlSubdomain());
            // add service metadata to service group NOT TO service group domain
            // little different view from DB Model - all for the users :) ..
            sgd.getServiceMetadata().stream().map(this::convertServiceMetadataToRo)
                    .forEach(smdro -> {
                        smdro.setSmlSubdomain(servGrpDomain.getSmlSubdomain());
                        smdro.setDomainCode(servGrpDomain.getDomainCode());
                        smdro.setDomainId(servGrpDomain.getDomainId());
                        smdro.setServiceGroupDomainId(servGrpDomain.getId());
                        serviceGroupRo.getServiceMetadata().add(smdro);
                    });
            //also add domain to service group
            serviceGroupRo.getServiceGroupDomains().add(servGrpDomain);
        });
        // add users
        dbServiceGroup.getUsers().forEach(usr -> {
            UserRO userRO = new UserRO();
            userRO.setId(usr.getId());
            userRO.setUsername(usr.getUsername());
            userRO.setActive(usr.isActive());
            userRO.setEmail(usr.getEmail());
            userRO.setRole(usr.getRole());
            serviceGroupRo.getUsers().add(userRO);
        });
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
    private ServiceMetadataRO convertServiceMetadataToRo(DBServiceMetadata sgmd) {
        ServiceMetadataRO smdro = new ServiceMetadataRO();
        smdro.setId(sgmd.getId());
        smdro.setDocumentIdentifier(sgmd.getDocumentIdentifier());
        smdro.setDocumentIdentifierScheme(sgmd.getDocumentIdentifierScheme());
        return smdro;
    }

    /**
     * Create new database entity - service metadata from resource object
     *
     * @param serviceMetadataRO
     * @return new database entity DBServiceMetadata
     */
    private DBServiceMetadata createServiceMetadataFromRo(ServiceMetadataRO serviceMetadataRO) {

        byte[] buff = validateServiceMetadata(serviceMetadataRO);
        DBServiceMetadata dbServiceMetadata = new DBServiceMetadata();
        dbServiceMetadata.setDocumentIdentifier(serviceMetadataRO.getDocumentIdentifier());
        dbServiceMetadata.setDocumentIdentifierScheme(serviceMetadataRO.getDocumentIdentifierScheme());
        dbServiceMetadata.setXmlContent(buff);

        return dbServiceMetadata;
    }


    /**
     * for ServiceGroupDomainRO returns DBServiceGroupDomain  from ServiceGroup list of domain. ServiceGroup domain is matched by Id
     * and verified by domain id.
     *
     * @param lstSGDomains
     * @param domainRo
     * @return
     */
    private DBServiceGroupDomain getSGDomainFromList(List<DBServiceGroupDomain> lstSGDomains, ServiceGroupDomainRO domainRo) {
        for (DBServiceGroupDomain dbServiceGroupDomain : lstSGDomains) {
            if (Objects.equals(dbServiceGroupDomain.getId(), domainRo.getId())) {
                // double check for domain
                if (!Objects.equals(dbServiceGroupDomain.getDomain().getId(), domainRo.getDomainId())) {
                    throw new SMPRuntimeException(INVALID_REQEUST, "Domain mismatch!","Domain id for does not match for servicegroup domain");
                }
                return dbServiceGroupDomain;
            }
        }
        return null;
    }

    /**
     * Validate if extension is valid by schema.
     *
     * @param sgExtension
     * @return
     */
    public ServiceGroupExtensionRO validateExtension(ServiceGroupExtensionRO sgExtension) {
        if (sgExtension == null) {
            throw new SMPRuntimeException(INVALID_REQEUST, "Validate extension", "Missing Extension parameter");
        } else if (StringUtils.isBlank(sgExtension.getExtension())) {
            sgExtension.setErrorMessage("Empty extension");
        } else {
            try {
                byte[] buff = sgExtension.getExtension().getBytes("UTF-8");
                ExtensionConverter.validateExtensionBySchema(buff); // validate by schema
                sgExtension.setErrorMessage(null);
            } catch (XmlInvalidAgainstSchemaException e) {
                sgExtension.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
            } catch (UnsupportedEncodingException e) {
                sgExtension.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
            }
        }
        return sgExtension;
    }

    /**
     * Validate if extension is valid by schema.
     *
     * @param serviceGroupRO
     * @return
     */
    public byte[] validateExtension(ServiceGroupRO serviceGroupRO) {
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
    }

    /**
     * TODO format extension - add root element and format...
     *
     * @param sgExtension
     * @return
     */
    public ServiceGroupExtensionRO formatExtension(ServiceGroupExtensionRO sgExtension) {
        if (sgExtension == null) {
            throw new SMPRuntimeException(INVALID_REQEUST, "Format extension", "Missing Extension parameter");
        } else if (StringUtils.isBlank(sgExtension.getExtension())) {
            sgExtension.setErrorMessage("Empty extension");
        } else {
            try {
                Source xmlInput = new StreamSource(new StringReader(sgExtension.getExtension()));
                StringWriter stringWriter = new StringWriter();
                StreamResult xmlOutput = new StreamResult(stringWriter);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setAttribute("indent-number", 4);
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(xmlInput, xmlOutput);
                sgExtension.setExtension(xmlOutput.getWriter().toString());
            } catch (TransformerConfigurationException e) {
                sgExtension.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
            } catch (TransformerException e) {
                sgExtension.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
            }
        }
        return sgExtension;
    }

    @Transactional
    public  ServiceMetadataRO getServiceMetadata(Long serviceMetadataId){

    }

}
