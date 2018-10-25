package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.edelivery.smp.conversion.ServiceMetadataConverter;
import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceMetadataDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataValidationRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

@Service
public class UIServiceMetadataService extends UIServiceBase<DBServiceMetadata, ServiceMetadataRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIServiceMetadataService.class);

    @Autowired
    DomainDao domainDao;

    @Autowired
    ServiceMetadataDao serviceMetadataDao;


    @Autowired
    UserDao userDao;

    @Autowired
    private CaseSensitivityNormalizer caseSensitivityNormalizer;


    @Override
    protected BaseDao<DBServiceMetadata> getDatabaseDao() {
        return serviceMetadataDao;
    }

    @Transactional
    public ServiceMetadataRO getServiceMetadataXMLById(Long serviceMetadataId) {
        DBServiceMetadata dbServiceMetadata = serviceMetadataDao.find(serviceMetadataId);
        ServiceMetadataRO serviceMetadataRO = new ServiceMetadataRO();

        serviceMetadataRO.setId(dbServiceMetadata.getId());
        serviceMetadataRO.setDocumentIdentifier(dbServiceMetadata.getDocumentIdentifier());
        serviceMetadataRO.setDocumentIdentifierScheme(dbServiceMetadata.getDocumentIdentifierScheme());
        serviceMetadataRO.setXmlContent(new String(dbServiceMetadata.getXmlContent()));
        return serviceMetadataRO;
    }

    /**
     * Check if service metadata parsers and if data match servicemetadata and service group...
     *
     * @param serviceMetadataRO
     * @return
     */

    public ServiceMetadataValidationRO validateServiceMetadata(ServiceMetadataValidationRO serviceMetadataRO) {
        byte[] buff;

        // convert to utf-8 byte array
        try {
            buff = serviceMetadataRO.getXmlContent().getBytes("UTF-8");
            serviceMetadataRO.setXmlContent(""); // no need to return back schema
        } catch (UnsupportedEncodingException e) {
            serviceMetadataRO.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
            serviceMetadataRO.setXmlContent(""); // no need to return back schema
            return serviceMetadataRO;
        }

        // validate by schema
        try {
            BdxSmpOasisValidator.validateXSD(buff);
        } catch (XmlInvalidAgainstSchemaException e) {
            serviceMetadataRO.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
            return serviceMetadataRO;
        }

        // validate data
        ServiceMetadata smd = ServiceMetadataConverter.unmarshal(buff);
        DocumentIdentifier xmlDI = caseSensitivityNormalizer.normalize(smd.getServiceInformation().getDocumentIdentifier());
        DocumentIdentifier headerDI = caseSensitivityNormalizer.normalizeDocumentIdentifier(serviceMetadataRO.getDocumentIdentifierScheme(),
                serviceMetadataRO.getDocumentIdentifier());
        ParticipantIdentifierType xmlPI = caseSensitivityNormalizer.normalize(smd.getServiceInformation().getParticipantIdentifier());
        ParticipantIdentifierType headerPI = caseSensitivityNormalizer.normalizeParticipantIdentifier(
                serviceMetadataRO.getParticipantScheme(),
                serviceMetadataRO.getParticipantIdentifier());

        if (!xmlDI.equals(headerDI)) {
            serviceMetadataRO.setErrorMessage("Document identifier and schema do not match!");
            return serviceMetadataRO;
        }

        if (!xmlPI.equals(headerPI)) {
            serviceMetadataRO.setErrorMessage("Participant identifier and schema do not match!");
            return serviceMetadataRO;
        }

        return serviceMetadataRO;
    }

}
