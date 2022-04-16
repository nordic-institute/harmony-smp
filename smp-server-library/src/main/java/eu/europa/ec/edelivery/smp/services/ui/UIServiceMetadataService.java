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
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_REQUEST;

/**
 * Serives for managing the Service metadata
 */
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
        LOG.debug("Get service metadata: {}", serviceMetadataId);
        DBServiceMetadata dbServiceMetadata = serviceMetadataDao.find(serviceMetadataId);
        ServiceMetadataRO serviceMetadataRO = new ServiceMetadataRO();

        serviceMetadataRO.setId(dbServiceMetadata.getId());
        serviceMetadataRO.setDocumentIdentifier(dbServiceMetadata.getDocumentIdentifier());
        serviceMetadataRO.setDocumentIdentifierScheme(dbServiceMetadata.getDocumentIdentifierScheme());
        serviceMetadataRO.setXmlContent(getConvertServiceMetadataToString( serviceMetadataId, dbServiceMetadata.getXmlContent()));
        return serviceMetadataRO;
    }

    private String getConvertServiceMetadataToString(Long id,  byte[] extension){
        try {
            return new String(extension, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Can not convert ServiceMetadata bytearray to 'UTF-8'", e);
            throw new IllegalCharsetNameException("UTF-8");
        }

    }

    /**
     * Check if service metadata parsers and if data match servicemetadata and service group...
     *
     * @param serviceMetadataRO
     * @return
     */

    public ServiceMetadataValidationRO validateServiceMetadata(ServiceMetadataValidationRO serviceMetadataRO) {
        byte[] buff;
        if (serviceMetadataRO == null) {
            throw new SMPRuntimeException(INVALID_REQUEST, "Validate service metadata", "Missing servicemetadata parameter");
        } else if (StringUtils.isBlank(serviceMetadataRO.getXmlContent())) {
            serviceMetadataRO.setErrorMessage("Service metadata xml must not be empty");
        } else {


            // validate xml  - first byte-array is expected to be in utf8 format
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


            if (serviceMetadataRO.getStatusAction() == EntityROStatus.NEW.getStatusNumber()){
                // check if service metadata already exists
                Optional<DBServiceMetadata> exists = serviceMetadataDao.findServiceMetadata(headerPI.getValue(), headerPI.getScheme(),
                        headerDI.getValue(), headerDI.getScheme());
                if (exists.isPresent()){
                    serviceMetadataRO.setErrorMessage("Document identifier and scheme already exist in database!");
                    return serviceMetadataRO;
                }
            }

            if (!xmlDI.equals(headerDI)) {
                serviceMetadataRO.setErrorMessage("Document identifier and scheme do not match!");
                return serviceMetadataRO;
            }

            if (!xmlPI.equals(headerPI)) {
                serviceMetadataRO.setErrorMessage("Participant identifier and scheme do not match!");
                return serviceMetadataRO;
            }
        }

        return serviceMetadataRO;
    }

}
