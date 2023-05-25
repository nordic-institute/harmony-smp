package eu.europa.ec.edelivery.smp.config.init;


import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.dao.ExtensionDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDefDao;
import eu.europa.ec.edelivery.smp.data.dao.SubresourceDefDao;
import eu.europa.ec.edelivery.smp.data.model.ext.DBExtension;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.smp.spi.ExtensionInfo;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.Optional;

/**
 * Class validates the registered extensions and updates the database. The validation is done at afterPropertiesSet(). Because the
 * transaction could not be yet initialized the method is wrapped wit the TransactionTemplate(txManager);
 *
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Component
public class SMPExtensionInitializer implements InitializingBean {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPExtensionInitializer.class);


    protected final ApplicationContext applicationContext;
    protected final PlatformTransactionManager txManager;
    protected final ExtensionDao extensionDao;
    protected final ResourceDefDao resourceDefDao;
    protected final SubresourceDefDao subresourceDefDao;

    protected final ConfigurationDao configurationDao;



    public SMPExtensionInitializer(ApplicationContext applicationContext,
                                   PlatformTransactionManager txManager,
                                   ExtensionDao extensionDao,
                                   ResourceDefDao resourceDefDao,
                                   SubresourceDefDao subresourceDefDao,
                                   ConfigurationDao configurationDao) {
        this.applicationContext = applicationContext;
        this.txManager = txManager;
        this.extensionDao = extensionDao;
        this.resourceDefDao = resourceDefDao;
        this.subresourceDefDao = subresourceDefDao;
        this.configurationDao = configurationDao;
    }

    /**
     * Validate and initialize extension configuration
     */
    public void afterPropertiesSet() {
        LOG.debug("Initialize DomiSMP extensions");
        // Transaction might not be yet initialized (@Transactional on this method does not help :) ).
        // Wrap the method to TransactionTemplate to make possible database property initialization
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(status -> {
            LOG.info("Start initial (re)load of the DomiSMP extension configuration");
            validateExtensionData();
            return null;
        });
    }

    public void validateExtensionData() {
        LOG.info("Load properties from database!");
        configurationDao.reloadPropertiesFromDatabase();
        // find all extension services
        Map<String, ExtensionInfo> registeredExtensions = applicationContext.getBeansOfType(ExtensionInfo.class);
        LOG.debug("Found registered extension count [{}]", registeredExtensions.size());
        // validate all extensions
        registeredExtensions.keySet()
                .forEach(name -> validateExtension(name, registeredExtensions.get(name)));
    }

    public void validateExtension(String extensionName, ExtensionInfo extensionInfo) {
        LOG.debug("Validate extension  [{}]", extensionName);
        Optional<DBExtension> extension = extensionDao.getExtensionByIdentifier(extensionInfo.identifier());
        if (extension.isPresent()) {
            updateExtension(extensionName, extensionInfo, extension.get());
        } else {
            registerExtension(extensionName, extensionInfo);
        }
    }

    public void validateResourceDefinition(ResourceDefinitionSpi resourceDefinitionSpi, DBExtension extension) {
        LOG.debug("Validate resourceDefinitionSpi [{}]", resourceDefinitionSpi);
        Optional<DBResourceDef> resourceDef = resourceDefDao.getResourceDefByIdentifier(resourceDefinitionSpi.identifier());
        if (resourceDef.isPresent()) {
            DBResourceDef dbResourceDef = resourceDef.get();
            if (StringUtils.equals(extension.getIdentifier(),dbResourceDef.getExtension().getIdentifier() )) {
                updateResourceSPI(resourceDefinitionSpi, dbResourceDef);
            } else {
                LOG.error("Skip resource definition update due to extension missmatch! ResourceDefinition [{}] is already registered for extension [{}]. The current resource extension identifier is [{}]!",
                        resourceDefinitionSpi,
                        extension,
                        dbResourceDef.getExtension());
            }
        } else {
            registerResourceSPI(resourceDefinitionSpi, extension);
        }
    }

    public void validateSubresourceDefinition(SubresourceDefinitionSpi resourceDefinitionSpi, DBResourceDef resourceDef) {
        LOG.debug("Validate subresourceDefinitionSpi [{}]", resourceDefinitionSpi);
        Optional<DBSubresourceDef> subresourceDef = subresourceDefDao.getSubresourceDefByIdentifier(resourceDefinitionSpi.identifier());
        if (subresourceDef.isPresent()) {
            DBSubresourceDef dbSubresourceDef = subresourceDef.get();
            if (StringUtils.equals(resourceDef.getIdentifier(),dbSubresourceDef.getResourceDef().getIdentifier() )) {
                updateSubresourceSPI(resourceDefinitionSpi, dbSubresourceDef);
            } else {
                LOG.error("Skip subresource definition update due to parent resource missmatch! SubresourceDefinition [{}] is already registered for parent resource [{}]. The current resource extension identifier is [{}]!",
                        resourceDefinitionSpi,
                        resourceDef, dbSubresourceDef.getResourceDef().getIdentifier());
            }
        } else {
            registerSubresourceSPI(resourceDefinitionSpi, resourceDef);
        }
    }

    public void registerExtension(String extensionName, ExtensionInfo extensionInfo) {
        LOG.debug("Register new extension  [{}]", extensionName);
        DBExtension extension = new DBExtension();
        extension.setImplementationName(extensionName);
        extension.setIdentifier(extensionInfo.identifier());
        extension.setName(extensionInfo.name());
        extension.setDescription(extensionInfo.description());
        extension.setVersion(extensionInfo.version());
        // register resources
        extensionInfo.resourceTypes().forEach(
                resourceDefinitionSpi -> validateResourceDefinition(resourceDefinitionSpi, extension)
        );
        extensionDao.persist(extension);
    }

    public void registerResourceSPI(ResourceDefinitionSpi resourceDefinitionSpi, DBExtension extension) {
        LOG.debug("Register new Resource definition [{}]", resourceDefinitionSpi);
        DBResourceDef resourceDef = new DBResourceDef();
        resourceDef.setIdentifier(resourceDefinitionSpi.identifier());
        updateResourceSPI(resourceDefinitionSpi, resourceDef);
        // bind to extension
        resourceDef.setExtension(extension);
        extension.getResourceDefs().add(resourceDef);
    }

    public void updateResourceSPI(ResourceDefinitionSpi resourceDefinitionSpi, DBResourceDef resourceDef) {
        LOG.debug("Update Resource definition [{}]", resourceDefinitionSpi);
        resourceDef.setName(resourceDefinitionSpi.name());
        resourceDef.setDescription(resourceDefinitionSpi.description());
        resourceDef.setMimeType(resourceDefinitionSpi.mimeType());
        resourceDef.setUrlSegment(resourceDefinitionSpi.defaultUrlSegment());
        resourceDef.setHandlerImplementationName(getHandlerSPIName(resourceDefinitionSpi.getResourceHandler()));
        resourceDefinitionSpi.getSuresourceSpiList().forEach(
                subresourceDefinitionSpi -> validateSubresourceDefinition(subresourceDefinitionSpi, resourceDef)
        );
    }

    public String getHandlerSPIName(ResourceHandlerSpi resourceHandlerSpi){
        return resourceHandlerSpi == null? null: resourceHandlerSpi.getClass().getSimpleName();
    }

    public void registerSubresourceSPI(SubresourceDefinitionSpi subresourceDefinitionSpi, DBResourceDef resourceDef) {
        LOG.debug("Register new SubResource  [{}]", subresourceDefinitionSpi);
        DBSubresourceDef subresourceDef = new DBSubresourceDef();
        subresourceDef.setIdentifier(subresourceDefinitionSpi.identifier());
        updateSubresourceSPI(subresourceDefinitionSpi, subresourceDef);

        // bind to resource
        resourceDef.getSubresources().add(subresourceDef);
        subresourceDef.setResourceDef(resourceDef);
    }

    public void updateSubresourceSPI(SubresourceDefinitionSpi subresourceDefinitionSpi, DBSubresourceDef subresourceDef) {
        LOG.debug("Update SubResource  [{}]", subresourceDefinitionSpi);
        subresourceDef.setName(subresourceDefinitionSpi.name());
        subresourceDef.setDescription(subresourceDefinitionSpi.description());
        subresourceDef.setMimeType(subresourceDefinitionSpi.mimeType());
        subresourceDef.setUrlSegment(subresourceDefinitionSpi.urlSegment());
        subresourceDef.setHandlerImplementationName(getHandlerSPIName(subresourceDefinitionSpi.getResourceHandler()));

    }



    public void updateExtension(String extensionName, ExtensionInfo extensionInfo, DBExtension extension) {
        LOG.debug("Update extension for implementationName [{}]", extensionInfo);
        extension.setName(extensionInfo.name());
        extension.setImplementationName(extensionName);
        extension.setDescription(extensionInfo.description());
        extension.setVersion(extensionInfo.version());
        // register resources
        extensionInfo.resourceTypes().forEach(
                resourceDefinitionSpi -> validateResourceDefinition(resourceDefinitionSpi, extension)
        );
    }
}
