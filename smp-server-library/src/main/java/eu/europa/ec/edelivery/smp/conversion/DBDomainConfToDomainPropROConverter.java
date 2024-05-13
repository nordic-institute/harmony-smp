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
public class DBDomainConfToDomainPropROConverter implements Converter<DBDomainConfiguration, DomainPropertyRO> {

    protected static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBDomainConfToDomainPropROConverter.class);
    private final ConfigurationService configurationService;

    public DBDomainConfToDomainPropROConverter(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public DomainPropertyRO convert(DBDomainConfiguration source) {
        if (source == null) {
            return null;
        }

        SMPDomainPropertyEnum enumType = SMPDomainPropertyEnum.getByProperty(source.getProperty()).orElse(null);
        if (enumType == null) {
            LOG.warn("Property {} is not supported by DomainPropertyRO, property is ignored!", source.getProperty());
            return null;
        }

        DomainPropertyRO target = new DomainPropertyRO();
        target.setProperty(source.getProperty());
        target.setSystemDefault(source.isUseSystemDefault());
        target.setValue(source.getValue());
        target.setValuePattern(enumType.getValuePattern().pattern());
        target.setType(enumType.getPropertyType().name());
        target.setSystemDefaultValue(configurationService.getDefaultDomainConfiguration(enumType));
        return target;
    }

}
