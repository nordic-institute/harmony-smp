package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.conversion.ExtensionConverter;
import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroupDomain;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ui.filters.ServiceGroupFilter;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.DOMAIN_NOT_EXISTS;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_ENCODING;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_REQEUST;

@Service
public class UIServiceGroupService extends UIServiceBase<DBServiceGroup, ServiceGroupRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIServiceGroupService.class);

    @Autowired
    DomainDao domainDao;

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
                                                      String sortOrder, ServiceGroupFilter filter, String domainCode) {

        DBDomain d  = null;
        if (!StringUtils.isBlank(domainCode)){
            Optional<DBDomain> od = domainDao.getDomainByCode(domainCode);
            if (od.isPresent()){
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
                serviceGroupRo.setIndex(iStartIndex++);
                lstRo.add(serviceGroupRo);
            }
            sg.getServiceEntities().addAll(lstRo);
        }
        return sg;
    }

    /**
     * Convert Database object to Rest object for UI
     * @param dbServiceGroup - database  entity
     * @return ServiceGroupRO
     */
    public ServiceGroupRO convertToRo(DBServiceGroup dbServiceGroup) {
        ServiceGroupRO serviceGroupRo = new ServiceGroupRO();
        serviceGroupRo.setId(dbServiceGroup.getId());
        serviceGroupRo.setParticipantIdentifier(dbServiceGroup.getParticipantIdentifier());
        serviceGroupRo.setParticipantScheme(dbServiceGroup.getParticipantScheme());
        dbServiceGroup.getServiceGroupDomains().forEach(sgd -> {
            DomainRO dmn = new DomainRO();
            dmn.setId(sgd.getDomain().getId());
            dmn.setDomainCode(sgd.getDomain().getDomainCode());
            dmn.setSmlSubdomain(sgd.getDomain().getSmlSubdomain());
            serviceGroupRo.getDomains().add(dmn);

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
    public ServiceGroupRO getServiceGroupById(Long serviceGroupId) {
        DBServiceGroup dbServiceGroup = getDatabaseDao().find(serviceGroupId);
        return  convertToRo(dbServiceGroup);
    }

    @Transactional
    public ServiceGroupExtensionRO getServiceGroupExtensionById(Long serviceGroupId) {
        ServiceGroupExtensionRO ex = new ServiceGroupExtensionRO();
        DBServiceGroup dbServiceGroup = getDatabaseDao().find(serviceGroupId);
        ex.setServiceGroupId(dbServiceGroup.getId());
        if (dbServiceGroup.getExtension()!=null) {
            ex.setExtension(new String(dbServiceGroup.getExtension()));
        }
        return ex;
    }

    @Transactional
    public void updateServiceGroupList(List<ServiceGroupRO> lst) {
        boolean suc = false;
        for (ServiceGroupRO dRo: lst){


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
     *  Method converts UI resource object entity to database entity and persists it to database
     * @param dRo
     */
    private void addNewServiceGroup(ServiceGroupRO dRo){
        DBServiceGroup dDb = convertFromRo(dRo);
        for (UserRO userRO: dRo.getUsers()) {
            DBUser du = userDao.find(userRO.getId());
            dDb.getUsers().add(du);
        }
        for (DomainRO domainRO: dRo.getDomains()) {
            DBDomain dmn = domainDao.find(domainRO.getId());
            DBServiceGroupDomain dsgdomain = new DBServiceGroupDomain();
            dsgdomain.setDomain(dmn);
            dDb.getServiceGroupDomains().add(dsgdomain);
        }
        if (dRo.getExtension()!=null){
            try {
                dDb.setExtension(dRo.getExtension().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new SMPRuntimeException(INVALID_ENCODING, "UTF-8");
            }
        }
        getDatabaseDao().persistFlushDetach(dDb);
    }

    /**
     *  Method converts UI resource object entity to database entity and update changes  to database
     * @param dRo
     */
    private void updateServiceGroup(ServiceGroupRO dRo){
        DBServiceGroup upd = getDatabaseDao().find(dRo.getId());
        upd.getUsers().clear();
        // update users
        List<UserRO> lstUsers = dRo.getUsers();
        for (UserRO userRO: lstUsers) {
            System.out.println("GET USER ID: " + userRO.getId());
            DBUser du = userDao.find(userRO.getId());
            upd.getUsers().add(du);
        }/*
        // get domains
        List<DBServiceGroupDomain> lstDomains = dRo.getDomains();
        for (DBDomain domainRO: dRo.getDomains()) {
            System.out.println("GET USER ID: " + userRO.getId());
            DBUser du = userDao.find(userRO.getId());
            upd.getUsers().add(du);
        }*/

        // and domain
        getDatabaseDao().update(upd);
    }


    public ServiceGroupExtensionRO validateExtension(ServiceGroupExtensionRO sgExtension){
        if (sgExtension==null) {
            throw new SMPRuntimeException(INVALID_REQEUST, "Validate extension", "Missing Extension parameter");
        }
        else if (StringUtils.isBlank(sgExtension.getExtension()) ){
            sgExtension.setErrorMessage("Empty extension");
        }
        else {
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
     * TODO format extension - add root element and format...
     * @param sgExtension
     * @return
     */
    public ServiceGroupExtensionRO formatExtension(ServiceGroupExtensionRO sgExtension) {
        if (sgExtension==null) {
            throw new SMPRuntimeException(INVALID_REQEUST, "Format extension", "Missing Extension parameter");
        }
        else if (StringUtils.isBlank(sgExtension.getExtension()) ){
            sgExtension.setErrorMessage("Empty extension");
        }
        else {
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

}
