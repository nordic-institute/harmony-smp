package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.ui.GroupRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;


/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class DBGroupToGroupROConverter implements Converter<DBGroup, GroupRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBGroupToGroupROConverter.class);

    @Override
    public GroupRO convert(DBGroup source) {

        GroupRO target = new GroupRO();
        try {
            BeanUtils.copyProperties(target, source);
            target.setGroupId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error occurred while converting DBResourceDef", e);
            return null;
        }
        return target;
    }
}
