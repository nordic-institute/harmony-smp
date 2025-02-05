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

import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.ui.ResourceDefinitionRO;
import eu.europa.ec.edelivery.smp.data.ui.SubresourceDefinitionRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;


/**
 *
 */
@Component
public class DBResourceDefToResourceDefinitionROConverter implements Converter<DBResourceDef, ResourceDefinitionRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBResourceDefToResourceDefinitionROConverter.class);
    private final ConversionService conversionService;

    public DBResourceDefToResourceDefinitionROConverter(@Lazy ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public ResourceDefinitionRO convert(DBResourceDef source) {

        ResourceDefinitionRO target = new ResourceDefinitionRO();
        try {
            BeanUtils.copyProperties(target, source);
            List<SubresourceDefinitionRO> resourceDefinitionROList = source.getSubresources().stream().map(resourceDef ->
                    conversionService.convert(resourceDef, SubresourceDefinitionRO.class)
            ).collect(Collectors.toList());


            target.getSubresourceDefinitions().addAll(resourceDefinitionROList);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error occurred while converting DBResourceDef", e);
            return null;
        }
        return target;
    }

}
