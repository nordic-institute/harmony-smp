package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.data.ui.SubresourceDefinitionRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;


/**
 *
 */
@Component
public class DBSubresourceDefToSubresourceDefinitionROConverter implements Converter<DBSubresourceDef, SubresourceDefinitionRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBSubresourceDefToSubresourceDefinitionROConverter.class);

    @Override
    public SubresourceDefinitionRO convert(DBSubresourceDef source) {

        SubresourceDefinitionRO target = new SubresourceDefinitionRO();
        try {
            BeanUtils.copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error occurred while converting DBResourceDef", e);
            return null;
        }
        return target;
    }

}
