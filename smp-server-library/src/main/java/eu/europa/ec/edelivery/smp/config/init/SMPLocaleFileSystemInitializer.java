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
package eu.europa.ec.edelivery.smp.config.init;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.SMPLanguageResourceService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Updates locale files on the disk with locales provided by DomiSMP. Any existing locales on the disk matching the
 * ones provided in DomiSMP are removed in the process of being replaced.
 *
 * @since 5.1
 * @author Sebastian-Ion TINCU
 */
@Component
public class SMPLocaleFileSystemInitializer {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPLocaleFileSystemInitializer.class);

    private final SMPLanguageResourceService smpLocaleService;

    public SMPLocaleFileSystemInitializer(SMPLanguageResourceService smpLocaleService) {
        this.smpLocaleService = smpLocaleService;
    }

    @PostConstruct
    public void init() {
        LOG.info("Initializing SMP locale filesystem");
        smpLocaleService.updateLocalesOnDisk();
    }
}
