package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.config.enums.SMPDomainPropertyEnum;
import eu.europa.ec.edelivery.smp.data.model.DBDomainConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.DomainPropertyRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts a {@link DBDomainConfiguration} to a {@link DomainPropertyRO}.
 *
 * @author Joze Rihtars
 * @since 5.1
 */
@Component
public class DomainPropEnumToDomainPropROConverter implements Converter<SMPDomainPropertyEnum, DomainPropertyRO> {

    protected static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainPropEnumToDomainPropROConverter.class);
    private final ConfigurationService configurationService;

    public DomainPropEnumToDomainPropROConverter(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Create DomainPropertyRO with system default value for given SMPDomainPropertyEnum.
     * Note: The method does not update the database value.
     *
     * @param property - property to create
     * @return DomainPropertyRO with system default value
     */
    @Override
    public DomainPropertyRO convert(SMPDomainPropertyEnum property) {

        DomainPropertyRO domainConfiguration = new DomainPropertyRO();

        domainConfiguration.setProperty(property.getProperty());
        domainConfiguration.setSystemDefaultValue(configurationService.getDefaultDomainConfiguration(property));
        domainConfiguration.setSystemDefault(true);
        domainConfiguration.setValuePattern(property.getValuePattern().pattern());
        domainConfiguration.setType(property.getPropertyType().name());
        domainConfiguration.setDesc(property.getDesc());
        return domainConfiguration;
    }
}
