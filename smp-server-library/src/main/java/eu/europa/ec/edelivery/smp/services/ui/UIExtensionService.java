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
