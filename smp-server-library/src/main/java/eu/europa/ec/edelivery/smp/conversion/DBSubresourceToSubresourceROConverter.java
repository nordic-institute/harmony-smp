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

import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.ui.SubresourceRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;


/**
 *
 */
@Component
public class DBSubresourceToSubresourceROConverter implements Converter<DBSubresource, SubresourceRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBSubresourceToSubresourceROConverter.class);

    @Override
    public SubresourceRO convert(DBSubresource source) {

        SubresourceRO target = new SubresourceRO();
        try {
            BeanUtils.copyProperties(target, source);
            target.setSubresourceTypeIdentifier(source.getSubresourceDef().getIdentifier());
            target.setSubresourceId(SessionSecurityUtils.encryptedEntityId(source.getId()));

        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error occurred while converting DBResource", e);
            return null;
        }
        return target;
    }
}
