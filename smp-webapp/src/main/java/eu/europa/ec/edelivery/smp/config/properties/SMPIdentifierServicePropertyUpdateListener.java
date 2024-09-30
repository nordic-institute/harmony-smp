/*-
 * #START_LICENSE#
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.config.properties;

import eu.europa.ec.edelivery.smp.config.PropertyUpdateListener;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.IdentifierFormatterService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum.*;

/**
 * It is used to update identifier configuration if properties are changed.
 * It listens for changes in the following properties:
 * <ul>
 *     <li>{@link SMPPropertyEnum#RESOURCE_IDENTIFIER_TMPL_SPLIT_REGEXP}</li>
 *     <li>{@link SMPPropertyEnum#RESOURCE_SCH_VALIDATION_REGEXP}</li>
 *     <li>{@link SMPPropertyEnum#RESOURCE_SCH_MANDATORY}</li>
 *     <li>{@link SMPPropertyEnum#RESOURCE_CASE_SENSITIVE_SCHEMES}</li>
 *     <li>{@link SMPPropertyEnum#SUBRESOURCE_CASE_SENSITIVE_SCHEMES}</li>
 * </ul>
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class SMPIdentifierServicePropertyUpdateListener implements PropertyUpdateListener {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPIdentifierServicePropertyUpdateListener.class);

    private static final List<String> namedCachesToClear = Arrays.asList(
            IdentifierFormatterService.CACHE_NAME_DOMAIN_RESOURCE_IDENTIFIER_FORMATTER,
            IdentifierFormatterService.CACHE_NAME_DOMAIN_SUBRESOURCE_IDENTIFIER_FORMATTER);


    private final CacheManager cacheManager;

    public SMPIdentifierServicePropertyUpdateListener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void updateProperties(Map<SMPPropertyEnum, Object> properties) {
        LOG.debug("Reset identifier format properties!");
        // reset formatter cache on shared property update
        this.cacheManager.getCacheNames().stream()
                .filter(namedCachesToClear::contains)
                .map(this.cacheManager::getCache)
                .filter(Objects::nonNull)
                .forEach(Cache::clear);
    }

    @Override
    public List<SMPPropertyEnum> handledProperties() {
        return Arrays.asList(
                RESOURCE_IDENTIFIER_TMPL_SPLIT_REGEXP,
                RESOURCE_SCH_VALIDATION_REGEXP,
                RESOURCE_SCH_MANDATORY,
                RESOURCE_CASE_SENSITIVE_SCHEMES,
                SUBRESOURCE_CASE_SENSITIVE_SCHEMES
        );
    }
}
