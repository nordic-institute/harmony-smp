package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.ui.ResourceRO;
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
public class DBResourceToResourceROConverter implements Converter<DBResource, ResourceRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBResourceToResourceROConverter.class);

    @Override
    public ResourceRO convert(DBResource source) {

        ResourceRO target = new ResourceRO();
        try {
            BeanUtils.copyProperties(target, source);
            target.setResourceTypeIdentifier(source.getDomainResourceDef().getResourceDef().getIdentifier());
            target.setResourceId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error occurred while converting DBResource", e);
            return null;
        }
        return target;
    }
}
