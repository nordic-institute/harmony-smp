package eu.europa.ec.edelivery.smp.services;


import eu.europa.ec.dynamicdiscovery.model.identifiers.types.EBCorePartyIdFormatterType;
import eu.europa.ec.edelivery.smp.config.enums.SMPDomainPropertyEnum;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyTypeEnum;
import eu.europa.ec.edelivery.smp.data.dao.DomainConfigurationDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainConfiguration;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.identifiers.IdentifierFormatter;
import eu.europa.ec.edelivery.smp.utils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring bean  provides Identifier formatters for the domain.
 * @since 5.1
 */
@Component
public class IdentifierFormatterService {

    public static final String CACHE_NAME_DOMAIN_RESOURCE_IDENTIFIER_FORMATTER = "domain-resource-identifier-formatter";
    public static final String CACHE_NAME_DOMAIN_SUBRESOURCE_IDENTIFIER_FORMATTER = "domain-subresource-identifier-formatter";

    private static final Logger LOG = LoggerFactory.getLogger(IdentifierFormatterService.class);

    private final DomainDao domainDao;
    private final DomainConfigurationDao domainConfigurationDao;
    private final ConfigurationService configurationService;

    public IdentifierFormatterService(DomainDao domainDao,
                                      DomainConfigurationDao domainConfigurationDao,
                                      ConfigurationService configurationService) {
        this.domainDao = domainDao;
        this.domainConfigurationDao = domainConfigurationDao;
        this.configurationService = configurationService;
    }

    /**
     * Method returns participant identifier formatter for given domain
     * @param domainCode domain code to get IdentifierFormatter
     */
    @Cacheable(CACHE_NAME_DOMAIN_RESOURCE_IDENTIFIER_FORMATTER)
    public IdentifierFormatter getResourceIdentifierFormatter(String domainCode) {

        if (StringUtils.isBlank(domainCode)) {
            throw new SMPRuntimeException(ErrorCode.DOMAIN_NOT_EXISTS,  domainCode);
        }
        DBDomain domain = domainDao.getDomainByCode(domainCode)
                .orElseThrow(() -> new SMPRuntimeException(ErrorCode.DOMAIN_NOT_EXISTS,  domainCode));

        List<DBDomainConfiguration> listDomainConf =  domainConfigurationDao.getDomainConfiguration(domain);
        IdentifierFormatter identifierFormatter = IdentifierFormatter.Builder
                .create()
                .addFormatterTypes(new EBCorePartyIdFormatterType())
                .build();

        identifierFormatter.setCaseSensitiveSchemas(getDomainConfigurationValue(listDomainConf, SMPDomainPropertyEnum.RESOURCE_CASE_SENSITIVE_SCHEMES));
        identifierFormatter.setSchemeMandatory(getDomainConfigurationValue(listDomainConf, SMPDomainPropertyEnum.RESOURCE_SCH_MANDATORY));
        identifierFormatter.setSchemeValidationPattern(getDomainConfigurationValue(listDomainConf, SMPDomainPropertyEnum.RESOURCE_SCH_VALIDATION_REGEXP));

        return identifierFormatter;
    }

    /**
     * Method returns participant identifier formatter for given domain
     * @param domain
     */
    /**
     * Method returns participant identifier formatter for given domain
     * @param domainCode domain code to get IdentifierFormatter
     */
    @Cacheable(CACHE_NAME_DOMAIN_SUBRESOURCE_IDENTIFIER_FORMATTER)
    public IdentifierFormatter getSubresourceIdentifierFormatter(String domainCode) {

        if (StringUtils.isBlank(domainCode)) {
            throw new SMPRuntimeException(ErrorCode.DOMAIN_NOT_EXISTS,  domainCode);
        }
        DBDomain domain = domainDao.getDomainByCode(domainCode)
                .orElseThrow(() -> new SMPRuntimeException(ErrorCode.DOMAIN_NOT_EXISTS,  domainCode));
        List<DBDomainConfiguration> listDomainConf =  domainConfigurationDao.getDomainConfiguration(domain);
        IdentifierFormatter identifierFormatter = IdentifierFormatter.Builder
                .create()
                .build();

        identifierFormatter.setCaseSensitiveSchemas(getDomainConfigurationValue(listDomainConf,
                SMPDomainPropertyEnum.SUBRESOURCE_CASE_SENSITIVE_SCHEMES));
        return identifierFormatter;
    }

    /**
     * Method returns parsed value for  property on given domain. If property is not found or use system default,
     * system default value is returned.
     * @param domain domain to get configuration value
     * @param property domain property type
     * @return parsed value for property
     * @param <T> type of returned value
     */
    public <T> T getDomainConfigurationValue(List<DBDomainConfiguration> domain, SMPDomainPropertyEnum property) {

        DBDomainConfiguration domainConfiguration = domain.stream()
                .filter(dc -> dc.getProperty().equals(property.getProperty()))
                .findFirst()
                .orElse(null);

        if (domainConfiguration == null || domainConfiguration.isUseSystemDefault()) {
            LOG.debug("Domain configuration value for property [{}] not found or use system default. Using system default value!", property);
            return configurationService.getDefaultDomainConfigurationValue(property);
        }

        String value = domainConfiguration.getValue();
        SMPPropertyEnum sysPropType = getSmpPropertyEnum(property);
        return (T) PropertyUtils.parseProperty(sysPropType, value, null);

    }

    private static SMPPropertyEnum getSmpPropertyEnum(SMPDomainPropertyEnum property) {
        SMPPropertyEnum sysPropType = property.getPropertyEnum();
        if (sysPropType.isEncrypted()) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Encrypted domain Properties are not supported!. Can not parse   ["
                    + property + "]!");
        }
        if (sysPropType.getPropertyType()  == SMPPropertyTypeEnum.PATH ||
                sysPropType.getPropertyType() == SMPPropertyTypeEnum.FILENAME) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "Path or filename domain properties are not supported!. Can not parse   ["
                    + property + "]!");
        }
        return sysPropType;
    }
}
