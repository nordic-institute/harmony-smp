package eu.europa.ec.edelivery.smp.services.resource;

import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_DOMAIN_CODE;


/**
 * The class resolves the domain header or  given path segment  sequence
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Service
public class DomainResolverService {

    /**
     * Domain pattern as defined in documentation since SMP 3.0.0
     */
    public static final Pattern DOMAIN_ID_PATTERN = Pattern.compile("[a-zA-Z0-9]{1,50}");
    final DomainDao domainDao;
    final ConfigurationService configurationService;

    public DomainResolverService(DomainDao domainDao, ConfigurationService configurationService) {
        this.domainDao = domainDao;
        this.configurationService = configurationService;
    }

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainResolverService.class);

    /**
     * DomiSMP resolves the domain in the following order.
     * <ol>
     * <li>If only one domain is registered, it sets it by default (legacy).</li>
     * <li>The next attempt is to determine it via HTTP Header "domain." If the header is set with the invalid "domain code," it throws the error.</li>
     * <li>The next attempt is with the first path parameter (must be at least two path parameters).</li>
     * <li>The next attempt is to set the default domain configured in DomiSMP configuration properties.</li>
     * <li>If the default domain is not set, it uses the first registered domain to the DomiSMP.</li>
     * <li>Throws Resource not found error</li>
     * </ol>
     * <p>
     * NOTE: To allow the domain path parameter and the HTTP header to be used together, the first path parameter is skipped if it matches a resolved domain with an HTTP parameter or "single domain condition" and if there are more than two path parameters.
     * <p/>
     *
     * @param headerParameter the http header
     * @return true if path parameter  matched the domain code and the resolving should continue with the next parameter
     */
    public DBDomain resolveDomain(String headerParameter, String pathParameter) {
        LOG.info("Resolve domain for HTTP header [{}] and path parameter [{}]", headerParameter, pathParameter);


        // get single domain
        Optional<DBDomain> optDomain = domainDao.getTheOnlyDomain();
        if (optDomain.isPresent()) {
            LOG.debug("Only one domain is registered to DomiSmp [{}]", optDomain.get().getDomainCode());
            return optDomain.get();
        }
        // get
        if (StringUtils.isNotBlank(headerParameter)) {
            optDomain = validatedAndReturnDomainByCode(headerParameter);
            if (optDomain.isPresent()) {
                LOG.debug("Located domain by the http header [{}]", headerParameter);
                return optDomain.get();
            } else {
                throw new SMPRuntimeException(ErrorCode.DOMAIN_NOT_EXISTS, headerParameter);
            }
        }

        optDomain = domainDao.getDomainByCode(pathParameter);
        if (optDomain.isPresent()) {
            LOG.debug("Located domain by the path parameter header [{}]", pathParameter);
            return optDomain.get();
        }

        String domainCode = configurationService.getDefaultDomainCode();
        optDomain = domainDao.getDomainByCode(domainCode);
        if (optDomain.isPresent()) {
            LOG.debug("Located domain by DomiSMP configuration [{}] value [{}]", SMPPropertyEnum.DEFAULT_DOMAIN.getProperty(), domainCode);
            return optDomain.get();
        }
        optDomain = domainDao.getFirstDomain();
        if (optDomain.isPresent()) {
            DBDomain domain = optDomain.get();
            LOG.info("Can not locate the domain, user the registered domain [{}]", domain.getDomainCode());
            return domain;
        }
        throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "No domain is configured for the DomiSMP instance!");
    }

    public Optional<DBDomain> validatedAndReturnDomainByCode(final String domain) {

        // else test if domain is ok.
        if (!DOMAIN_ID_PATTERN.matcher(domain).matches()) {
            throw new SMPRuntimeException(INVALID_DOMAIN_CODE, domain, DOMAIN_ID_PATTERN);
        }
        // get domain by code
        return domainDao.getDomainByCode(domain);
    }
}
