/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
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
