package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.data.dao.BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.SubresourceDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceMetadataValidationRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.nio.charset.IllegalCharsetNameException;
import java.security.cert.CertificateException;

/**
 * Services for managing the Service metadata
 */
@Service
public class UIServiceMetadataService extends UIServiceBase<DBSubresource, ServiceMetadataRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIServiceMetadataService.class);

    protected final DomainDao domainDao;
    protected final SubresourceDao serviceMetadataDao;
    protected final UserDao userDao;
    protected final IdentifierService caseSensitivityNormalizer;
    protected final ConfigurationService configurationService;


    public UIServiceMetadataService(DomainDao domainDao,
                                    SubresourceDao serviceMetadataDao,
                                    UserDao userDao,
                                    IdentifierService caseSensitivityNormalizer,
                                    ConfigurationService configurationService) {
        this.domainDao = domainDao;
        this.serviceMetadataDao = serviceMetadataDao;
        this.userDao = userDao;
        this.caseSensitivityNormalizer = caseSensitivityNormalizer;
        this.configurationService = configurationService;

    }

    @Override
    protected BaseDao<DBSubresource> getDatabaseDao() {
        return serviceMetadataDao;
    }

    @Transactional
    public ServiceMetadataRO getServiceMetadataXMLById(Long serviceMetadataId) {
        LOG.debug("Get service metadata: {}", serviceMetadataId);
        DBSubresource dbSubresource = serviceMetadataDao.find(serviceMetadataId);
        ServiceMetadataRO serviceMetadataRO = new ServiceMetadataRO();

        serviceMetadataRO.setId(dbSubresource.getId());
        serviceMetadataRO.setDocumentIdentifier(dbSubresource.getIdentifierValue());
        serviceMetadataRO.setDocumentIdentifierScheme(dbSubresource.getIdentifierScheme());

        return serviceMetadataRO;
    }

    private String getConvertServiceMetadataToString(Long id, byte[] extension) {
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
      /*  byte[] buff;
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


            Identifier headerDI = caseSensitivityNormalizer.normalizeDocument(
                    serviceMetadataRO.getDocumentIdentifierScheme(),
                    serviceMetadataRO.getDocumentIdentifier());
            Identifier headerPI = caseSensitivityNormalizer.normalizeParticipant(
                    serviceMetadataRO.getParticipantScheme(),
                    serviceMetadataRO.getParticipantIdentifier());


            // validate by schema
            try {
                BdxSmpOasisValidator.validateXSD(buff);
            } catch (XmlInvalidAgainstSchemaException e) {
                serviceMetadataRO.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
                return serviceMetadataRO;
            }

       */
/* TODO
            // validate data
            ServiceMetadata smd = ServiceMetadataConverter.unmarshal(buff);
            if (smd.getRedirect() != null) {
                if (StringUtils.isBlank(smd.getRedirect().getHref())) {
                    serviceMetadataRO.setErrorMessage("Redirect URL must must be empty!");
                    return serviceMetadataRO;
                }
            }

            if (smd.getServiceInformation() != null) {
                Identifier xmlDI = caseSensitivityNormalizer.normalizeDocument(smd.getServiceInformation().getDocumentIdentifier());
                ParticipantIdentifierType xmlPI = caseSensitivityNormalizer.normalizeParticipant(smd.getServiceInformation().getParticipantIdentifier());
                if (!xmlDI.equals(headerDI)) {
                    serviceMetadataRO.setErrorMessage("Document identifier and scheme do not match!");
                    return serviceMetadataRO;
                }

                if (!xmlPI.equals(headerPI)) {
                    serviceMetadataRO.setErrorMessage("Participant identifier and scheme do not match!");
                    return serviceMetadataRO;
                }
            }

            if (serviceMetadataRO.getStatusAction() == EntityROStatus.NEW.getStatusNumber()) {
                // check if service metadata already exists
                Optional<DBSubresource> exists = serviceMetadataDao.findServiceMetadata(headerPI.getValue(), headerPI.getScheme(),
                        headerDI.getValue(), headerDI.getScheme());
                if (exists.isPresent()) {
                    serviceMetadataRO.setErrorMessage("Document identifier and scheme already exist in database!");
                    return serviceMetadataRO;
                }
            }
            try {
                validateServiceMetadataCertificates(smd);
            } catch (CertificateException e) {
                serviceMetadataRO.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
                return serviceMetadataRO;
            }
            }
*/

        //return serviceMetadataRO;
        return null;
    }

    /**
     * Method validates certificates in all endpoints.
     *
     * @param smd ServiceMetadata document
     * @throws CertificateException exception if certificate is not valid or the allowed key type

    public void validateServiceMetadataCertificates(ServiceMetadata smd) throws CertificateException {
    List<EndpointType> endpointTypeList = searchAllEndpoints(smd);
    for (EndpointType endpointType : endpointTypeList) {
    validateCertificate(endpointType.getCertificate());
    }


    }
     */
    /**
     * Method returns all EndpointTypes
     *
     * @param smd
     * @return public List<EndpointType> searchAllEndpoints(ServiceMetadata smd) {
    List<ProcessType> processTypeList = smd.getServiceInformation() != null ?
    smd.getServiceInformation().getProcessList().getProcesses() : Collections.emptyList();

    List<EndpointType> endpointTypeList = new ArrayList<>();
    processTypeList.stream().forEach(processType -> endpointTypeList.addAll(processType.getServiceEndpointList() != null ?
    processType.getServiceEndpointList().getEndpoints() : Collections.emptyList()));

    return endpointTypeList;
    }
     */
    /**
     * Validate the certificate
     *
     * @param crtData x509 encoded byte array
     * @throws CertificateException

    public void validateCertificate(byte[] crtData) throws CertificateException {
    if (crtData == null || crtData.length == 0) {
    LOG.debug("Skip certificate validation: Empty certificate.");
    return;
    }
    X509Certificate cert = X509CertificateUtils.getX509Certificate(crtData);
    // validate is certificate is valid
    cert.checkValidity();
    // validate if certificate has the right key algorithm
    PublicKey key = cert.getPublicKey();
    List<String> allowedKeyAlgs = configurationService.getAllowedDocumentCertificateTypes();
    if (allowedKeyAlgs == null || allowedKeyAlgs.isEmpty()) {
    LOG.debug("Ignore the service metadata certificate key type validation (Empty property: [{}]).", DOCUMENT_RESTRICTION_CERT_TYPES.getProperty());
    return;
    }

    if (StringUtils.equalsAnyIgnoreCase(key.getAlgorithm(), allowedKeyAlgs.toArray(new String[]{}))) {
    LOG.debug("Certificate has valid key algorithm [{}]. Allowed algorithms: [{}] .", key.getAlgorithm(), allowedKeyAlgs);
    return;
    }
    LOG.debug("Certificate has invalid key algorithm [{}]. Allowed algorithms: [{}] .", key.getAlgorithm(), allowedKeyAlgs);
    throw new CertificateException("Certificate does not have allowed key type!");
    } */
}
