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
