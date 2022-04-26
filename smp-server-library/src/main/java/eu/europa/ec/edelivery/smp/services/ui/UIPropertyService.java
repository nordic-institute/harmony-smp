package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.cron.SMPDynamicCronTrigger;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.model.DBConfiguration;
import eu.europa.ec.edelivery.smp.data.ui.PropertyRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResultProperties;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.cron.CronTriggerConfig.TRIGGER_BEAN_PROPERTY_REFRESH;
import static eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum.SMP_CLUSTER_ENABLED;
import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT;

/**
 * UI property services
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Service
public class UIPropertyService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIPropertyService.class);
    ConfigurationDao configurationDao;
    final SMPDynamicCronTrigger refreshPropertiesTrigger;

    public UIPropertyService(ConfigurationDao configurationDao,
                             @Qualifier(TRIGGER_BEAN_PROPERTY_REFRESH) SMPDynamicCronTrigger refreshPropertiesTrigger) {
        this.configurationDao = configurationDao;
        this.refreshPropertiesTrigger = refreshPropertiesTrigger;
    }

    /**
     * Method returns Domain resource object list for page.
     *
     * @param page
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @param filterByProperty
     * @return
     */
    public ServiceResultProperties getTableList(int page, int pageSize,
                                                String sortField,
                                                String sortOrder, String filterByProperty) {

        LOG.debug("Get properties for page [{}], pageSize [{}] and filter [{}]", page, pageSize, filterByProperty);
        List<SMPPropertyEnum> filteredProperties = Arrays.asList(SMPPropertyEnum.values()).stream()
                .filter(prop -> StringUtils.isBlank(filterByProperty)
                        || StringUtils.containsIgnoreCase(prop.getProperty(),filterByProperty))
                .collect(Collectors.toList());
        LOG.debug("Got filtered properties count [{}]", filteredProperties.size());

        Map<String, DBConfiguration> changedProps = configurationDao.getPendingUpdateProperties().stream()
                .collect(Collectors.toMap(DBConfiguration::getProperty, Function.identity()));

        List<PropertyRO> properties = filteredProperties.stream()
                .skip( page * (long)pageSize)
                .limit(pageSize)
                .map(prop -> createProperty(prop, changedProps))
                .collect(Collectors.toList());

        ServiceResultProperties result = new ServiceResultProperties();
        result.getServiceEntities().addAll(properties);
        result.setCount((long) filteredProperties.size());
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setServerRestartNeeded(configurationDao.isServerRestartNeeded());
        return result;
    }

    public PropertyRO createProperty(SMPPropertyEnum propertyType, Map<String, DBConfiguration> changedProps) {
        PropertyRO property = new PropertyRO(propertyType.getProperty(),
                configurationDao.getCachedProperty(propertyType),
                propertyType.getPropertyType().name(),
                propertyType.getDesc());

        property.setEncrypted(propertyType.isEncrypted());
        property.setRestartNeeded(propertyType.isRestartNeeded());
        property.setMandatory(propertyType.isMandatory());

        if (changedProps.containsKey(property.getProperty())) {
            property.setNewValue(changedProps.get(propertyType.getProperty()).getValue());
            property.setUpdateDate(refreshPropertiesTrigger.getNextExecutionDate());
        }
        return property;
    }

    @Transactional
    public void updatePropertyList(List<PropertyRO> properties) {
        for (PropertyRO property : properties) {
            configurationDao.setPropertyToDatabase(property.getProperty(), property.getValue());
        }
        Boolean isClusterEnabled = (Boolean) configurationDao.getCachedPropertyValue(SMP_CLUSTER_ENABLED);
        if (isClusterEnabled) {
            LOG.info("Properties were updated in database. Changed properties will be activated to all cluster nodes at: [{}]!",
                    ISO_8601_EXTENDED_DATETIME_FORMAT.format(refreshPropertiesTrigger.getNextExecutionDate()));
            return;
        }
        configurationDao.reloadPropertiesFromDatabase();
    }
}
