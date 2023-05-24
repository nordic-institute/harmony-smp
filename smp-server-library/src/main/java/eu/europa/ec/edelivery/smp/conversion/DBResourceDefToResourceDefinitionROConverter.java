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
    private ConversionService conversionService;

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
