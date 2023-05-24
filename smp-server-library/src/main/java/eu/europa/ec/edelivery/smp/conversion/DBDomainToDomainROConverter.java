package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;


/**
 *
 */
@Component
public class DBDomainToDomainROConverter implements Converter<DBDomain, DomainRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBDomainToDomainROConverter.class);

    @Override
    public DomainRO convert(DBDomain source) {

        DomainRO target = new DomainRO();
        try {
            BeanUtils.copyProperties(target, source);
            List<String> domainDocuments = source.getDomainResourceDefs().stream().map(dbDomainResourceDef -> dbDomainResourceDef.getResourceDef().getIdentifier()).collect(Collectors.toList());
            target.getResourceDefinitions().addAll(domainDocuments);
            target.setDomainId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error occurred while converting DBDomain", e);
            return null;
        }
        return target;
    }
}
