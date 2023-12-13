/*-
 * #%L
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.ExtensionDao;
import eu.europa.ec.edelivery.smp.data.model.ext.DBExtension;
import eu.europa.ec.edelivery.smp.data.ui.ExtensionRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UIExtensionService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIExtensionService.class);

    private final ExtensionDao extensionDao;
    private final ConversionService conversionService;


    public UIExtensionService(ExtensionDao extensionDao, ConversionService conversionService) {
        this.extensionDao = extensionDao;
        this.conversionService = conversionService;
    }

    @Transactional
    public List<ExtensionRO> getExtensions() {
        List<DBExtension> extensions = extensionDao.getAllExtensions();
        LOG.info("Got extension count: [{}]",  extensions.size());
        return extensions.stream().map(this::convertAndValidate).collect(Collectors.toList());
    }

    public ExtensionRO convertAndValidate(DBExtension extension) {
        ExtensionRO extensionRO = conversionService.convert(extension, ExtensionRO.class);
        return extensionRO;
    }
}
