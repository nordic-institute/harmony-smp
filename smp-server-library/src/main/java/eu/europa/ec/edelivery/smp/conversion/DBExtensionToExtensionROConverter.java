package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.ext.DBExtension;
import eu.europa.ec.edelivery.smp.data.ui.ExtensionRO;
import eu.europa.ec.edelivery.smp.data.ui.ResourceDefinitionRO;
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
public class DBExtensionToExtensionROConverter implements Converter<DBExtension, ExtensionRO> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DBExtensionToExtensionROConverter.class);
    private ConversionService conversionService;

    public DBExtensionToExtensionROConverter( @Lazy ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public ExtensionRO convert(DBExtension source) {

        ExtensionRO target = new ExtensionRO();
        try {
            BeanUtils.copyProperties(target, source);
            List<ResourceDefinitionRO> resourceDefinitionROList =  source.getResourceDefs().stream().map(resourceDef ->
                    conversionService.convert(resourceDef, ResourceDefinitionRO.class)
            ).collect(Collectors.toList());
            target.getResourceDefinitions().addAll(resourceDefinitionROList);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error occurred while converting DBExtension", e);
            return null;
        }
        return target;
    }

}
