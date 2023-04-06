/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.dao;

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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY;

/**
 * The Extension repository class
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class ExtensionDao extends BaseDao<DBExtension> implements InitializingBean {


    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ExtensionDao.class);

    protected final ApplicationContext applicationContext;
    protected final PlatformTransactionManager txManager;
    protected final ResourceDefDao resourceDefDao;
    protected final SubresourceDefDao subresourceDefDao;

    public ExtensionDao(ApplicationContext applicationContext, PlatformTransactionManager txManager, ResourceDefDao resourceDefDao, SubresourceDefDao subresourceDefDao) {
        this.applicationContext = applicationContext;
        this.txManager = txManager;
        this.resourceDefDao = resourceDefDao;
        this.subresourceDefDao = subresourceDefDao;
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
        // find all extension services
        Map<String, ExtensionInfo> registeredExtensions = applicationContext.getBeansOfType(ExtensionInfo.class);
        LOG.debug("Found registered extension count [{}]", registeredExtensions.size());
        // validate all extensions
        registeredExtensions.keySet()
                .forEach(name -> validateExtension(name, registeredExtensions.get(name)));
    }

    public void validateExtension(String extensionName, ExtensionInfo extensionInfo) {
        LOG.debug("Validate extension  [{}]", extensionName);
        Optional<DBExtension> extension = getExtensionByIdentifier(extensionInfo.identifier());
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
        persist(extension);
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


    /**
     * Returns extension records from the database.
     *
     * @return the list of extension records from smp_extension table
     */
    public List<DBExtension> getAllExtensions() {
        TypedQuery<DBExtension> query = memEManager.createNamedQuery(QUERY_EXTENSION_ALL, DBExtension.class);
        return query.getResultList();
    }

    /**
     * Returns the extension by implementation name (spring bean name).
     * Returns the extension or Optional.empty() if there is no extension.
     *
     * @return Returns the extension or Optional.empty() if there is no extension.
     * @throws IllegalStateException if no domain is not configured
     */
    public Optional<DBExtension> getExtensionByIdentifier(String identifier) {
        try {
            TypedQuery<DBExtension> query = memEManager.createNamedQuery(QUERY_EXTENSION_BY_IDENTIFIER, DBExtension.class);
            query.setParameter(PARAM_IDENTIFIER, identifier);
            DBExtension extension = query.getSingleResult();
            extension.getResourceDefs();
            return Optional.of(extension);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException(ILLEGAL_STATE_DOMAIN_MULTIPLE_ENTRY.getMessage(identifier));
        }
    }
}
