/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Registrar allowing registration of custom {@link org.springframework.core.convert.converter.Converter converters} for
 * the back-end domain model and autowiring them with the {@link org.springframework.core.convert.ConversionService} and
 * possibly other Spring beans (by first autowiring them into this registrar).
 *
 * @author Sebastian-Ion TINCU
 * @since 4.1
 */
@Component
public class ConvertersRegistrar {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ConvertersRegistrar.class);

    @Autowired
    private ConfigurableConversionService conversionRegistry;

    @Autowired
    public void registerCustomConverters(List<Converter<?,?>> converters) {
        for (Converter<?, ?> converter : converters) {
            conversionRegistry.addConverter(converter);
        }
        LOG.info("Finished registering custom converters: {}", converters);
    }
}
