package eu.europa.ec.edelivery.smp.services;


import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.CONFIGURATION_ERROR;
import static eu.europa.ec.edelivery.smp.logging.SMPMessageCode.*;


/**
 * Service for domain
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Service
public class SMLIntegrationService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMLIntegrationService.class);
    private static final String ERROR_MESSAGE_DNS_NOT_ENABLED = "SML integration is not enabled!";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SmlConnector smlConnector;

    @Autowired
    private DomainDao domainDao;

    @Autowired
    private IdentifierService identifierService;


    /**
     * Method in transaction update domain status and registers domain to SML.
     * If registration fails  - transaction is rolled back
     *
     * @param domain
     */
    @Transactional
    public void registerDomain(DBDomain domain) {
        if (!isSMLIntegrationEnabled()) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, ERROR_MESSAGE_DNS_NOT_ENABLED);
        }
        domain.setSmlRegistered(true);
        domainDao.update(domain);
        smlConnector.registerDomain(domain);
    }


    /**
     * Method in transaction update domain status and registers domain to SML.
     * If registration fails  - transaction is rolled back
     *
     * @param domain
     */
    @Transactional
    public void unRegisterDomain(DBDomain domain) {
        if (!isSMLIntegrationEnabled()) {
            throw new SMPRuntimeException(CONFIGURATION_ERROR, ERROR_MESSAGE_DNS_NOT_ENABLED);
        }

        domain.setSmlRegistered(false);
        domainDao.update(domain);
        smlConnector.unregisterDomain(domain);
    }


    /**
     * Method in transaction update resource status and registers it to SML.
     * If registration fails  - transaction is rolled back
     *
     * @param resource
     * @param domain
     */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerParticipant(DBResource resource, DBDomain domain) {

        LOG.businessDebug(BUS_SML_REGISTER_SERVICE_GROUP, resource.getIdentifierValue(), resource.getIdentifierScheme(), domain.getDomainCode());
        if (!isSMLIntegrationEnabled()) {
            String msg = "SML integration is not enabled!";
            LOG.businessWarn(BUS_SML_REGISTER_SERVICE_GROUP_FAILED, resource.getIdentifierValue(), resource.getIdentifierScheme(), domain.getDomainCode(), msg);
        }
        Identifier normalizedParticipantId = identifierService
                .normalizeParticipant(resource.getIdentifierScheme(), resource.getIdentifierValue());
        // register only not registered services
        if (!resource.isSmlRegistered()) {
            // update value
            resource.setSmlRegistered(true);
            String customNaptrService = getNaptrServiceForResource(resource);
            smlConnector.registerInDns(normalizedParticipantId, domain, customNaptrService);
            LOG.businessDebug(BUS_SML_REGISTER_SERVICE_GROUP, resource.getIdentifierValue(), resource.getIdentifierScheme(), domain.getDomainCode());
        } else {
            LOG.businessWarn(BUS_SML_REGISTER_SERVICE_GROUP_ALREADY_REGISTERED, resource.getIdentifierValue(), resource.getIdentifierScheme(), domain.getDomainCode());
        }
    }

    public String getNaptrServiceForResource(DBResource resource){
        LOG.info("Get naptr service for resource: [{}]", resource );
        if (resource == null
                || resource.getDomainResourceDef() == null
                || resource.getDomainResourceDef().getResourceDef() == null
                || StringUtils.isBlank(resource.getDomainResourceDef().getResourceDef().getIdentifier())){
            LOG.info("return null naptr service for resource: [{}]", resource );
            return null;
        }
        String resDefIdentifier = resource.getDomainResourceDef().getResourceDef().getIdentifier();
        LOG.info("return null naptr service for resource: [{}] and document type [{}]", resource, resDefIdentifier );
        Map<String, String> map = configurationService.getCustomNaptrServicesMap();

        if (map!=null &&  map.containsKey(resDefIdentifier) ) {
            return map.get(resDefIdentifier);
        }
        LOG.info("return null because configuration does not have document type and document type [{}]", resource, resDefIdentifier );
        return null;

    }

    /**
     * Method in transaction update resource status and unregisters participant to SML.
     * <p>
     * If registration fails  - transaction is rolled back
     *
     * @param resource
     * @param domain
     */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void unregisterParticipant(DBResource resource, DBDomain domain) {
        LOG.businessDebug(BUS_SML_UNREGISTER_SERVICE_GROUP, resource.getIdentifierValue(), resource.getIdentifierScheme(), domain.getDomainCode());
        if (!isSMLIntegrationEnabled()) {
            String msg = "SML integration is not enabled!";
            LOG.businessWarn(BUS_SML_UNREGISTER_SERVICE_GROUP_FAILED, resource.getIdentifierValue(), resource.getIdentifierScheme(), domain.getDomainCode(), msg);
        }

        // unregister only  registered participants
        if (resource.isSmlRegistered()) {
            // update value
            resource.setSmlRegistered(false);
            unregisterParticipantFromSML(resource, domain);
            LOG.businessDebug(BUS_SML_UNREGISTER_SERVICE_GROUP, resource.getIdentifierValue(), resource.getIdentifierScheme(), domain.getDomainCode());
        } else {
            LOG.businessWarn(BUS_SML_UNREGISTER_SERVICE_GROUP_ALREADY_REGISTERED, resource.getIdentifierValue(), resource.getIdentifierScheme(), domain.getDomainCode());
        }
    }

    /**
     * Method unregisters participant from SML. It does not check if Participant is in database or of is unregistered
     *
     * @param resource - Participant
     * @param domain   - unregister to domain
     */

    public boolean unregisterParticipantFromSML(DBResource resource, DBDomain domain) {
        LOG.businessDebug(BUS_SML_UNREGISTER_SERVICE_GROUP, resource.getIdentifierValue(), resource.getIdentifierScheme(), domain.getDomainCode());

        Identifier normalizedParticipantId = identifierService
                .normalizeParticipant(resource.getIdentifierScheme(), resource.getIdentifierValue());

        // unregister only registered participants
        return smlConnector.unregisterFromDns(normalizedParticipantId, domain);

    }
    public boolean isSMLIntegrationEnabled() {
        return configurationService.isSMLIntegrationEnabled();
    }
}
